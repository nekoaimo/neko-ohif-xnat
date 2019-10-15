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
import org.nrg.xnatx.ohifviewer.etherj.AbstractDisplayable;
import org.nrg.xnatx.ohifviewer.etherj.dicom.Patient;
import org.nrg.xnatx.ohifviewer.etherj.dicom.PatientComparator;
import org.nrg.xnatx.ohifviewer.etherj.dicom.PatientRoot;
import org.nrg.xnatx.ohifviewer.etherj.dicom.DicomUtils;

/**
 *
 * @author jamesd
 */
final class DefaultPatientRoot extends AbstractDisplayable implements PatientRoot
{
	private final Map<String,Patient> patientMap = new HashMap<>();

	@Override
	public Patient addPatient(Patient patient)
	{
		return patientMap.put(DicomUtils.makePatientKey(patient), patient);
	}

	@Override
	public void display(PrintStream ps, String indent, boolean recurse)
	{
		ps.println(indent+getClass().getName());
		String pad = indent+"  * ";
		int nPatients = patientMap.size();
		ps.println(pad+"PatientList: "+nPatients+" patient"+
			(nPatients == 1 ? "" : "s"));
		if (recurse)
		{
			List<Patient> patientList = getPatientList();
			for (Patient patient : patientList)
			{
				patient.display(ps, indent+"  ", true);
			}
		}
	}

	@Override
	public Patient getPatient(String key)
	{
		return patientMap.get(key);
	}

	@Override
	public int getPatientCount()
	{
		return patientMap.size();
	}

	@Override
	public List<Patient> getPatientList()
	{
		List<Patient> patientList = new ArrayList<>();
		Set<Map.Entry<String,Patient>> entries = patientMap.entrySet();
		Iterator<Map.Entry<String,Patient>> iter = entries.iterator();
		while (iter.hasNext())
		{
			Map.Entry<String,Patient> entry = iter.next();
			patientList.add(entry.getValue());
		}
		Collections.sort(patientList, PatientComparator.Natural);
		return patientList;
	}

	@Override
	public boolean hasPatient(String key)
	{
		return patientMap.containsKey(key);
	}

	@Override
	public Patient removePatient(String key)
	{
		return patientMap.remove(key);
	}
	
}
