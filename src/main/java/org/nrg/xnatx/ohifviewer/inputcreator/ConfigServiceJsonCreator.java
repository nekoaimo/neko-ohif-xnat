/*********************************************************************
 * Copyright (c) 2020, Institute of Cancer Research
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
package org.nrg.xnatx.ohifviewer.inputcreator;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xft.security.UserI;
import org.nrg.xnatx.ohifviewer.ViewerUtils;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.plugin.PluginUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author roban
 */
public class ConfigServiceJsonCreator
{
	private static final Logger logger =
		LoggerFactory.getLogger(ImageSessionJsonCreator.class);
	private static final String SEP = File.separator;
	private static final String xnatArchivePath =
		XDAT.getSiteConfigPreferences().getArchivePath();

	public String create(String sessionId) throws PluginException
	{
		return create(sessionId, null);
	}

	public String create(String sessionId, UserI user) throws PluginException
	{
		try
		{
			return create(PluginUtils.getImageSessionData(sessionId, user));
		}
		catch (PluginException ex)
		{
			logger.warn(ex.getMessage());
			throw new PluginException("Session not found: "+sessionId,
				PluginCode.HttpUnprocessableEntity, ex);
		}
	}

	public String create(XnatImagesessiondata sessionData) throws PluginException
	{
		if (sessionData == null)
		{
			throw new PluginException("SessionData must not be null",
				PluginCode.HttpUnprocessableEntity);
		}
		String sessionId = sessionData.getId();
		Map<String,String> seriesUidToScanIdMap =
			PluginUtils.getImageScanUidIdMap(sessionData);

		Map<String,String> dirInfo = ViewerUtils.getDirectoryInfo(
			sessionId);
		String proj = dirInfo.get("proj");
		String expLabel = dirInfo.get("expLabel");
		String subj = dirInfo.get("subj");

		logger.debug("Experiment path: {}",
			PluginUtils.getExperimentPath(sessionData));
		String xnatScanPath = PluginUtils.getExperimentPath(sessionData)+"SCANS";
		logger.info("Creating JSON metadata for {}", xnatScanPath);
		String xnatExperimentScanUrl = getXnatScanUrl(proj, subj, expLabel);
		logger.info("xnatExperimentScanUrl: {}", xnatExperimentScanUrl);

		String json = null;
		try
		{
			CreateOhifViewerMetadata jsonCreator = new CreateOhifViewerMetadata(
				xnatScanPath, xnatExperimentScanUrl, seriesUidToScanIdMap);
			json = jsonCreator.jsonify(sessionId);
			clearLegacyJsonFile(xnatArchivePath, proj, expLabel, sessionId);
		}
		catch (IOException ex)
		{
			logger.error("Jsonifier exception:\n" + ex.getMessage());
			throw new PluginException("Jsonifier exception:\n" + ex.getMessage(),
				PluginCode.IO, ex);
		}
		return json;
	}

	private void clearLegacyJsonFile(String xnatArchivePath, String proj,
		String expLabel, String sessionId) throws IOException
	{
		File jsonFile = new File(getStudyPath(xnatArchivePath, proj, expLabel,
			sessionId));
		File metaDir = jsonFile.getParentFile();
		if (metaDir.exists())
		{
			if (jsonFile.exists())
			{
				jsonFile.delete();
				logger.info("Legacy JSON deleted: "+jsonFile.getPath());
			}
			String[] contents = metaDir.list();
			if (contents.length == 0)
			{
				metaDir.delete();
				logger.debug("Empty directory removed: "+metaDir.getPath());
			}
		}
	}

	private String getStudyPath(String xnatArchivePath, String proj,
		String expLabel, String experimentId)
	{
		return xnatArchivePath+SEP+proj+SEP+"arc001"+SEP+expLabel+
			SEP+"RESOURCES/metadata/"+experimentId+".json";
	}

	private String getXnatScanUrl(String project, String subject,
		String experimentId)
	{
		return "/data/archive/projects/"+project+"/subjects/"+subject
			+"/experiments/"+experimentId+"/scans/";
	}

}
