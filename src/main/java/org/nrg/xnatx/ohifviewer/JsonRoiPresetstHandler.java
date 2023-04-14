/*********************************************************************
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
package org.nrg.xnatx.ohifviewer;

import org.nrg.config.entities.Configuration;
import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.config.services.ConfigService;
import org.nrg.framework.constants.Scope;
import org.nrg.xft.security.UserI;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author mo.alsad
 */
public class JsonRoiPresetstHandler
{
    private static final Logger logger = LoggerFactory.getLogger(
        JsonRoiPresetstHandler.class);

    private static final String CreateProjectReason = "Creating Project ROI Presets JSON";
    private static final String OhifROIPresetsBaseToolName = "ohif-roi-presets";
    private static final String ProjectJsonToolPath = "project-json";

    private final ConfigService configService;
    private final Lock configServiceLock = new ReentrantLock();

    public JsonRoiPresetstHandler(final ConfigService configService)
    {
        this.configService = configService;
    }

    /**
     *
     * @param projectId
     * @param roiType: AIM, SEG, MEAS, ALL
     * @return
     */
    public Configuration getProjectJsonConfig(String projectId, String roiType)
    {
        String configToolName = getRoiPresetToolName(roiType);
        return configService.getConfig(configToolName, ProjectJsonToolPath,
            Scope.Project, projectId);
    }

    /**
     *
     * @param user
     * @param projectId
     * @param roiType: AIM, SEG, MEAS
     * @param json
     * @throws PluginException
     */
    public void setProjectJsonConfig(UserI user, String projectId, String roiType, String json)
        throws PluginException
    {
        String configToolName = getRoiPresetToolName(roiType);
        try
        {
            // Method may be called in muliple threads, only access shared vars
            // under lock.
            configServiceLock.lock();
            configService.replaceConfig(user.getUsername(), CreateProjectReason,
                    configToolName, ProjectJsonToolPath, true, json, Scope.Project,
                    projectId);
            logger.info(roiType+" ROI presets stored for project "+projectId);
        }
        catch (ConfigServiceException ex)
        {
            throw new PluginException("Error storing ROI presets config",
                PluginCode.ConfigService, ex);
        }
        finally
        {
            configServiceLock.unlock();
        }
    }

    /**
     *
     * @param roiType: AIM, SEG, MEAS
     * @return
     */
    private String getRoiPresetToolName(String roiType)
    {
        return OhifROIPresetsBaseToolName + '-' + roiType.toLowerCase();
    }
}
