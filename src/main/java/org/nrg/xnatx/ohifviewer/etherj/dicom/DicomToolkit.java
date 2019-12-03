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
import java.util.HashMap;
import java.util.Map;
import org.dcm4che2.data.DicomObject;
import org.nrg.xnatx.ohifviewer.etherj.PathScan;
import org.nrg.xnatx.ohifviewer.etherj.dicom.impl.DefaultDicomToolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory and factory locator class for <code>etherj.dicom</code> package.
 * @author jamesd
 */
public abstract class DicomToolkit
{
	/** Key for default toolkit. */
	public static final String Default = "default";

	private static final Logger logger = LoggerFactory.getLogger(DicomToolkit.class);
	private static final Map<String,DicomToolkit> toolkitMap = new HashMap<>();

	static
	{
		toolkitMap.put(Default, new DefaultDicomToolkit());
	}

	/**
	 * Returns the default toolkit, equivalent to <code>getToolkit(Default)</code>.
	 * @return the toolkit
	 */
	public static DicomToolkit getToolkit()
	{
		return getToolkit(Default);
	}

	/**
	 * Returns the toolkit associated with the supplied key or null.
	 * @param key the key
	 * @return the toolkit
	 */
	public static DicomToolkit getToolkit(String key)
	{
		return toolkitMap.get(key);
	}

	/**
	 * Associates the <code>DicomToolkit</code> with the key.
	 * @param key the key
	 * @param toolkit the toolkit
	 * @return the previous toolkit associated with the key or null
	 */
	public static DicomToolkit setToolkit(String key, DicomToolkit toolkit)
	{
		DicomToolkit tk = toolkitMap.put(key, toolkit);
		logger.info(toolkit.getClass().getName()+" set with key '"+key+"'");
		return tk;
	}

	/**
	 * Returns a new <code>PathScan</code> for <code>DicomObject</code>s.
	 * @return the path scanner
	 */
	public abstract PathScan<DicomObject> createPathScan();

	/**
	 * Returns a new <code>Patient</code> created from the supplied
	 * <code>SopInstance</code>.
	 * @param sopInst the SOP instance
	 * @return the patient
	 */
	public abstract Patient createPatient(SopInstance sopInst);

	/**
	 * Returns a new <code>Patient</code> with the supplied name, birth date and
	 * ID.
	 * @param name the name
	 * @param birthDate the birth date
	 * @param id the ID
	 * @return the patient
	 */
	public abstract Patient createPatient(String name, String birthDate, String id);

	/**
	 * Returns a new <code>PatientRoot</code>.
	 * @return the patient root
	 */
	public abstract PatientRoot createPatientRoot();

	/**
	 * Returns a new <code>Series</code> from the supplied <code>SopInstance</code>.
	 * @param sopInstance the SOP instance
	 * @return the series
	 */
	public abstract Series createSeries(SopInstance sopInstance);

	/**
	 * Returns a new <code>Series</code> from the supplied UID.
	 * @param uid the UID
	 * @return the series
	 */
	public abstract Series createSeries(String uid);

	/**
	 * Returns a new <code>SopInstance</code> from the file at the supplied path.
	 * @param path the path
	 * @return the SOP instance
	 */
	public abstract SopInstance createSopInstance(String path);

	/**
	 * Returns a new <code>SopInstance</code> from the supplied <code>File</code>.
	 * @param file the file
	 * @return the SOP instance
	 */
	public abstract SopInstance createSopInstance(File file);

	/**
	 * Returns a new <code>SopInstance</code> from the supplied <code>File</code>
	 * and <code>DicomObject</code>.
	 * @param file the file
	 * @param dcm the DICOM object
	 * @return the SOP instance
	 */
	public abstract SopInstance createSopInstance(File file, DicomObject dcm);

	/**
	 * Returns a new <code>Study</code> from the supplied <code>SopInstance</code>.
	 * @param sopInstance the SOP instance
	 * @return the study
	 */
	public abstract Study createStudy(SopInstance sopInstance);

	/**
	 * Returns a new <code>Study</code> from the supplied UID.
	 * @param uid the UID
	 * @return the study
	 */
	public abstract Study createStudy(String uid);

	/*
	 *	Protected constructor to prevent direct instantiation
	 */
	protected DicomToolkit()
	{}

}
