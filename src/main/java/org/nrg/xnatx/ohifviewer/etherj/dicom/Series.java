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
 * A series from the DICOM real world model.
 * @author jamesd
 */
public interface Series extends Displayable
{
	/**
	 * 
	 * @param sopInstance
	 * @return 
	 */
	public SopInstance addSopInstance(SopInstance sopInstance);

	/**
	 *
	 */
	public void compact();

	/**
	 *
	 * @return
	 */
	public String getDate();

	/**
	 *
	 * @return
	 */
	public String getDescription();

	/**
	 *
	 * @return
	 */
	public String getModality();

	/**
	 *
	 * @return
	 */
	public int getNumber();

	/**
	 *
	 * @param uid
	 * @return
	 */
	public SopInstance getSopInstance(String uid);

	/**
	 *
	 * @return
	 */
	public int getSopInstanceCount();

	/**
	 *
	 * @return
	 */
	public List<SopInstance> getSopInstanceList();

	/**
	 *
	 * @return
	 */
	public String getStudyUid();

	/**
	 *
	 * @return
	 */
	public double getTime();

	/**
	 *
	 * @return
	 */
	public String getUid();

	/**
	 *
	 * @param uid
	 * @return
	 */
	public boolean hasSopInstance(String uid);

	/**
	 *
	 * @param uid
	 * @return
	 */
	public SopInstance removeSopInstance(String uid);

	/**
	 *
	 * @param description
	 */
	public void setDescription(String description);

	/**
	 *
	 * @param modality
	 */
	public void setModality(String modality);

	/**
	 *
	 * @param number
	 */
	public void setNumber(int number);

	/**
	 *
	 * @param studyUid
	 */
	public void setStudyUid(String studyUid);

	/**
	 *
	 * @param time
	 */
	public void setTime(double time);
	/**
	 *
	 * @param uid
	 */
	public void setUid(String uid);
}
