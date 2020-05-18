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

import icr.etherj.dicom.DicomUtils;
import icr.etherj.dicom.iod.Contour;
import icr.etherj.dicom.iod.IodUtils;
import icr.etherj.dicom.iod.Iods;
import icr.etherj.dicom.iod.ReferencedFrameOfReference;
import icr.etherj.dicom.iod.RoiContour;
import icr.etherj.dicom.iod.RtReferencedSeries;
import icr.etherj.dicom.iod.RtReferencedStudy;
import icr.etherj.dicom.iod.RtStruct;
import icr.etherj.dicom.iod.StructureSetRoi;
import icr.etherj.dicom.iod.module.RoiContourModule;
import icr.etherj.dicom.iod.module.StructureSetModule;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.dcm4che2.data.DicomObject;
import icr.xnat.plugin.roi.entity.Roi;
import org.nrg.xnatx.roi.Constants;

/**
 *
 * @author jamesd
 */
public class RtStructRoiCollection extends AbstractRoiCollection
	implements RoiCollection
{
	private final RtStruct rtStruct;

	/**
	 *
	 * @param id
	 * @param rawBytes
	 * @throws PluginException
	 */
	public RtStructRoiCollection(String id, byte[] rawBytes)
		throws PluginException
	{
		super(id, rawBytes);
		setFileExtension("dcm");
		setFileFormat("DICOM");
		setTypeDescription("RT Structure Set");
		try
		{
			DicomObject dcm = DicomUtils.readDicomObject(
				new ByteArrayInputStream(rawBytes));
			rtStruct = Iods.rtStruct(dcm);
		}
		catch (IOException ex)
		{
			throw new PluginException(ex.getMessage(), PluginCode.IO, ex);
		}
		catch (IllegalArgumentException ex)
		{
			throw new PluginException(ex.getMessage(), PluginCode.IllegalArgument, ex);
		}
		buildUidSets();
	}

	@Override
	public String getDate()
	{
		return IodUtils.getDate(rtStruct, "yyyyMMdd");
	}

	@Override
	public List<Roi> getRoiList()
	{
		List<Roi> roiList = new ArrayList<>();
		String rtUid = rtStruct.getSopInstanceUid();
		StructureSetModule ssm = rtStruct.getStructureSetModule();
		List<StructureSetRoi> ssrList = ssm.getStructureSetRoiList();
		for (StructureSetRoi ssr : ssrList)
		{
			Roi roi = new Roi();
			roi.setUid(rtUid+"."+ssr.getRoiNumber());
			roi.setName(ssr.getRoiName());
			roi.setGeometricType(Constants.ContourStack);
			roi.setRoiCollectionId(getId());
			roiList.add(roi);
		}
		return roiList;
	}

	@Override
	public String getName()
	{
		return rtStruct.getStructureSetModule().getStructureSetLabel();
	}

	@Override
	public String getTime()
	{
		return IodUtils.getTime(rtStruct, "HHmmss");
	}

	@Override
	public String getType()
	{
		return Constants.RtStruct;
	}

	@Override
	public String getUid()
	{
		return rtStruct.getSopInstanceUid();
	}

	private void buildUidSets()
	{
		StructureSetModule ssm = rtStruct.getStructureSetModule();
		for (ReferencedFrameOfReference refFoR :
			ssm.getReferencedFrameOfReferenceList())
		{
			for (RtReferencedStudy refStudy : refFoR.getRtReferencedStudyList())
			{
				addStudyUid(refStudy.getReferencedSopInstanceUid());
				for (RtReferencedSeries refSeries :
					refStudy.getRtReferencedSeriesList())
				{
					addSeriesUid(refSeries.getSeriesInstanceUid());
					refSeries.getContourImageList().forEach(
						(ci) -> addSopInstanceUid(ci.getReferencedSopInstanceUid()));
				}
			}
		}
		// Theoretically not needed as all SOP instance UIDs should be in the
		// StructureSetModule
		RoiContourModule rcm = rtStruct.getRoiContourModule();
		for (RoiContour roi : rcm.getRoiContourList())
		{
			for (Contour contour : roi.getContourList())
			{
				contour.getContourImageList().forEach(
					(ci) -> addSopInstanceUid(ci.getReferencedSopInstanceUid()));
			}
		}
	}

}
