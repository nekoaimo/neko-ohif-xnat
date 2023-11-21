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

import icr.etherj2.PathScan;
import icr.etherj2.PathScanContext;
import icr.etherj2.StringUtils;
import icr.etherj2.dicom.DicomToolkit;
import icr.etherj2.dicom.DicomUtils;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.plugin.PluginUtils;
import org.nrg.xnatx.roi.service.DicomSpatialDataService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
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

		Attributes dcm = new Attributes();
		dcm.setString(Tag.SOPClassUID, VR.UI, dsd.getSopClassUid());
		dcm.setString(Tag.SOPInstanceUID, VR.UI, dsd.getSopInstanceUid());
		dcm.setString(Tag.FrameOfReferenceUID, VR.UI, dsd.getFrameOfReferenceUid());
		dcm.setString(Tag.StudyInstanceUID, VR.UI, dsd.getStudyUid());
		dcm.setString(Tag.SeriesInstanceUID, VR.UI, dsd.getSeriesUid());
		dcm.setDouble(Tag.ImagePositionPatient, VR.DS, dsd.fetchImagePositionPatientAsDoubles());
		dcm.setDouble(Tag.ImageOrientationPatient, VR.DS, dsd.fetchImageOrientationPatientAsDoubles());
		dcm.setDouble(Tag.PixelSpacing, VR.DS, dsd.fetchPixelSpacingAsDoubles());

		dcmMap.put(dcm.getString(Tag.SOPInstanceUID), dcm);
	}

	public static Map<String,Attributes> getDicomObjectMap(DicomSpatialDataService service, XnatImagescandata scanData)
		throws PluginException
	{
		logger.debug("Fetching from DicomSpatialDataService");
		Map<String,Attributes> dcmMap = new HashMap<>();
		String seriesUid = scanData.getUid();
		if (StringUtils.isNullOrEmpty(seriesUid))
		{
			logger.warn("Series UID not present for session " + scanData.getImageSessionId() +
					", scan " + scanData.getId());
			return dcmMap;
		}
		if (buildFromService(service, seriesUid, dcmMap))
		{
			return dcmMap;
		}
		XnatImagesessiondata sessionData = scanData.getImageSessionData();
		String scanPath = PluginUtils.getScanPath(sessionData, scanData);
		DicomReceiver dcmRx = new DicomReceiver(seriesUid, dcmMap);
		PathScan<Attributes> scanner = DicomToolkit.getToolkit().createPathScan(Tag.PixelData);
		scanner.addContext(dcmRx);
		try
		{
			scanner.scan(scanPath);
		}
		catch (IOException ex)
		{
			throw new PluginException(ex.getMessage(), PluginCode.IO, ex);
		}
		logger.debug(String.format("%d items found for series UID %s", dcmMap.size(), seriesUid));

		return dcmMap;
	}

	public static void saveToService(DicomSpatialDataService service, Map<String,Attributes> dcmMap)
	{
		logger.debug("Saving to DicomSpatialDataService");
		if (dcmMap.isEmpty())
		{
			logger.warn("No DICOM spatial data to store");
			return;
		}
		List<DicomSpatialData> dsdList = new ArrayList<>();
		for (Attributes dcm : dcmMap.values())
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

	private static boolean buildFromService(DicomSpatialDataService service, String seriesUid,
		Map<String, Attributes> dcmMap)
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

	private static DicomSpatialData createDicomSpatialData(Attributes dcm)
	{
		DicomSpatialData dsd = new DicomSpatialData();

		dsd.setSopClassUid(dcm.getString(Tag.SOPClassUID, ""));
		dsd.setSopInstanceUid(dcm.getString(Tag.SOPInstanceUID, ""));
		dsd.setFrameOfReferenceUid(dcm.getString(Tag.FrameOfReferenceUID, ""));
		dsd.setStudyUid(dcm.getString(Tag.StudyInstanceUID, ""));
		dsd.setSeriesUid(dcm.getString(Tag.SeriesInstanceUID, ""));
		String[] array = dcm.getStrings(Tag.ImagePositionPatient);
		dsd.setImagePositionPatient(array != null ? String.join("\\", array) : null);
		array = dcm.getStrings(Tag.ImageOrientationPatient);
		dsd.setImageOrientationPatient(array != null ? String.join("\\", array) : null);
		array = dcm.getStrings(Tag.PixelSpacing);
		dsd.setPixelSpacing(array != null ? String.join("\\", array) : null);

		return dsd;
	}

	private static List<DicomSpatialData> createMultiframeDicomSpatialData(Attributes dcm)
	{
		List<DicomSpatialData> dsdList = new ArrayList<>();
		String sopClassUid = dcm.getString(Tag.SOPClassUID, "");
		String sopInstUid = dcm.getString(Tag.SOPInstanceUID, "");
		String forUid = dcm.getString(Tag.FrameOfReferenceUID, "");
		String studyUid = dcm.getString(Tag.StudyInstanceUID, "");
		String seriesUid = dcm.getString(Tag.SeriesInstanceUID, "");
		int nFrames = dcm.getInt(Tag.NumberOfFrames, -1);
		if (nFrames < 0) {
			logger.warn("NumberOfFrames not found, cannot create multiframe DicomSpatialData");
			return dsdList;
		}
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

	private static Attributes createMultiframeSpatialDicom(Map<String,Attributes> dcmMap, DicomSpatialData dsd)
	{
		Attributes dcm = new Attributes();
		dcm.setString(Tag.SOPClassUID, VR.UI, dsd.getSopClassUid());
		dcm.setString(Tag.SOPInstanceUID, VR.UI, dsd.getSopInstanceUid());
		dcm.setString(Tag.FrameOfReferenceUID, VR.UI, dsd.getFrameOfReferenceUid());
		dcm.setString(Tag.StudyInstanceUID, VR.UI, dsd.getStudyUid());
		dcm.setString(Tag.SeriesInstanceUID, VR.UI, dsd.getSeriesUid());
		// Create the per-frame sequence, filled in later: populateMultiframeSpatialDicom()
		dcm.newSequence(Tag.PerFrameFunctionalGroupsSequence, 1);

		dcmMap.put(dcm.getString(Tag.SOPInstanceUID), dcm);

		return dcm;
	}

	private static double[] getImageOrientationPatient(Attributes dcm, int frame)
	{
		Attributes poDcm = DicomUtils.getFunctionGroup(dcm, Tag.PlaneOrientationSequence, frame - 1);
		double[] ori = poDcm.getDoubles(Tag.ImageOrientationPatient);
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

	private static double[] getImagePositionPatient(Attributes dcm, int frame)
	{
		Attributes ppDcm = DicomUtils.getFunctionGroup(dcm, Tag.PlanePositionSequence, frame - 1);
		double[] pos = ppDcm.getDoubles(Tag.ImagePositionPatient);
		if ((pos == null) || (pos.length != 3))
		{
			String serUid = dcm.getString(Tag.SeriesInstanceUID);
			String extra = (serUid != null) ? " - Series "+serUid+", Frame "+frame : "";
			logger.warn("ImagePositionPatient missing or invalid"+extra);
			pos = new double[] {};
		}
		return pos;
	}

	private static double[] getPixelSpacing(Attributes dcm, int frame)
	{
		Attributes psDcm = DicomUtils.getFunctionGroup(dcm, Tag.PixelMeasuresSequence, frame - 1);
		double[] spacing = psDcm.getDoubles(Tag.PixelSpacing);
		if ((spacing == null) || (spacing.length != 2))
		{
			String serUid = dcm.getString(Tag.SeriesInstanceUID);
			String extra = (serUid != null) ? " - Series "+serUid+", Frame "+frame : "";
			logger.warn("PixelSpacing missing or invalid"+extra);
			spacing = new double[] {};
		}
		return spacing;
	}

	private static void populateMultiframeSpatialDicom(Attributes dcm, DicomSpatialData dsd)
	{
		int frame = dsd.getFrameNumber()-1;
		Attributes ppDcm = DicomUtils.getFunctionGroup(dcm, Tag.PlanePositionSequence, frame);
		ppDcm.setDouble(Tag.ImagePositionPatient, VR.DS, dsd.fetchImagePositionPatientAsDoubles());
		Attributes poDcm = DicomUtils.getFunctionGroup(dcm, Tag.PlaneOrientationSequence, frame);
		poDcm.setDouble(Tag.ImageOrientationPatient, VR.DS, dsd.fetchImageOrientationPatientAsDoubles());
		Attributes psDcm = DicomUtils.getFunctionGroup(dcm, Tag.PixelMeasuresSequence, frame);
		psDcm.setDouble(Tag.PixelSpacing, VR.DS, dsd.fetchPixelSpacingAsDoubles());
	}

	private DsdUtils()
	{}

	public static class DsdComparator implements Comparator<DicomSpatialData>
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

	private static class DicomReceiver implements PathScanContext<Attributes>
	{
		private final Map<String,Attributes> dcmMap;
		private final String seriesUid;

		public DicomReceiver(String seriesUid, Map<String,Attributes> dcmMap)
		{
			this.seriesUid = seriesUid;
			this.dcmMap = dcmMap;
		}

		@Override
		public void notifyItemFound(Path path, Attributes dcm)
		{
			String uid = dcm.getString(Tag.SeriesInstanceUID);
			if (seriesUid.equals(uid))
			{
				// Exclude pixel data to save RAM
				dcmMap.put(dcm.getString(Tag.SOPInstanceUID), dcm);
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
