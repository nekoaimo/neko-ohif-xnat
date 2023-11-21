/* ********************************************************************
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
package org.nrg.xnatx.roi.process;

import icr.etherj2.StringUtils;
import org.nrg.xnatx.roi.data.RoiCollection;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;

/**
 *
 * @author jamesd
 */
public class RoiCollectionWriterWrapper implements FileWriterWrapperI
{
	private final RoiCollection roiCollection;

	public RoiCollectionWriterWrapper(RoiCollection roiCollection)
		throws IllegalArgumentException
	{
		if (roiCollection == null)
		{
			throw new IllegalArgumentException("ROI collection must not be null");
		}
		this.roiCollection = roiCollection;
	}

	@Override
	public void delete()
	{}

	@Override
	public InputStream getInputStream() throws IOException
	{
		return roiCollection.getStream();
	}

	@Override
	public String getName()
	{
		// Must not return null or empty. Used to name file. ROI collection uses SeriesDescription as default but this
		// is type 3 and may not exist.
		String name = roiCollection.getName();
		if (StringUtils.isNullOrEmpty(name))
		{
			return "ROI";
		}
		// Remove any characters that are not legal for filenames and spaces. Spaces in filenames are bad
		name = name.replaceAll("[\\\\/:*?\"<>| ]", "");
		return name.isEmpty() ? "ROI" : name;
	}

	@Override
	public String getNestedPath()
	{
		return null;
	}

	@Override
	public UPLOAD_TYPE getType()
	{
		return FileWriterWrapperI.UPLOAD_TYPE.INBODY;
	}

	@Override
	public void write(File file) throws Exception
	{
		Files.copy(roiCollection.getStream(), Paths.get(file.getAbsolutePath()));
	}

}
