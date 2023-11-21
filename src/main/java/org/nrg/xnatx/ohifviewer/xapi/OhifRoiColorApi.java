/* ********************************************************************
 * Copyright (c) 2022, Institute of Cancer Research
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import icr.etherj2.StringUtils;
import io.swagger.annotations.*;
import org.nrg.config.entities.Configuration;
import org.nrg.config.services.ConfigService;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.Project;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.security.UserI;
import org.nrg.xnatx.ohifviewer.JsonROIColorListHandler;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.plugin.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.ArrayIndexOutOfBoundsException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InvalidNameException;

/**
 *
 * @author mo.alsad
 */
@Deprecated
@Api(description="OHIF Viewer ROI Color API")
@XapiRestController
@RequestMapping(value = "/ohifroicolor")
public class OhifRoiColorApi extends AbstractXapiRestController
{
	private static final Logger logger = LoggerFactory.getLogger(
		OhifViewerApi.class);

	private final JsonROIColorListHandler jsonHandler;

	@Autowired
	public OhifRoiColorApi(final ConfigService configService,
                           final UserManagementServiceI userManagementService,
                           final RoleHolder roleHolder)
	{
		super(userManagementService, roleHolder);
		jsonHandler = new JsonROIColorListHandler(configService);
		logger.info("OHIF Viewer ROI Color XAPI initialised");
	}

	private static class RoiColor {
		private String label;
		private String color;

		public RoiColor(@JsonProperty("label") String label,
						@JsonProperty("color") String color) {
			this.label = label;
			this.color = color;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}
	}

	@ApiOperation(value = "Returns the ROI Colors JSON for the specified project ID.")
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "The project was located and JSON ROI Color list returned."),
		@ApiResponse(code = 403, message = "The user does not have permission to view the indicated project."),
		@ApiResponse(code = 404, message = "The JSON ROI Color list was not found for the indicated project."),
		@ApiResponse(code = 500, message = "An unexpected error occurred.")
	})
	@XapiRequestMapping(
		value = "projects/{projectId}/roicolor",
		produces = MediaType.APPLICATION_JSON_VALUE,
		method = RequestMethod.GET,
		restrictTo = AccessLevel.Read
	)
	@ResponseBody
	public ResponseEntity<List<RoiColor>> getProjectRoiColorJson(
		final @ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId)
		throws PluginException
	{
		UserI user = getSessionUser();
		Security.checkProject(user, projectId);
		Configuration jsonConfig = jsonHandler.getProjectJsonConfig(projectId);
		return processJsonConfig(jsonConfig);
	}

	@ApiOperation(value = "Sets the ROI Colors JSON for the specified project.")
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "The JSON ROI Color list was stored."),
		@ApiResponse(code = 403, message = "The user does not have permission to access the resource."),
		@ApiResponse(code = 422, message = "Unprocessable entity, most likely malformed JSON or URL in the JSON."),
		@ApiResponse(code = 500, message = "An unexpected error occurred.")
	})
	@XapiRequestMapping(
		value = "projects/{projectId}/roicolor",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		method = RequestMethod.PUT,
		restrictTo = AccessLevel.Admin
	)
	@ResponseBody
	public ResponseEntity<String> setProjectRoiColorJson(
		final @ApiParam(value="Project ID") @PathVariable("projectId") @Project String projectId,
		@RequestBody List<RoiColor> roiColorList)
		throws PluginException
	{
		UserI user = getSessionUser();
		Security.checkProject(user, projectId);
		String json = parseRoiColorList(roiColorList);
		jsonHandler.setProjectJson(user, projectId, json);
		return new ResponseEntity<>("Project "+projectId+" ROI colors updated",
			HttpStatus.OK);
	}

	private List<RoiColor> buildRoiColorList(String json) throws PluginException
	{
		ObjectMapper mapper = new ObjectMapper();
		CollectionType javaType = mapper.getTypeFactory()
			.constructCollectionType(List.class, RoiColor.class);
		List<RoiColor> roiColorList;
		try
		{
			roiColorList = mapper.readValue(json, javaType);
		}
		catch (IOException ex)
		{
			throw new PluginException("Error creating JSON: "+ex.getMessage(),
				PluginCode.HttpInternalError, ex);
		}
		return roiColorList;
	}

	private String parseRoiColorList(List<RoiColor> roiColorList) throws PluginException
	{
		ObjectMapper mapper = new ObjectMapper();
		String json;
		try
		{
			for (RoiColor roiColor : roiColorList)
			{
				// Check ROI label and color are valid
				if (roiColor.label == null || roiColor.label.isEmpty()) {
					throw new InvalidNameException("ROI label cannot be empty");
				}
				String[] colors = roiColor.color.split(",");
				if (colors.length != 3) {
					throw new ArrayIndexOutOfBoundsException("");
				}
				for (String color : colors) {
					int colorValue = Integer.parseInt(color.trim());
					if (colorValue < 0 || colorValue > 255) {
						throw new NumberFormatException("");
					}
				}
			}
			json = mapper.writeValueAsString(roiColorList);
		}
		catch (InvalidNameException ex)
		{
			throw new PluginException("Invalid ROI label entry",
				PluginCode.IllegalArgument, ex);
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
			throw new PluginException("Invalid ROI color entry, provide a comma-separated list of RGB values",
					PluginCode.IllegalArgument, ex);
		}
		catch (NumberFormatException ex)
		{
			throw new PluginException("Invalid ROI color entry, values should be between 0 and 255",
					PluginCode.IllegalArgument, ex);
		}
		catch (JsonProcessingException ex)
		{
			throw new PluginException("Error creating JSON: "+ex.getMessage(),
				PluginCode.HttpUnprocessableEntity, ex);
		}
		return json;
	}

	private ResponseEntity<List<RoiColor>> processJsonConfig(
		Configuration jsonConfig) throws PluginException
	{
		List<RoiColor> list;
		if (jsonConfig == null)
		{
			list = new ArrayList<>();
			list.add(new RoiColor("No ROI color list found", ""));
			return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
		}
		String json = jsonConfig.getContents();
		if (StringUtils.isNullOrEmpty(json))
		{
			list = new ArrayList<>();
			list.add(new RoiColor("No ROI color list found", ""));
			return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
		}
		list = buildRoiColorList(json);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

}
