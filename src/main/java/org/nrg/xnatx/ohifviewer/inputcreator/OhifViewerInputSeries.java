/********************************************************************
* Copyright (c) 2017, Institute of Cancer Research
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
* Java class: OhifViewerInputSeries.java
* First created on Sep 12, 2017 at 11:10:36 AM
*
* Component of OhifViewerInput, which is serialised to JSON by
* CreateOhifViewerInputJson.java
*
*********************************************************************/

package org.nrg.xnatx.ohifviewer.inputcreator;

import org.nrg.xnatx.ohifviewer.ViewerUtils;

import com.google.common.collect.ImmutableList;
import icr.etherj2.dicom.Series;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OhifViewerInputSeries extends OhifViewerInputItem
{
	private static final Logger logger = LoggerFactory.getLogger(
		OhifViewerInputSeries.class);

	private String Modality;
	private String SeriesDate;
	private String SeriesDescription;
	private String SeriesInstanceUID;
	private int SeriesNumber;
	private String SeriesTime;

	private final List<OhifViewerInputInstance> instances = new ArrayList<>();

	public OhifViewerInputSeries(Series ser)
	{
		if (ser == null)
		{
			logger.error("Series is null");
			return;
		}
		SeriesInstanceUID = ser.getUid();
		SeriesDescription = ser.getDescription();
		SeriesNumber = ser.getNumber();
		SeriesDate = ViewerUtils.getValidatedDateString(ser.getDate());;
		String seriesDate = ser.getDate();
		if (seriesDate != null && !seriesDate.trim().isEmpty())
			SeriesDate = seriesDate;
		try
		{
			double seriesTime = ser.getTime();
			if (Double.isNaN(seriesTime))
				seriesTime = 0.0;
			SeriesTime = String.format("%013.6f", ser.getTime());
		}
		catch (Exception e)
		{
			SeriesTime = "00000";
		}
		Modality = ser.getModality();
	}

	public void addInstances(OhifViewerInputInstance instance)
	{
		instances.add(instance);
	}

	public List<OhifViewerInputInstance> getInstances()
	{
		return ImmutableList.copyOf(instances);
	}

	public String getSeriesDescription()
	{
		return SeriesDescription;
	}

	public String getSeriesInstanceUid()
	{
		return SeriesInstanceUID;
	}

	public int getSeriesNumber()
	{
		return SeriesNumber;
	}

}
