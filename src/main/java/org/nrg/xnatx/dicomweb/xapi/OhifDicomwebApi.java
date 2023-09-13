/********************************************************************
 * Copyright (c) 2023, Institute of Cancer Research
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
package org.nrg.xnatx.dicomweb.xapi;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.Experiment;
import org.nrg.xapi.rest.Project;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.security.UserI;
import org.nrg.xnatx.dicomweb.service.inputcreator.DicomwebInputHandler;
import org.nrg.xnatx.dicomweb.service.qido.QidoRsModel;
import org.nrg.xnatx.dicomweb.service.qido.QidoRsService;
import org.nrg.xnatx.dicomweb.toolkit.query.QIDO;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.plugin.PluginUtils;
import org.nrg.xnatx.plugin.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author mo.alsad
 */
@Slf4j
@Api(description = "OHIF Viewer DICOMweb API")
@SwaggerDefinition
@XapiRestController
@RequestMapping(value = "/viewerDicomweb")
public class OhifDicomwebApi extends AbstractXapiRestController
{
	private final Lock genAllDwDataLock = new ReentrantLock();
	private final DicomwebInputHandler dwInputHandler;
	private final QidoRsService qidoRsService;

	@Autowired
	public OhifDicomwebApi(final DicomwebInputHandler dwInputHandler,
		final UserManagementServiceI userManagementService,
		final RoleHolder roleHolder, final QidoRsService qidoRsService)
	{
		super(userManagementService, roleHolder);
		this.dwInputHandler = dwInputHandler;
		this.qidoRsService = qidoRsService;
		log.info("Viewer DICOMweb XAPI initialised");
	}

	/*
	############################################################
		DICOMweb data generation
	############################################################
	*/

	@ApiOperation(value = "Generates DICOMweb data for the specified experiment ID.")
	@ApiResponses(
		{
			@ApiResponse(code = 201, message = "The DICOMweb data has been created."),
			@ApiResponse(code = 403, message = "The user does not have permission to post to the indicated experiment."),
			@ApiResponse(code = 500, message = "An unexpected error occurred.")
		})
	@XapiRequestMapping(
		value = "projects/{projectId}/experiments/{experimentId}",
		method = RequestMethod.POST,
		restrictTo = AccessLevel.Edit
	)
	public ResponseEntity<String> postExperimentDicomweb(
		final @ApiParam(value = "Project ID") @PathVariable("projectId") @Project String projectId,
		final @ApiParam(value = "Experiment ID") @PathVariable("experimentId") @Experiment String experimentId)
		throws PluginException
	{
		UserI user = getSessionUser();
		XnatImagesessiondata sessionData = checkPermissions(user, projectId,
			experimentId, Security.Edit, Security.Read);

		log.info("Session " + experimentId + " DICOMweb data creation requested");
		dwInputHandler.createDicomwebData(sessionData, user);
		log.info("Session " + experimentId + " DICOMweb data creation complete");

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/*
	############################################################
		QIDO-RS
	############################################################
	*/

	@ApiOperation(value = "QIDO-RS Search for studies.")
	@QidoApiResponses
	@QidoRequestMapping(value = "projects/{projectId}/experiments/{experimentId}/rs/studies")
	public ResponseEntity<StreamingResponseBody> searchForStudies(
		final HttpServletRequest request,
		final @ApiParam(value = "Project ID") @PathVariable("projectId") @Project String projectId,
		final @ApiParam(value = "Experiment ID") @PathVariable("experimentId") @Experiment String experimentId,
		final @ApiParam(value = "Query Parameters") @RequestParam(value = "queryParams", required = false) MultiValueMap<String,String> queryParams)
		throws PluginException
	{
		UserI user = getSessionUser();
		XnatImagesessiondata sessionData = checkPermissions(user, projectId,
			experimentId, Security.Read);

		return qidoRsService.search(sessionData, request, queryParams,
			QidoRsModel.STUDY, null,
			null, QIDO.STUDY);
	}

	private XnatImagesessiondata checkPermissions(UserI user, String projectId,
		String experimentId, String... permissions) throws PluginException
	{
		Security.checkProject(user, projectId);
		Security.checkSession(user, experimentId);
		XnatImagesessiondata sessionData = PluginUtils.getImageSessionData(
			experimentId, user);
		Security.checkPermissions(user, sessionData.getXSIType() + "/project",
			projectId, permissions);

		if (!PluginUtils.isSharedIntoProject(sessionData, projectId))
		{
			log.info(
				"Experiment " + experimentId + " is not part of Project " + projectId);
			throw new PluginException(
				"Experiment " + experimentId + " is not part of Project " + projectId,
				PluginCode.HttpNotFound);
		}

		return sessionData;
	}
}
