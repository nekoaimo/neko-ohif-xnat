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

import java.util.Comparator;
import java.util.Objects;

/**
 *
 * @author jamesd
 */
public class StudyUidContainer implements Comparable<StudyUidContainer>
{
	private String projectId = "";
	private String subjectId = "";
	private String subjectLabel = "";
	private String sessionId = "";
	private String sessionLabel = "";
	private String xsiType = "";

	/**
	 *
	 */
	public StudyUidContainer()
	{}

	/**
	 *
	 * @param projectId
	 * @param subjectId
	 * @param subjectLabel
	 * @param sessionId
	 * @param sessionLabel
	 * @param xsiType
	 * @throws IllegalArgumentException
	 */
	public StudyUidContainer(String projectId, String subjectId,
		String subjectLabel, String sessionId, String sessionLabel, String xsiType)
		throws IllegalArgumentException
	{
		checkValid(projectId, "Project ID");
		checkValid(subjectId, "Subject ID");
		checkValid(subjectLabel, "Subject Label");
		checkValid(sessionId, "Session ID");
		checkValid(sessionLabel, "Session Label");
		checkValid(xsiType, "XSI Type");
		this.projectId = projectId;
		this.subjectId = subjectId;
		this.subjectLabel = subjectLabel;
		this.sessionId = sessionId;
		this.sessionLabel = sessionLabel;
		this.xsiType = xsiType;
	}

	@Override
	public int compareTo(StudyUidContainer other)
	{
		if (other.equals(this))
		{
			return 0;
		}
		int value = projectId.compareTo(other.projectId);
		if (value != 0)
		{
			return value;
		}
		value = subjectId.compareTo(other.subjectId);
		if (value != 0)
		{
			return value;
		}
		return sessionId.compareTo(other.sessionId);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final StudyUidContainer other = (StudyUidContainer) obj;
		return Objects.equals(this.projectId, other.projectId) &&
			Objects.equals(this.subjectId, other.subjectId) &&
			Objects.equals(this.sessionId, other.sessionId) &&
			Objects.equals(this.subjectLabel, other.subjectLabel) &&
			Objects.equals(this.sessionLabel, other.sessionLabel) &&
			Objects.equals(this.xsiType, other.xsiType);
	}

	/**
	 * @return the projectId
	 */
	public String getProjectId()
	{
		return projectId;
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId()
	{
		return sessionId;
	}

	/**
	 * @return the sessionLabel
	 */
	public String getSessionLabel()
	{
		return sessionLabel;
	}

	/**
	 * @return the subjectId
	 */
	public String getSubjectId()
	{
		return subjectId;
	}

	/**
	 * @return the subjectLabel
	 */
	public String getSubjectLabel()
	{
		return subjectLabel;
	}

	/**
	 * @return the xsiType
	 */
	public String getXsiType()
	{
		return xsiType;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(projectId, subjectId, sessionId, subjectLabel,
			sessionLabel, xsiType);
	}

	/**
	 * @param projectId the projectId to set
	 * @throws IllegalArgumentException
	 */
	public void setProjectId(String projectId) throws IllegalArgumentException
	{
		checkValid(projectId, "Project ID");
		this.projectId = projectId;
	}

	/**
	 * @param sessionId the sessionId to set
	 * @throws IllegalArgumentException
	 */
	public void setSessionId(String sessionId) throws IllegalArgumentException
	{
		checkValid(sessionId, "Session ID");
		this.sessionId = sessionId;
	}

	/**
	 * @param sessionLabel the sessionLabel to set
	 * @throws IllegalArgumentException
	 */
	public void setSessionLabel(String sessionLabel)
		throws IllegalArgumentException
	{
		checkValid(sessionLabel, "Session Label");
		this.sessionLabel = sessionLabel;
	}

	/**
	 * @param subjectId the subjectId to set
	 * @throws IllegalArgumentException
	 */
	public void setSubjectId(String subjectId) throws IllegalArgumentException
	{
		checkValid(subjectId, "Subject ID");
		this.subjectId = subjectId;
	}

	/**
	 * @param subjectLabel the subjectLabel to set
	 * @throws IllegalArgumentException
	 */
	public void setSubjectLabel(String subjectLabel)
		throws IllegalArgumentException
	{
		checkValid(subjectLabel, "Subject Label");
		this.subjectLabel = subjectLabel;
	}

	/**
	 * @param xsiType the xsiType to set
	 * @throws IllegalArgumentException
	 */
	public void setXsiType(String xsiType)
		throws IllegalArgumentException
	{
		checkValid(xsiType, "XSI Type");
		this.xsiType = xsiType;
	}

	private void checkValid(String value, String name)
		throws IllegalArgumentException
	{
		if ((value == null) || value.isEmpty())
		{
			throw new IllegalArgumentException(name+" null or empty");
		}
	}

	/**
	 *
	 */
	public static class LabelComparator implements Comparator<StudyUidContainer>
	{
		@Override
		public int compare(StudyUidContainer a, StudyUidContainer b)
		{
			if (a.equals(b))
			{
				return 0;
			}
			int value = a.projectId.compareTo(b.projectId);
			if (value != 0)
			{
				return value;
			}
			value = a.subjectLabel.compareTo(b.subjectLabel);
			if (value != 0)
			{
				return value;
			}
			return a.sessionLabel.compareTo(b.sessionLabel);
		}
	}

}

