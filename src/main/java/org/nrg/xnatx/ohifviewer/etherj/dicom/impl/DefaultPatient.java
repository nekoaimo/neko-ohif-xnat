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
import org.nrg.xnatx.ohifviewer.etherj.AbstractDisplayable;
import org.nrg.xnatx.ohifviewer.etherj.dicom.Patient;
import org.nrg.xnatx.ohifviewer.etherj.dicom.Study;
import org.nrg.xnatx.ohifviewer.etherj.dicom.StudyComparator;

/**
 *
 * @author jamesd
 */
final class DefaultPatient extends AbstractDisplayable implements Patient
{
	private String birthDate;
	private String comments = "";
	private String id;
	private String name;
	private String otherId = "";
	private final Map<String,Study> studyMap = new HashMap<>();

	DefaultPatient(String name, String birthDate, String id)
	{
		this.name = (name == null) ? "" : name;
		this.birthDate = ((birthDate == null) || birthDate.isEmpty()) ? 
			"00000000" : birthDate;
		this.id = (id == null) ? "" : id;
	}

	@Override
	public Study addStudy(Study study)
	{
		return studyMap.put(study.getUid(), study);
	}

	@Override
	public void display(PrintStream ps, String indent, boolean recurse)
	{
		ps.println(indent+getClass().getName());
		String pad = indent+"  * ";
		ps.println(pad+"Name: "+name);
		ps.println(pad+"BirthDate: "+birthDate);
		ps.println(pad+"Id: "+id);
		ps.println(pad+"OtherId: "+otherId);
		ps.println(pad+"Comments: "+comments);
		int nStudies = studyMap.size();
		ps.println(pad+"StudyList: "+nStudies+" stud"+
			(nStudies != 1 ? "ies" : "y"));
		if (recurse)
		{
			List<Study> studyList = getStudyList();
			for (Study study : studyList)
			{
				study.display(ps, indent+"  ", true);
			}
		}
	}

	@Override
	public String getBirthDate()
	{
		return birthDate;
	}

	@Override
	public String getComments()
	{
		return comments;
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getOtherId()
	{
		return otherId;
	}

	@Override
	public Study getStudy(String uid)
	{
		return studyMap.get(uid);
	}

	@Override
	public int getStudyCount()
	{
		return studyMap.size();
	}

	@Override
	public List<Study> getStudyList()
	{
		List<Study> studyList = new ArrayList<>();
		Set<Entry<String,Study>> entries = studyMap.entrySet();
		Iterator<Entry<String,Study>> iter = entries.iterator();
		while (iter.hasNext())
		{
			Entry<String,Study> entry = iter.next();
			studyList.add(entry.getValue());
		}
		Collections.sort(studyList, StudyComparator.Natural);
		return studyList;
	}

	@Override
	public boolean hasStudy(String uid)
	{
		return studyMap.containsKey(uid);
	}

	@Override
	public Study removeStudy(String uid)
	{
		return studyMap.remove(uid);
	}

	@Override
	public void setBirthDate(String birthDate)
	{
		this.birthDate = (birthDate == null) ? "" : birthDate;
	}

	@Override
	public void setComments(String comments)
	{
		this.comments = (comments == null) ? "" : comments;
	}

	@Override
	public void setId(String id)
	{
		this.id = (id == null) ? "" : id;
	}

	@Override
	public void setName(String name)
	{
		this.name = (name == null) ? "" : name;
	}

	@Override
	public void setOtherId(String otherId)
	{
		this.otherId = (otherId == null) ? "" : otherId;
	}

}
