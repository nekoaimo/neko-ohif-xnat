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

import java.io.PrintStream;
import java.text.DecimalFormat;

/**
 * @author mo.alsad
 */
public class Measurement extends AbstractDisplayable
{
    private static final DecimalFormat df = new DecimalFormat("0.00");
    // Comment is used for ArrowAnnotation
    private String comment = "";
    private String name = "";
    private String unit = "";
    private float value = 0;

    public Measurement()
    {
    }

    public Measurement(String comment, String name, float value, String unit)
    {
        this.comment = comment;
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    @Override
    public void display(PrintStream ps, String indent, boolean recurse)
    {
        ps.println(indent+getClass().getName());
        String pad = indent+"  * ";
        if (name.equals("arrow"))
        {
            ps.println(pad+String.format("%s = %s", name, comment));
        } else
        {
            ps.println(pad+String.format("%s = %,.2f %s %s", name, value, unit, comment));
        }
    }

    public String getComment()
    {
        return comment;
    }

    public String getName()
    {
        return name;
    }

    public String getUnit()
    {
        return unit;
    }

    public float getValue()
    {
        return value;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public void setValue(float value)
    {
        this.value = value;
    }
}
