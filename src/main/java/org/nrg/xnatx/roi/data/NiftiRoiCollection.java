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

import icr.etherj.StringUtils;
import icr.etherj.Uids;
import icr.etherj.nifti.Nifti;
import icr.etherj.nifti.NiftiReader;
import icr.etherj.nifti.NiftiToolkit;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import icr.xnat.plugin.roi.entity.Roi;
import org.nrg.xnatx.roi.Constants;

/**
 *
 * @author jamesd
 */
public class NiftiRoiCollection extends AbstractRoiCollection
	implements RoiCollection
{
	private final Nifti nifti;
	private final String uid = Uids.generateDicomUid();

	/**
	 *
	 * @param id
	 * @param rawBytes
	 * @throws PluginException
	 */
	public NiftiRoiCollection(String id, byte[] rawBytes) throws PluginException
	{
		super(id, rawBytes);
		setFileExtension("nii.gz");
		setFileFormat("NIFTI");
		setTypeDescription("NIfTI file");
		NiftiReader reader = NiftiToolkit.getToolkit().createReader();
		try
		{
			nifti = reader.read(new ByteArrayInputStream(rawBytes), true);
		}
		catch (IOException ex)
		{
			throw new PluginException(ex.getMessage(), PluginCode.IO, ex);
		}
		catch (IllegalArgumentException ex)
		{
			throw new PluginException(ex.getMessage(), PluginCode.IllegalArgument, ex);
		}
	}

	@Override
	public String getDate()
	{
		return "19000101";
	}

	@Override
	public String getName()
	{
		String name = nifti.getHeader().getDescription();
		if (StringUtils.isNullOrEmpty(name))
		{
			return "NIFTI";
		}
		name = name.trim();
		return StringUtils.isNullOrEmpty(name) ? "NIFTI" : name;
	}

	@Override
	public List<Roi> getRoiList()
	{
		return new ArrayList<>();
	}

	@Override
	public String getTime()
	{
		return "000000";
	}

	@Override
	public String getType()
	{
		return Constants.Nifti;
	}

	@Override
	public String getUid()
	{
		return uid;
	}

	/**
	 *
	 * @param studyUid
	 * @param seriesUid
	 * @param sopInstUids
	 * @throws IllegalArgumentException
	 */
	public void setDicomUids(String studyUid, String seriesUid,
		Set<String> sopInstUids) throws IllegalArgumentException
	{
		if (StringUtils.isNullOrEmpty(studyUid))
		{
			throw new IllegalArgumentException("Study UID must not be null or empty");
		}
		if (StringUtils.isNullOrEmpty(seriesUid))
		{
			throw new IllegalArgumentException("Series UID must not be null or empty");
		}
		if ((sopInstUids == null) || sopInstUids.isEmpty())
		{
			throw new IllegalArgumentException("SOP instance set must not be null or empty");
		}
		addStudyUid(studyUid);
		addSeriesUid(seriesUid);
		for (String uid : sopInstUids)
		{
			addSopInstanceUid(uid);
		}
	}
}
