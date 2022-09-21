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
package icr.etherj.meas;

import icr.etherj.AbstractDisplayable;
import icr.etherj.Uids;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mo.alsad
 */
public class ImageMeasurement extends AbstractDisplayable
{
    private final List<Measurement> measurements = new ArrayList<>();
    // ToDo: convert codingSequence to object (array of objects)
    private String codingSequence = "";
    private String color = "";
    private boolean dashedLine = false;
    private String data = "";
    private String description = "";
    private ImageReference imageReference = null;
    private int lineThickness = 1;
    private String name = "";
    private String toolType = "";
    private String uuid = Uids.generateDicomUid();
    private String viewport = "";
    private boolean visible = true;

    public ImageMeasurement()
    {
    }

    public void addMeasurement(Measurement meas)
    {
        measurements.add(meas);
    }

    @Override
    public void display(PrintStream ps, String indent, boolean recurse)
    {
        ps.println(indent+getClass().getName());
        String pad = indent+"  * ";
        ps.println(pad + "UUID: " + uuid);
        ps.println(pad + "ToolType: " + toolType);
        ps.println(pad + "Name: " + name);
        ps.println(pad + "Description: " + description);
        ps.println(pad + "Color: " + color);
        ps.println(pad + "LineThickness: " + lineThickness);
        ps.println(pad + "DashedLine: " + dashedLine);
        ps.println(pad + "Visible: " + visible);

        if (imageReference != null) {
            imageReference.display(ps, indent + "  ");
        }

        ps.println(pad + "CodingSequence: " + codingSequence);
        ps.println(pad + "Viewport: " + viewport);
        ps.println(pad + "Data: " + data);

        ps.println(pad + "Measurements:");
        for (Measurement meas : measurements) {
            meas.display(ps, indent + "    ");
        }
    }

    public String getCodingSequence()
    {
        return codingSequence;
    }

    public String getColor()
    {
        return color;
    }

    public String getData()
    {
        return data;
    }

    public String getDescription()
    {
        return description;
    }

    public ImageReference getImageReference()
    {
        return imageReference;
    }

    public int getLineThickness()
    {
        return lineThickness;
    }

    public List<Measurement> getMeasurements()
    {
        return measurements;
    }

    public String getName()
    {
        return name;
    }

    public String getToolType()
    {
        return toolType;
    }

    public String getUuid()
    {
        return uuid;
    }

    public String getViewport()
    {
        return viewport;
    }

    public boolean isDashedLine()
    {
        return dashedLine;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setCodingSequence(String codingSequence)
    {
        this.codingSequence = codingSequence;
    }

    public void setColor(String color)
    {
        this.color = color;
    }

    public void setDashedLine(boolean dashedLine)
    {
        this.dashedLine = dashedLine;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setImageReference(ImageReference imageReference)
    {
        this.imageReference = imageReference;
    }

    public void setLineThickness(int lineThickness)
    {
        this.lineThickness = lineThickness;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setToolType(String toolType)
    {
        this.toolType = toolType;
    }

    public void setUuid(String uuid)
    {
        if (uuid != null && !uuid.isEmpty())
        {
            this.uuid = uuid;
        } else
        {
            throw new IllegalArgumentException("UUID must not be null or empty");
        }
    }

    public void setViewport(String viewport)
    {
        this.viewport = viewport;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }
}
