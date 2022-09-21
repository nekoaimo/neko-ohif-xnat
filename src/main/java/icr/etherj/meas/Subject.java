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

/**
 *
 * @author mo.alsad
 */
public class Subject extends AbstractDisplayable
{
    private String birthDate = "00000000";
    private String ethnicGroup = "";
    private String id = "";
    private String name = "";
    private String sex = "";

    public Subject()
    {}

    /**
     *
     * @param name
     * @param birthDate
     * @param id
     */
    public Subject(String name, String birthDate, String id)
    {
        this.name = (name == null) ? "" : name;
        this.birthDate = ((birthDate == null) || birthDate.isEmpty()) ?
                "00000000" : birthDate;
        this.id = (id == null) ? "" : id;
    }

    @Override
    public void display(PrintStream ps, String indent, boolean recurse)
    {
        ps.println(indent+getClass().getName());
        String pad = indent+"  * ";
        ps.println(pad+"Name: "+name);
        ps.println(pad+"BirthDate: "+birthDate);
        ps.println(pad+"Id: "+id);
        if (!sex.isEmpty())
        {
            ps.println(pad+"Sex: "+sex);
        }
        if (!ethnicGroup.isEmpty())
        {
            ps.println(pad+"EthnicGroup: "+ethnicGroup);
        }
    }

    /**
     * @return the birthDate
     */
    public String getBirthDate()
    {
        return birthDate;
    }

    /**
     * @return the ethnicGroup
     */
    public String getEthnicGroup()
    {
        return ethnicGroup;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the sex
     */
    public String getSex()
    {
        return sex;
    }

    /**
     * @param birthDate the birthDate to set
     */
    public void setBirthDate(String birthDate)
    {
        this.birthDate = ((birthDate == null) || birthDate.isEmpty()) ?
                "00000000" : birthDate;
    }

    /**
     * @param ethnicGroup the ethnicGroup to set
     */
    public void setEthnicGroup(String ethnicGroup)
    {
        this.ethnicGroup = (ethnicGroup == null) ? "" : ethnicGroup;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = (id == null) ? "" : id;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = (name == null) ? "" : name;
    }

    /**
     * @param sex the sex to set
     */
    public void setSex(String sex)
    {
        this.sex = (sex == null) ? "" : sex;
    }
}
