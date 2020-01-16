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
import java.io.PrintStream;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.nrg.xnatx.ohifviewer.etherj.AbstractDisplayable;
import org.nrg.xnatx.ohifviewer.etherj.dicom.SopInstance;
import org.nrg.xnatx.ohifviewer.etherj.dicom.DicomUtils;
import org.nrg.xnatx.ohifviewer.etherj.dicom.Modality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * $Id$
 *
 * @author adminjamesd
 */
class DefaultSopInstance extends AbstractDisplayable implements SopInstance
{
	private static final Logger logger =
		LoggerFactory.getLogger(DefaultSopInstance.class);

	private String acqTime = "";
	private int colCount = 0;
	private String contentTime = "";
	private File file;
	private int frameCount = 0;
	private String frameOfRefUid = "";
	private double[] imageOrientation = {Double.NaN,Double.NaN,Double.NaN,
		Double.NaN,Double.NaN,Double.NaN};
	private double[] imagePosition = {Double.NaN,Double.NaN,Double.NaN};
	private int instanceNumber = 1;
	private String modality = "";
	private double[] pixelSpacing = {Double.NaN,Double.NaN};
	private int rowCount = 0;
	private String seriesDate = "";
	private String seriesTime = "";
	private String seriesUid = "";
	private double sliceLoc = Double.NaN;
	private SoftReference<DicomObject> softDcm;
	private String sopClassUid = "";
	private String studyDate = "";
	private String studyTime = "";
	private String studyUid = "";
	private String uid = "";

	DefaultSopInstance(File file)
	{
		this.file = file;
		softDcm = new SoftReference<>(null);
	}

	DefaultSopInstance(File file, DicomObject dcm)
	{
		this(file, dcm, false);
	}

	DefaultSopInstance(File file, DicomObject dcm, boolean discard)
	{
		this.file = file;
		if (dcm == null)
		{
			softDcm = new SoftReference<>(null);
			return;
		}
		sopClassUid = dcm.getString(Tag.SOPClassUID);
		uid = dcm.getString(Tag.SOPInstanceUID);
		instanceNumber = dcm.getInt(Tag.InstanceNumber, 1);
		modality = dcm.getString(Tag.Modality);
		seriesUid = dcm.getString(Tag.SeriesInstanceUID);
		studyUid = dcm.getString(Tag.StudyInstanceUID);
		if (dcm.contains(Tag.NumberOfFrames))
		{
			frameCount = dcm.getInt(Tag.NumberOfFrames);
		}
		else
		{
			if (DicomUtils.isImageSopClass(sopClassUid))
			{
				frameCount = 1;
			}
		}
		imageOrientation = dcm.getDoubles(Tag.ImageOrientationPatient,
			imageOrientation);
		imagePosition = dcm.getDoubles(Tag.ImagePositionPatient, imagePosition);
		pixelSpacing = dcm.getDoubles(Tag.PixelSpacing, pixelSpacing);
		rowCount = dcm.getInt(Tag.Rows);
		colCount = dcm.getInt(Tag.Columns);
		sliceLoc = dcm.getDouble(Tag.SliceLocation, Double.NaN);
		studyDate = dcm.getString(Tag.StudyDate);
		studyTime = dcm.getString(Tag.StudyTime);
		seriesDate = dcm.getString(Tag.SeriesDate);
		seriesTime = dcm.getString(Tag.SeriesTime);
		if (dcm.contains(Tag.AcquisitionTime))
		{
			acqTime = dcm.getString(Tag.AcquisitionTime);
		}
		if (dcm.contains(Tag.ContentTime))
		{
			contentTime = dcm.getString(Tag.ContentTime);
		}
		frameOfRefUid = dcm.getString(Tag.FrameOfReferenceUID);

		/* Discard determines whether the supplied DICOM object should be retained
		* after extraction of info. Discard would be true if the caller wanted to
		* create a new SOPInstance without having to load the contents of file
		* e.g. from a database query result
		*/
		if (discard)
		{
			softDcm = new SoftReference<>(null);
		}
		else
		{
			softDcm = new SoftReference<>(dcm);
		}
	}

	@Override
	public final void compact()
	{
		softDcm.clear();
		softDcm = new SoftReference<>(null);
		logger.trace("SOPInstance compacted: {}", file.getPath());
	}

	@Override
	public void display(PrintStream ps, String indent, boolean recurse)
	{
		ps.println(indent+getClass().getName());
		String pad = indent+"  * ";
		ps.println(pad+"File: "+file.getAbsolutePath());
		ps.println(pad+"Modality: "+modality);
		ps.println(pad+"InstanceNumber: "+instanceNumber);
		ps.println(pad+"NumberOfFrames: "+frameCount);
		ps.println(pad+"Uid: "+uid);
		ps.println(pad+"SopClassUid: "+sopClassUid);
		ps.println(pad+"SeriesUid: "+seriesUid);
		ps.println(pad+"StudyUid: "+studyUid);
		ps.println(pad+"FrameOfReferenceUid: "+frameOfRefUid);
		ps.println(pad+"ImagePositionPatient: "+
			String.format("%f/%f/%f", imagePosition[0], imagePosition[1],
				imagePosition[2]));
		ps.println(pad+"ImageOrientationPatient: "+
			String.format("%f/%f/%f/%f/%f/%f", imageOrientation[0],
				imageOrientation[1], imageOrientation[2], imageOrientation[3],
				imageOrientation[4], imageOrientation[5]));
		ps.println(pad+"PixelSpacing: "+
			String.format("%f/%f", pixelSpacing[0], pixelSpacing[1]));
		ps.println(pad+"SliceLocation: "+sliceLoc);
		ps.println(pad+"RowCount: "+rowCount);
		ps.println(pad+"ColumnCount: "+colCount);
		ps.println(pad+"AcquisitionTime: "+acqTime);
		ps.println(pad+"ContentTime: "+contentTime);
		ps.println(pad+"SeriesDate: "+seriesDate);
		ps.println(pad+"SeriesTime: "+seriesTime);
		ps.println(pad+"StudyDate: "+studyDate);
		ps.println(pad+"StudyTime: "+studyTime);
		ps.println(pad+"Compact: "+
			(softDcm.get() == null ? "true" : "false"));
	}

	@Override
	public String getAcquisitionTime()
	{
		return acqTime;
	}

	@Override
	public int getColumnCount()
	{
		return colCount;
	}

	@Override
	public String getContentTime()
	{
		return contentTime;
	}

	@Override
	public DicomObject getDicomObject()
	{
		return dcm();
	}

	@Override
	public File getFile()
	{
		return file;
	}

	@Override
	public String getFrameOfReferenceUid()
	{
		return frameOfRefUid;
	}

	@Override
	public double[] getImageOrientationPatient()
	{
		return Arrays.copyOf(imageOrientation, 6);
	}

	@Override
	public double[] getImageOrientationPatient(int frame)
	{
		return Arrays.copyOf(imageOrientation, 6);
	}

	@Override
	public double[] getImagePositionPatient()
	{
		return Arrays.copyOf(imagePosition, 3);
	}

	@Override
	public double[] getImagePositionPatient(int frame)
	{
		return Arrays.copyOf(imagePosition, 3);
	}

	@Override
	public int getInstanceNumber()
	{
		return instanceNumber;
	}

	@Override
	public String getModality()
	{
		return modality;
	}

	@Override
	public int getNumberOfFrames()
	{
		return frameCount;
	}

	@Override
	public String getPath()
	{
		return file.getPath();
	}

	@Override
	public double[] getPixelSpacing()
	{
		return Arrays.copyOf(pixelSpacing, 2);
	}

	@Override
	public double[] getPixelSpacing(int frame)
	{
		return Arrays.copyOf(pixelSpacing, 2);
	}

	@Override
	public Set<String> getReferencedSopInstanceUidSet()
	{
		Set<String> uids = new HashSet<>();
		DicomObject dcm = this.dcm();
		DicomElement refSq = dcm.get(Tag.ReferencedImageSequence);
		if (refSq != null)
		{
			int nItems = refSq.countItems();
			for (int i=0; i<nItems; i++)
			{
				DicomObject item = refSq.getDicomObject(i);
				uids.add(item.getString(Tag.ReferencedSOPInstanceUID));
			}
		}
		if (!modality.equals(Modality.RTSTRUCT))
		{
			return uids;
		}
		DicomElement roiContourSq = dcm.get(Tag.ROIContourSequence);
		int nRoi = roiContourSq.countItems();
		for (int i=0; i<nRoi; i++)
		{
			DicomObject roiContourItem = roiContourSq.getDicomObject(i);
			DicomElement contourSq = roiContourItem.get(Tag.ContourSequence);
			if (contourSq == null)
			{
				continue;
			}
			int nContours = contourSq.countItems();
			for (int j=0; j<nContours; j++)
			{
				DicomObject contourItem = contourSq.getDicomObject(j);
				DicomElement contourImageSq = contourItem.get(
					Tag.ContourImageSequence);
				if (contourImageSq == null)
				{
					continue;
				}
				int nContourImages = contourImageSq.countItems();
				for (int k=0; k<nContourImages; k++)
				{
					DicomObject imageItem = contourImageSq.getDicomObject(k);
					uids.add(imageItem.getString(Tag.ReferencedSOPInstanceUID));
				}
			}
		}
		return uids;
	}

	@Override
	public int getRowCount()
	{
		return rowCount;
	}

	@Override
	public String getSeriesDate()
	{
		return seriesDate;
	}

	@Override
	public String getSeriesTime()
	{
		return seriesTime;
	}

	@Override
	public String getSeriesUid()
	{
		return seriesUid;
	}

	@Override
	public double getSliceLocation()
	{
		return sliceLoc;
	}

	@Override
	public double getSliceLocation(int frame)
	{
		return sliceLoc;
	}

	@Override
	public String getSopClassUid()
	{
		return sopClassUid;
	}

	@Override
	public String getStudyDate()
	{
		return studyDate;
	}

	@Override
	public String getStudyTime()
	{
		return studyTime;
	}

	@Override
	public String getStudyUid()
	{
		return studyUid;
	}

	@Override
	public String getUid()
	{
		return uid;
	}

	@Override
	public void setAcquisitionTime(String time)
	{
		acqTime = (time == null) ? "" : time;
	}

	@Override
	public void setColumnCount(int cols) throws IllegalArgumentException
	{
		if (cols < 0)
		{
			throw new IllegalArgumentException("Column count must be >= 0");
		}
		colCount = cols;
	}

	@Override
	public void setContentTime(String time)
	{
		contentTime = (time == null) ? "" : time;
	}

	@Override
	public void setFrameOfReferenceUid(String uid) throws IllegalArgumentException
	{
		frameOfRefUid = (uid == null) ? "" : uid;
	}

	@Override
	public void setImageOrientationPatient(double[] orientation)
		throws IllegalArgumentException
	{
		if ((orientation == null) || (orientation.length != 6))
		{
			throw new IllegalArgumentException("Image orientation must be six elements");
		}
		System.arraycopy(orientation, 0, imagePosition, 0, 6);
	}

	@Override
	public void setImagePositionPatient(double[] position)
		throws IllegalArgumentException
	{
		if ((position == null) || (position.length != 3))
		{
			throw new IllegalArgumentException("Image position must be three elements");
		}
		System.arraycopy(position, 0, imagePosition, 0, 3);
	}

	@Override
	public void setInstanceNumber(int number)
	{
		instanceNumber = number;
	}

	@Override
	public void setModality(String modality)
	{
		this.modality = (modality == null) ? "" : modality;
	}

	@Override
	public void setNumberOfFrames(int frameCount)
	{
		this.frameCount = frameCount;
	}

	@Override
	public void setPixelSpacing(double[] spacing) throws IllegalArgumentException
	{
		if ((spacing == null) || (spacing.length != 2))
		{
			throw new IllegalArgumentException("Pixel spacing must be two elements");
		}
		System.arraycopy(spacing, 0, pixelSpacing, 0, 2);
	}

	@Override
	public void setRowCount(int rows) throws IllegalArgumentException
	{
		if (rows < 0)
		{
			throw new IllegalArgumentException("Row count must be >= 0");
		}
		rowCount = rows;
	}

	@Override
	public void setSeriesDate(String date)
	{
		seriesDate = (date == null) ? "" : date;
	}

	@Override
	public void setSeriesTime(String time)
	{
		seriesTime = (time == null) ? "" : time;
	}

	@Override
	public void setSeriesUid(String uid) throws IllegalArgumentException
	{
		if ((uid == null) || uid.isEmpty())
		{
			throw new IllegalArgumentException("UID must not be null or empty");
		}
		seriesUid = uid;
	}

	@Override
	public void setSliceLocation(double location)
	{
		sliceLoc = location;
	}

	@Override
	public void setSopClassUid(String uid) throws IllegalArgumentException
	{
		if ((uid == null) || uid.isEmpty())
		{
			throw new IllegalArgumentException("UID must not be null or empty");
		}
		sopClassUid = uid;
	}

	@Override
	public void setStudyDate(String date)
	{
		studyDate = (date == null) ? "" : date;
	}

	@Override
	public void setStudyTime(String time)
	{
		studyTime = (time == null) ? "" : time;
	}

	@Override
	public void setStudyUid(String uid) throws IllegalArgumentException
	{
		if ((uid == null) || uid.isEmpty())
		{
			throw new IllegalArgumentException("UID must not be null or empty");
		}
		studyUid = uid;
	}

	@Override
	public void setUid(String uid) throws IllegalArgumentException
	{
		if ((uid == null) || uid.isEmpty())
		{
			throw new IllegalArgumentException("UID must not be null or empty");
		}
		this.uid = uid;
	}

	protected final DicomObject dcm()
	{
		DicomObject dcm = softDcm.get();
		if (dcm == null)
		{
			try
			{
				dcm = DicomUtils.readDicomFile(file);
				softDcm = new SoftReference<>(dcm);
				logger.trace("Lazy load of SOPInstance: {}", file.getPath());
			}
			catch (IOException exIO)
			{
				logger.error(
					"Cannot reload soft reference target: {}", file.getPath(),
					exIO);
			}
		}
		return dcm;
	}

}
