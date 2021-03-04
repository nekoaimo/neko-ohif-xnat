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

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jamesd
 */
public abstract class AbstractRoiCollection implements RoiCollection
{
	private final static Logger logger = LoggerFactory.getLogger(
		AbstractRoiCollection.class);

	private final byte[] rawBytes;
	private String fileExtension = "file";
	private String fileFormat = "";
	private String id;
	private String label = "";
	private String projectId = "";
	private final Set<String> seriesUids = new HashSet<>();
	private String sessionId = "";
	private final Set<String> sopInstUids = new HashSet<>();
	private final Set<String> studyUids = new HashSet<>();
	private String subjectId = "";
	private String typeDescription = "";

	public AbstractRoiCollection(String id, byte[] rawBytes) throws IllegalArgumentException
	{
		if ((id == null) || id.isEmpty())
		{
			throw new IllegalArgumentException(
				"ID must not be null or empty");
		}
		this.id = id;
		if ((rawBytes == null) || (rawBytes.length == 0))
		{
			throw new IllegalArgumentException(
				"Collection bytes must not be null or empty");
		}
		this.rawBytes = Arrays.copyOf(rawBytes, rawBytes.length);
	}

	@Override
	public String getFileExtension()
	{
		return fileExtension;
	}

	@Override
	public String getFileFormat()
	{
		return fileFormat;
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public String getLabel()
	{
		return label;
	}

	@Override
	public String getProjectId()
	{
		return projectId;
	}

	@Override
	public Set<String> getSeriesUids()
	{
		return ImmutableSet.copyOf(seriesUids);
	}

	@Override
	public String getSessionId()
	{
		return sessionId;
	}

	@Override
	public Set<String> getSopInstanceUids()
	{
		return ImmutableSet.copyOf(sopInstUids);
	}

	@Override
	public InputStream getStream()
	{
		return new ByteArrayInputStream(rawBytes);
	}

	@Override
	public Set<String> getStudyUids()
	{
		return ImmutableSet.copyOf(studyUids);
	}

	@Override
	public String getSubjectId()
	{
		return subjectId;
	}

	@Override
	public String getTypeDescription()
	{
		return typeDescription;
	}

	@Override
	public void setId(String id) throws IllegalArgumentException
	{
		if ((id == null) || id.isEmpty())
		{
			throw new IllegalArgumentException(
				"ID must not be null or empty");
		}
		this.id = id;
	}

	@Override
	public void setLabel(String label) throws IllegalArgumentException
	{
		if ((label == null) || label.isEmpty())
		{
			throw new IllegalArgumentException(
				"Label must not be null or empty");
		}
		this.label = label;
	}

	@Override
	public void setProjectId(String projectId) throws IllegalArgumentException
	{
		if ((projectId == null) || projectId.isEmpty())
		{
			throw new IllegalArgumentException(
				"Project ID must not be null or empty");
		}
		this.projectId = projectId;
	}

	@Override
	public void setSessionId(String sessionId) throws IllegalArgumentException
	{
		if ((sessionId == null) || sessionId.isEmpty())
		{
			throw new IllegalArgumentException(
				"Session ID must not be null or empty");
		}
		this.sessionId = sessionId;
	}

	@Override
	public void setSubjectId(String subjectId) throws IllegalArgumentException
	{
		if ((subjectId == null) || subjectId.isEmpty())
		{
			throw new IllegalArgumentException(
				"Subject ID must not be null or empty");
		}
		this.subjectId = subjectId;
	}

	@Override
	public void write(File file) throws IOException
	{
		Files.write(rawBytes, file);
	}

	/**
	 *
	 * @param uid
	 * @return
	 */
	protected final boolean addSeriesUid(String uid)
	{
		if (logger.isDebugEnabled() && !seriesUids.contains(uid))
		{
			logger.debug("Series UID added: {}", uid);
		}
		return seriesUids.add(uid);
	}

	/**
	 *
	 * @param uid
	 * @return
	 */
	protected final boolean addSopInstanceUid(String uid)
	{
		return sopInstUids.add(uid);
	}

	/**
	 *
	 * @param uid
	 * @return
	 */
	protected final boolean addStudyUid(String uid)
	{
		if (logger.isDebugEnabled() && !studyUids.contains(uid))
		{
			logger.debug("Study UID added: {}", uid);
		}
		return studyUids.add(uid);
	}

	/**
	 *
	 * @param extension
	 * @throws IllegalArgumentException
	 */
	protected final void setFileExtension(String extension)
		throws IllegalArgumentException
	{
		if ((extension == null) || extension.isEmpty())
		{
			throw new IllegalArgumentException(
				"File extension must not be null or empty");
		}
		this.fileExtension = extension;
	}

	/**
	 *
	 * @param format
	 * @throws IllegalArgumentException
	 */
	protected final void setFileFormat(String format)
		throws IllegalArgumentException
	{
		if ((format == null) || format.isEmpty())
		{
			throw new IllegalArgumentException(
				"File format must not be null or empty");
		}
		this.fileFormat = format;
	}

	/**
	 *
	 * @param typeDescription
	 * @throws IllegalArgumentException
	 */
	protected final void setTypeDescription(String typeDescription)
		throws IllegalArgumentException
	{
		if ((typeDescription == null) || typeDescription.isEmpty())
		{
			throw new IllegalArgumentException(
				"Type description must not be null or empty");
		}
		this.typeDescription = typeDescription;
	}

}
