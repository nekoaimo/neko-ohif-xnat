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
import icr.etherj.XmlException;
import icr.etherj.aim.AimToolkit;
import icr.etherj.aim.AimUtils;
import icr.etherj.aim.DicomImageReference;
import icr.etherj.aim.Image;
import icr.etherj.aim.ImageAnnotation;
import icr.etherj.aim.ImageAnnotationCollection;
import icr.etherj.aim.ImageReference;
import icr.etherj.aim.ImageSeries;
import icr.etherj.aim.ImageStudy;
import icr.etherj.aim.XmlParser;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import icr.xnat.plugin.roi.entity.Roi;
import org.nrg.xnatx.roi.Constants;

/**
 *
 * @author jamesd
 */
public class AimRoiCollection extends AbstractRoiCollection
	implements RoiCollection
{
	private final ImageAnnotationCollection iac;

	/**
	 *
	 * @param id
	 * @param rawBytes
	 * @throws PluginException
	 */
	public AimRoiCollection(String id, byte[] rawBytes) throws PluginException
	{
		super(id, rawBytes);
		setFileExtension("xml");
		setFileFormat("XML");
		setTypeDescription("AIM instance file");
		XmlParser parser = AimToolkit.getToolkit().createXmlParser();
		try
		{
			iac = parser.parse(new ByteArrayInputStream(rawBytes));
			buildUidSets();
		}
		catch (XmlException ex)
		{
			throw new PluginException(ex.getMessage(), PluginCode.XML, ex);
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
		Date dt = AimUtils.parseDateTime(iac.getDateTime());
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format(dt);
	}

	@Override
	public String getName()
	{
		String name = iac.getDescription();
		if (StringUtils.isNullOrEmpty(name))
		{
			return "AIM";
		}
		name = name.trim();
		return StringUtils.isNullOrEmpty(name) ? "AIM" : name;
	}

	@Override
	public List<Roi> getRoiList()
	{
		List<Roi> roiList = new ArrayList<>();
		List<ImageAnnotation> iaList = iac.getAnnotationList();
		for (ImageAnnotation ia : iaList)
		{
			if (ia.getMarkupCount() == 0)
			{
				continue;
			}
			Roi roi = new Roi();
			roi.setUid(ia.getUid());
			roi.setName(ia.getName());
			roi.setGeometricType(Constants.ContourStack);
			roi.setRoiCollectionId(getId());
			roiList.add(roi);
		}
		return roiList;
	}

	@Override
	public String getTime()
	{
		Date dt = AimUtils.parseDateTime(iac.getDateTime());
		DateFormat format = new SimpleDateFormat("HHmmss");
		return format.format(dt);
	}

	@Override
	public String getType()
	{
		return Constants.AIM;
	}

	@Override
	public String getUid()
	{
		return iac.getUid();
	}

	private void buildUidSets()
	{
		for (ImageAnnotation ia : iac.getAnnotationList())
		{
			for (ImageReference ir : ia.getReferenceList())
			{
				if (!(ir instanceof DicomImageReference))
				{
					continue;
				}
				DicomImageReference dir = (DicomImageReference) ir;
				ImageStudy study = dir.getStudy();
				addStudyUid(study.getInstanceUid());
				ImageSeries series = study.getSeries();
				addSeriesUid(series.getInstanceUid());
				for (Image im : series.getImageList())
				{
					addSopInstanceUid(im.getSopInstanceUid());
				}
			}
		}
	}

}
