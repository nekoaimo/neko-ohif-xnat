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

import java.util.List;
import org.nrg.xnatx.ohifviewer.etherj.Displayable;

/**
 * A collection of <code>Patient</code>s.
 * @author jamesd
 */
public interface PatientRoot extends Displayable
{
	/**
	 * Adds a <code>Patient</code> to the root and associates it with its key.
	 * @param patient the patient
	 * @return the patient previously associated with the key, or null
	 */
	public Patient addPatient(Patient patient);

	/**
	 * Returns the <code>Patient</code> associated with the key or null.
	 * @param key the key
	 * @return the patient
	 */
	public Patient getPatient(String key);

	/**
	 * Returns the number of <code>Patient</code>s in the root.
	 * @return the patient count
	 */
	public int getPatientCount();

	/**
	 * Returns the list of <code>Patient</code>s in the root.
	 * @return the list
	 */
	public List<Patient> getPatientList();

	/**
	 * Returns true if the root contains a <code>Patient</code> associated with
	 * the key.
	 * @param key they key
	 * @return true if a patient is associated with the key
	 */
	public boolean hasPatient(String key);

	/**
	 * Removes the <code>Patient</code> associated with the key.
	 * @param key the key
	 * @return the patient previously associated with the key
	 */
	public Patient removePatient(String key);

}
