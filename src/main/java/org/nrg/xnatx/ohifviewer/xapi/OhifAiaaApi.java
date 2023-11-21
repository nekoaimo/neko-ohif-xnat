/* ********************************************************************
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
package org.nrg.xnatx.ohifviewer.xapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import icr.etherj2.StringUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.nrg.config.entities.Configuration;
import org.nrg.config.services.ConfigService;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.Project;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xft.security.UserI;
import org.nrg.xnatx.ohifviewer.JsonAiaaServerListHandler;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.plugin.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author jamesd
 */
@Api(description="OHIF Viewer AIAA API")
@XapiRestController
@RequestMapping(value = "/ohifaiaa")
public class OhifAiaaApi extends AbstractXapiRestController
{
	private static final Logger logger = LoggerFactory.getLogger(
		OhifViewerApi.class);

	private final JsonAiaaServerListHandler jsonHandler;

	@Autowired
	public OhifAiaaApi(final ConfigService configService,
		final UserManagementServiceI userManagementService,
		final RoleHolder roleHolder)
	{
		super(userManagementService, roleHolder);
		jsonHandler = new JsonAiaaServerListHandler(configService);
		logger.info("OHIF Viewer AIAA XAPI initialised");
	}

	@ApiOperation(value = "Returns the AIAA server JSON for the specified project ID.")
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "The project was located and JSON server list returned."),
		@ApiResponse(code = 403, message = "The user does not have permission to view the indicated project."),
		@ApiResponse(code = 404, message = "The JSON server list was not found for the indicated project."),
		@ApiResponse(code = 500, message = "An unexpected error occurred.")
	})
	@XapiRequestMapping(
		value = "projects/{projectId}/servers",
		produces = MediaType.APPLICATION_JSON_VALUE,
		method = RequestMethod.GET,
		restrictTo = AccessLevel.Read
	)
	@ResponseBody
	public ResponseEntity<List<String>> getProjectAiaaJson(
		final @ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId)
		throws PluginException
	{
		UserI user = getSessionUser();
		Security.checkProject(user, projectId);
		Configuration jsonConfig = jsonHandler.getProjectJsonConfig(projectId);
		return processJsonConfig(jsonConfig);
	}

	@ApiOperation(value = "Returns the AIAA server JSON for the site.")
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "The JSON server list was returned."),
		@ApiResponse(code = 403, message = "The user does not have permission to access the resource."),
		@ApiResponse(code = 404, message = "The site JSON server list was not found."),
		@ApiResponse(code = 500, message = "An unexpected error occurred.")
	})
	@XapiRequestMapping(
		value = "servers",
		produces = MediaType.APPLICATION_JSON_VALUE,
		method = RequestMethod.GET,
		restrictTo = AccessLevel.Read
	)
	@ResponseBody
	public ResponseEntity<List<String>> getSiteAiaaJson()
		throws PluginException
	{
		Configuration jsonConfig = jsonHandler.getSiteJsonConfig();
		return processJsonConfig(jsonConfig);
	}

	@ApiOperation(value = "Sets the AIAA server JSON for the specified project.")
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "The JSON server list was returned."),
		@ApiResponse(code = 403, message = "The user does not have permission to access the resource."),
		@ApiResponse(code = 422, message = "Unprocessable entity, most likely malformed JSON or URL in the JSON."),
		@ApiResponse(code = 500, message = "An unexpected error occurred.")
	})
	@XapiRequestMapping(
		value = "projects/{projectId}/servers",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		method = RequestMethod.PUT,
		restrictTo = AccessLevel.Admin
	)
	@ResponseBody
	public ResponseEntity<String> setProjectAiaaJson(
		final @ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		@RequestBody List<String> serverList)
		throws PluginException
	{
		UserI user = getSessionUser();
		Security.checkProject(user, projectId);
		String json = parseServerList(serverList);
		jsonHandler.setProjectJson(user, projectId, json);
		return new ResponseEntity<>("Project "+projectId+" server list updated",
			HttpStatus.OK);
	}

	@ApiOperation(value = "Sets the AIAA server JSON for the site.")
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "The site JSON AIAA server list was updated."),
		@ApiResponse(code = 403, message = "The user does not have permission to update the server list."),
		@ApiResponse(code = 422, message = "Unprocessable entity, most likely malformed JSON or URL in the JSON."),
		@ApiResponse(code = 500, message = "An unexpected error occurred.")
	})
	@XapiRequestMapping(
		value = "servers",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		method = RequestMethod.PUT,
		restrictTo = AccessLevel.Admin
	)
	@ResponseBody
	public ResponseEntity<String> setSiteAiaaJson(
		@RequestBody List<String> serverList)
		throws PluginException
	{
		String json = parseServerList(serverList);
		jsonHandler.setSiteJson(getSessionUser(), json);
		return new ResponseEntity<>("Site server list updated", HttpStatus.OK);
	}

	private List<String> buildServerList(String json) throws PluginException
	{
		ObjectMapper mapper = new ObjectMapper();
		CollectionType javaType = mapper.getTypeFactory()
			.constructCollectionType(List.class, String.class);
		List<String> serverList;
		try
		{
			serverList = mapper.readValue(json, javaType);
		}
		catch (IOException ex)
		{
			throw new PluginException("Error creating JSON: "+ex.getMessage(),
				PluginCode.HttpInternalError, ex);
		}
		return serverList;
	}

	private String parseServerList(List<String> serverList) throws PluginException
	{
		ObjectMapper mapper = new ObjectMapper();
		String json;
		try
		{
			for (String server : serverList)
			{
				// Check server URL is valid
				URL junk = new URL(server);
			}
			json = mapper.writeValueAsString(serverList);
		}
		catch (MalformedURLException ex)
		{
			throw new PluginException("Malformed URL",
				PluginCode.HttpUnprocessableEntity, ex);
		}
		catch (JsonProcessingException ex)
		{
			throw new PluginException("Error creating JSON: "+ex.getMessage(),
				PluginCode.HttpUnprocessableEntity, ex);
		}
		return json;
	}

	private ResponseEntity<List<String>> processJsonConfig(
		Configuration jsonConfig) throws PluginException
	{
		List<String> list;
		if (jsonConfig == null)
		{
			list = new ArrayList<>();
			list.add("No AIAA server list found");
			return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
		}
		String json = jsonConfig.getContents();
		if (StringUtils.isNullOrEmpty(json))
		{
			list = new ArrayList<>();
			list.add("No AIAA server list found");
			return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
		}
		list = buildServerList(json);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

}
