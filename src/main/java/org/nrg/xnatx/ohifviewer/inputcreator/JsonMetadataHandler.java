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

import icr.etherj.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.hibernate.engine.jdbc.ClobProxy;
import org.nrg.config.services.ConfigService;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xft.security.UserI;
import org.nrg.xnatx.ohifviewer.entity.OhifSessionData;
import org.nrg.xnatx.ohifviewer.service.OhifSessionDataService;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.plugin.PluginUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jamesd
 */
public class JsonMetadataHandler
{
	/**
	 * JSON metadata revision. This is incremented each time the JSON metadata
	 * format or content is changed to allow runtime detection of outdated
	 * metadata
	 */
	public static final int JsonRevision = 2;

	private static final Logger logger = LoggerFactory.getLogger(
		JsonMetadataHandler.class);

	private static final String CreateReason = "Creating Session JSON";
	private static final String JsonRevisionToolPath = "json-revision";
	private static final String OhifViewerToolName = "ohif-viewer";
	private static final String SessionJsonToolPath = "session-json";

	private final OhifSessionDataService ohifSessionDataService;

	public JsonMetadataHandler(final OhifSessionDataService ohifSessionDataService)
	{
		this.ohifSessionDataService = ohifSessionDataService;
	}

	/**
	 * Create and store JSON metadata for the specified session and user.
	 * @param sessionId
	 * @param user
	 * @return the JSON metadata
	 * @throws PluginException
	 */
	public void createAndStoreJsonConfig(String sessionId, UserI user)
		throws PluginException
	{
		XnatImagesessiondata sessionData = PluginUtils.getImageSessionData(
			sessionId, user);
		createAndStoreJsonConfig(sessionData, user);
	}

	/**
	 * Create and store JSON metadata for the specified session and user.
	 * @param sessionData
	 * @param user
	 * @return the JSON metadata
	 * @throws PluginException
	 */
	public void createAndStoreJsonConfig(XnatImagesessiondata sessionData,
		UserI user) throws PluginException
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
		String sessionId = sessionData.getId();
		logger.info("Creating session metadata for "+sessionId);
		ConfigServiceJsonCreator creator = new ConfigServiceJsonCreator();
		Path jsonPath = creator.create(sessionData);
		try (BufferedReader reader = new BufferedReader(new FileReader(jsonPath.toFile()))) {
			OhifSessionData ohifSessionData = new OhifSessionData();
			ohifSessionData.setSessionId(sessionId);
			ohifSessionData.setRevision(Integer.toString(JsonRevision));
			ohifSessionData.setSessionJson(ClobProxy.generateProxy(reader, Files.size(jsonPath)));
			ohifSessionDataService.createOrUpdate(ohifSessionData);
		} catch (FileNotFoundException e) {
			throw new PluginException(jsonPath + " Not Found", PluginCode.FileNotFound);
		} catch (IOException e) {
			throw new PluginException("Failed to load " + jsonPath);
		} finally {
			// Delete json file
			try {
				Files.delete(jsonPath);
				logger.debug("{} has been deleted", jsonPath);
			} catch (IOException e) {
				logger.warn("Failed to delete {}", jsonPath);
			}
		}

		logger.info("Session "+sessionId+" metadata created and stored");
	}

	/**
	 * Returns <code>true</code> if JSON metadata is present and the revision is 
	 * valid and up to date.
	 * @param sessionId
	 * @return
	 */
	public boolean isJsonValid(String sessionId)
	{
		OhifSessionData ohifSessionData = ohifSessionDataService.getSessionData(sessionId);
		if (ohifSessionData == null)
		{
			logger.debug("JSON not found");
			return false;
		}

		String revision = ohifSessionData.getRevision();
		// Check the stored revision. If it doesn't exist, is empty or lower than
		// the Json
		if (StringUtils.isNullOrEmpty(revision))
		{
			logger.debug("JSON revision empty");
			return false;
		}
		else
		{
			logger.debug("Stored JSON revision: {}", revision);
			int rev;
			try
			{
				rev = Integer.parseInt(revision);
			}
			catch (NumberFormatException ex)
			{
				logger.debug("JSON revision invalid: {}", revision);
				return false;
			}
			if (rev < JsonRevision)
			{
				logger.debug("JSON revision outdated: "+rev+" < "+JsonRevision);
				return false;
			}
		}
		return true;
	}

}
