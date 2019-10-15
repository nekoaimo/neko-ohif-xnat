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
import java.util.Set;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.nrg.xnatx.ohifviewer.etherj.AbstractDisplayable;
import org.nrg.xnatx.ohifviewer.etherj.dicom.Series;
import org.nrg.xnatx.ohifviewer.etherj.dicom.SopInstance;
import org.nrg.xnatx.ohifviewer.etherj.dicom.DicomUtils;
import org.nrg.xnatx.ohifviewer.etherj.dicom.SopInstanceComparator;

/**
 *
 * @author jamesd
 */
final class DefaultSeries extends AbstractDisplayable implements Series
{
	private String date = "";
	private String desc = "";
	private String modality = "";
	private int number = 0;
	private String studyUid = "";
	private double time = 0;
	private String uid = "";
	private final Map<String,SopInstance> sopInstMap = new HashMap<>();

	DefaultSeries(SopInstance sopInst)
	{
		DicomObject dcm = sopInst.getDicomObject();
		date = dcm.getString(Tag.SeriesDate);
		String value = dcm.getString(Tag.SeriesDescription);
		desc = (value == null) ? "" : value;
		modality = dcm.getString(Tag.Modality);
		number = dcm.getInt(Tag.SeriesNumber);
		studyUid = dcm.getString(Tag.StudyInstanceUID);
		try
		{
			time = DicomUtils.tmToSeconds(dcm.getString(Tag.SeriesTime));
		}
		catch (NumberFormatException exIgnore)
		{
			time = 0;
		}
		uid = dcm.getString(Tag.SeriesInstanceUID);
	}

	DefaultSeries(String uid)
	{
		this.uid = uid;
	}

	@Override
	public SopInstance addSopInstance(SopInstance sopInstance)
	{
		return sopInstMap.put(sopInstance.getUid(), sopInstance);
	}

	@Override
	public void compact()
	{
		for (SopInstance sopInst : sopInstMap.values())
		{
			sopInst.compact();
		}
	}

	@Override
	public void display(PrintStream ps, String indent, boolean recurse)
	{
		ps.println(indent+getClass().getName());
		String pad = indent+"  * ";
		ps.println(pad+"Number: "+number);
		ps.println(pad+"Modality: "+modality);
		ps.println(pad+"Description: "+desc);
		ps.println(pad+"Date: "+date);
		ps.println(pad+"Time: "+DicomUtils.secondsToTm(time));
		ps.println(pad+"Uid: "+uid);
		ps.println(pad+"StudyUid: "+studyUid);
		int nInstances = sopInstMap.size();
		ps.println(pad+"InstanceList: "+nInstances+" SOP instance"+
			((nInstances != 1) ? "s" : ""));
		if (recurse)
		{
			for (SopInstance sopInst : sopInstMap.values())
			{
				sopInst.display(ps, indent+"  ");
			}
		}
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
	public String getModality()
	{
		return modality;
	}

	@Override
	public int getNumber()
	{
		return number;
	}

	@Override
	public SopInstance getSopInstance(String uid)
	{
		return sopInstMap.get(uid);
	}

	@Override
	public int getSopInstanceCount()
	{
		return sopInstMap.size();
	}

	@Override
	public List<SopInstance> getSopInstanceList()
	{
		List<SopInstance> sopInstList = new ArrayList<>();
		Set<Map.Entry<String,SopInstance>> entries = sopInstMap.entrySet();
		Iterator<Map.Entry<String,SopInstance>> iter = entries.iterator();
		while (iter.hasNext())
		{
			Map.Entry<String,SopInstance> entry = iter.next();
			sopInstList.add(entry.getValue());
		}
		Collections.sort(sopInstList, SopInstanceComparator.Natural);
		return sopInstList;
	}

	@Override
	public String getStudyUid()
	{
		return studyUid;
	}

	@Override
	public double getTime()
	{
		return time;
	}

	@Override
	public String getUid()
	{
		return uid;
	}

	@Override
	public boolean hasSopInstance(String uid)
	{
		return sopInstMap.containsKey(uid);
	}

	@Override
	public SopInstance removeSopInstance(String uid)
	{
		return sopInstMap.remove(uid);
	}
	
	@Override
	public void setDescription(String description)
	{
		this.desc = (description == null) ? "" : description;
	}

	@Override
	public void setModality(String modality)
	{
		if ((modality == null) || modality.isEmpty())
		{
			throw new IllegalArgumentException("Invalid modality: "+modality);
		}
		this.modality = modality;
	}

	@Override
	public void setNumber(int number)
	{
		this.number = number;
	}

	@Override
	public void setStudyUid(String uid)
	{
		if ((uid == null) || uid.isEmpty())
		{
			throw new IllegalArgumentException("Invalid study UID: "+uid);
		}
		studyUid = uid;
	}

	@Override
	public void setTime(double time)
	{
		this.time = time;
	}

	@Override
	public void setUid(String uid)
	{
		if ((uid == null) || uid.isEmpty())
		{
			throw new IllegalArgumentException("Invalid series UID: "+uid);
		}
		this.uid = uid;
	}

}
