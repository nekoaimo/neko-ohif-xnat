/********************************************************************
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
package org.nrg.xnatx.ohifviewer.xapi;

import icr.etherj.StringUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.io.IOUtils;
import org.nrg.config.entities.Configuration;
import org.nrg.config.services.ConfigService;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.Project;
import org.nrg.xapi.rest.Experiment;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.model.XnatSubjectassessordataI;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xft.security.UserI;
import org.nrg.xnatx.ohifviewer.inputcreator.JsonMetadataHandler;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.plugin.PluginUtils;
import org.nrg.xnatx.plugin.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 *
 * @author jpetts
 */
@Api(description="OHIF Viewer Metadata API")
@XapiRestController
@RequestMapping(value = "/viewer")
public class OhifViewerApi extends AbstractXapiRestController
{
	private static final Logger logger = LoggerFactory.getLogger(
		OhifViewerApi.class);

	private final Lock genAllJsonLock = new ReentrantLock();
	private final JsonMetadataHandler jsonHandler;

	@Autowired
	public OhifViewerApi(final ConfigService configService,
		final UserManagementServiceI userManagementService,
		final RoleHolder roleHolder)
	{
		super(userManagementService, roleHolder);
		jsonHandler = new JsonMetadataHandler(configService);
		logger.info("OHIF Viewer XAPI initialised");
	}

	@ApiOperation(value = "Checks if Session level JSON exists")
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "OK, the session JSON exists."),
		@ApiResponse(code = 403, message = "The user does not have permission to view the indicated experiment."),
		@ApiResponse(code = 404, message = "The specified JSON does not exist."),
		@ApiResponse(code = 500, message = "An unexpected error occurred."),
	})
	@XapiRequestMapping(
		value = "projects/{projectId}/experiments/{experimentId}/exists",
		produces = MediaType.APPLICATION_JSON_VALUE,
		method = RequestMethod.GET,
		restrictTo = AccessLevel.Read)
	public ResponseEntity<String> doesStudyJsonExist(
		final @ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		final @ApiParam(value="Experiment ID") @PathVariable("experimentId") @Experiment String experimentId)
		throws PluginException
	{
		UserI user = getSessionUser();
		Security.checkProject(user, projectId);
		Security.checkSession(user, experimentId);
		XnatImagesessiondata sessionData = PluginUtils.getImageSessionData(
			experimentId, user);
		Security.checkPermissions(user, sessionData.getXSIType()+"/project",
			projectId, Security.Read);

		if (!PluginUtils.isSharedIntoProject(sessionData, projectId))
		{
			logger.info("Experiment "+experimentId+" is not part of Project "+
				projectId);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		if (!jsonHandler.isJsonValid(experimentId))
		{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Configuration jsonConfig = jsonHandler.getJsonConfig(experimentId);
		return (jsonConfig != null)
			? new ResponseEntity<>(HttpStatus.OK)
			: new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@ApiOperation(value = "Returns the session JSON for the specified experiment ID.")
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "The session was located and properly rendered to JSON."),
		@ApiResponse(code = 403, message = "The user does not have permission to view the indicated experiment."),
		@ApiResponse(code = 500, message = "An unexpected error occurred.")
	})
	@XapiRequestMapping(
		value = "projects/{projectId}/experiments/{experimentId}",
		produces = MediaType.APPLICATION_JSON_VALUE,
		method = RequestMethod.GET,
		restrictTo = AccessLevel.Read
	)
	@ResponseBody
	public ResponseEntity<StreamingResponseBody> getExperimentJson(
		final @ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		final @ApiParam(value="Experiment ID") @PathVariable("experimentId") @Experiment String experimentId)
		throws PluginException
	{
		UserI user = getSessionUser();
		Security.checkProject(user, projectId);
		Security.checkSession(user, experimentId);
		XnatImagesessiondata sessionData = PluginUtils.getImageSessionData(
			experimentId, user);
		Security.checkPermissions(user, sessionData.getXSIType()+"/project", projectId,
			Security.Read);

		if (!PluginUtils.isSharedIntoProject(sessionData, projectId))
		{
			logger.info("Experiment "+experimentId+" is not part of Project "+
				projectId);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		String json;
		if (jsonHandler.isJsonValid(experimentId))
		{
			Configuration jsonConfig = jsonHandler.getJsonConfig(experimentId);
			if (jsonConfig == null)
			{
				json = jsonHandler.createAndStoreJsonConfig(sessionData, user);
			}
			else
			{
				json = jsonConfig.getContents();
				if (StringUtils.isNullOrEmpty(json))
				{
					json = jsonHandler.createAndStoreJsonConfig(sessionData, user);
				}
			}
		}
		else
		{
			// Revision check failed? Recreate regardless
			json = jsonHandler.createAndStoreJsonConfig(sessionData, user);
		}
		StreamingResponseBody srb = createResponseBody(json);
		return new ResponseEntity<>(srb, HttpStatus.OK);
	}

	@ApiOperation(value = "Generates the session JSON for the specified experiment ID.")
	@ApiResponses(
	{
		@ApiResponse(code = 201, message = "The session JSON has been created."),
		@ApiResponse(code = 403, message = "The user does not have permission to post to the indicated experient."),
		@ApiResponse(code = 500, message = "An unexpected error occurred.")
	})
	@XapiRequestMapping(
		value = "projects/{projectId}/experiments/{experimentId}",
		method = RequestMethod.POST,
		restrictTo = AccessLevel.Edit
	)
	public ResponseEntity<String> postExperimentJson(
		final @ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		final @ApiParam(value="Experiment ID") @PathVariable("experimentId") @Experiment String experimentId)
		throws PluginException
	{
		UserI user = getSessionUser();
		Security.checkProject(user, projectId);
		Security.checkSession(user, experimentId);
		XnatImagesessiondata sessionData = PluginUtils.getImageSessionData(
			experimentId, user);
		Security.checkPermissions(user, sessionData.getXSIType()+"/project", projectId,
			Security.Edit, Security.Read);

		if (!PluginUtils.isSharedIntoProject(sessionData, projectId))
		{
			logger.info("Experiment "+experimentId+" is not part of Project "+
				projectId);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		logger.info("Session "+experimentId+" metadata creation requested");
		jsonHandler.createAndStoreJsonConfig(sessionData, user);
		logger.info("Session "+experimentId+" metadata creation complete");

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ApiOperation(value = "Generates the session JSON for every session in the project.")
	@ApiResponses(
	{
		@ApiResponse(code = 201, message = "The JSON metadata has been created for every session in the project."),
		@ApiResponse(code = 403, message = "The user does not have permission to perform this action."),
		@ApiResponse(code = 423, message = "This process is already underway and is locked."),
		@ApiResponse(code = 500, message = "An unexpected error occurred.")
	})
	@XapiRequestMapping(
		value = "projects/{projectId}",
		method = RequestMethod.POST,
		restrictTo = AccessLevel.Admin
	)
	public ResponseEntity<String> postGenerateProjectJson(
		final @ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId)
		throws PluginException
	{
		// Don't allow more generate all processes to be started if one is already
		// running
		if (!genAllJsonLock.tryLock())
		{
			return new ResponseEntity<>(HttpStatus.LOCKED);
		}
		HttpStatus status;
		try
		{
			logger.info("Project "+projectId+" metadata creation requested");
			status = generateProjectMetadata(projectId);
			logger.info("Project "+projectId+" metadata creation complete");
		}
		finally
		{
			genAllJsonLock.unlock();
		}
		return new ResponseEntity<>(status);
	}

	@ApiOperation(value = "Generates the session JSON for every session in the subject.")
	@ApiResponses(
	{
		@ApiResponse(code = 201, message = "The JSON metadata has been created for every session in the subject."),
		@ApiResponse(code = 403, message = "The user does not have permission to perform this action."),
		@ApiResponse(code = 423, message = "This process is already underway and is locked."),
		@ApiResponse(code = 500, message = "An unexpected error occurred.")
	})
	@XapiRequestMapping(
		value = "projects/{projectId}/subjects/{subjectId}",
		method = RequestMethod.POST,
		restrictTo = AccessLevel.Admin
	)
	public ResponseEntity<String> postGenerateSubjectJson(
		final @ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		final @ApiParam(value="Subject ID") @PathVariable("subjectId") @Project String subjectId)
		throws PluginException
	{
		// Don't allow more generate all processes to be started if one is already
		// running
		if (!genAllJsonLock.tryLock())
		{
			return new ResponseEntity<>(HttpStatus.LOCKED);
		}
		HttpStatus status;
		try
		{
			logger.info("Subject "+subjectId+" metadata creation requested");
			status = generateSubjectMetadata(projectId, subjectId);
			logger.info("Subject "+subjectId+" metadata creation complete");
		}
		finally
		{
			genAllJsonLock.unlock();
		}
		return new ResponseEntity<>(status);
	}

	@ApiOperation(value = "Generates the session JSON for every session in the database.")
	@ApiResponses(
	{
		@ApiResponse(code = 201, message = "The JSON metadata has been created for every session in the database."),
		@ApiResponse(code = 403, message = "The user does not have permission to perform this action."),
		@ApiResponse(code = 423, message = "This process is already underway and is locked."),
		@ApiResponse(code = 500, message = "An unexpected error occurred.")
	})
	@XapiRequestMapping(
		value = "generate-all-metadata",
		method = RequestMethod.POST,
		restrictTo = AccessLevel.Admin
	)
	public ResponseEntity<String> setAllJson() throws PluginException
	{
		// Don't allow more generate all processes to be started if one is already
		// running
		if (!genAllJsonLock.tryLock())
		{
			return new ResponseEntity<>(HttpStatus.LOCKED);
		}
		HttpStatus status;
		try
		{
			logger.info("All projects metadata creation requested");
			status = generateAllMetadata();
			logger.info("All projects metadata creation complete");
		}
		finally
		{
			genAllJsonLock.unlock();
		}
		return new ResponseEntity<>(status);
	}

	private StreamingResponseBody createResponseBody(String input)
		throws PluginException
	{
		StreamingResponseBody srb = null;
		try (InputStream is = IOUtils.toInputStream(input, StandardCharsets.UTF_8))
		{
			srb = (final OutputStream output) -> { IOUtils.copy(is, output); };
		}
		catch (IOException ex)
		{
			throw new PluginException("IO error streaming JSON: "+ex.getMessage(),
				PluginCode.IO, ex);
		}
		return srb;
	}

	private HttpStatus generateAllMetadata() throws PluginException
	{
		UserI user = getSessionUser();
		List<XnatExperimentdata> experiments =
			XnatExperimentdata.getAllXnatExperimentdatas(user, true);
		List<String> exptIds = getImageSessionIds(experiments);
		return generateMetadata(exptIds);
	}

	private HttpStatus generateMetadata(List<String> exptIds)
		throws PluginException
	{
		// Create image session JSON in a multithreaded fashion if available
		int numThreads = Runtime.getRuntime().availableProcessors();
		numThreads = (numThreads > 4) ? 4 : numThreads;
		logger.info("Thread count for parallel JSON creation: " + numThreads);
		ExecutorService service = Executors.newFixedThreadPool(numThreads);

		UserI user = getSessionUser();
		List<Callable<Void>> tasks = new ArrayList<>();
		for (String id : exptIds)
		{
			logger.info("ImageSession ID: "+id);
			tasks.add((Callable<Void>) () ->
			{
				try
				{
					jsonHandler.createAndStoreJsonConfig(id, user);
				}
				catch (Exception ex)
				{
					// Need to catch everything, especially unexpected exceptions
					logger.warn(
						"Error creating JSON for session "+id+": "+ex.getMessage(),
						ex);
				}
				return null;
			});
		}
		try
		{
			service.invokeAll(tasks);
		}
		catch (InterruptedException ex)
		{
			throw new PluginException(
				"JSON creation interrupted: "+ex.getMessage(),
				PluginCode.HttpInternalError, ex);
		}
		finally
		{
			service.shutdown();
		}
		return HttpStatus.CREATED;
	}

	private HttpStatus generateProjectMetadata(String projectId)
		throws PluginException
	{
		UserI user = getSessionUser();
		XnatProjectdata projectData = XnatProjectdata.getProjectByIDorAlias(
			projectId, user, false);
		List<String> exptIds = getImageSessionIds(projectData.getExperiments());
		return generateMetadata(exptIds);
	}

	private HttpStatus generateSubjectMetadata(String projectId,
		String subjectId) throws PluginException
	{
		UserI user = getSessionUser();
		XnatSubjectdata subjectData = XnatSubjectdata.getXnatSubjectdatasById(
			subjectId, user, true);
		if (!subjectData.getProject().equals(projectId))
		{
			throw new PluginException(
				"Subject "+subjectId+" not found in project "+projectId,
				PluginCode.HttpUnprocessableEntity);
		}
		List<String> exptIds = new ArrayList<>();
		for (XnatSubjectassessordataI assessorData :
			subjectData.getExperiments_experiment())
		{
			if (assessorData instanceof XnatImagesessiondata)
			{
				exptIds.add(assessorData.getId());
			}
		}
		return generateMetadata(exptIds);
		
	}

	private List<String> getImageSessionIds(List<XnatExperimentdata> experiments)
	{
		List<String> exptIds = new ArrayList<>();
		for (XnatExperimentdata experimentData : experiments)
		{
			if (experimentData instanceof XnatImagesessiondata)
			{
				exptIds.add(experimentData.getId());
			}
		}
		return exptIds;
	}

}
