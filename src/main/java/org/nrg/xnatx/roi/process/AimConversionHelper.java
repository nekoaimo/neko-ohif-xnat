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

import com.google.common.collect.ImmutableSet;
import icr.etherj2.XmlException;
import icr.etherj2.aim.Aim;
import icr.etherj2.aim.ImageAnnotationCollection;
import icr.etherj2.aim.XmlParser;
import icr.etherj2.dicom.ConversionException;
import icr.etherj2.dicom.DicomToolkit;
import icr.etherj2.dicom.RoiConverter;
import icr.etherj2.dicom.iod.Iods;
import icr.etherj2.dicom.iod.RtStruct;
import org.dcm4che3.data.UID;
import org.nrg.xnatx.ohifviewer.service.OhifSessionDataService;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.roi.Constants;
import org.nrg.xnatx.roi.data.RoiCollection;
import org.nrg.xnatx.roi.service.DicomSpatialDataService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.io.DicomOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jamesd
 */
public class AimConversionHelper extends AbstractConversionHelper {
    private final static Logger logger = LoggerFactory.getLogger(AimConversionHelper.class);
    private final static Set<String> outputTypes = new HashSet<>();

    static {
        outputTypes.add(Constants.RtStruct);
        outputTypes.add(Constants.AIM);
    }

    /**
     * Returns a new <code>AimConversionHelper</code>.
     *
     * @param roiCollection   the {@link RoiCollection} to convert
     * @param targetType      the type to convert to
     * @param ohifJsonService the {@link OhifSessionDataService}
     * @throws PluginException if collection type is <b><i>not</i></b> AIM or if target type is AIM or not supported
     */
    public AimConversionHelper(RoiCollection roiCollection, String targetType,
                               DicomSpatialDataService spatialDataService, OhifSessionDataService ohifJsonService)
            throws PluginException {
        super(roiCollection, targetType, spatialDataService, ohifJsonService);
        if (!Constants.AIM.equals(roiCollection.getType())) {
            throw new PluginException("ROI collection type must be: " + Constants.AIM,
                    PluginCode.HttpUnprocessableEntity);
        }
        if (Constants.AIM.equals(targetType)) {
            throw new PluginException("Target type equals native type: " + targetType,
                    PluginCode.HttpUnprocessableEntity);
        }
        if (!outputTypes.contains(targetType)) {
            throw new PluginException("Unsupported target type: " + targetType, PluginCode.HttpUnprocessableEntity);
        }
    }

    @Override
    public byte[] convert() throws PluginException {
        byte[] result = null;
        try {
            XmlParser parser = Aim.xmlParser();
            ImageAnnotationCollection iac = parser.parse(roiCollection.getStream());
            logger.debug("IAC read: {}", iac.getDescription());

            RoiConverter converter = DicomToolkit.getToolkit().createRoiConverter();
            if (targetType.equals(Constants.RtStruct)) {
                RtStruct rtStruct = converter.toRtStruct(iac, getDicomObjectMap());
                Attributes dcm = Iods.pack(rtStruct);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DicomOutputStream os = new DicomOutputStream(baos, UID.ImplicitVRLittleEndian);
                dcm.writeTo(os);
                result = baos.toByteArray();
                logger.debug("RTSTRUCT bytes created");
            } else {
                // Should never be executed as the targetType is checked in ctor
                throw new PluginException("Unknown or unsupported target type: " + targetType,
                        PluginCode.HttpInternalError);
            }
        } catch (IOException ex) {
            throw new PluginException("Error converting ROI collection",
                    PluginCode.IO, ex);
        } catch (IllegalArgumentException ex) {
            throw new PluginException("Error converting ROI collection",
                    PluginCode.IllegalArgument, ex);
        } catch (XmlException ex) {
            throw new PluginException("Error converting ROI collection",
                    PluginCode.XML, ex);
        } catch (ConversionException ex) {
            throw new PluginException("Error converting ROI collection",
                    PluginCode.HttpInternalError, ex);
        }
        return result;
    }

    @Override
    public Set<String> outputTypes() {
        return ImmutableSet.copyOf(outputTypes);
    }

}
