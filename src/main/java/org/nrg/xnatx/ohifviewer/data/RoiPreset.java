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
package org.nrg.xnatx.ohifviewer.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author mo.alsad
 */
public class RoiPreset
{
    @ApiModelProperty(value = "ROI Color", example = "[128,128,128]", required = false)
    @JsonProperty("color")
    private int[] color = new int[]{0, 0, 0};

    @ApiModelProperty(value = "ROI Label", example = "Muscle", required = true)
    @JsonProperty("label")
    private String label = "";

    public RoiPreset() { }

    public RoiPreset( String label,int[] color)
    {
        this.label = label;
        clampAndFillColorArray(color);
    }

    public int[] getColor()
    {
        return Arrays.copyOf(color, color.length);
    }

    public String getLabel()
    {
        return label;
    }

    public void setColor(int[] color)
    {
        clampAndFillColorArray(color);
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        RoiPreset other = (RoiPreset) obj;
        return Objects.equals(label, other.label);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(label);
    }

    @Override
    public String toString()
    {
        return "RoiPreset: label="+label+", color="+Arrays.toString(color);
    }

    private void clampAndFillColorArray(int[] color)
    {
        if (color != null && color.length == 3)
        {
            for (int i = 0; i < color.length; i++)
            {
                // Clamp the value to [0, 255] bounds
                int value = Math.max(color[i], 0);
                this.color[i] = Math.min(value, 255);
            }
        }
    }
}
