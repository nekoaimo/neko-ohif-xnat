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
import icr.etherj2.dicom.DicomToolkit;
import org.dcm4che3.data.Attributes;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.roi.Constants;
import org.nrg.xnatx.plugin.PluginUtils;
import icr.xnat.plugin.roi.entity.DicomSpatialData;
import org.nrg.xnatx.roi.data.DsdUtils;
import org.nrg.xnatx.roi.data.RoiCollection;
import org.nrg.xnatx.roi.service.DicomSpatialDataService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dcm4che3.data.Tag;
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
public abstract class AbstractConversionHelper implements CollectionConverter.Helper
{
	private final static Logger logger = LoggerFactory.getLogger(
		AbstractConversionHelper.class);

	private final static Map<String,String> targetTypeDescs = new HashMap<>();
	private final static Map<String,String> targetTypeFormats = new HashMap<>();

	private Map<String, Attributes> dcmMap = null;
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
	public Map<String,Attributes> getDicomObjectMap() throws PluginException
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
		IcrRoicollectiondata collectData = RoiUtils.getCollectionDataById(roiCollection.getId());
		logger.debug("Building DICOM object map for ROI collection "+ collectData.getLabel());
		List<IcrRoicollectiondataSeriesuid> seriesUidList = collectData.getReferences_seriesuid();
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
			Map<String,Attributes> dicomObjectMap = new HashMap<>();
			DicomReceiver dcmRx = new DicomReceiver(seriesUid, dicomObjectMap);
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
			if (dicomObjectMap.isEmpty())
			{
				throw new PluginException(
					"No DICOM files found for series UID "+seriesUid+" in path: "+scanPath, PluginCode.FileNotFound);
			}
			logger.debug(String.format("%d items found for series UID %s", dicomObjectMap.size(), seriesUid));
			dcmMap.putAll(dicomObjectMap);
		}
		DsdUtils.saveToService(spatialDataService, ImmutableMap.copyOf(dcmMap));
		logger.debug(String.format("%d items found for ROI collection %s", dcmMap.size(), collectData.getLabel()));
	}

	private boolean buildFromSpatialDataService(List<IcrRoicollectiondataSeriesuid> seriesUidList)
	{
		List<DicomSpatialData> dsdList = new ArrayList<>();
		for (IcrRoicollectiondataSeriesuid uid : seriesUidList)
		{
			List<DicomSpatialData> found = spatialDataService.findForSeries(uid.getSeriesuid());
			if (found != null)
			{
				dsdList.addAll(found);
			}
		}
		if (dsdList.isEmpty())
		{
			return false;
		}

		dsdList.sort(new DsdUtils.DsdComparator());
		dcmMap = new HashMap<>();
		for (DicomSpatialData dsd : dsdList)
		{
			DsdUtils.createSpatialDicom(dcmMap, dsd);
		}

		logger.info("DICOM spatial data fetched from service");
		return true;
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

	private class DicomReceiver implements PathScanContext<Attributes>
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
