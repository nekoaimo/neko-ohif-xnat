/********************************************************************
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

/********************************************************************
* @author Simon J Doran
* Java class: OhifViewerInputStudy.java
* First created on Sep 12, 2017 at 11:10:00 AM
*
* Component of OhifViewerInput, which is serialised to JSON by
* CreateOhifViewerInputJson.java
*********************************************************************/

package org.nrg.xnatx.ohifviewer.inputcreator;

import com.google.gson.annotations.SerializedName;
import org.nrg.xnatx.ohifviewer.ViewerUtils;

import com.google.common.collect.ImmutableList;
import icr.etherj2.dicom.Patient;
import icr.etherj2.dicom.SopInstance;
import icr.etherj2.dicom.Study;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OhifViewerInputStudy extends OhifViewerInputItem
{
	private static final Logger logger = LoggerFactory.getLogger(OhifViewerInputStudy.class);

    @SerializedName("series")
    private final List<OhifViewerInputSeries> seriesList = new ArrayList<>();
    transient private final Map<String, OhifViewerInputSeries> seriesMap = new LinkedHashMap<>();
    private String PatientID;
	private String PatientName;
	private String StudyDate;
	private String StudyDescription;
	private String StudyInstanceUID;
	private String StudyTime;

    public void allocateStudyTime(SopInstance sopInst)
    {
        StudyTime = sopInst.getStudyTime();
    }

    public OhifViewerInputStudy(Study study, Patient patient)
    {
        if (study == null) {
            logger.error("Study is null");
        } else {
            StudyInstanceUID = study.getUid();
            StudyDescription = study.getDescription();
            StudyDate = ViewerUtils.getValidatedDateString(study.getDate());
        }
        if (patient == null) {
            logger.error("Patient is null");
        } else {
            PatientName = patient.getName();
            PatientID = patient.getId();
        }
    }

    public void addSeries(OhifViewerInputSeries series)
    {
        if (series != null) {
            String uid = series.getSeriesInstanceUid();
            if (!seriesMap.containsKey(uid)) {
                seriesMap.put(series.getSeriesInstanceUid(), series);
                seriesList.add(series);
            }
        }
    }

	public String getPatientName()
	{
		return PatientName;
	}

	public List<OhifViewerInputSeries> getSeriesList()
	{
        return ImmutableList.copyOf(seriesMap.values());
	}

	public String getStudyInstanceUid()
	{
		return StudyInstanceUID;
	}

    public OhifViewerInputSeries getSeries(String seriesInstanceUid)
    {
        return seriesMap.get(seriesInstanceUid);
    }
}
