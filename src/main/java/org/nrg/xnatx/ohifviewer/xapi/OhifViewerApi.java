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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.XDAT;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import org.nrg.xft.security.UserI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.Project;
import org.nrg.xapi.rest.Experiment;
import org.nrg.xdat.om.XnatExperimentdataShare;
import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xnatx.ohifviewer.PluginCode;
import org.nrg.xnatx.ohifviewer.PluginException;
import org.nrg.xnatx.ohifviewer.PluginUtils;
import org.nrg.xnatx.ohifviewer.Security;
import org.nrg.xnatx.ohifviewer.inputcreator.CreateExperimentMetadata;
import org.nrg.xnatx.ohifviewer.inputcreator.RunnableCreateExperimentMetadata;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author jpetts
 */
@Api("Get and set viewer metadata.")
@XapiRestController
@RequestMapping(value = "/viewer")
public class OhifViewerApi extends AbstractXapiRestController
{
	private static final Logger logger = LoggerFactory.getLogger(OhifViewerApi.class);
	private static final String SEP = File.separator;
	private static Boolean generateAllJsonLocked = false;

	@Autowired
	public OhifViewerApi(final UserManagementServiceI userManagementService,
		final RoleHolder roleHolder)
	{
		super(userManagementService, roleHolder);
	}

	/*=================================
	// Study level GET/POST
	=================================*/
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
		Security.checkPermissions(user, sessionData.getXSIType()+"/project", projectId,
			Security.Read);

		boolean isSessionSharedIntoProject = sessionSharedIntoProject(
			experimentId, projectId);
		if (!isSessionSharedIntoProject)
		{
			logger.info("Project IDs not equal");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		String xnatArchivePath = XDAT.getSiteConfigPreferences().getArchivePath();
		// Get directory info from _experimentId
		Map<String,String> experimentData = getDirectoryInfo(experimentId);
		String proj = experimentData.get("proj");
		String expLabel = experimentData.get("expLabel");
		String readFilePath = getStudyPath(xnatArchivePath, proj, expLabel,
			experimentId);
		File file = new File(readFilePath);
		if (file.exists())
		{
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

		String xnatArchivePath = XDAT.getSiteConfigPreferences().getArchivePath();
		// Get directory info from _experimentId
		Map<String, String> experimentData = getDirectoryInfo(experimentId);
		String expLabel = experimentData.get("expLabel");
		String proj = experimentData.get("proj");

		boolean isSessionSharedIntoProject = sessionSharedIntoProject(
			experimentId, projectId);
		if (!isSessionSharedIntoProject)
		{
			logger.info("Project IDs not equal");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		String readFilePath = getStudyPath(xnatArchivePath, proj, expLabel,
			experimentId);
		File file = new File(readFilePath);
		if (!file.exists())
		{
			// JSON doesn't exist, so generate and cache it.
			CreateExperimentMetadata.createMetadata(experimentId);
		}
		StreamingResponseBody srb = createResponseBody(file);
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

		boolean isSessionSharedIntoProject = sessionSharedIntoProject(
			experimentId, projectId);
		if (!isSessionSharedIntoProject)
		{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		logger.info("Creating experiment metadata for "+experimentId);
		HttpStatus returnHttpStatus = CreateExperimentMetadata.createMetadata(
			experimentId);

		return new ResponseEntity<>(returnHttpStatus);
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
		// Don't allow more generate all processes to be started if one is already running
		if (generateAllJsonLocked == true)
		{
			return new ResponseEntity<>(HttpStatus.LOCKED);
		}
		else
		{
			generateAllJsonLocked = true;
		}
		HttpStatus status;
		// Ensure lock boolean is reset on an exception
		try
		{
			status = generateAllMetadata();
		}
		finally
		{
			generateAllJsonLocked = false;
		}
		return new ResponseEntity<>(status);
	}

	private StreamingResponseBody createResponseBody(File file)
		throws PluginException
	{
		StreamingResponseBody srb = null;
		try
		{
			final InputStream is = Files.newInputStream(file.toPath());
			srb = new StreamingResponseBody()
			{
				@Override
				public void writeTo(final OutputStream output) throws IOException
				{
					IOUtils.copy(is, output);
				}
			};
		}
		catch (IOException ex)
		{
			throw new PluginException("IO error for "+file.getPath(),
				PluginCode.IO, ex);
		}
		return srb;
	}

	private HttpStatus generateAllMetadata() throws PluginException
	{
		List<String> experimentIds = getAllExperimentIds();

		// Executes experiment JSON creation in a multithreaded fashion if avialable
		int numThreads = Runtime.getRuntime().availableProcessors();
		logger.info("Thread count for parallel JSON creation: " + numThreads);
		ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
		// Create a CountDownLatch in order to check when all processes are finished
		CountDownLatch doneSignal = new CountDownLatch(experimentIds.size());

		for (String experimentId : experimentIds)
		{
			logger.info("experimentId " + experimentId);
			RunnableCreateExperimentMetadata createExperimentMetadata
				= new RunnableCreateExperimentMetadata(doneSignal, experimentId);
			executorService.submit(createExperimentMetadata);
		}

		HttpStatus status;
		try
		{
			doneSignal.await();
			status = HttpStatus.CREATED;
		}
		catch (InterruptedException ex)
		{
			throw new PluginException(
				"JSON creation thread interrupted: "+ex.getMessage(),
				PluginCode.HttpInternalError, ex);
		}

		return status;
	}

	/*=================================
	// Series level GET/POST- WIP
	=================================*/

 /*

    @ApiOperation(value = "Returns 200 if series level JSON exists")
    @ApiResponses({
      @ApiResponse(code = 200, message = "OK, The session JSON exists."),
      @ApiResponse(code = 403, message = "The user does not have permission to view the indicated experiment."),
      @ApiResponse(code = 404, message = "The specified JSON does not exist."),
      @ApiResponse(code = 500, message = "An unexpected error occurred.")
    })
    @XapiRequestMapping(
      value = "projects/{_projectId}/experiments/{_experimentId}/scans/{_scanId}/exists",
      produces = MediaType.APPLICATION_JSON_VALUE,
      method = RequestMethod.GET,
      restrictTo = AccessLevel.Read
    )
    public ResponseEntity<String> doesSeriesJsonExist(
      @PathVariable("_projectId") @ProjectId final String _projectId,
      final @PathVariable String _experimentId, @PathVariable String _scanId)
      throws IOException
    {
      // Grab the data archive path
      String xnatArchivePath = XDAT.getSiteConfigPreferences().getArchivePath();

      // Get directory info from _experimentId
      HashMap<String,String> experimentData = getDirectoryInfo(_experimentId);
      String proj     = experimentData.get("proj");
      String expLabel = experimentData.get("expLabel");

      if (!proj.equals(_projectId)) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }

      String readFilePath = getSeriesPath(xnatArchivePath, proj, expLabel, _scanId);
      File file = new File(readFilePath);
      if (file.exists())
      {
        return new ResponseEntity<>(HttpStatus.OK);
      }
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @ApiOperation(value = "Returns the session JSON for the specified series.")
    @ApiResponses({
      @ApiResponse(code = 200, message = "The session was located and properly rendered to JSON."),
      @ApiResponse(code = 403, message = "The user does not have permission to view the indicated experiment."),
      @ApiResponse(code = 500, message = "An unexpected error occurred.")
    })
    @XapiRequestMapping(
      value = "projects/{_projectId}/experiments/{_experimentId}/scans/{_scanId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      method = RequestMethod.GET,
      restrictTo = AccessLevel.Read
    )
    public StreamingResponseBody getSeriesJson(
      @PathVariable("_projectId") @ProjectId final String _projectId,
      final @PathVariable String _experimentId, @PathVariable String _scanId)
      throws FileNotFoundException
    {
    // Grab the data archive path
      String xnatArchivePath = XDAT.getSiteConfigPreferences().getArchivePath();

      // Get directory info from _experimentId
      HashMap<String,String> experimentData = getDirectoryInfo(_experimentId);
      String proj     = experimentData.get("proj");
      String expLabel = experimentData.get("expLabel");

      String readFilePath = getSeriesPath(xnatArchivePath, proj, expLabel, _scanId);

      final Reader reader = new FileReader(readFilePath);

      return new StreamingResponseBody() {
          @Override
          public void writeTo(final OutputStream output) throws IOException {
              IOUtils.copy(reader, output);
          }
      };
    }


    @ApiOperation(value = "Generates the session JSON for the specified series.")
    @ApiResponses({
      @ApiResponse(code = 201, message = "The session JSON has been created."),
      @ApiResponse(code = 403, message = "The user does not have permission to view the indicated experient."),
      @ApiResponse(code = 500, message = "An unexpected error occurred.")
    })
    @XapiRequestMapping(
      value = "projects/{_projectId}/experiments/{_experimentId}/scans/{_scanId}",
      method = RequestMethod.POST,
      restrictTo = AccessLevel.Edit
    )
    public ResponseEntity<String> postSeriesJson(
      @PathVariable("_projectId") @ProjectId final String _projectId,
      final @PathVariable String _experimentId, @PathVariable String _scanId)
      throws IOException
    {
      // Grab the data archive path
      String xnatRootURL      = XDAT.getSiteConfigPreferences().getSiteUrl();
      String xnatArchivePath  = XDAT.getSiteConfigPreferences().getArchivePath();

      // Get directory info from _experimentId
      HashMap<String,String> experimentData = getDirectoryInfo(_experimentId);
      String proj     = experimentData.get("proj");

      if (!proj.equals(_projectId)) {
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
      }

      // Runs creation process within the active thread.
      RunnableCreateSeriesMetadata createSeriesMetadata =
                new RunnableCreateSeriesMetadata(xnatRootURL, xnatArchivePath, _experimentId, _scanId, null);
      HttpStatus returnHttpStatus = createSeriesMetadata.runOnCurrentThread();

      return new ResponseEntity<String>(returnHttpStatus);
    }

	 */
	private List<String> getAllExperimentIds()
	{
		List<String> experimentIds = new ArrayList<>();
		UserI user = getSessionUser();
		List<XnatExperimentdata> experiments =
			XnatExperimentdata.getAllXnatExperimentdatas(user, true);
		for (XnatExperimentdata experimentI : experiments)
		{
			if (experimentI instanceof XnatImagesessiondata)
			{
				experimentIds.add(experimentI.getId());
			}
		}

		return experimentIds;
	}

	private Map<String,String> getDirectoryInfo(String experimentId)
		throws PluginException
	{
		// Get Experiment data and Project data from the experimentId

		XnatExperimentdata expData = null;
		XnatProjectdata projData = null;
		XnatImagesessiondata session = null;

		try
		{
			expData = XnatExperimentdata.getXnatExperimentdatasById(
				experimentId, null, false);
			projData = expData.getProjectData();
			session = (XnatImagesessiondata) expData;
		}
		catch (Exception ex)
		{
			throw new PluginException(
				"Experiment "+experimentId+" not found in project",
				PluginCode.HttpNotFound);
		}

		// Get the subject data
		XnatSubjectdata subjData = XnatSubjectdata.getXnatSubjectdatasById(
			session.getSubjectId(), null, false);

		// Get the required info
		String expLabel = expData.getArchiveDirectoryName();
		String proj = projData.getId();
		String subj = subjData.getLabel();

		// Construct a HashMap to return data
		Map<String,String> result = new HashMap<>();
		result.put("expLabel", expLabel);
		result.put("proj", proj);
		result.put("subj", subj);

		return result;
	}

	private String getStudyPath(String xnatArchivePath, String proj,
		String expLabel, String _experimentId)
	{
		String filePath = xnatArchivePath + SEP + proj + SEP + "arc001"
			+ SEP + expLabel + SEP + "RESOURCES/metadata/" + _experimentId + ".json";
		return filePath;
	}

	private boolean sessionSharedIntoProject(String experimentId, String projectId)
		throws PluginException
	{
		logger.info("OhifViewerApi::sessionSharedIntoProject("+experimentId+", "+
			projectId+")");
		XnatExperimentdata expData = null;
		XnatImagesessiondata session = null;
		try
		{
			expData = XnatExperimentdata.getXnatExperimentdatasById(experimentId,
				null, false);
			session = (XnatImagesessiondata) expData;
		}
		catch (Exception ex)
		{
			logger.error("Experiment not found: "+experimentId, ex);
			throw new PluginException("Experiment not found: "+experimentId,
				PluginCode.HttpUnprocessableEntity);
		}

		if (expData.getProject().equals(projectId))
		{
			logger.info("Experiment "+experimentId+" belongs to project "+projectId);
			return true;
		}

		List<XnatExperimentdataShare> xnatExperimentdataShareList =
			session.getSharing_share();
		for (XnatExperimentdataShare share : xnatExperimentdataShareList)
		{
			logger.info("Share project ID: "+share.getProject());
			if (share.getProject().equals(projectId))
			{
				return true;
			}
		}
		return false;
	}

	/*
    private String getSeriesPath(String xnatArchivePath, String proj, String expLabel, String _scanId)
    {
      String filePath = xnatArchivePath + SEP + proj + SEP + "arc001"
      + SEP + expLabel + SEP + "RESOURCES/metadata/" + _scanId +".json";
      return filePath;
    }
	 */
}
