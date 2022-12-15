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
package org.nrg.xnatx.roi.xapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.io.ByteStreams;
import icr.etherj.IoUtils;
import icr.etherj.StringUtils;
import icr.etherj.Uids;
import icr.etherj.dicom.ConversionException;
import icr.etherj.dicom.DicomToolkit;
import icr.etherj.dicom.DicomUtils;
import icr.etherj.dicom.RoiConverter;
import icr.etherj.dicom.iod.Iods;
import icr.etherj.dicom.iod.Segmentation;
import icr.etherj.nifti.Nifti;
import icr.etherj.nifti.NiftiToolkit;
import icr.etherj.nifti.NiftiWriter;
import org.nrg.xnatx.roi.Constants;
import org.nrg.xnatx.plugin.ElementPermissions;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.plugin.Security;
import org.nrg.xnatx.roi.data.*;
import org.nrg.xnatx.roi.process.CollectionConverter;
import org.nrg.xnatx.roi.process.CollectionStorage;
import org.nrg.xnatx.roi.service.RoiService;
import org.nrg.xnatx.plugin.PluginUtils;
import org.nrg.xnatx.roi.process.DefaultCollectionConverter;
import org.nrg.xnatx.roi.process.DefaultCollectionStorage;
import icr.xnat.plugin.roi.entity.Roi;
import org.nrg.xnatx.roi.process.ProcessUtils;
import org.nrg.xnatx.roi.process.Result;
import org.nrg.xnatx.roi.service.DicomSpatialDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dcm4che2.data.DicomObject;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.Experiment;
import org.nrg.xapi.rest.Project;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.IcrRoicollectiondata;
import org.nrg.xdat.om.IcrRoicollectiondataSeriesuid;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.security.UserI;
import org.nrg.xnatx.roi.RoiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author jamesd
 */
@Api(description="OHIF Viewer ROI API")
@XapiRestController
@RequestMapping(value="/roi")
@JsonIgnoreProperties(value = { "created" })
public class OhifRoiApi extends AbstractXapiProjectRestController
{
	private final static Logger logger = LoggerFactory.getLogger(OhifRoiApi.class);

	private final static String RoiCollElement = "icr:roiCollectionData/project";

	private final RoiService roiService;
	private final DicomSpatialDataService spatialDataService;

	@Autowired
	public OhifRoiApi(final RoiService roiService,
		final DicomSpatialDataService spatialDataService,
		final UserManagementServiceI userManagementService,
		final RoleHolder roleHolder)
	{
		super(userManagementService, roleHolder);
		this.roiService = roiService;
		this.spatialDataService = spatialDataService;
		logger.info("OHIF ROI XAPI initialised");
	}

	@ApiOperation(value="Returns permissions on a schema element")
	@ApiResponses({
		@ApiResponse(code=200, message="Permissions returned"),
		@ApiResponse(code=422, message="Unprocessable request")
	})
	@XapiRequestMapping(
		value="/projects/{projectId}/permissions/{element}",
		method=RequestMethod.GET,
		restrictTo=AccessLevel.Read,
		produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<ElementPermissions> checkPermission(
		@ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		@ApiParam(value="Schema Element") @PathVariable("element") String element)
		throws PluginException
	{
		logger.info("RoiApi::checkPermission(projectId="+projectId+
			", element="+element+")");
		UserI user = getSessionUser();
		if (logger.isDebugEnabled())
		{
			logger.debug("GET /projects/"+projectId+"/permissions/"+element+
				" by user "+user.getUsername());
		}
		Security.checkProject(user, projectId);
		ElementPermissions perms = Security.getPermissions(user, projectId, element);
		return new ResponseEntity<>(perms, HttpStatus.OK);
	}

	@ApiOperation(value="Tests existence of ROI collection")
	@ApiResponses({
		@ApiResponse(code=200, message="Collection returned"),
		@ApiResponse(code=422, message="Unprocessable request")
	})
	@XapiRequestMapping(
		value="/projects/{projectId}/exists/{idOrLabel}",
		method=RequestMethod.GET,
		restrictTo=AccessLevel.Read)
	@ResponseBody
	public ResponseEntity<Boolean> collectionExists(
		@ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		@ApiParam(value="Collection ID|Label") @PathVariable("idOrLabel") String idOrLabel)
		throws PluginException
	{
		logger.info("RoiApi::collectionExists(projectId="+projectId+
			", idOrLabel="+idOrLabel+")");
		UserI user = getSessionUser();
		if (logger.isDebugEnabled())
		{
			logger.debug("GET /projects/"+
				projectId+"/exists/"+idOrLabel+" by user "+user.getUsername());
		}
		Security.checkProject(user, projectId);
		Security.checkPermissions(user, RoiCollElement, projectId, Security.Read);
		IcrRoicollectiondata collectData = RoiUtils.getCollectionData(
			projectId, idOrLabel);
		return new ResponseEntity<>((collectData != null), HttpStatus.OK);
	}

	@ApiOperation(value="Spatial data cache control")
	@ApiResponses({
		@ApiResponse(code=200, message="Command completed"),
		@ApiResponse(code=422, message="Unprocessable request")
	})
	@XapiRequestMapping(
		value="/projects/{projectId}/sdcache/{sessionId}",
		method=RequestMethod.POST,
		restrictTo=AccessLevel.Admin)
	@ResponseBody
	public ResponseEntity<String> controlSdCache(
		@ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		@ApiParam(value="Session ID") @PathVariable("sessionId") @Experiment String sessionId,
		@ApiParam(value="Command", allowableValues="CLEAR,REFRESH") @RequestParam(value="cmd", required=true) String command)
		throws PluginException
	{
		logger.info("RoiApi::controlSdCache(projectId="+projectId+
			", sessionId="+sessionId+", cmd="+command+")");
		UserI user = getSessionUser();
		if (logger.isDebugEnabled())
		{
			logger.debug("POST /projects/"+projectId+"/sdcache/"+sessionId+
				" by user "+user.getUsername());
		}
		Security.checkProject(user, projectId);
		Security.checkSession(user, sessionId);
		XnatImagesessiondata sessionData = PluginUtils.getImageSessionData(
			sessionId, user);
		Security.checkPermissions(user, sessionData.getXSIType()+"/project", projectId,
			Security.Create, Security.Edit, Security.Read);
		Security.checkPermissions(user, RoiCollElement, projectId,
			Security.Create, Security.Edit, Security.Read);

		Result result;
		switch (command)
		{
			case "CLEAR":
				result = clearSdCache(sessionId);
				break;
			case "REFRESH":
				result = refreshSdCache(sessionId);
				break;
			default:
				result = new Result("Invalid cache command: "+command,
					HttpStatus.UNPROCESSABLE_ENTITY);
		}

		return new ResponseEntity<>(result.getMessage(), result.getStatus());
	}

	@ApiOperation(value="Deletes an ROI collection")
	@ApiResponses({
		@ApiResponse(code=200, message="Collection deleted"),
		@ApiResponse(code=422, message="Unprocessable request")
	})
	@XapiRequestMapping(
		value="/projects/{projectId}/collections/{idOrLabel}",
		method=RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<String> deleteCollection(
		@ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		@ApiParam(value="Collection ID|Label") @PathVariable("idOrLabel") String idOrLabel)
		throws PluginException
	{
		logger.info("RoiApi::deleteCollection(projectId="+projectId+
			", idOrLabel="+idOrLabel+")");
		UserI user = getSessionUser();
		if (logger.isDebugEnabled())
		{
			logger.debug("DELETE /projects/"+projectId+"/collections/"+idOrLabel+
				" by user "+user.getUsername());
		}
		Security.checkProject(user, projectId);
		IcrRoicollectiondata collectData = RoiUtils.getCollectionData(projectId,
			idOrLabel);
		if (collectData == null)
		{
			return new ResponseEntity<>("ROI collection "+idOrLabel+
					" not valid for project "+projectId, 
				HttpStatus.UNPROCESSABLE_ENTITY);
		}
		Security.checkPermissions(user, collectData, Security.Delete);

		PersistentWorkflowI workflow = null;
		EventMetaI eventMeta = null;
		try
		{
			workflow = ProcessUtils.buildOpenWorkflow(user,
				collectData.getImageSessionData().getItem(),
				"Delete ROI Collection");
			eventMeta = workflow.buildEvent();
			roiService.deleteCollectionRois(collectData.getId());
			XnatProjectdata projectData = collectData.getProjectData();
			collectData.delete(projectData, user, true, eventMeta);
			ProcessUtils.complete(workflow, eventMeta);
		}
		catch (PluginException ex)
		{
			ProcessUtils.safeFail(workflow, eventMeta);
			throw ex;
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ApiOperation(value="Returns an ROI collection")
	@ApiResponses({
		@ApiResponse(code=200, message="Collection returned"),
		@ApiResponse(code=422, message="Unprocessable request")
	})
	@XapiRequestMapping(
		value="/projects/{projectId}/collections/{idOrLabel}",
		method=RequestMethod.GET,
		restrictTo=AccessLevel.Read,
		produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public ResponseEntity<InputStreamResource> getCollection(
		@ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		@ApiParam(value="Collection ID|Label") @PathVariable("idOrLabel") String idOrLabel,
		@ApiParam(value="Type", allowableValues="AIM,RTSTRUCT,SEG,MEAS") @RequestParam(value="type", required=false, defaultValue="") String requestedType)
		throws PluginException
	{
		logger.info("RoiApi::getCollection(projectId="+projectId+
			", idOrLabel="+idOrLabel+", requestedType="+requestedType+")");
		UserI user = getSessionUser();
		if (logger.isDebugEnabled())
		{
			logger.debug("GET /projects/"+projectId+"/collections/"+idOrLabel+
				" by user "+user.getUsername());
		}
		Security.checkProject(user, projectId);
		IcrRoicollectiondata collectData = RoiUtils.getCollectionData(projectId,
			idOrLabel);
		if (collectData == null)
		{
			throw new PluginException("Bad ID or label: "+idOrLabel,
				PluginCode.HttpUnprocessableEntity);
		}
		Security.checkPermissions(user, collectData, Security.Read);
		String collectType = collectData.getCollectiontype();
		if (requestedType.isEmpty())
		{
			requestedType = collectType;
		}
		String modality = PluginUtils.getImageSessionModality(
			collectData.getImageSessionData());
		checkType(requestedType, modality);
//		if (Constants.Nifti.equals(requestedType) &&
//			 Constants.Segmentation.equals(collectType))
//		{
//			return segToNifti(user, collectData);
//		}
		File requestedFile = RoiUtils.getCollectionFile(user, collectData,
			requestedType);
		if (!requestedType.equals(collectType) && (requestedFile == null))
		{
			if (!isConvertibleTo(collectType, requestedType))
			{
				logger.info("Collection type "+collectType+
					" cannot be converted to requested type "+requestedType);
				return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
			}
			logger.info("Conversion required for requested type: {}", requestedType);
			// A collaborator typically has only read permission. Return 404 for a
			// missing file instead of 403 (forbidden) which implies the collaborator
			// doesn't have read permission (which they do to have got this far)
			if (!canEdit(user, projectId))
			{
				return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
			}
			// Create missing type
			requestedFile = createMissingType(user, collectData, requestedType);
		}
		InputStreamResource isr;
		try
		{
			isr = new InputStreamResource(
				Files.newInputStream(requestedFile.toPath()));
		}
		catch (IOException ex)
		{
			throw new PluginException(ex.getMessage(),
				PluginCode.HttpInternalError, ex);
		}
		return new ResponseEntity<>(isr, HttpStatus.OK);
	}

	@ApiOperation(value="Returns ROIs for an ROI collection")
	@ApiResponses({
		@ApiResponse(code=200, message="Collection returned"),
		@ApiResponse(code=422, message="Unprocessable request")
	})
	@XapiRequestMapping(
		value="/projects/{projectId}/rois/{idOrLabel}",
		method=RequestMethod.GET,
		restrictTo=AccessLevel.Read,
		produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<List<Roi>> getCollectionRois(
		@ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		@ApiParam(value="Collection ID|Label") @PathVariable("idOrLabel") String idOrLabel)
		throws PluginException
	{
		logger.info("RoiApi::getCollectionRois(projectId="+projectId+
			", idOrLabel="+idOrLabel+")");
		UserI user = getSessionUser();
		if (logger.isDebugEnabled())
		{
			logger.debug("GET /projects/"+
				projectId+"/rois/"+idOrLabel+" by user "+user.getUsername());
		}
		Security.checkProject(user, projectId);
		IcrRoicollectiondata collectData = RoiUtils.getCollectionData(projectId,
			idOrLabel);
		if (collectData == null)
		{
			throw new PluginException("Bad ID or label: "+idOrLabel,
				PluginCode.HttpUnprocessableEntity);
		}
		Security.checkPermissions(user, collectData, Security.Read);
		List<Roi> roiList = roiService.getCollectionRois(collectData.getId());
		return new ResponseEntity<>(roiList, HttpStatus.OK);
	}

	@ApiOperation(value="Returns a map of series UIDs to scan IDs")
	@ApiResponses({
		@ApiResponse(code=200, message="Map returned"),
		@ApiResponse(code=422, message="Unprocessable request")
	})
	@XapiRequestMapping(
		value="/projects/{projectId}/sessions/{sessionId}/uididmap",
		method=RequestMethod.GET,
		restrictTo=AccessLevel.Read,
		produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String,String>> getSeriesUidIdMap(
		@ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		@ApiParam(value="Session ID") @PathVariable("sessionId") @Experiment String sessionId)
		throws PluginException
	{
		logger.info("RoiApi::getSeriesUidIdMap(projectId="+projectId+
			", sessionId="+sessionId+")");
		UserI user = getSessionUser();
		if (logger.isDebugEnabled())
		{
			logger.debug("GET /projects/"+projectId+
				"/sessions/"+sessionId+"/uididmap by user "+user.getUsername());
		}
		Security.checkProject(user, projectId);
		Map<String,String> uidIdMap = PluginUtils.getImageScanUidIdMap(sessionId,
			user);
		return new ResponseEntity<>(uidIdMap, HttpStatus.OK);
	}

	@ApiOperation(value="Returns a list of SOP instance UIDs")
	@ApiResponses({
		@ApiResponse(code=200, message="UIDs returned"),
		@ApiResponse(code=422, message="Unprocessable request")
	})
	@XapiRequestMapping(
		value="/projects/{projectId}/sessions/{sessionId}/scans/{scanId}/uids",
		method=RequestMethod.GET,
		restrictTo=AccessLevel.Read,
		produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Set<String>> getSopInstanceUids(
		@ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		@ApiParam(value="Session ID") @PathVariable("sessionId") @Experiment String sessionId,
		@ApiParam(value="Scan ID") @PathVariable("scanId") String scanId)
		throws PluginException
	{
		logger.info("RoiApi::getSopInstanceUids(projectId="+projectId+
			", sessionId="+sessionId+", scanId="+scanId+")");
		UserI user = getSessionUser();
		if (logger.isDebugEnabled())
		{
			logger.debug("GET /projects/"+projectId+"/sessions/"+sessionId+
				"/scans/"+scanId+"/uids by user "+user.getUsername());
		}
		Security.checkProject(user, projectId);
		Set<String> set = PluginUtils.getSopInstanceUids(user, sessionId, scanId);
		return new ResponseEntity<>(set, HttpStatus.OK);
	}

	@ApiOperation(value="Returns a list of study containers")
	@ApiResponses({
		@ApiResponse(code=200, message="Containers returned"),
		@ApiResponse(code=422, message="Unprocessable request")
	})
	@XapiRequestMapping(
		value="/projects/{projectId}/containers/{studyUid}",
		method=RequestMethod.GET,
		restrictTo=AccessLevel.Read,
		produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Set<StudyUidContainer>> getStudyUidContainers(
		@ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		@ApiParam(value="Study UID") @PathVariable("studyUid") String studyUid)
		throws PluginException
	{
		logger.info("RoiApi::getStudyUidContainers(projectId="+projectId+
			", studyUid="+studyUid+")");
		UserI user = getSessionUser();
		if (logger.isDebugEnabled())
		{
			logger.debug("GET /projects/"+projectId+"/containers/"+studyUid+
				" by user "+user.getUsername());
		}
		Security.checkProject(user, projectId);
		Set<StudyUidContainer> set = getStudyUidContainerSet(user, projectId,
			studyUid);
		return new ResponseEntity<>(set, HttpStatus.OK);
	}

	@ApiOperation(value="Stores an ROI collection")
	@ApiResponses({
		@ApiResponse(code=200, message="Collection stored"),
		@ApiResponse(code=422, message="Unprocessable request")
	})
	@XapiRequestMapping(
		value="/projects/{projectId}/sessions/{sessionId}/collections/{label}",
		method=RequestMethod.PUT,
		consumes=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public ResponseEntity<String> putCollection(
		@ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		@ApiParam(value="Session ID") @PathVariable("sessionId") @Experiment String sessionId,
		@ApiParam(value="Collection Label") @PathVariable("label") String label,
		@ApiParam(value="Type", allowableValues="AIM,RTSTRUCT,SEG,MEAS") @RequestParam(value="type", required=true) String type,
		@ApiParam(value="Overwrite") @RequestParam(value="overwrite", required=false, defaultValue="false") boolean overwrite,
//		@ApiParam(value="Series UID") @RequestParam(value="seriesuid", required=false, defaultValue="") String seriesUid,
		InputStream is)
		throws PluginException
	{
		logger.info("RoiApi::putCollection(projectId="+projectId+
			", sessionId="+sessionId+", label="+label+", type="+type+
			", overwrite="+overwrite+")");
//			", overwrite="+overwrite+", seriesUid="+seriesUid+")");
		UserI user = getSessionUser();
		if (logger.isDebugEnabled())
		{
			logger.debug("PUT /projects/"+projectId+"/sessions/"+sessionId+
				"/collections/"+label+" by user "+user.getUsername());
		}
		Security.checkProject(user, projectId);
		Security.checkSession(user, sessionId);
		XnatImagesessiondata sessionData = PluginUtils.getImageSessionData(
			sessionId, user);
		Security.checkPermissions(user, sessionData.getXSIType()+"/project", projectId,
			Security.Create, Security.Edit, Security.Read);
		Security.checkPermissions(user, RoiCollElement, projectId,
			Security.Create, Security.Edit, Security.Read);

		String modality = PluginUtils.getImageSessionModality(sessionData);
		checkType(type, modality);
		// Null seriesUid until NIfTI re-enabled
		String seriesUid = null;
		String collectId = checkExisting(projectId, sessionId, label, overwrite);
		RoiCollection roiCollection = createRoiCollection(user, projectId,
			sessionId, collectId, label, is, type, seriesUid);
		CollectionStorage storage = new DefaultCollectionStorage();
		Result result = storage.store(user, roiCollection, roiService);
		storeAlternateTypes(user, roiCollection, modality);

		return new ResponseEntity<>(result.getMessage(), result.getStatus());
	}

	@ApiOperation(value="Regenerate alternate format(s) of ROI collection")
	@ApiResponses({
		@ApiResponse(code=200, message="Command completed"),
		@ApiResponse(code=422, message="Unprocessable request")
	})
	@XapiRequestMapping(
		value="/projects/{projectId}/collections/{idOrLabel}/regen",
		method=RequestMethod.POST,
		restrictTo=AccessLevel.Admin)
	@ResponseBody
	public ResponseEntity<String> regenDerivedCollection(
		@ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		@ApiParam(value="Collection ID|Label") @PathVariable("idOrLabel") String idOrLabel)
		throws PluginException
	{
		logger.info("RoiApi::regenDerivedCollection(projectId="+projectId+
			", idOrLabel="+idOrLabel+")");
		UserI user = getSessionUser();
		if (logger.isDebugEnabled())
		{
			logger.debug("GET /projects/"+projectId+"/collections/"+idOrLabel+
				"/regen by user "+user.getUsername());
		}
		Security.checkProject(user, projectId);
		IcrRoicollectiondata collectData = RoiUtils.getCollectionData(projectId,
			idOrLabel);
		if (collectData == null)
		{
			throw new PluginException("Bad ID or label: "+idOrLabel,
				PluginCode.HttpUnprocessableEntity);
		}
		Security.checkPermissions(user, collectData, Security.Edit, Security.Read);

		Result result = regen(collectData, user);

		return new ResponseEntity<>(result.getMessage(), result.getStatus());
	}

	@ApiOperation(value="Regenerate alternate format(s) for all ROI collections in a project")
	@ApiResponses({
		@ApiResponse(code=200, message="Command completed"),
		@ApiResponse(code=422, message="Unprocessable request")
	})
	@XapiRequestMapping(
		value="/projects/{projectId}/regen",
		method=RequestMethod.POST,
		restrictTo=AccessLevel.Admin)
	@ResponseBody
	public ResponseEntity<String> regenDerivedCollectionProject(
		@ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId)
		throws PluginException
	{
		logger.info("RoiApi::regenDerivedCollectionProject(projectId="+
			projectId+")");
		UserI user = getSessionUser();
		if (logger.isDebugEnabled())
		{
			logger.debug("GET /projects/"+projectId+"/regen by user "+
				user.getUsername());
		}
		Security.checkProject(user, projectId);

		List<XnatImagesessiondata> sessions =
			PluginUtils.getImageSessionDataByProject(projectId);
		for (XnatImagesessiondata sessionData : sessions)
		{
			Security.checkPermissions(user, sessionData,
				Security.Create, Security.Edit, Security.Read);
			List<IcrRoicollectiondata> collections =
				RoiUtils.getCollectionDataBySession(projectId,
					sessionData.getId());
			for (IcrRoicollectiondata collectData : collections)
			{
				Security.checkPermissions(user, collectData, Security.Edit,
					Security.Read);
				regen(collectData, user);
			}
		}
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

	@ApiOperation(value="Regenerate alternate format(s) for all ROI collections in a session")
	@ApiResponses({
		@ApiResponse(code=200, message="Command completed"),
		@ApiResponse(code=422, message="Unprocessable request")
	})
	@XapiRequestMapping(
		value="/projects/{projectId}/sessions/{sessionId}/regen",
		method=RequestMethod.POST,
		restrictTo=AccessLevel.Admin)
	@ResponseBody
	public ResponseEntity<String> regenDerivedCollectionSession(
		@ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		@ApiParam(value="Session ID") @PathVariable("sessionId") @Experiment String sessionId)
		throws PluginException
	{
		logger.info("RoiApi::regenDerivedCollectionSession(projectId="+projectId+
			", idOrLabel="+sessionId+")");
		UserI user = getSessionUser();
		if (logger.isDebugEnabled())
		{
			logger.debug("GET /projects/"+projectId+"/sessions/"+sessionId+
				"/regen by user "+user.getUsername());
		}
		Security.checkProject(user, projectId);
		Security.checkSession(user, sessionId);
		XnatImagesessiondata sessionData = PluginUtils.getImageSessionData(
			sessionId, user);
		Security.checkPermissions(user, sessionData,
			Security.Create, Security.Edit, Security.Read);

		List<IcrRoicollectiondata> collections =
			RoiUtils.getCollectionDataBySession(projectId, sessionId);
		for (IcrRoicollectiondata collectData : collections)
		{
			Security.checkPermissions(user, collectData, Security.Edit,
				Security.Read);
			regen(collectData, user);
		}
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

	private boolean canEdit(UserI user, String projectId) throws PluginException
	{
		boolean value = false;
		try
		{
			Security.checkPermissions(user, RoiCollElement, projectId,
				Security.Edit);
			value = true;
		}
		catch (PluginException ex)
		{
			if (!PluginCode.HttpForbidden.equals(ex.getCode()))
			{
				throw ex;
			}
		}
		return value;
	}

	private String checkExisting(String projectId, String sessionId,
		String label, boolean overwrite) throws PluginException
	{
		IcrRoicollectiondata collectData = RoiUtils.getCollectionDataByLabel(
			projectId, label);
		if (collectData == null)
		{
			return null;
		}
		if (!sessionId.equals(collectData.getImagesessionId()))
		{
			throw new PluginException(
				"ROI collection exists in a different session",
				PluginCode.HttpConflict);
		}
		if (!overwrite)
		{
			throw new PluginException("ROI collection exists, overwrite not specified",
				PluginCode.HttpUnprocessableEntity);
		}
		return collectData.getId();
	}

	private void checkType(String type, String modality) throws PluginException
	{
		switch (type)
		{
			case Constants.AIM:
				return;
			case Constants.RtStruct:
			case Constants.Segmentation:
			case Constants.Measurement:
				if (!is3D(modality) && !modality.equals("US"))
				{
					throw new PluginException(
						"Collection type "+type+" not supported for modality "+modality,
						PluginCode.HttpUnprocessableEntity);
				}
				return;
			case Constants.Nifti:
				return;
			default:
				throw new PluginException("Unsupported collection type: "+type,
					PluginCode.HttpUnprocessableEntity);
		}
	}

	private Result clearSdCache(String sessionId)
		throws PluginException
	{
		XnatImagesessiondata sessionData = PluginUtils.getImageSessionData(
			sessionId, null);
		List<XnatImagescandata> scans = sessionData.getScans_scan();
		for (XnatImagescandata scanData : scans)
		{
			String seriesUid = scanData.getUid();
			if (!StringUtils.isNullOrEmpty(seriesUid))
			{
				spatialDataService.deleteForSeries(seriesUid);
			}
		}

		return new Result("Cache cleared for session "+sessionId, HttpStatus.OK);
	}

	private void convertCollection(UserI user, RoiCollection roiCollection,
		String requestedType) throws PluginException
	{
		// Should have been already checked but cover it in case
		switch (requestedType)
		{
			case Constants.AIM:
			case Constants.RtStruct:
				break;
			default:
				throw new PluginException(
					"Unknown collection type requested: "+requestedType,
					PluginCode.HttpUnprocessableEntity);
		}
		CollectionConverter converter = new DefaultCollectionConverter();
		Result result = converter.convert(user, roiCollection, requestedType,
			spatialDataService);
		HttpStatus status = result.getStatus();
		if (!(status.equals(HttpStatus.CREATED) || status.equals(HttpStatus.OK)))
		{
			throw new PluginException("Conversion error - "+result.getMessage(),
				PluginCode.HttpInternalError);
		}
	}

	private File createMissingType(UserI user, IcrRoicollectiondata collectData,
		String requestedType) throws PluginException
	{
		logger.info("Recreating missing type "+requestedType+" for ROI collection "+
			collectData.getLabel());
		File collectFile = RoiUtils.getCollectionFile(user, collectData,
			collectData.getCollectiontype());
		InputStream is = null;
		try
		{
			is = Files.newInputStream(collectFile.toPath());
			RoiCollection roiCollection = createRoiCollection(user,
				collectData.getProject(), collectData.getImagesessionId(),
				collectData.getId(), collectData.getLabel(), is,
				collectData.getCollectiontype(), null);
			storeAlternateTypes(user, roiCollection);
			// Refresh the collection info
			collectData = RoiUtils.getCollectionDataById(collectData.getId());
		}
		catch (IOException ex)
		{
			throw new PluginException("Error creating missing type",
				PluginCode.HttpInternalError, ex);
		}
		finally
		{
			IoUtils.safeClose(is);
		}
		return RoiUtils.getCollectionFile(user, collectData, requestedType);
	}

	private RoiCollection createRoiCollection(UserI user, String projectId,
		String sessionId, String id, String label, InputStream is, String type,
		String seriesUid)
		throws PluginException
	{
		RoiCollection roiCollection;
		if ((id == null) || id.isEmpty())
		{
			id = "RoiCollection_"+Uids.createShortUnique();
		}
		try
		{
			switch (type)
			{
				case Constants.AIM:
					roiCollection = new AimRoiCollection(id,
						ByteStreams.toByteArray(is));
					break;
				case Constants.RtStruct:
					roiCollection = new RtStructRoiCollection(id,
						ByteStreams.toByteArray(is));
					break;
				case Constants.Segmentation:
					roiCollection = new SegmentationRoiCollection(id,
						ByteStreams.toByteArray(is));
					break;
				case Constants.Nifti:
					roiCollection = new NiftiRoiCollection(id,
						ByteStreams.toByteArray(is));
					populateUids((NiftiRoiCollection) roiCollection, sessionId, user,
						seriesUid);
					break;
				case Constants.Measurement:
					roiCollection = new JsonMeasurementCollection(id,
							ByteStreams.toByteArray(is));
					break;
				default:
					throw new PluginException("Unknown ROI collection type: "+type,
						PluginCode.HttpUnprocessableEntity);
			}
		}
		catch (IOException ex)
		{
			throw new PluginException("Error reading input stream - "+ex.getMessage(),
				PluginCode.IO, ex);
		}
		roiCollection.setProjectId(projectId);
		roiCollection.setSessionId(sessionId);
		XnatImagesessiondata sessionData = PluginUtils.getImageSessionData(
			sessionId, user);
		roiCollection.setSubjectId(sessionData.getSubjectData().getId());
		roiCollection.setLabel(label);
	
		return roiCollection;
	}

	private Set<StudyUidContainer> getStudyUidContainerSet(UserI user,
		String projectId, String studyUid)
	{
		Set<StudyUidContainer> set = new LinkedHashSet<>();
		CriteriaCollection projectCc = new CriteriaCollection("OR");
		projectCc.addClause("xnat:imageSessionData/project", projectId);
		projectCc.addClause("xnat:imageSessionData/sharing/share/project", projectId);
		CriteriaCollection cc = new CriteriaCollection("AND");
		cc.addClause(projectCc);
		cc.addClause("xnat:imageSessionData/UID", studyUid);
		List<XnatImagesessiondata> sessionList = 
			XnatImagesessiondata.getXnatImagesessiondatasByField(cc, user, false);
		for (XnatImagesessiondata sessionData : sessionList)
		{
			// Permissions can vary by modality so can't use xnat:imageSessionData
			// Filter out any results without read permission.
			String xsiType = sessionData.getXSIType();
			try
			{
				Security.checkPermissions(user, xsiType+"/project", projectId,
					Security.Read);
			}
			catch (PluginException ex)
			{
				continue;
			}
			XnatSubjectdata subjectData = sessionData.getSubjectData();
			try
			{
				StudyUidContainer container = new StudyUidContainer(projectId,
					subjectData.getId(), subjectData.getLabel(), sessionData.getId(),
					sessionData.getLabel(), xsiType);
				set.add(container);
			}
			catch (IllegalArgumentException ex)
			{
				logger.warn("Error locating StudyUidContainer for {}: {}", studyUid,
					ex.getMessage());
			}
		}
		return set;
	}

	private boolean isConvertibleTo(String collectType, String requestedType)
	{
		switch (collectType)
		{
			case Constants.AIM:
				return Constants.RtStruct.equals(requestedType);
			case Constants.RtStruct:
				return Constants.AIM.equals(requestedType);
			default:
		}
		return false;
	}

	private boolean is3D(String modality)
	{
		// Only certain modalities define the ImagePlane module and are located
		// in 3D space
		switch (modality)
		{
			case "MR":
			case "CT":
			case "PT":
				return true;
			default:
				// CR, SC, etc
				return false;
		}
	}

	private void populateUids(NiftiRoiCollection roiCollection, String sessionId,
		UserI user, String seriesUid) throws PluginException
	{
		if (StringUtils.isNullOrEmpty(seriesUid))
		{
			logger.warn("NIFTI ROI collection: series UID parameter not supplied");
			return;
		}
		XnatImagesessiondata sessionData = PluginUtils.getImageSessionData(
			sessionId, user);
		String studyUid = sessionData.getUid();
		if (StringUtils.isNullOrEmpty(studyUid))
		{
			logger.warn("NIFTI ROI collection missing study UID");
			return;
		}
		XnatImagescandata scanData = PluginUtils.getImageScanDataByUid(sessionData,
			seriesUid);
		if (scanData == null)
		{
			logger.warn(
				"NIFTI ROI collection: cannot locate scan for series UID - {}",
				seriesUid);
			return;
		}
		Set<String> sopInstUids = PluginUtils.getSopInstanceUids(user, sessionId,
			scanData.getId());
		roiCollection.setDicomUids(studyUid, seriesUid, sopInstUids);
	}

	private Result refreshSdCache(String sessionId) throws PluginException
	{
		XnatImagesessiondata sessionData = PluginUtils.getImageSessionData(
			sessionId, null);
		List<XnatImagescandata> scans = sessionData.getScans_scan();
			for (XnatImagescandata scanData : scans)
		{
			String seriesUid = scanData.getUid();
			if (StringUtils.isNullOrEmpty(seriesUid))
			{
				continue;
			}
			spatialDataService.deleteForSeries(seriesUid);
			Map<String,DicomObject> dcmMap = DsdUtils.getDicomObjectMap(
				spatialDataService, scanData);
			DsdUtils.saveToService(spatialDataService, dcmMap);
		}

		return new Result("Cache refreshed for session "+sessionId, HttpStatus.OK);
	}

	private Result regen(IcrRoicollectiondata collectData, UserI user)
		throws PluginException
	{
		String seriesUid = null;
		String type = collectData.getCollectiontype();
		File collectFile = RoiUtils.getCollectionFile(user, collectData, type);
		InputStream is = null;
		try
		{
			is = Files.newInputStream(collectFile.toPath());
			RoiCollection roiCollection = createRoiCollection(user,
				collectData.getProject(), collectData.getImagesessionId(),
				collectData.getId(), collectData.getLabel(), is, type, seriesUid);
			storeAlternateTypes(user, roiCollection);
		}
		catch (IOException ex)
		{
			throw new PluginException("Error reading file: "+collectFile.toPath(),
				PluginCode.HttpInternalError, ex);
		}
		finally
		{
			IoUtils.safeClose(is);
		}
		String message = "ROI collection "+collectData.getId()+
			" derived data regenerated";
		logger.info(message);

		return new Result(message, HttpStatus.OK);
	}

	private ResponseEntity<InputStreamResource> segToNifti(UserI user,
		IcrRoicollectiondata collectData) throws PluginException
	{
		File segFile = RoiUtils.getCollectionFile(user, collectData,
			Constants.Segmentation);
		List<IcrRoicollectiondataSeriesuid> seriesUidList =
			collectData.getReferences_seriesuid();
		if (seriesUidList.isEmpty())
		{
			throw new PluginException("Unable to locate referenced series UID",
				PluginCode.HttpUnprocessableEntity);
		}
		XnatImagescandata scanData = PluginUtils.getImageScanDataByUid(
			collectData.getImageSessionData(), seriesUidList.get(0).getSeriesuid());
		Map<String,DicomObject> dcmMap = DsdUtils.getDicomObjectMap(
			spatialDataService, scanData);

		Segmentation seg;
		InputStreamResource isr;
		try
		{
			DicomObject dcm = DicomUtils.readDicomFile(segFile);
			seg = Iods.segmentation(dcm);
			RoiConverter converter = DicomToolkit.getToolkit().createRoiConverter();
			Nifti nifti = converter.toNifti(seg, dcmMap);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			NiftiWriter writer = NiftiToolkit.getToolkit().createWriter();
			writer.write(nifti, baos);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			isr = new InputStreamResource(bais);
		}
		catch (IOException | ConversionException ex)
		{
			throw new PluginException(ex.getMessage(),
				PluginCode.HttpInternalError, ex);
		}
		return new ResponseEntity<>(isr, HttpStatus.OK);
	}

	private void storeAlternateTypes(UserI user, RoiCollection roiCollection)
		throws PluginException
	{
		storeAlternateTypes(user, roiCollection, null);
	}

	private void storeAlternateTypes(UserI user, RoiCollection roiCollection,
		String modality) throws PluginException
	{
		String type = roiCollection.getType();
		switch (type)
		{
			case Constants.AIM:
				if (is3D(modality))
				{
					convertCollection(user, roiCollection, Constants.RtStruct);
				}
				else
				{
					logger.info("Collection type "+type+
						" does not have alternate types for modality "+modality);
				}
				break;
			case Constants.RtStruct:
				convertCollection(user, roiCollection, Constants.AIM);
				break;
			case Constants.Segmentation:
			case Constants.Nifti:
			case Constants.Measurement:
				logger.info("Collection type "+type+" does not have alternate types");
				break;
			default:
				throw new PluginException(
					"Unknown ROI collection type: "+type,
					PluginCode.HttpUnprocessableEntity);
		}
	}

}
