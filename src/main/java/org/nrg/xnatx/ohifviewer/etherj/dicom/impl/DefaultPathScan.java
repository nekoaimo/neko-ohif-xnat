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
import org.nrg.xnatx.ohifviewer.etherj.AbstractPathScan;
import org.nrg.xnatx.ohifviewer.etherj.dicom.DicomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Searches paths for DICOM image data
 *
 * @author James d'Arcy
 */
final class DefaultPathScan extends AbstractPathScan<DicomObject>
{
	private static final Logger logger =
		LoggerFactory.getLogger(DefaultPathScan.class);

	/**
	 * Constructor
	 *
	 */
	public DefaultPathScan()
	{
	}

	@Override
	public DicomObject scanFile(File file) throws IOException
	{
		if (!file.isFile() || !file.canRead())
		{
			logger.warn("Not a file or cannot be read: {}", file.getPath());
			return null;
		}

		DicomObject dcm = null;
		try
		{
			dcm = DicomUtils.readDicomFile(file);
		}
		catch (IOException exIO)
		{
			logger.warn("Cannot scan file: "+file.getPath(), exIO);
			throw exIO;
		}
		// Ignore null or presentation state
		if (dcm != null)
		{ 
			String uid = dcm.getString(Tag.SOPClassUID);
			if ((uid == null) || uid.startsWith("1.2.840.10008.5.1.4.1.1.11"))
			{
				return null;
			}
		}

		return dcm;
	}

}
