/* ********************************************************************
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
package org.nrg.xnatx.roi.process;

import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xft.XFTItem;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.utils.WorkflowUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jamesd
 */
public class ProcessUtils
{
	private final static Logger logger = LoggerFactory.getLogger(
		ProcessUtils.class);

	/**
	 *
	 * @param user
	 * @param item
	 * @param actionDescription
	 * @return
	 * @throws PluginException
	 */
	public static PersistentWorkflowI buildOpenWorkflow(UserI user, XFTItem item,
		String actionDescription) throws PluginException
	{
		PersistentWorkflowI workflow;
		try
		{
			workflow = PersistentWorkflowUtils.buildOpenWorkflow(user, item,
				EventUtils.newEventInstance(
					EventUtils.CATEGORY.DATA,
					EventUtils.TYPE.WEB_SERVICE,
					actionDescription));
		}
		catch (PersistentWorkflowUtils.JustificationAbsent |
			PersistentWorkflowUtils.ActionNameAbsent |
			PersistentWorkflowUtils.IDAbsent ex)
		{
			throw new PluginException("Error creating workflow",
				PluginCode.HttpInternalError, ex);
		}
		logger.info("Workflow created: "+workflow.getId()+" - "+actionDescription);
		return workflow;
	}

	/**
	 *
	 * @param workflow
	 * @param eventMeta
	 * @throws PluginException
	 */
	public static void complete(PersistentWorkflowI workflow, EventMetaI eventMeta)
		throws PluginException
	{
		try
		{
			if (workflow != null)
			{
				WorkflowUtils.complete(workflow, eventMeta);
				logger.info("Workflow completed: "+workflow.getId()+" - "+
					workflow.getPipelineName());
			}
		}
		catch (Exception ex)
		{
			throw new PluginException("Error completing workflow",
				PluginCode.HttpInternalError, ex);
		}
	}

	/**
	 *
	 * @param user
	 * @param xsiType
	 * @param securityId
	 * @param projectId
	 * @param actionDescription
	 * @return
	 * @throws PluginException
	 */
	public static PersistentWorkflowI getOrCreateWorkflowData(UserI user,
		String xsiType, String securityId, String projectId,
		String actionDescription) throws PluginException
	{
		PersistentWorkflowI workflow;
		try
		{
			workflow = PersistentWorkflowUtils.getOrCreateWorkflowData(null, user, 
				xsiType, securityId, projectId,
				EventUtils.newEventInstance(EventUtils.CATEGORY.DATA,
					EventUtils.TYPE.WEB_SERVICE, actionDescription));
		}
		catch (PersistentWorkflowUtils.JustificationAbsent |
			PersistentWorkflowUtils.ActionNameAbsent ex)
		{
			throw new PluginException("Error creating workflow",
				PluginCode.HttpInternalError, ex);
		}
		logger.info("Workflow created: "+workflow.getId()+" - "+actionDescription);
		return workflow;
	}

	/**
	 *
	 * @param workflow
	 * @param eventMeta
	 */
	public static void safeFail(PersistentWorkflowI workflow, EventMetaI eventMeta)
	{
		try
		{
			if (workflow != null)
			{
				WorkflowUtils.fail(workflow, eventMeta);
				logger.warn("Workflow failed: "+workflow.getId()+" - "+
					workflow.getPipelineName());
			}
		}
		catch (Exception ex)
		{
			logger.error("Error registering workflow failure", ex);
		}
	}

	private ProcessUtils()
	{}
}
