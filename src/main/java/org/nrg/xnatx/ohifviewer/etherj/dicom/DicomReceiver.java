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
package org.nrg.xnatx.ohifviewer.etherj.dicom;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dcm4che2.data.DicomObject;
import org.nrg.xnatx.ohifviewer.etherj.PathScanContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>PathScanContext</code> for <code>DicomObject</code>s.
 * @author jamesd
 */
public class DicomReceiver implements PathScanContext<DicomObject>
{
	private static final Logger logger =
		LoggerFactory.getLogger(DicomReceiver.class);
	private Map<String,Patient> patientMap = null;
	private Map<String,SopInstance> sopInstMap;
	private List<Duplicate> duplicates;
	private final DicomToolkit toolkit = DicomToolkit.getToolkit();

	/**
	 * Returns the list of <code>PatientRoot</code>s containing duplicate
	 * <code>SopInstance</code>s found during path scan.
	 * @return the list
	 */
	public List<PatientRoot> getDuplicates()
	{
		List<PatientRoot> dupeList = new ArrayList<>();
		for (Duplicate dupe : duplicates)
		{
			dupeList.add(createRoot(dupe.patientMap));
		}
		return dupeList;
	}

	/**
	 * Returns the <code>PatientRoot</code> found during path scan.
	 * @return the <code>PatientRoot</code>
	 */
	public PatientRoot getPatientRoot()
	{
		return createRoot(patientMap);
	}

	@Override
	public void notifyItemFound(File file, DicomObject dcm)
	{
		processSopInst(toolkit.createSopInstance(file, dcm));
	}

	@Override
	public void notifyScanFinish()
	{ /* Deliberate no-op */ }

	@Override
	public void notifyScanStart()
	{
		patientMap = new HashMap<>();
		sopInstMap = new HashMap<>();
		duplicates = new ArrayList<>();
	}

	private PatientRoot createRoot(Map<String,Patient> patientMap)
	{
		PatientRoot root = toolkit.createPatientRoot();
		if (patientMap == null)
		{
			return root;
		}
		Set<Map.Entry<String,Patient>> entries = patientMap.entrySet();
		Iterator<Map.Entry<String,Patient>> iter = entries.iterator();
		while (iter.hasNext())
		{
			Map.Entry<String,Patient> entry = iter.next();
			root.addPatient(entry.getValue());
		}
		return root;
	}

	private Duplicate findDuplicate(SopInstance sopInst)
	{
		Duplicate dupe;
		if (duplicates.isEmpty())
		{
			dupe = new Duplicate();
			duplicates.add(dupe);
			return dupe;
		}
		String uid = sopInst.getUid();
		// Search for a duplicate that doesn't contain the SOP instance already
		for (Duplicate duplicate : duplicates)
		{
			if (duplicate.sopInstMap.containsKey(uid))
			{
				return duplicate;
			}
		}
		// Create a new duplicate as all existing ones have been checked
		dupe = new Duplicate();
		duplicates.add(dupe);

		return dupe;
	}

	private Patient findPatient(Map<String, Patient> patientMap,
		SopInstance sopInst)
	{
		String key = DicomUtils.makePatientKey(sopInst);
		if (!patientMap.containsKey(key))
		{
			Patient patient = toolkit.createPatient(sopInst);
			patientMap.put(key, patient);
			logger.trace("New patient. Name: {}, ID: ", patient.getName(),
				patient.getId());
		}
		return patientMap.get(key);
	}

	private Series findSeries(Study study, SopInstance sopInst)
	{
		String uid = sopInst.getSeriesUid();
		if (!study.hasSeries(uid))
		{
			Series series = toolkit.createSeries(sopInst);
			study.addSeries(series);
			logger.trace("New series. Number: {}, Description: {}", 
				series.getNumber(), series.getDescription());
		}
		return study.getSeries(uid);
	}

	private Study findStudy(Patient patient, SopInstance sopInst)
	{
		String uid = sopInst.getStudyUid();
		if (!patient.hasStudy(uid))
		{
			Study study = toolkit.createStudy(sopInst);
			patient.addStudy(study);
			logger.trace("New study. ID: {}, Description: ", study.getId(),
				study.getDescription());
		}
		return patient.getStudy(uid);
	}

	private void processSopInst(SopInstance sopInst)
	{
		String uid = sopInst.getUid();
		Map<String,Patient> patMap = patientMap;
		Map<String,SopInstance> siMap = sopInstMap;
		if (sopInstMap.containsKey(uid))
		{
			Duplicate dupe = findDuplicate(sopInst);
			patMap = dupe.patientMap;
			siMap = dupe.sopInstMap;
		}
		Patient patient = findPatient(patMap, sopInst);
		Study study = findStudy(patient, sopInst);
		Series series = findSeries(study, sopInst);
		series.addSopInstance(sopInst);
		siMap.put(uid, sopInst);
	}

	private class Duplicate
	{
		public Map<String,Patient> patientMap = new HashMap<>();
		public Map<String,SopInstance> sopInstMap = new HashMap<>();
	}
}
