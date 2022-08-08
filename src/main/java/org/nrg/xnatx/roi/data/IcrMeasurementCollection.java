/*********************************************************************
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
package org.nrg.xnatx.roi.data;

import icr.etherj.meas.ImageMeasurementCollection;
import icr.xnat.plugin.roi.entity.Roi;
import org.nrg.xnatx.plugin.PluginException;

import java.util.List;

/**
 *
 * @author mo.alsad
 */
public class IcrMeasurementCollection extends AbstractRoiCollection
        implements RoiCollection
{
    private final ImageMeasurementCollection imc;

    /**
     *
     * @param id
     * @param rawBytes
     * @throws PluginException
     */
    public IcrMeasurementCollection(String id, byte[] rawBytes) throws PluginException {
        super(id, rawBytes);
        setFileExtension("json");
        setFileFormat("JSON");
        setTypeDescription("Measurement instance file");
    }

    @Override
    public String getDate() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<Roi> getRoiList() {
        return null;
    }

    @Override
    public String getTime() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getUid() {
        return null;
    }
}
