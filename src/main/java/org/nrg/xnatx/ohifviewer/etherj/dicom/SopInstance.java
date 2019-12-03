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
package org.nrg.xnatx.ohifviewer.etherj.dicom;

import java.io.File;
import java.util.Set;
import org.dcm4che2.data.DicomObject;
import org.nrg.xnatx.ohifviewer.etherj.Displayable;

/**
 * A DICOM SOP instance that wraps a <code>DicomObject</code>.
 * @author jamesd
 */
public interface SopInstance extends Displayable
{
	/**
	 * Reduce heap memory usage by releasing the wrapped <code>DicomObject</code>.
	 */
	public void compact();

	/**
	 * Returns the acquisition time.
	 * @return the acquisition time
	 */
	public String getAcquisitionTime();

	/**
	 * Returns the number of columns in the image.
	 * @return the column count
	 */
	public int getColumnCount();

	/**
	 * Returns the content time.
	 * @return the content time
	 */
	public String getContentTime();

	/**
	 * Returns the wrapped <code>DicomObject</code>, lazy-loading it if required.
	 * @return the DICOM object
	 */
	public DicomObject getDicomObject();

	/**
	 * Returns the origin file.
	 * @return the file
	 */
	public File getFile();

	/**
	 * Returns the frame of reference UID.
	 * @return the UID
	 */
	public String getFrameOfReferenceUid();

	/**
	 * Returns the image orientation patient, a six element array containing the
	 * direction cosines of the image rows and columns.
	 * @return the image orientation patient
	 */
	public double[] getImageOrientationPatient();

	/**
	 * Returns the image orientation patient for the frame, a six element array
	 * containing the direction cosines of the image rows and columns.
	 * @param frame
	 * @return the image orientation patient
	 */
	public double[] getImageOrientationPatient(int frame);

	/**
	 * Returns the image position patient, a three element array.
	 * @return the image position patient
	 */
	public double[] getImagePositionPatient();

	/**
	 * Returns the image position patient for the frame, a three element array.
	 * @param frame
	 * @return the image position patient
	 */
	public double[] getImagePositionPatient(int frame);

	/**
	 * Returns the instance number.
	 * @return the instance number
	 */
	public int getInstanceNumber();

	/**
	 * Returns the modality.
	 * @return the modality
	 */
	public String getModality();

	/**
	 * Returns the number of frames in the SOP instance.
	 * @return the number of frames
	 */
	public int getNumberOfFrames();

	/**
	 * Returns the path.
	 * @return the path
	 */
	public String getPath();

	/**
	 * Returns the pixel spacing.
	 * @return the pixel spacing
	 */
	public double[] getPixelSpacing();

	/**
	 * Returns the pixel spacing for the frame.
	 * @param frame
	 * @return the pixel spacing
	 */
	public double[] getPixelSpacing(int frame);

	/**
	 * Returns the set of referenced SOP instance UIDs.
	 * @return the set of UIDs
	 */
	public Set<String> getReferencedSopInstanceUidSet();

	/**
	 * Returns the number of rows.
	 * @return the row count
	 */
	public int getRowCount();

	/**
	 * Returns the series date.
	 * @return the date
	 */
	public String getSeriesDate();

	/**
	 * Returns the series time.
	 * @return the time
	 */
	public String getSeriesTime();

	/**
	 * Returns the series UID.
	 * @return the series UID
	 */
	public String getSeriesUid();

	/**
	 * Returns the slice location.
	 * @return the slice location
	 */
	public double getSliceLocation();

	/**
	 * Returns the slice location for the frame.
	 * @param frame
	 * @return the slice location
	 */
	public double getSliceLocation(int frame);

	/**
	 * Returns the SOP class UID.
	 * @return the UID
	 */
	public String getSopClassUid();

	/**
	 * Returns the study date.
	 * @return the study date
	 */
	public String getStudyDate();

	/**
	 * Returns the study time.
	 * @return the study time
	 */
	public String getStudyTime();

	/**
	 * Returns the study instance UID.
	 * @return the UID
	 */
	public String getStudyUid();

	/**
	 * Returns the SOP instance UID.
	 * @return the UID
	 */
	public String getUid();

	/**
	 * Sets the acquisition time.
	 * @param time the time to set
	 */
	public void setAcquisitionTime(String time);

	/**
	 * Sets the number of columns.
	 * @param cols the column count to set
	 * @throws IllegalArgumentException if column count is less than zero
	 */
	public void setColumnCount(int cols) throws IllegalArgumentException;

	/**
	 * Sets the content time.
	 * @param time the time to set
	 */
	public void setContentTime(String time);

	/**
	 * Sets the frame of reference UID.
	 * @param uid the UID to set
	 * @throws IllegalArgumentException if UID is null or empty
	 */
	public void setFrameOfReferenceUid(String uid) throws IllegalArgumentException;

	/**
	 * Sets the image orientation patient, a six element array containing the
	 * direction cosines of the image rows and columns.
	 * @param orientation the image orientation patient
	 * @throws IllegalArgumentException if the orientation is not a six
	 * element array
	 */
	public void setImageOrientationPatient(double[] orientation)
		throws IllegalArgumentException;

	/**
	 * Returns the image position patient, a three element array.
	 * @param position the image position patient
	 * @throws IllegalArgumentException if the position is not a three
	 * element array
	 */
	public void setImagePositionPatient(double[] position)
		throws IllegalArgumentException;

	/**
	 * Sets the instance number.
	 * @param number the number to set
	 */
	public void setInstanceNumber(int number);

	/**
	 * Sets the modality.
	 * @param modality the modality to set
	 */
	public void setModality(String modality);

	/**
	 * Sets the number of frames in the instance.
	 * @param frameCount the frame count to set
	 */
	public void setNumberOfFrames(int frameCount);

	/**
	 * Sets the pixel spacing, a two element array.
	 * @param spacing the spacing to set
	 * @throws IllegalArgumentException if spacing is not a two element array
	 */
	public void setPixelSpacing(double[] spacing);

	/**
	 * Sets the number of rows.
	 * @param rows the row count to set
	 * @throws IllegalArgumentException if row count is less than 0
	 */
	public void setRowCount(int rows);

	/**
	 * Sets the series date.
	 * @param date the date to set
	 */
	public void setSeriesDate(String date);

	/**
	 * Sets the series time.
	 * @param time the time to set
	 */
	public void setSeriesTime(String time);

	/**
	 * Sets the series instance UID.
	 * @param uid the uid to set
	 * @throws IllegalArgumentException if UID is null or empty
	 */
	public void setSeriesUid(String uid);

	/**
	 * Sets the slice location
	 * @param location the location to set
	 */
	public void setSliceLocation(double location);

	/**
	 * Sets the SOP class UID.
	 * @param uid the UID to set
	 * @throws IllegalArgumentException if UID is null or empty
	 */
	public void setSopClassUid(String uid);

	/**
	 * Sets the study date.
	 * @param date the date to set
	 */
	public void setStudyDate(String date);

	/**
	 * Sets the study time.
	 * @param time the time to set
	 */
	public void setStudyTime(String time);

	/**
	 * Sets the study instance UID.
	 * @param uid the UID to set
	 * @throws IllegalArgumentException if UID is null or empty
	 */
	public void setStudyUid(String uid);

	/**
	 * Sets the SOP instance UID.
	 * @param uid the UID to set
	 * @throws IllegalArgumentException if UID is null or empty
	 */
	public void setUid(String uid);
	
}
