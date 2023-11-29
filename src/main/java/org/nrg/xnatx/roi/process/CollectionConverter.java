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

import org.nrg.xnatx.ohifviewer.service.OhifSessionDataService;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.roi.data.RoiCollection;
import org.nrg.xnatx.roi.service.DicomSpatialDataService;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.dcm4che3.data.Attributes;
import org.nrg.xft.security.UserI;

/**
 * @author jamesd
 */
public interface CollectionConverter {
    /**
     * Convert an ROI collection to another type
     *
     * @param user          the user
     * @param roiCollection the ROI collection
     * @param targetType    the target type
     * @return a message and HTTP return code
     * @throws PluginException if any failure occurs
     */
    Result convert(UserI user, RoiCollection roiCollection, String targetType,
                   DicomSpatialDataService spatialDataService, OhifSessionDataService ohifJsonService)
            throws PluginException;

    /**
     *
     */
    interface Helper {
        /**
         * Returns the <code>byte[]</code> containing the conversion of the RoiCollection's original format data.
         *
         * @return the converted data
         * @throws PluginException if conversion fails
         */
        byte[] convert() throws PluginException;

        /**
         * @return
         */
        public File getCollectionFile();

        /**
         * @return
         * @throws PluginException
         */
        Map<String, Attributes> getDicomObjectMap() throws PluginException;

        /**
         * @return
         */
        String getTargetFileFormat();

        /**
         * @return
         */
        String getTargetType();

        /**
         * @return
         */
        public String getTargetTypeDescription();

        /**
         * @return
         */
        Set<String> outputTypes();
    }

}
