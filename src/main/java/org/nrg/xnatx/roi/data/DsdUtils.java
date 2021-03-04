/*********************************************************************
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

import icr.etherj.PathScan;
import icr.etherj.PathScanContext;
import icr.etherj.StringUtils;
import icr.etherj.dicom.DicomToolkit;
import icr.etherj.dicom.DicomUtils;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.plugin.PluginUtils;
import org.nrg.xnatx.roi.service.DicomSpatialDataService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.SequenceDicomElement;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import icr.xnat.plugin.roi.entity.DicomSpatialData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jamesd
 */
public class DsdUtils
{
	private final static Logger logger = LoggerFactory.getLogger(
		DsdUtils.class);

	public static Map<String,DicomObject> getDicomObjectMap(
		DicomSpatialDataService service, XnatImagescandata scanData)
		throws PluginException
	{
		Map<String,DicomObject> dcmMap = new HashMap<>();
		String seriesUid = scanData.getUid();
		if (StringUtils.isNullOrEmpty(seriesUid))
		{
			logger.warn("Series UID not present for session "+
				scanData.getImageSessionId()+", scan "+scanData.getId());
			return dcmMap;
		}
		if (buildFromService(service, seriesUid, dcmMap))
		{
			return dcmMap;
		}
		XnatImagesessiondata sessionData = scanData.getImageSessionData();
		String scanPath = PluginUtils.getScanPath(sessionData, scanData);
		DicomReceiver dcmRx = new DicomReceiver(seriesUid, dcmMap);
		PathScan<DicomObject> scanner = DicomToolkit.getToolkit().createPathScan();
		scanner.addContext(dcmRx);
		try
		{
			scanner.scan(scanPath);
		}
		catch (IOException ex)
		{
			throw new PluginException(ex.getMessage(), PluginCode.IO, ex);
		}
		logger.debug(String.format("%d items found for series UID %s",
			dcmMap.size(), seriesUid));

		return dcmMap;
	}

	public static void saveToService(DicomSpatialDataService service,
		Map<String,DicomObject> dcmMap)
	{
		if (dcmMap.isEmpty())
		{
			logger.warn("No DICOM spatial data to store");
			return;
		}
		List<DicomSpatialData> dsdList = new ArrayList<>();
		for (DicomObject dcm : dcmMap.values())
		{
			if (DicomUtils.isMultiframeImageSopClass(dcm.getString(Tag.SOPClassUID)))
			{
				dsdList.addAll(createMultiframeDicomSpatialData(dcm));
			}
			else
			{
				dsdList.add(createDicomSpatialData(dcm));
			}
		}
		service.create(dsdList);
		logger.info("DICOM spatial data saved to service");
	}

	private static boolean buildFromService(
		DicomSpatialDataService service, String seriesUid,
		Map<String, DicomObject> dcmMap)
	{
		List<DicomSpatialData> dsdList = new ArrayList<>();
		List<DicomSpatialData> found = service.findForSeries(seriesUid);
		if (found != null)
		{
			dsdList.addAll(found);
		}
		if (dsdList.isEmpty())
		{
			return false;
		}

		dsdList.sort(new DsdComparator());
		for (DicomSpatialData dsd : dsdList)
		{
			createSpatialDicom(dcmMap, dsd);
		}

		logger.info("DICOM spatial data fetched from service");
		return true;
	}

	private static DicomSpatialData createDicomSpatialData(DicomObject dcm)
	{
		DicomSpatialData dsd = new DicomSpatialData();
	
		dsd.setSopClassUid(dcm.getString(Tag.SOPClassUID, ""));
		dsd.setSopInstanceUid(dcm.getString(Tag.SOPInstanceUID, ""));
		dsd.setFrameOfReferenceUid(dcm.getString(Tag.FrameOfReferenceUID, ""));
		dsd.setStudyUid(dcm.getString(Tag.StudyInstanceUID, ""));
		dsd.setSeriesUid(dcm.getString(Tag.SeriesInstanceUID, ""));
		String[] empty = new String[] {};
		String[] array = dcm.getStrings(Tag.ImagePositionPatient, empty);
		dsd.setImagePositionPatient(StringUtils.join("\\", array));
		array = dcm.getStrings(Tag.ImageOrientationPatient, empty);
		dsd.setImageOrientationPatient(StringUtils.join("\\", array));
		array = dcm.getStrings(Tag.PixelSpacing, empty);
		dsd.setPixelSpacing(StringUtils.join("\\", array));
	
		return dsd;
	}

	private static List<DicomSpatialData> createMultiframeDicomSpatialData(
		DicomObject dcm)
	{
		List<DicomSpatialData> dsdList = new ArrayList<>();
		String sopClassUid = dcm.getString(Tag.SOPClassUID, "");
		String sopInstUid = dcm.getString(Tag.SOPInstanceUID, "");
		String forUid = dcm.getString(Tag.FrameOfReferenceUID, "");
		String studyUid = dcm.getString(Tag.StudyInstanceUID, "");
		String seriesUid = dcm.getString(Tag.SeriesInstanceUID, "");
		int nFrames = dcm.getInt(Tag.NumberOfFrames);
		for (int i=0; i<nFrames; i++)
		{
			DicomSpatialData dsd = new DicomSpatialData();
			int frame = i+1;
			dsd.setSopClassUid(sopClassUid);
			dsd.setSopInstanceUid(sopInstUid);
			dsd.setFrameOfReferenceUid(forUid);
			dsd.setStudyUid(studyUid);
			dsd.setSeriesUid(seriesUid);
			dsd.setFrameNumber(frame);
			dsd.setImagePositionPatient(getImagePositionPatient(dcm, frame));
			dsd.setImageOrientationPatient(getImageOrientationPatient(dcm, frame));
			dsd.setPixelSpacing(getPixelSpacing(dcm, frame));

			dsdList.add(dsd);
		}

		return dsdList;
	}

	private static DicomObject createMultiframeSpatialDicom(DicomSpatialData dsd)
	{
		DicomObject dcm = new BasicDicomObject();
		dcm.putString(Tag.SOPClassUID, VR.UI, dsd.getSopClassUid());
		dcm.putString(Tag.SOPInstanceUID, VR.UI, dsd.getSopInstanceUid());
		dcm.putString(Tag.FrameOfReferenceUID, VR.UI,
			dsd.getFrameOfReferenceUid());
		dcm.putString(Tag.StudyInstanceUID, VR.UI, dsd.getStudyUid());
		dcm.putString(Tag.SeriesInstanceUID, VR.UI, dsd.getSeriesUid());
		// Create the per-frame sequence, filled in later:
		// populateMultiframeSpatialDicom()
		dcm.add(new SequenceDicomElement(
			Tag.PerFrameFunctionalGroupsSequence, VR.SQ, false, new ArrayList<>(),
			dcm));

		return dcm;
	}

	private static void createSpatialDicom(Map<String,DicomObject> dcmMap,
		DicomSpatialData dsd)
	{
		String sopClassUid = dsd.getSopClassUid();
		if (!DicomUtils.isImageSopClass(sopClassUid))
		{
			return;
		}
		String sopInstUid = dsd.getSopInstanceUid();
		if (DicomUtils.isMultiframeImageSopClass(sopClassUid))
		{
			DicomObject dcm = dcmMap.get(sopInstUid);
			if (dcm == null)
			{
				dcm = createMultiframeSpatialDicom(dsd);
				dcmMap.put(dcm.getString(Tag.SOPInstanceUID), dcm);
			}
			populateMultiframeSpatialDicom(dcm, dsd);
			return;
		}

		DicomObject dcm =  new BasicDicomObject();
		dcm.putString(Tag.SOPClassUID, VR.UI, dsd.getSopClassUid());
		dcm.putString(Tag.SOPInstanceUID, VR.UI, dsd.getSopInstanceUid());
		dcm.putString(Tag.FrameOfReferenceUID, VR.UI,
			dsd.getFrameOfReferenceUid());
		dcm.putString(Tag.StudyInstanceUID, VR.UI, dsd.getStudyUid());
		dcm.putString(Tag.SeriesInstanceUID, VR.UI, dsd.getSeriesUid());
		dcm.putDoubles(Tag.ImagePositionPatient, VR.DS,
			dsd.fetchImagePositionPatientAsDoubles());
		dcm.putDoubles(Tag.ImageOrientationPatient, VR.DS,
			dsd.fetchImageOrientationPatientAsDoubles());
		dcm.putDoubles(Tag.PixelSpacing, VR.DS, dsd.fetchPixelSpacingAsDoubles());

		dcmMap.put(dcm.getString(Tag.SOPInstanceUID), dcm);
	}

	private static double[] getImageOrientationPatient(DicomObject dcm, int frame)
	{
		double[] ori = dcm.getDoubles(new int[] {
			Tag.PerFrameFunctionalGroupsSequence, frame-1,
			Tag.PlaneOrientationSequence, 0,
			Tag.ImageOrientationPatient});
		if ((ori == null) || (ori.length != 6))
		{
			ori = dcm.getDoubles(new int[] {
				Tag.SharedFunctionalGroupsSequence, 0,
				Tag.PlaneOrientationSequence, 0,
				Tag.ImageOrientationPatient});
		}
		if ((ori == null) || (ori.length != 6))
		{
			String serUid = dcm.getString(Tag.SeriesInstanceUID);
			String extra = (serUid != null)
				? " - Series "+serUid+", Frame "+frame
				: "";
			logger.warn("ImageOrientationPatient missing or invalid"+extra);
			ori = new double[] {};
		}
		return ori;
	}

	private static double[] getImagePositionPatient(DicomObject dcm, int frame)
	{
		double[] pos = dcm.getDoubles(new int[] {
			Tag.PerFrameFunctionalGroupsSequence, frame-1,
			Tag.PlanePositionSequence, 0,
			Tag.ImagePositionPatient});
		if ((pos == null) || (pos.length != 3))
		{
			String serUid = dcm.getString(Tag.SeriesInstanceUID);
			String extra = (serUid != null) ? " - Series "+serUid+", Frame "+frame : "";
			logger.warn("ImagePositionPatient missing or invalid"+extra);
			pos = new double[] {};
		}
		return pos;
	}

	private static double[] getPixelSpacing(DicomObject dcm, int frame)
	{
		double[] spacing = dcm.getDoubles(new int[] {
			Tag.PerFrameFunctionalGroupsSequence, frame-1,
			Tag.PixelMeasuresSequence, 0,
			Tag.PixelSpacing});
		if ((spacing == null) || (spacing.length != 2))
		{
			spacing = dcm.getDoubles(new int[] {
				Tag.SharedFunctionalGroupsSequence, 0,
				Tag.PixelMeasuresSequence, 0,
				Tag.PixelSpacing});
		}
		if ((spacing == null) || (spacing.length != 2))
		{
			String serUid = dcm.getString(Tag.SeriesInstanceUID);
			String extra = (serUid != null) ? " - Series "+serUid+", Frame "+frame : "";
			logger.warn("PixelSpacing missing or invalid"+extra);
			spacing = new double[] {};
		}
		return spacing;
	}

	private static void populateMultiframeSpatialDicom(DicomObject dcm,
		DicomSpatialData dsd)
	{
		int frame = dsd.getFrameNumber()-1;
		dcm.putDoubles(new int[] {Tag.PerFrameFunctionalGroupsSequence, frame,
			Tag.PlanePositionSequence, 0, Tag.ImagePositionPatient}, VR.DS,
			dsd.fetchImagePositionPatientAsDoubles());
		dcm.putDoubles(new int[] {Tag.PerFrameFunctionalGroupsSequence, frame,
			Tag.PlaneOrientationSequence, 0, Tag.ImageOrientationPatient}, VR.DS,
			dsd.fetchImageOrientationPatientAsDoubles());
		dcm.putDoubles(new int[] {Tag.PerFrameFunctionalGroupsSequence, frame,
			Tag.PixelMeasuresSequence, 0, Tag.PixelSpacing}, VR.DS,
			dsd.fetchPixelSpacingAsDoubles());
	}

	private DsdUtils()
	{}

	private static class DsdComparator implements Comparator<DicomSpatialData>
	{
		@Override
		public int compare(DicomSpatialData a, DicomSpatialData b)
		{
			if (a == null)
			{
				return (b == null) ? 0 : -1;
			}
			if (b == null)
			{
				return 1;
			}
			if (a == b)
			{
				return 0;
			}
			int value = a.getSopInstanceUid().compareTo(b.getSopInstanceUid());
			if (value != 0)
			{
				return value;
			}
			return (int) Math.signum(a.getFrameNumber()-b.getFrameNumber());
		}
	}

	private static class DicomReceiver implements PathScanContext<DicomObject>
	{
		private final Map<String,DicomObject> dcmMap;
		private final String seriesUid;

		public DicomReceiver(String seriesUid, Map<String,DicomObject> dcmMap)
		{
			this.seriesUid = seriesUid;
			this.dcmMap = dcmMap;
		}

		@Override
		public void notifyItemFound(File file, DicomObject dcm)
		{
			String uid = dcm.getString(Tag.SeriesInstanceUID);
			if (seriesUid.equals(uid))
			{
				// Exclude pixel data to save RAM
				dcmMap.put(dcm.getString(Tag.SOPInstanceUID),
					dcm.exclude(new int[] {Tag.PixelData}));
			}
		}

		@Override
		public void notifyScanFinish()
		{}

		@Override
		public void notifyScanStart()
		{}
	}
}
