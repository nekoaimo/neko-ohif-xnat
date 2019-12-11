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
package org.nrg.xnatx.roi.process;

import com.google.common.collect.ImmutableMap;
import icr.etherj.PathScan;
import icr.etherj.PathScanContext;
import icr.etherj.StringUtils;
import icr.etherj.dicom.DicomToolkit;
import icr.etherj.dicom.DicomUtils;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.roi.Constants;
import org.nrg.xnatx.plugin.PluginUtils;
import icr.xnat.plugin.roi.entity.DicomSpatialData;
import org.nrg.xnatx.roi.data.RoiCollection;
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
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.om.IcrRoicollectiondata;
import org.nrg.xdat.om.IcrRoicollectiondataSeriesuid;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xnatx.roi.RoiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jamesd
 */
public abstract class AbstractConversionHelper
	implements CollectionConverter.Helper
{
	private final static Logger logger = LoggerFactory.getLogger(
		AbstractConversionHelper.class);

	private final static Map<String,String> targetTypeDescs = new HashMap<>();
	private final static Map<String,String> targetTypeFormats = new HashMap<>();

	private Map<String,DicomObject> dcmMap = null;
	protected final RoiCollection roiCollection;
	protected final DicomSpatialDataService spatialDataService;
	protected final String targetType;

	static
	{
		targetTypeDescs.put(Constants.RtStruct, "RT Structure Set");
		targetTypeDescs.put(Constants.AIM, "AIM Instance File");
		targetTypeFormats.put(Constants.RtStruct, "DICOM");
		targetTypeFormats.put(Constants.AIM, "XML");
	}

	/**
	 *
	 * @param roiCollection
	 * @param targetType
	 * @param spatialDataService
	 * @throws org.nrg.xnatx.plugin.PluginException
	 */
	public AbstractConversionHelper(RoiCollection roiCollection,
		String targetType, DicomSpatialDataService spatialDataService)
		throws PluginException
	{
		this.roiCollection = roiCollection;
		this.targetType = targetType;
		this.spatialDataService = spatialDataService;
	}

	@Override
	public File getCollectionFile()
	{
		return null;
	}

	@Override
	public Map<String,DicomObject> getDicomObjectMap() throws PluginException
	{
		if (dcmMap == null)
		{
			buildDcmMap();
		}
		return ImmutableMap.copyOf(dcmMap);
	}

	@Override
	public String getTargetFileFormat()
	{
		return targetTypeFormats.getOrDefault(targetType, "Unknown");
	}

	@Override
	public String getTargetType()
	{
		return targetType;
	}

	@Override
	public String getTargetTypeDescription()
	{
		return targetTypeDescs.getOrDefault(targetType, "Unknown");
	}

	private void buildDcmMap() throws PluginException
	{
		IcrRoicollectiondata collectData = RoiUtils.getCollectionDataById(
			roiCollection.getId());
		logger.debug("Building DicomObject map for ROI collection "+
			collectData.getLabel());
		List<IcrRoicollectiondataSeriesuid> seriesUidList =
			collectData.getReferences_seriesuid();
		if (buildFromSpatialDataService(seriesUidList))
		{
			return;
		}
		XnatImagesessiondata sessionData = collectData.getImageSessionData();
		dcmMap = new HashMap<>();
		for (IcrRoicollectiondataSeriesuid collDataSeriesUid : seriesUidList)
		{
			String seriesUid = collDataSeriesUid.getSeriesuid();
			if ((seriesUid == null) || seriesUid.isEmpty())
			{
				logger.warn("Referenced series UIDs null or empty");
				continue;
			}
			String scanId = getScanId(sessionData, seriesUid);
			String scanPath = PluginUtils.getScanPath(sessionData, sessionData.getScanById(scanId));
			logger.debug("Scan path: {}", scanPath);
			Map<String,DicomObject> dicomObjectMap = new HashMap<>();
			DicomReceiver dcmRx = new DicomReceiver(seriesUid, dicomObjectMap);
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
			if (dicomObjectMap.isEmpty())
			{
				throw new PluginException(
					"No DICOM files found for series UID "+seriesUid+" in path: "+scanPath,
					PluginCode.FileNotFound);
			}
			logger.debug(String.format("%d items found for series UID %s",
				dicomObjectMap.size(), seriesUid));
			dcmMap.putAll(dicomObjectMap);
		}
		saveToSpatialDataService();
		logger.debug(String.format("%d items found for ROI collection %s",
			dcmMap.size(), collectData.getLabel()));
	}

	private boolean buildFromSpatialDataService(
		List<IcrRoicollectiondataSeriesuid> seriesUidList)
	{
		List<DicomSpatialData> dsdList = new ArrayList<>();
		for (IcrRoicollectiondataSeriesuid uid : seriesUidList)
		{
			List<DicomSpatialData> found =
				spatialDataService.findForSeries(uid.getSeriesuid());
			if (found != null)
			{
				dsdList.addAll(found);
			}
		}
		if (dsdList.isEmpty())
		{
			return false;
		}

		dsdList.sort(new DsdComparator());
		dcmMap = new HashMap<>();
		for (DicomSpatialData dsd : dsdList)
		{
			createSpatialDicom(dsd);
		}

		logger.info("DICOM spatial data fetched from service");
		return true;
	}

	private DicomSpatialData createDicomSpatialData(DicomObject dcm)
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

	private List<DicomSpatialData> createMultiframeDicomSpatialData(
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

	private DicomObject createMultiframeSpatialDicom(DicomSpatialData dsd)
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

		dcmMap.put(dcm.getString(Tag.SOPInstanceUID), dcm);

		return dcm;
	}

	private void createSpatialDicom(DicomSpatialData dsd)
	{
		String sopInstUid = dsd.getSopInstanceUid();
		String sopClassUid = dsd.getSopClassUid();
		if (DicomUtils.isMultiframeImageSopClass(sopClassUid))
		{
			DicomObject dcm = dcmMap.get(sopInstUid);
			if (dcm == null)
			{
				dcm = createMultiframeSpatialDicom(dsd);
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

	private double[] getImageOrientationPatient(DicomObject dcm, int frame)
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

	private double[] getImagePositionPatient(DicomObject dcm, int frame)
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

	private double[] getPixelSpacing(DicomObject dcm, int frame)
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

	private String getScanId(XnatImagesessiondata sessionData, String refSeriesUid)
		throws PluginException
	{
		logger.debug("Finding scan ID for series UID: {}", refSeriesUid);
		for (XnatImagescandataI scan : sessionData.getScans_scan())
		{
			String seriesUid = scan.getUid();
			if ((seriesUid != null) && seriesUid.equals(refSeriesUid))
			{
				String id = scan.getId();
				logger.debug("Scan ID: {}", id);
				return id;
			}
		}
		throw new PluginException("No scan found for series UID: "+refSeriesUid,
			PluginCode.HttpNotFound);
	}

	private void populateMultiframeSpatialDicom(DicomObject dcm,
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

	private void saveToSpatialDataService()
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
		spatialDataService.create(dsdList);
		logger.info("DICOM spatial data saved to service");
	}

	private class DsdComparator implements Comparator<DicomSpatialData>
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

	private class DicomReceiver implements PathScanContext<DicomObject>
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
