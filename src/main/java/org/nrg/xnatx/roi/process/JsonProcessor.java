/* ********************************************************************
 * Copyright (c) 2023, Institute of Cancer Research
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * (2) Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *
 * (3) Neither the name of the Institute of Cancer Research nor the
 *     names of its contributors may be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *********************************************************************/
package org.nrg.xnatx.roi.process;

import com.google.gson.*;
import icr.etherj2.dicom.DicomUtils;
import org.dcm4che3.data.*;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.plugin.PluginUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class JsonProcessor {
    private final static Logger logger = LoggerFactory.getLogger(JsonProcessor.class);

    public void getSpatialDicom(XnatImagesessiondata sessionData, String json, Set<String> uids,
                                Map<String, Attributes> dcmMap) {
        if (uids.isEmpty()) {
            return;
        }
        // Parse as raw JSON types as the OhifViewerInput classes don't have default constructors or validating
        // accessors
        long tick = System.currentTimeMillis();
        JsonElement tree;
        try {
            tree = JsonParser.parseString(json);
        } catch (JsonSyntaxException ex) {
            logger.warn("Unable to process session JSON: " + ex.getMessage(), ex);
            return;
        }
        long parsed = System.currentTimeMillis();
        logger.info("JSON parsed to tree: {}ms", parsed - tick);
        // Navigate tree without constantly testing and recasting elements, catch the IllegalStateException if
        // anything is incorrectly typed
        try {
            JsonObject root = tree.getAsJsonObject(); // OhifViewerInput
            // Should only ever be one study
            JsonArray studies = root.getAsJsonArray("studies");
            int nStudies = studies.size();
            if (nStudies == 0) {
                logger.error("Zero studies found in session JSON");
                return;
            }
            if (nStudies > 1) {
                logger.warn("More than one study found in JSON: {}", nStudies);
            }
            JsonObject study = studies.get(0).getAsJsonObject(); // OhifViewerInputStudy
            String studyUid = study.getAsJsonPrimitive("StudyInstanceUID").getAsString();
            JsonArray seriesArr = study.getAsJsonArray("series"); // [OhifViewerInputSeries]
            for (JsonElement elem : seriesArr) {
                JsonObject series = elem.getAsJsonObject();
                String seriesUid = series.getAsJsonPrimitive("SeriesInstanceUID").getAsString();
                if (!uids.contains(seriesUid)) {
                    continue;
                }
                JsonArray instanceArr = series.getAsJsonArray("instances");
                for (JsonElement instElem : instanceArr) {
                    JsonObject inst = instElem.getAsJsonObject(); // OhifViewerInputInstance
                    JsonObject meta = inst.getAsJsonObject("metadata"); // OhifViewerInputInstanceMetadata
                    String classUid = meta.getAsJsonPrimitive("SOPClassUID").getAsString();
                    String instUid = meta.getAsJsonPrimitive("SOPInstanceUID").getAsString();
                    String forUid = meta.getAsJsonPrimitive("FrameOfReferenceUID").getAsString();
                    Attributes dcm = new Attributes();
                    dcm.setString(Tag.StudyInstanceUID, VR.UI, studyUid);
                    dcm.setString(Tag.SeriesInstanceUID, VR.UI, seriesUid);
                    dcm.setString(Tag.SOPInstanceUID, VR.UI, instUid);
                    dcm.setString(Tag.SOPClassUID, VR.UI, classUid);
                    dcm.setString(Tag.FrameOfReferenceUID, VR.UI, forUid);
                    if (DicomUtils.isMultiframeImageSopClass(classUid)) {
                        logger.warn("MultiFrame SOP class found: {}", UID.nameOf(classUid));
                        try {
                            processMultiFrame(sessionData, seriesUid, inst, meta, dcm);
                        } catch (IOException ex) {
                            logger.warn("Error reading multiframe DICOM: " + ex.getMessage(), ex);
                        }
                    } else {
                        dcm.setDouble(Tag.ImagePositionPatient, VR.DS,
                                getDoubleArray(meta.getAsJsonArray("ImagePositionPatient"), 3));
                        dcm.setDouble(Tag.ImageOrientationPatient, VR.DS,
                                getDoubleArray(meta.getAsJsonArray("ImageOrientationPatient"), 6));
                        dcm.setDouble(Tag.PixelSpacing, VR.DS,
                                getDoubleArray(meta.getAsJsonArray("PixelSpacing"), 2));
                    }
                    dcmMap.put(instUid, dcm);
                }
            }
            logger.debug("JSON tree to spatial DICOM: {}ms", System.currentTimeMillis() - parsed);
        } catch (IllegalStateException ex) {
            logger.warn("JSON processing failed: " + ex.getMessage(), ex);
            dcmMap.clear();
            return;
        }
    }

    private double[] getDoubleArray(JsonArray jsonArray, int length) {
        if (jsonArray.size() != length) {
            throw new IllegalStateException("Invalid array length. Expected: " + length + " Found: " + jsonArray.size());
        }
        double[] array = new double[length];
        for (int i = 0; i < length; i++) {
            array[i] = jsonArray.get(i).getAsDouble();
        }
        return array;
    }

    private String getFilename(JsonObject inst) throws IllegalStateException {
        String url = inst.getAsJsonPrimitive("url").getAsString();
        String[] tokens = url.split("/");
        if (tokens.length < 1) {
            throw new IllegalStateException("URL has no components: " + url);
        }
        return tokens[tokens.length - 1];
    }

    private double[] getImageOrientationPatient(Attributes dcm, int frame) {
        Attributes poDcm = DicomUtils.getFunctionGroup(dcm, Tag.PlaneOrientationSequence, frame - 1);
        double[] ori = poDcm.getDoubles(Tag.ImageOrientationPatient);
        if ((ori == null) || (ori.length != 6)) {
            String serUid = dcm.getString(Tag.SeriesInstanceUID);
            String extra = (serUid != null)
                    ? " - Series " + serUid + ", Frame " + frame
                    : "";
            logger.warn("ImageOrientationPatient missing or invalid" + extra);
            ori = new double[]{};
        }
        return ori;
    }

    private double[] getImagePositionPatient(Attributes dcm, int frame) {
        Attributes ppDcm = DicomUtils.getFunctionGroup(dcm, Tag.PlanePositionSequence, frame - 1);
        double[] pos = ppDcm.getDoubles(Tag.ImagePositionPatient);
        if ((pos == null) || (pos.length != 3)) {
            String serUid = dcm.getString(Tag.SeriesInstanceUID);
            String extra = (serUid != null) ? " - Series " + serUid + ", Frame " + frame : "";
            logger.warn("ImagePositionPatient missing or invalid" + extra);
            pos = new double[]{};
        }
        return pos;
    }

    private double[] getPixelSpacing(Attributes dcm, int frame) {
        Attributes psDcm = DicomUtils.getFunctionGroup(dcm, Tag.PixelMeasuresSequence, frame - 1);
        double[] spacing = psDcm.getDoubles(Tag.PixelSpacing);
        if ((spacing == null) || (spacing.length != 2)) {
            String serUid = dcm.getString(Tag.SeriesInstanceUID);
            String extra = (serUid != null) ? " - Series " + serUid + ", Frame " + frame : "";
            logger.warn("PixelSpacing missing or invalid" + extra);
            spacing = new double[]{};
        }
        return spacing;
    }

    private void processMultiFrame(XnatImagesessiondata sessionData, String seriesUid, JsonObject inst, JsonObject meta,
                                   Attributes dcm)
            throws IllegalStateException, IOException {
        // The JSON doesn't contain multiframe IPP, IOP and pixel spacing, fetch from file
        int nFrames = meta.getAsJsonPrimitive("NumberOfFrames").getAsInt();
        Sequence pfSq = dcm.newSequence(Tag.PerFrameFunctionalGroupsSequence, nFrames);
        String filename = getFilename(inst);
        Attributes source = readMultiFrameSource(sessionData, seriesUid, filename);
        int sourceFrames = source.getInt(Tag.NumberOfFrames, -1);
        if (sourceFrames != nFrames) {
            throw new IllegalStateException("Frame count mismatch. Expected: " + nFrames + " Found: " + sourceFrames);
        }
        // Iterate with frame number not array index (frame == idx+1)
        for (int frame = 1; frame <= nFrames; frame++) {
            Attributes item = new Attributes();
            pfSq.add(item);
            Attributes ppDcm = newAttrIn(item, Tag.PlanePositionSequence);
            ppDcm.setDouble(Tag.ImagePositionPatient, VR.DS, getImagePositionPatient(source, frame));
            Attributes poDcm = newAttrIn(item, Tag.PlaneOrientationSequence);
            poDcm.setDouble(Tag.ImageOrientationPatient, VR.DS, getImageOrientationPatient(source, frame));
            Attributes psDcm = newAttrIn(item, Tag.PixelMeasuresSequence);
            psDcm.setDouble(Tag.PixelSpacing, VR.DS, getPixelSpacing(source, frame));
        }
    }

    private Attributes newAttrIn(Attributes item, int sqTag) {
        Sequence sq = item.newSequence(sqTag, 1);
        Attributes dcm = new Attributes();
        sq.add(dcm);
        return dcm;
    }

    private Attributes readMultiFrameSource(XnatImagesessiondata sessionData, String seriesUid, String filename)
            throws IOException {
        long tick = System.currentTimeMillis();
        XnatImagescandataI scanData = null;
        for (XnatImagescandataI scan : sessionData.getScans_scan()) {
            String uid = scan.getUid();
            if (seriesUid.equals(uid)) {
                scanData = scan;
                break;
            }
        }
        if (scanData == null) {
            throw new IOException("No scan found in session " + sessionData.getId() + " for UID " + seriesUid);
        }
        String scanPath = PluginUtils.getScanPath(sessionData, scanData);
        long pathFound = System.currentTimeMillis();
        logger.debug("JSON tree sub-op - MF file path from DB: {}ms", pathFound - tick);
        File file = Paths.get(scanPath, filename).toFile();
        Attributes dcm = DicomUtils.readDicomFile(file, Tag.PixelData);
        logger.debug("JSON tree sub-op - Load MF file: {}ms", System.currentTimeMillis() - pathFound);
        return dcm;
    }
}

