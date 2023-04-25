/*********************************************************************
 * Copyright (c) 2018, Institute of Cancer Research
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
package org.nrg.xnatx.roi.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import icr.xnat.plugin.roi.entity.Roi;

/**
 *
 * @author jamesd
 */
public interface RoiCollection
{
	/**
	 *
	 * @return
	 */
	public String getDate();

	/**
	 *
	 * @return
	 */
	public String getFileExtension();

	/**
	 *
	 * @return
	 */
	public String getFileFormat();

	/**
	 *
	 * @return
	 */
	public String getId();

	/**
	 *
	 * @return
	 */
	public String getLabel();

	/**
	 * Returns the name of the collection, must not return null or empty.
	 * @return
	 */
	public String getName();

	/**
	 *
	 * @return
	 */
	public String getProjectId();

	/**
	 *
	 * @return
	 */
	public List<Roi> getRoiList();

	/**
	 *
	 * @return
	 */
	public Set<String> getSeriesUids();

	/**
	 *
	 * @return
	 */
	public String getSessionId();

	/**
	 *
	 * @return
	 */
	public Set<String> getSopInstanceUids();

	/**
	 *
	 * @return
	 */
	public InputStream getStream();

	/**
	 *
	 * @return
	 */
	public Set<String> getStudyUids();

	/**
	 *
	 * @return
	 */
	public String getSubjectId();

	/**
	 *
	 * @return
	 */
	public String getTime();
	
	/**
	 *
	 * @return
	 */
	public String getType();
	
	/**
	 *
	 * @return
	 */
	public String getTypeDescription();

	/**
	 *
	 * @return
	 */
	public String getUid();

	/**
	 *
	 * @param id
	 * @throws IllegalArgumentException
	 */
	public void setId(String id) throws IllegalArgumentException;

	/**
	 *
	 * @param label
	 * @throws IllegalArgumentException
	 */
	public void setLabel(String label) throws IllegalArgumentException;

	/**
	 *
	 * @param projectId
	 */
	public void setProjectId(String projectId) throws IllegalArgumentException;

	/**
	 *
	 * @param sessionId
	 */
	public void setSessionId(String sessionId) throws IllegalArgumentException;

	/**
	 *
	 * @param subjectId
	 */
	public void setSubjectId(String subjectId) throws IllegalArgumentException;

	/**
	 *
	 * @param file
	 * @throws java.io.IOException
	 */
	public void write(File file) throws IOException;
}
