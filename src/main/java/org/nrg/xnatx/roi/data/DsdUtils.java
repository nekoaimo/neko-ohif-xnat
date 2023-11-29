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
package org.nrg.xnatx.roi.data;

import icr.etherj2.dicom.DicomUtils;
import org.dcm4che3.data.Sequence;

import java.util.Comparator;
import java.util.Map;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import icr.xnat.plugin.roi.entity.DicomSpatialData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jamesd
 */
public class DsdUtils {
    private final static Logger logger = LoggerFactory.getLogger(DsdUtils.class);

    public static void createSpatialDicom(Map<String, Attributes> dcmMap, DicomSpatialData dsd) {
        String sopClassUid = dsd.getSopClassUid();
        if (!DicomUtils.isImageSopClass(sopClassUid)) {
            return;
        }
        String sopInstUid = dsd.getSopInstanceUid();
        if (DicomUtils.isMultiframeImageSopClass(sopClassUid)) {
            Attributes dcm = dcmMap.get(sopInstUid);
            if (dcm == null) {
                dcm = createMultiframeSpatialDicom(dcmMap, dsd);
                dcmMap.put(dcm.getString(Tag.SOPInstanceUID), dcm);
            }
            populateMultiframeSpatialDicom(dcm, dsd);
            return;
        }

        Attributes dcm = createBaseSpatialDicom(dsd);
        dcm.setDouble(Tag.ImagePositionPatient, VR.DS, dsd.fetchImagePositionPatientAsDoubles());
        dcm.setDouble(Tag.ImageOrientationPatient, VR.DS, dsd.fetchImageOrientationPatientAsDoubles());
        dcm.setDouble(Tag.PixelSpacing, VR.DS, dsd.fetchPixelSpacingAsDoubles());

        dcmMap.put(dcm.getString(Tag.SOPInstanceUID), dcm);
    }

    private static Attributes createBaseSpatialDicom(DicomSpatialData dsd) {
        Attributes dcm = new Attributes();
        dcm.setString(Tag.SOPClassUID, VR.UI, dsd.getSopClassUid());
        dcm.setString(Tag.SOPInstanceUID, VR.UI, dsd.getSopInstanceUid());
        dcm.setString(Tag.FrameOfReferenceUID, VR.UI, dsd.getFrameOfReferenceUid());
        dcm.setString(Tag.StudyInstanceUID, VR.UI, dsd.getStudyUid());
        dcm.setString(Tag.SeriesInstanceUID, VR.UI, dsd.getSeriesUid());
        return dcm;
    }

    private static Attributes createMultiframeSpatialDicom(Map<String, Attributes> dcmMap, DicomSpatialData dsd) {
        Attributes dcm = createBaseSpatialDicom(dsd);
        // Create the per-frame sequence, filled in later: populateMultiframeSpatialDicom()
        dcm.newSequence(Tag.PerFrameFunctionalGroupsSequence, 1);

        dcmMap.put(dcm.getString(Tag.SOPInstanceUID), dcm);

        return dcm;
    }

    private static Attributes createNestedDataset(Attributes parent, int sqTag) {
        Attributes dcm = new Attributes(1);
        Sequence ppSq = parent.ensureSequence(sqTag, 1);
        ppSq.add(dcm);
        return dcm;
    }

    private static void populateMultiframeSpatialDicom(Attributes dcm, DicomSpatialData dsd) {
        int frame = dsd.getFrameNumber() - 1;
        Sequence pfSq = dcm.ensureSequence(Tag.PerFrameFunctionalGroupsSequence, 1);
        Attributes frameDcm = new Attributes(3);
        pfSq.add(frame, frameDcm);
        Attributes ppDcm = createNestedDataset(frameDcm, Tag.PlanePositionSequence);
        ppDcm.setDouble(Tag.ImagePositionPatient, VR.DS, dsd.fetchImagePositionPatientAsDoubles());
        Attributes poDcm = createNestedDataset(frameDcm, Tag.PlaneOrientationSequence);
        poDcm.setDouble(Tag.ImageOrientationPatient, VR.DS, dsd.fetchImageOrientationPatientAsDoubles());
        Attributes psDcm = createNestedDataset(frameDcm, Tag.PixelMeasuresSequence);
        psDcm.setDouble(Tag.PixelSpacing, VR.DS, dsd.fetchPixelSpacingAsDoubles());
    }

    private DsdUtils() {
    }

    public static class DsdComparator implements Comparator<DicomSpatialData> {
        @Override
        public int compare(DicomSpatialData a, DicomSpatialData b) {
            if (a == null) {
                return (b == null) ? 0 : -1;
            }
            if (b == null) {
                return 1;
            }
            if (a == b) {
                return 0;
            }
            int value = a.getSopInstanceUid().compareTo(b.getSopInstanceUid());
            if (value != 0) {
                return value;
            }
            return (int) Math.signum(a.getFrameNumber() - b.getFrameNumber());
        }
    }

}
