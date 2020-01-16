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

import java.io.File;
import java.io.IOException;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.nrg.xnatx.ohifviewer.etherj.PathScan;
import org.nrg.xnatx.ohifviewer.etherj.dicom.DicomToolkit;
import org.nrg.xnatx.ohifviewer.etherj.dicom.DicomUtils;
import org.nrg.xnatx.ohifviewer.etherj.dicom.Patient;
import org.nrg.xnatx.ohifviewer.etherj.dicom.PatientRoot;
import org.nrg.xnatx.ohifviewer.etherj.dicom.Series;
import org.nrg.xnatx.ohifviewer.etherj.dicom.SopInstance;
import org.nrg.xnatx.ohifviewer.etherj.dicom.Study;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jamesd
 */
public class DefaultDicomToolkit extends DicomToolkit
{
	private static final Logger logger =
		LoggerFactory.getLogger(DefaultDicomToolkit.class);

	@Override
	public PathScan<DicomObject> createPathScan()
	{
		return new DefaultPathScan();
	}

	@Override
	public Patient createPatient(SopInstance sopInst)
	{
		DicomObject dcm = sopInst.getDicomObject();
		String name = dcm.getString(Tag.PatientName);
		name = (name == null) ? "" : name.replace(' ', '_');
		String birthDate = dcm.getString(Tag.PatientBirthDate);
		String id = dcm.getString(Tag.PatientID);
		Patient patient = new DefaultPatient(name, birthDate, id);
		patient.setOtherId(dcm.getString(Tag.OtherPatientIDs));
		String comments = dcm.getString(Tag.PatientComments);
		patient.setComments((comments == null) ? "" : comments);

		return patient;
	}

	@Override
	public Patient createPatient(String name, String birthDate, String id)
	{
		return new DefaultPatient(name, birthDate, id);
	}

	@Override
	public PatientRoot createPatientRoot()
	{
		return new DefaultPatientRoot();
	}

	@Override
	public Series createSeries(SopInstance sopInst)
	{
		return new DefaultSeries(sopInst);
	}

	@Override
	public Series createSeries(String uid)
	{
		return new DefaultSeries(uid);
	}

	@Override
	public SopInstance createSopInstance(File file)
	{
		SopInstance sopInst = null;
		try
		{
			DicomObject dcm = DicomUtils.readDicomFile(file);
			return createSopInstance(file, dcm);
		}
		catch (IOException ex)
		{
			logger.warn("Error reading file: "+file.getPath(), ex);
		}
		return sopInst;
	}

	@Override
	public SopInstance createSopInstance(File file, DicomObject dcm)
	{
		return new DefaultSopInstance(file, dcm);
	}

	@Override
	public SopInstance createSopInstance(String path)
	{
		return createSopInstance(new File(path));
	}

	@Override
	public Study createStudy(SopInstance sopInst)
	{
		return new DefaultStudy(sopInst);
	}

	@Override
	public Study createStudy(String uid)
	{
		return new DefaultStudy(uid);
	}

}
