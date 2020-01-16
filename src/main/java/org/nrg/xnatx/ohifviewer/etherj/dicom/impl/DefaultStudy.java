/*********************************************************************
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
package org.nrg.xnatx.ohifviewer.etherj.dicom.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.nrg.xnatx.ohifviewer.etherj.AbstractDisplayable;
import org.nrg.xnatx.ohifviewer.etherj.dicom.Modality;
import org.nrg.xnatx.ohifviewer.etherj.dicom.Series;
import org.nrg.xnatx.ohifviewer.etherj.dicom.SeriesComparator;
import org.nrg.xnatx.ohifviewer.etherj.dicom.SopInstance;
import org.nrg.xnatx.ohifviewer.etherj.dicom.Study;

/**
 *
 * @author jamesd
 */
final class DefaultStudy extends AbstractDisplayable implements Study
{
	private String accession = "";
	private String date = "";
	private String desc = "";
	private String id = "";
	private long modality = 0;
	private String uid = "";
	private final Map<String,Series> seriesMap = new HashMap<>();

	DefaultStudy(SopInstance sopInst)
	{
		DicomObject dcm = sopInst.getDicomObject();
		String value = dcm.getString(Tag.AccessionNumber);
		if (value != null)
		{
			accession = value;
		}
		date = dcm.getString(Tag.StudyDate);
		value = dcm.getString(Tag.StudyDescription);
		if (value != null)
		{
			desc = value;
		}
		value = dcm.getString(Tag.StudyID);
		if (value != null)
		{
			id = value;
		}
		uid = dcm.getString(Tag.StudyInstanceUID);
	}

	DefaultStudy(String uid)
	{
		this.uid = uid;
	}
	
	@Override
	public Series addSeries(Series series)
	{
		// Update the study's modality bitmask
		modality |= Modality.bitmask(series.getModality());

		return seriesMap.put(series.getUid(), series);
	}

	@Override
	public void display(PrintStream ps, String indent, boolean recurse)
	{
		ps.println(indent+getClass().getName());
		String pad = indent+"  * ";
		ps.println(pad+"Date: "+date);
		ps.println(pad+"Id: "+id);
		ps.println(pad+"Modality: "+getModality());
		ps.println(pad+"Description: "+desc);
		ps.println(pad+"Accession: "+accession);
		ps.println(pad+"Uid: "+uid);
		ps.println(pad+"SeriesList: "+seriesMap.size()+" series");
		List<Series> seriesList = getSeriesList();
		for (Series series : seriesList)
		{
			series.display(ps, indent+"  ");
		}
	}

	@Override
	public String getAccession()
	{
		return accession;
	}

	@Override
	public String getDate()
	{
		return date;
	}

	@Override
	public String getDescription()
	{
		return desc;
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public String getModality()
	{
		return Modality.allStrings(modality);
	}

	@Override
	public Series getSeries(String uid)
	{
		return seriesMap.get(uid);
	}

	@Override
	public int getSeriesCount()
	{
		return seriesMap.size();
	}

	@Override
	public List<Series> getSeriesList()
	{
		List<Series> seriesList = new ArrayList<>();
		Set<Map.Entry<String,Series>> entries = seriesMap.entrySet();
		Iterator<Map.Entry<String,Series>> iter = entries.iterator();
		while (iter.hasNext())
		{
			Map.Entry<String,Series> entry = iter.next();
			seriesList.add(entry.getValue());
		}
		Collections.sort(seriesList, SeriesComparator.Natural);

		return seriesList;
	}

	@Override
	public String getUid()
	{
		return uid;
	}

	@Override
	public boolean hasSeries(String uid)
	{
		return seriesMap.containsKey(uid);
	}

	@Override
	public Series removeSeries(String uid)
	{
		Series removed = seriesMap.remove(uid);
		// Recompute the study's modality bitmask
		modality = 0;
		Set<Entry<String,Series>> entries = seriesMap.entrySet();
		for (Entry<String,Series> entry : entries)
		{
			modality |= Modality.bitmask(entry.getValue().getModality());
		}

		return removed;
	}
	
	@Override
	public void setAccession(String accession)
	{
		this.accession = accession;
	}

	@Override
	public void setDate(String date)
	{
		this.date = date;
	}

	@Override
	public void setDescription(String description)
	{
		this.desc = description;
	}

	@Override
	public void setId(String id)
	{
		this.id = id;
	}

	@Override
	public void setUid(String uid)
	{
		this.uid = uid;
	}

}
