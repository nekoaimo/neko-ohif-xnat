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
package org.nrg.xnatx.ohifviewer.event.listeners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.nrg.config.services.ConfigService;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xft.event.entities.WorkflowStatusEvent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnatx.ohifviewer.inputcreator.JsonMetadataHandler;
import org.nrg.xnatx.plugin.PluginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.Consumer;

import static reactor.bus.selector.Selectors.R;

/**
 *
 * @author jpetts
 */
@Service
public class OhifViewerEventListener
	implements Consumer<Event<WorkflowStatusEvent>>
{
	private static final Logger logger = LoggerFactory.getLogger(
		OhifViewerEventListener.class);

	private final JsonMetadataHandler jsonHandler;
	private final Set<String> logPipelines = new HashSet<>();
	private final List<String> triggerRegexes = new ArrayList<>();
	private final Set<String> triggerPipelines = new HashSet<>();

	@Inject
	public OhifViewerEventListener(EventBus eventBus, ConfigService configService)
	{
		eventBus.on(
			R(WorkflowStatusEvent.class.getName()+
				"[.]?("+PersistentWorkflowUtils.COMPLETE+")"),
			this);
		jsonHandler = new JsonMetadataHandler(configService);
		createTriggers();
		logger.info("OHIF Viewer event listener initialised");
	}

	@Override
	public void accept(Event<WorkflowStatusEvent> event)
	{
		final WorkflowStatusEvent wfsEvent = event.getData();
		if (wfsEvent.getWorkflow() instanceof WrkWorkflowdata)
		{
			handleEvent(wfsEvent);
		}
	}

	private void createTriggers()
	{
		logPipelines.add("Configured project sharing");

		// Trigger pipelines are the pipeline names of workflow events we should
		// rebuild the viewer JSON for.
		triggerPipelines.add("Transferred"); // Session created
		triggerPipelines.add("Merged"); // Data added to existing session
		triggerPipelines.add("Update");
		triggerPipelines.add("Folder Deleted");
		triggerPipelines.add("Folder Created");
		triggerPipelines.add("Removed scan");
		triggerPipelines.add("Modified Subject");
		triggerPipelines.add("Created resource");
		triggerPipelines.add("Modified project");

		// Trigger regexes match pipeline names that are not fixed.
		triggerRegexes.add("Modified .* Session");
	}

	private void handleEvent(WorkflowStatusEvent wfsEvent)
	{
		final WrkWorkflowdata workflow = (WrkWorkflowdata) wfsEvent.getWorkflow();
		String pipelineName = workflow.getPipelineName();
		String experimentId = workflow.getId();

		if (logger.isDebugEnabled())
		{
			logger.debug("Handling event in OhifViewerEventListener. PipelineName: "+
				pipelineName+", datatype: "+workflow.getDataType()+", ID: "+
				experimentId);
		}
		if (logPipelines.contains(pipelineName))
		{
			logger.info("No action taken for event: "+pipelineName);
			return;
		}
		if (triggerPipelines.contains(pipelineName)
			|| triggerMatches(pipelineName))
		{
			UserI user = workflow.getUser();
			XnatImagesessiondata sessionData =
					XnatImagesessiondata.getXnatImagesessiondatasById(
						experimentId, user, false);
			if (sessionData == null)
			{
				return;
			}
			if (logger.isDebugEnabled())
			{
				logger.debug("Rebuilding viewer JSON metadata for experiment: "
					+experimentId+" User: "+user.getUsername()
					+" Trigger event: '"+pipelineName+"'");
			}
			try
			{
				jsonHandler.createAndStoreJsonConfig(sessionData, user);
			}
			catch (PluginException ex)
			{
				logger.warn(ex.getMessage(), ex);
			}
		}
	}

	private boolean triggerMatches(String pipelineName)
	{
		for (String regex : triggerRegexes)
		{
			if (pipelineName.matches(regex))
			{
				return true;
			}
		}
		return false;
	}

}
