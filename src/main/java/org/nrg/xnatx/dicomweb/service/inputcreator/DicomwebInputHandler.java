package org.nrg.xnatx.dicomweb.service.inputcreator;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xft.security.UserI;

import org.nrg.xnatx.dicomweb.conf.DicomwebDeviceConfiguration;
import org.nrg.xnatx.dicomweb.entity.DwPatient;
import org.nrg.xnatx.dicomweb.entity.DwStudy;
import org.nrg.xnatx.dicomweb.service.hibernate.DicomwebDataService;
import org.nrg.xnatx.dicomweb.toolkit.DicomwebEntityValidator;
import org.nrg.xnatx.dicomweb.toolkit.DicomwebConstants;
import org.nrg.xnatx.dicomweb.toolkit.DicomwebInput;
import org.nrg.xnatx.dicomweb.toolkit.DicomwebUtils;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.plugin.PluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DicomwebInputHandler
{
	private final DicomwebDataService dwDataService;

	@Autowired
	public DicomwebInputHandler(final DicomwebDataService dwDataService)
	{
		this.dwDataService = dwDataService;
	}

	public void createDicomwebData(String sessionId, UserI user)
		throws PluginException
	{
		XnatImagesessiondata sessionData = PluginUtils.getImageSessionData(
			sessionId, user);
		createDicomwebData(sessionData, user);
	}

	public void createDicomwebData(XnatImagesessiondata sessionData, UserI user)
		throws PluginException
	{
		createDicomwebData(sessionData, user, false);
	}

	public void createDicomwebData(XnatImagesessiondata sessionData, UserI user,
		boolean ignoreExisting) throws PluginException
	{
		if (sessionData == null)
		{
			throw new PluginException("SessionData must not be null",
				PluginCode.HttpUnprocessableEntity);
		}
		if (user == null)
		{
			throw new PluginException("User must not be null",
				PluginCode.HttpUnprocessableEntity);
		}

		String modality = PluginUtils.getImageSessionModality(sessionData);
		if (!DicomwebDeviceConfiguration.isDicomwebModality(modality))
		{
			throw new PluginException("Modality " + modality + " is not supported",
				PluginCode.Unsupported);
		}

		String sessionId = sessionData.getId();

		DwStudy prevStudy = dwDataService.getStudyBySessionId(sessionId, false);

		// Do not recreate if a valid study is available with keep existing flag
		if (DicomwebEntityValidator.isValidEntity(prevStudy) && !ignoreExisting)
		{
			return;
		}

		Map<String,String> xnatIds = DicomwebUtils.getXnatIds(sessionData);

		DicomwebInputCreator dwiCreator = new DicomwebInputCreator(sessionData,
			xnatIds);
		DicomwebInput dwi = dwiCreator.scanPathAndCreateInput();

		try
		{
			dwDataService.createOrUpdate(dwi);
		}
		catch (Exception e)
		{
			throw new PluginException(
				"Unable to create DICOMweb data for session " + sessionId, e);
		}
	}
}
