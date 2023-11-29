/* ********************************************************************
 * Copyright (c) 2018, Institute of Cancer Research
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

import com.google.common.collect.ImmutableMap;
import icr.etherj2.PathScan;
import icr.etherj2.PathScanContext;
import icr.etherj2.dicom.DicomDiff;
import icr.etherj2.dicom.DicomToolkit;
import icr.etherj2.dicom.DicomUtils;
import org.dcm4che3.data.Attributes;
import org.nrg.xnatx.ohifviewer.service.OhifSessionDataService;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.roi.Constants;
import org.nrg.xnatx.plugin.PluginUtils;
import icr.xnat.plugin.roi.entity.DicomSpatialData;
import org.nrg.xnatx.roi.data.DsdUtils;
import org.nrg.xnatx.roi.data.RoiCollection;
import org.nrg.xnatx.roi.service.DicomSpatialDataService;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import org.dcm4che3.data.Tag;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.om.IcrRoicollectiondata;
import org.nrg.xdat.om.IcrRoicollectiondataSeriesuid;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xnatx.roi.RoiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jamesd
 */
public abstract class AbstractConversionHelper implements CollectionConverter.Helper {
    private final static Logger logger = LoggerFactory.getLogger(AbstractConversionHelper.class);

    private final static Map<String, String> targetTypeDescs = new HashMap<>();
    private final static Map<String, String> targetTypeFormats = new HashMap<>();

    private Map<String, Attributes> dcmMap = null;
    private final OhifSessionDataService ohifJsonService;
    protected final RoiCollection roiCollection;
    protected final DicomSpatialDataService spatialDataService;
    protected final String targetType;

    static {
        targetTypeDescs.put(Constants.RtStruct, "RT Structure Set");
        targetTypeDescs.put(Constants.AIM, "AIM Instance File");
        targetTypeFormats.put(Constants.RtStruct, "DICOM");
        targetTypeFormats.put(Constants.AIM, "XML");
    }

    /**
     * Returns a new conversion helper.
     * @param roiCollection the <code>RoiCollection</code> to convert
     * @param targetType the type to convert to
     * @param ohifJsonService the {@link OhifSessionDataService}
     */
    public AbstractConversionHelper(RoiCollection roiCollection, String targetType,
                                    DicomSpatialDataService spatialDataService, OhifSessionDataService ohifJsonService) {
        this.roiCollection = roiCollection;
        this.targetType = targetType;
        this.spatialDataService = spatialDataService;
        this.ohifJsonService = ohifJsonService;
    }

    @Override
    public File getCollectionFile() {
        return null;
    }

    @Override
    public Map<String, Attributes> getDicomObjectMap() throws PluginException {
        if (dcmMap == null) {
            dcmMap = buildDcmMap();
        }
        return ImmutableMap.copyOf(dcmMap);
    }

    @Override
    public String getTargetFileFormat() {
        return targetTypeFormats.getOrDefault(targetType, "Unknown");
    }

    @Override
    public String getTargetType() {
        return targetType;
    }

    @Override
    public String getTargetTypeDescription() {
        return targetTypeDescs.getOrDefault(targetType, "Unknown");
    }

    private Map<String, Attributes> buildDcmMap() throws PluginException {
        IcrRoicollectiondata collectData = RoiUtils.getCollectionDataById(roiCollection.getId());
        logger.debug("Building DICOM object map for ROI collection " + collectData.getLabel());
        XnatImagesessiondata sessionData = collectData.getImageSessionData();
        List<IcrRoicollectiondataSeriesuid> seriesUidList = collectData.getReferences_seriesuid();
        Map<String, Attributes> jsonMap = new HashMap<>();
        boolean jsonOk = buildFromOhifJsonService(sessionData, seriesUidList, jsonMap);
        Map<String, Attributes> dsdMap = new HashMap<>();
        if (buildFromSpatialDataService(seriesUidList, dsdMap)) {
            boolean compareOk = compareSd(dsdMap, jsonMap);
            if (!compareOk) {
                logger.warn("Using DicomSpatialDataService data!");
                return dsdMap;
            }
        }
        if (jsonOk) {
            return jsonMap;
        }
        logger.warn("DicomSpatialData not found in session JSON. Building from filesystem.");
        Map<String, Attributes> attrMap = new HashMap<>();
        for (IcrRoicollectiondataSeriesuid collDataSeriesUid : seriesUidList) {
            String seriesUid = collDataSeriesUid.getSeriesuid();
            if ((seriesUid == null) || seriesUid.isEmpty()) {
                logger.warn("Referenced series UIDs null or empty");
                continue;
            }
            String scanId = getScanId(sessionData, seriesUid);
            String scanPath = PluginUtils.getScanPath(sessionData, sessionData.getScanById(scanId));
            logger.debug("Scan path: {}", scanPath);
            Map<String, Attributes> dicomObjectMap = new HashMap<>();
            DicomReceiver dcmRx = new DicomReceiver(seriesUid, dicomObjectMap);
            PathScan<Attributes> scanner = DicomToolkit.getToolkit().createPathScan(Tag.PixelData);
            scanner.addContext(dcmRx);
            try {
                scanner.scan(scanPath);
            } catch (IOException ex) {
                throw new PluginException(ex.getMessage(), PluginCode.IO, ex);
            }
            if (dicomObjectMap.isEmpty()) {
                throw new PluginException(
                        "No DICOM files found for series UID " + seriesUid + " in path: " + scanPath,
                        PluginCode.FileNotFound);
            }
            logger.debug(String.format("%d items found for series UID %s", dicomObjectMap.size(), seriesUid));
            attrMap.putAll(dicomObjectMap);
        }
        logger.debug(String.format("%d items found for ROI collection %s", attrMap.size(), collectData.getLabel()));
        return attrMap;
    }

    private boolean buildFromOhifJsonService(XnatImagesessiondata sessionData,
                                             List<IcrRoicollectiondataSeriesuid> seriesUidList,
                                             Map<String, Attributes> attrMap) throws PluginException {
        long tick = System.currentTimeMillis();
        StringWriter writer = new StringWriter();
        ohifJsonService.transferSessionJson(sessionData.getId(), writer);
        Set<String> uids = seriesUidList.stream()
                .map(IcrRoicollectiondataSeriesuid::getSeriesuid)
                .collect(Collectors.toSet());
        long loaded = System.currentTimeMillis();
        logger.info("Raw JSON fetched from OhifSessionDataService: {}ms", loaded - tick);
        JsonProcessor jp = new JsonProcessor();
        jp.getSpatialDicom(sessionData, writer.toString(), uids, attrMap);

        logger.info("DICOM spatial data loaded from JSON or MultiFrame DICOM: {}ms",
                System.currentTimeMillis() - loaded);
        return true;
    }

    private boolean buildFromSpatialDataService(List<IcrRoicollectiondataSeriesuid> seriesUidList,
                                                Map<String, Attributes> attrMap) {
        long tick = System.currentTimeMillis();
        List<DicomSpatialData> dsdList = new ArrayList<>();
        for (IcrRoicollectiondataSeriesuid uid : seriesUidList) {
            List<DicomSpatialData> found = spatialDataService.findForSeries(uid.getSeriesuid());
            if (found != null) {
                dsdList.addAll(found);
            }
        }
        if (dsdList.isEmpty()) {
            return false;
        }

        dsdList.sort(new DsdUtils.DsdComparator());
        dsdList.forEach(dsd -> DsdUtils.createSpatialDicom(attrMap, dsd));

        logger.info("DICOM spatial data fetched from SpatialDataService: {}ms", System.currentTimeMillis() - tick);
        return true;
    }

    private boolean compareSd(Map<String, Attributes> dsdMap, Map<String, Attributes> jsonMap) {
        boolean ok = true;
        if (jsonMap.size() != dsdMap.size()) {
            ok = false;
            logger.debug("Size mismatch. JSON: {}, DSD: {}", jsonMap.size(), dsdMap.size());
        }
        for (Attributes dsdDcm : dsdMap.values()) {
            String uid = dsdDcm.getString(Tag.SOPInstanceUID);
            Attributes jsonDcm = jsonMap.getOrDefault(uid, null);
            if (jsonDcm == null) {
                ok = false;
                logger.debug("No JSON match found for DSD: {}", uid);
                continue;
            }
            DicomDiff diff = new DicomDiff(dsdDcm, jsonDcm);
            if (diff.isEmpty()) {
                continue;
            }
            ok = false;
            String output = "Empty";
            try (
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(baos)
            ) {
                diff.dump(ps);
                output = baos.toString();
            } catch (IOException ex) {
                logger.warn("Error printing diff: " + ex.getMessage());
            }
            logger.debug("Diff: {} elements for {}", diff.size(), uid);
            logger.debug(output);
        }
        return ok;
    }

    private String getScanId(XnatImagesessiondata sessionData, String refSeriesUid) throws PluginException {
        logger.debug("Finding scan ID for series UID: {}", refSeriesUid);
        for (XnatImagescandataI scan : sessionData.getScans_scan()) {
            String seriesUid = scan.getUid();
            if ((seriesUid != null) && seriesUid.equals(refSeriesUid)) {
                String id = scan.getId();
                logger.debug("Scan ID: {}", id);
                return id;
            }
        }
        throw new PluginException("No scan found for series UID: " + refSeriesUid, PluginCode.HttpNotFound);
    }

    private static class DicomReceiver implements PathScanContext<Attributes> {
        private final Map<String, Attributes> dcmMap;
        private final String seriesUid;

        public DicomReceiver(String seriesUid, Map<String, Attributes> dcmMap) {
            this.seriesUid = seriesUid;
            this.dcmMap = dcmMap;
        }

        @Override
        public void notifyItemFound(Path path, Attributes dcm) {
            String uid = dcm.getString(Tag.SeriesInstanceUID);
            if (seriesUid.equals(uid)) {
                dcmMap.put(dcm.getString(Tag.SOPInstanceUID), dcm);
            }
        }

        @Override
        public void notifyScanFinish() {
        }

        @Override
        public void notifyScanStart() {
        }
    }
}
