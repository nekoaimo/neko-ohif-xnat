package org.nrg.xnatx.dicomweb.service.inputcreator;

import icr.etherj.PathScan;
import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.data.Attributes;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xnatx.dicomweb.toolkit.*;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.plugin.PluginUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DicomwebInputCreator
{
	private final XnatImagesessiondata sessionData;

	private final Map<String,String> xnatIds;
	private String experimentPath;
	private String xnatExperimentScanUrl;
	private Map<String,String> seriesUidToScanIdMap;

	public DicomwebInputCreator(XnatImagesessiondata sessionData,
		Map<String,String> xnatIds) throws PluginException
	{
		if (sessionData == null)
		{
			throw new PluginException("SessionData must not be null",
				PluginCode.HttpUnprocessableEntity);
		}

		this.sessionData = sessionData;
		this.xnatIds = xnatIds;
		initParameters();
	}

	public DicomwebInput scanPathAndCreateInput() throws PluginException
	{
		String xnatScanPath = experimentPath + "SCANS";
		log.info("Creating DICOMweb data for {}", xnatScanPath);

		DicomwebPathScanContext ctx = new DicomwebPathScanContext(xnatIds,
			xnatExperimentScanUrl, seriesUidToScanIdMap, experimentPath);
		PathScan<Attributes> pathScan = new DicomwebPathScan();
		pathScan.addContext(ctx);
		try
		{
			pathScan.scan(xnatScanPath, true);
			return ctx.getDicomwebInput();
		}
		catch (IOException e)
		{
			log.error("DICOMweb data creation exception:\n" + e.getMessage());
			throw new PluginException(
				"DICOMweb data creation exception:\n" + e.getMessage(),
				PluginCode.IO, e);
		}
	}

	private void initParameters()
	{
		seriesUidToScanIdMap = PluginUtils.getImageScanUidIdMap(sessionData);

		experimentPath = PluginUtils.getExperimentPath(sessionData);

		xnatExperimentScanUrl = "/data/experiments/" + xnatIds.get(
			DicomwebConstants.XNAT_SESSION_ID) + "/scans/";
	}
}
