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
package org.nrg.xnatx.roi.data;

import icr.etherj2.StringUtils;
import icr.etherj2.dicom.DicomUtils;
import icr.etherj2.dicom.iod.DerivationImage;
import icr.etherj2.dicom.iod.FunctionalGroupsFrame;
import icr.etherj2.dicom.iod.IodUtils;
import icr.etherj2.dicom.iod.Iods;
import icr.etherj2.dicom.iod.ReferencedSeries;
import icr.etherj2.dicom.iod.Segment;
import icr.etherj2.dicom.iod.Segmentation;
import icr.etherj2.dicom.iod.SegmentationFunctionalGroupsFrame;
import icr.etherj2.dicom.iod.SegmentationPerFrameFunctionalGroups;
import icr.etherj2.dicom.iod.SourceImage;
import icr.etherj2.dicom.iod.module.CommonInstanceReferenceModule;
import icr.etherj2.dicom.iod.module.MultiframeFunctionalGroupsModule;
import icr.etherj2.dicom.iod.module.SegmentationImageModule;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.dcm4che3.data.Attributes;
import icr.xnat.plugin.roi.entity.Roi;
import org.nrg.xnatx.roi.Constants;

/**
 *
 * @author jamesd
 */
public class SegmentationRoiCollection extends AbstractRoiCollection implements RoiCollection
{
	private final Segmentation seg;

	/**
	 * Returns a new SegmentationRoiCollection.
	 *
	 * @param id       the RoiCollection ID
	 * @param rawBytes the raw bytes of the IAC
	 * @throws PluginException if any error occurs during parsing
	 */
	public SegmentationRoiCollection(String id, byte[] rawBytes) throws PluginException {
		super(id, rawBytes);
		setFileExtension("dcm");
		setFileFormat("DICOM");
		setTypeDescription("DICOM Segmentation");
		try {
			Attributes dcm = DicomUtils.readAttributes(new ByteArrayInputStream(rawBytes));
			seg = Iods.segmentation(dcm);
		} catch (IOException ex) {
			throw new PluginException(ex.getMessage(), PluginCode.IO, ex);
		} catch (IllegalArgumentException ex) {
			throw new PluginException(ex.getMessage(), PluginCode.IllegalArgument, ex);
		}
		buildUidSets();
	}

	@Override
	public String getDate()
	{
		return IodUtils.getDate(seg, "yyyyMMdd");
	}

	@Override
	public String getName()
	{
		String name = seg.getSegmentationSeriesModule().getSeriesDescription();
		if (StringUtils.isNullOrEmpty(name))
		{
			return "SEG";
		}
		name = name.trim();
		return StringUtils.isNullOrEmpty(name) ? "SEG" : name;
	}

	@Override
	public List<Roi> getRoiList()
	{
		List<Roi> roiList = new ArrayList<>();
		String segUid = seg.getSopInstanceUid();
		SegmentationImageModule sim = seg.getSegmentationImageModule();
		List<Segment> segmentList = sim.getSegmentList();
		for (Segment segment : segmentList)
		{
			Roi roi = new Roi();
			roi.setUid(segUid+"."+segment.getSegmentNumber());
			roi.setName(segment.getSegmentLabel());
			roi.setGeometricType(Constants.Segmentation);
			roi.setRoiCollectionId(getId());
			roiList.add(roi);
		}

		return roiList;
	}

	@Override
	public String getTime()
	{
		return IodUtils.getTime(seg, "HHmmss");
	}

	@Override
	public String getType()
	{
		return Constants.Segmentation;
	}

	@Override
	public String getUid()
	{
		return seg.getSopInstanceUid();
	}
	
	private void buildUidSets()
	{
		addStudyUid(seg.getGeneralStudyModule().getStudyInstanceUid());
		CommonInstanceReferenceModule cirm = seg.getCommonInstanceReferenceModule();
		for (ReferencedSeries refSeries : cirm.getReferencedSeriesList())
		{
			addSeriesUid(refSeries.getSeriesInstanceUid());
			refSeries.getReferencedInstanceList().forEach((refInst) ->
				addSopInstanceUid(refInst.getReferencedSopInstanceUid()));
		}

		// Derivation image module
		MultiframeFunctionalGroupsModule mfgm =
			seg.getMultiframeFunctionalGroupsModule();
		SegmentationPerFrameFunctionalGroups segPffg = 
			(SegmentationPerFrameFunctionalGroups) mfgm.getPerFrameFunctionalGroups();
		for (FunctionalGroupsFrame frame : segPffg.getFrameList())
		{
			SegmentationFunctionalGroupsFrame segFrame =
				(SegmentationFunctionalGroupsFrame) frame;
			for (DerivationImage derivImage : segFrame.getDerivationImages())
			{
				for (SourceImage image : derivImage.getSourceImageList())
				{
					addSopInstanceUid(image.getReferencedSopInstanceUid());
				}
			}
		}
	}
}
