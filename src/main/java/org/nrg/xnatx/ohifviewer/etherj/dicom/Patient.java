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
 * A patient for the DICOM real world model.
 * @author jamesd
 */
public interface Patient extends Displayable
{
	/**
	 * Adds a <code>Study</code> to the patient and associates it with its UID.
	 * @param study the study
	 * @return the previous study associated with the study UID, or null
	 */
	public Study addStudy(Study study);

	/**
	 * Returns the birth date.
	 * @return the birth date
	 */
	public String getBirthDate();

	/**
	 * Returns the comments.
	 * @return the comments
	 */
	public String getComments();

	/**
	 * Returns the ID.
	 * @return the ID
	 */
	public String getId();

	/**
	 * Returns the name.
	 * @return the name
	 */
	public String getName();

	/**
	 * Returns the other ID.
	 * @return the other ID
	 */
	public String getOtherId();

	/**
	 * Returns the <code>Study</code> associated with the UID.
	 * @param uid the UID
	 * @return the study
	 */
	public Study getStudy(String uid);

	/**
	 * Returns the number of studies in the patient.
	 * @return the study count
	 */
	public int getStudyCount();

	/**
	 * Returns a list of studies in the patient.
	 * @return the list
	 */
	public List<Study> getStudyList();

	/**
	 * Returns true if there is a <code>Study</code> associated with the UID.
	 * @param uid the UID
	 * @return true if a study exists
	 */
	public boolean hasStudy(String uid);

	/**
	 * Removes the <code>Study</code> associated with the UID.
	 * @param uid the UID
	 * @return the study previously associated with the UID
	 */
	public Study removeStudy(String uid);

	/**
	 * Sets the birth date.
	 * @param birthDate the birth date to set
	 */
	public void setBirthDate(String birthDate);

	/**
	 * Sets the comments.
	 * @param comments the comments to set
	 */
	public void setComments(String comments);

	/**
	 * Sets the ID.
	 * @param id the ID to set
	 */
	public void setId(String id);

	/**
	 * Sets the name.
	 * @param name the name to set
	 */
	public void setName(String name);

	/**
	 * Sets the other ID.
	 * @param otherId the other ID to set
	 */
	public void setOtherId(String otherId);
}
