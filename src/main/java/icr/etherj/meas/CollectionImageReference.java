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
import java.util.ArrayList;
import java.util.List;

/**
 * @author mo.alsad
 */
public class CollectionImageReference extends AbstractDisplayable
{
    private final List<ImageReference> imageCollection = new ArrayList<>();
    private String Modality = "";
    private String PatientID = "";
    private String SeriesInstanceUID = "";
    private String StudyInstanceUID = "";

    public CollectionImageReference()
    {
    }

    public CollectionImageReference(String modality, String patientID, String seriesInstanceUID, String studyInstanceUID)
    {
        Modality = modality;
        PatientID = patientID;
        SeriesInstanceUID = seriesInstanceUID;
        StudyInstanceUID = studyInstanceUID;
    }

    public void addImageReference(ImageReference imageReference)
    {
        this.imageCollection.add(imageReference);
    }

    @Override
    public void display(PrintStream ps, String indent, boolean recurse)
    {
        ps.println(indent+getClass().getName());
        String pad = indent+"  * ";
        ps.println(pad + "Modality: " + Modality);
        ps.println(pad + "PatientID: " + PatientID);
        ps.println(pad + "StudyInstanceUID: " + StudyInstanceUID);
        ps.println(pad + "SeriesInstanceUID: " + SeriesInstanceUID);

        ps.println(pad + "imageCollection:");
        for (ImageReference image : imageCollection) {
            image.display(ps, indent + "    ");
        }
    }

    public List<ImageReference> getImageCollection()
    {
        return imageCollection;
    }

    public String getModality()
    {
        return Modality;
    }

    public String getPatientID()
    {
        return PatientID;
    }

    public String getSeriesInstanceUID()
    {
        return SeriesInstanceUID;
    }

    public String getStudyInstanceUID()
    {
        return StudyInstanceUID;
    }

    public void setModality(String modality)
    {
        Modality = modality;
    }

    public void setPatientID(String patientID)
    {
        PatientID = patientID;
    }

    public void setSeriesInstanceUID(String seriesInstanceUID)
    {
        SeriesInstanceUID = seriesInstanceUID;
    }

    public void setStudyInstanceUID(String studyInstanceUID)
    {
        StudyInstanceUID = studyInstanceUID;
    }
}
