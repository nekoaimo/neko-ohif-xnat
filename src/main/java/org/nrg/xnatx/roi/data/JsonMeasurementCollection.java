/*********************************************************************
 * Copyright (c) 2022, Institute of Cancer Research
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

import icr.etherj2.JsonException;
import icr.etherj2.StringUtils;
import icr.etherj2.aim.AimUtils;
import icr.etherj2.meas.*;
import icr.xnat.plugin.roi.entity.Roi;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.nrg.xnatx.roi.Constants;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author mo.alsad
 */
public class JsonMeasurementCollection extends AbstractRoiCollection implements RoiCollection
{

	private final MeasurementCollection imc;

	public JsonMeasurementCollection(String id, byte[] rawBytes) throws PluginException
	{
		super(id, rawBytes);
		setFileExtension("json");
		setFileFormat("JSON");
		setTypeDescription("Measurement Collection");
		DefaultJsonParser parser = new DefaultJsonParser();
		try
		{
			imc = parser.parse(new ByteArrayInputStream(rawBytes));
			buildUidSets();
		}
		catch (JsonException ex)
		{
			throw new PluginException(ex.getMessage(), PluginCode.JSON, ex);
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

	/**
	 * @return
	 */
	@Override
	public String getDate()
	{
		Date dt = AimUtils.parseDateTime(imc.getCreated());
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format(dt);
	}

	public MeasurementCollection getMeasurementCollection()
	{
		return imc;
	}

	/**
	 * @return
	 */
	@Override
	public String getName()
	{
		String name = imc.getName();
		if (StringUtils.isNullOrEmpty(name))
		{
			return "MEAS";
		}
		name = name.trim();
		return StringUtils.isNullOrEmpty(name) ? "MEAS" : name;
	}

	/**
	 * @return
	 */
	@Override
	public List<Roi> getRoiList()
	{
		List<Roi> roiList = new ArrayList<>();
		List<ImageMeasurement> imList = imc.getImageMeasurementList();
		for (ImageMeasurement im : imList)
		{
			Roi roi = new Roi();
			roi.setUid(im.getUuid());
			roi.setName(im.getName());
			roi.setGeometricType(im.getToolType());
			roi.setRoiCollectionId(getId());
			roiList.add(roi);
		}
		return roiList;
	}

	/**
	 * @return
	 */
	@Override
	public String getTime()
	{
		Date dt = AimUtils.parseDateTime(imc.getCreated());
		DateFormat format = new SimpleDateFormat("HHmmss");
		return format.format(dt);
	}

	/**
	 * @return
	 */
	@Override
	public String getType()
	{
		return Constants.Measurement;
	}

	/**
	 * @return
	 */
	@Override
	public String getUid()
	{
		return imc.getUuid();
	}

	private void buildUidSets()
	{
		CollectionImageReference cir = imc.getImageReference();
		addSeriesUid(cir.getSeriesInstanceUID());
		addStudyUid(cir.getStudyInstanceUID());
		List<ImageReference> ic = cir.getImageCollection();
		for (ImageReference ir : ic)
		{
			addSopInstanceUid(ir.getSOPInstanceUID());
		}
	}
}
