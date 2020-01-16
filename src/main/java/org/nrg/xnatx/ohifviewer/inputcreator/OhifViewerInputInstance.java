/********************************************************************
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
package org.nrg.xnatx.ohifviewer.inputcreator;

import org.nrg.dcm.SOPModel;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.nrg.xnatx.ohifviewer.etherj.dicom.Series;
import org.nrg.xnatx.ohifviewer.etherj.dicom.SopInstance;

/**
 *
 * @author jpetts
 */
public class OhifViewerInputInstance extends OhifViewerInputItem {
	protected static final String RESOURCES = "/resources/";
	protected static final String FILES = "/files/";
	private static final Logger logger = LoggerFactory.getLogger(OhifViewerInputInstance.class);

	private String sopInstanceUid;
	private Integer instanceNumber;
	private Integer columns;
	private Integer rows;
	private String numberOfFrames;
	private String frameOfReferenceUID;
	private String imagePositionPatient;
	private String imageOrientationPatient;
	private String pixelSpacing;
	private String url;

	public OhifViewerInputInstance(SopInstance sop, Series ser,
		String xnatScanUrl, String scanId)
	{
		setSopInstanceUid(sop.getUid());
		setInstanceNumber(sop.getInstanceNumber());
		setColumns(sop.getColumnCount());
		setRows(sop.getRowCount());
		setFrameOfReferenceUID(sop.getFrameOfReferenceUid());
		setImagePositionPatient(dbl2DcmString(sop.getImagePositionPatient()));
		setImageOrientationPatient(dbl2DcmString(sop.getImageOrientationPatient()));
		setPixelSpacing(dbl2DcmString(sop.getPixelSpacing()));

		// Set number of frames if multiframe image, set to empty string if not so the viewer ignores it.
		if (sop.getNumberOfFrames() > 1)
		{
			setNumberOfFrames(Integer.toString(sop.getNumberOfFrames()));
		}
		else
		{
			setNumberOfFrames("");
		}

		String file = new File(sop.getPath()).getName();
		String sopClassUid = sop.getSopClassUid();
		String resource = getResourceType(sopClassUid);
		xnatScanUrl = selectCorrectProtocol(xnatScanUrl);
		String urlString = xnatScanUrl + scanId + RESOURCES + resource + FILES + file;
		setUrl(urlString);
	}

	public Integer getColumns()
	{
		return columns;
	}

	public String getFrameOfReferenceUID()
	{
		return frameOfReferenceUID;
	}

	public String getImageOrientationPatient()
	{
		return imageOrientationPatient;
	}

	public String getImagePositionPatient()
	{
		return imagePositionPatient;
	}

	public Integer getInstanceNumber()
	{
		return instanceNumber;
	}

	public String getNumberOfFrames()
	{
		return numberOfFrames;
	}

	public String getPixelSpacing()
	{
		return pixelSpacing;
	}

	public Integer getRows()
	{
		return rows;
	}

	public String getSopInstanceUid()
	{
		return sopInstanceUid;
	}

	private String getResourceType(String sopClassUid)
	{
		return (SOPModel.isPrimaryImagingSOP(sopClassUid))
			? "DICOM" : "secondary";
	}

	public String getUrl()
	{
		return url;
	}

	private String selectCorrectProtocol(String xnatScanUrl)
	{
		try
		{
		  xnatScanUrl = selectProtocol(xnatScanUrl);
		}
		catch (Exception ex)
		{
		  logger.error(ex.getMessage());
		}

		return xnatScanUrl;
	}

	private String selectProtocol(String xnatScanUrl) throws Exception
	{
		if (xnatScanUrl.contains("https"))
		{
			return xnatScanUrl.replace("https", "dicomweb");
		}
		else if (xnatScanUrl.contains("http"))
		{
			return xnatScanUrl.replace("http", "dicomweb");
		}
		else
		{
			throw new Exception("Unrecognised protocol in XNAT url");
		}
	}

	private void setColumns(Integer columns)
	{
		this.columns = columns;
	}

	private void setFrameOfReferenceUID(String frameOfReferenceUID)
	{
		this.frameOfReferenceUID = frameOfReferenceUID;
	}

	private void setImageOrientationPatient(String imageOrientationPatient)
	{
		this.imageOrientationPatient = imageOrientationPatient;
	}

	private void setImagePositionPatient(String imagePositionPatient)
	{
		this.imagePositionPatient = imagePositionPatient;
	}

	private void setInstanceNumber(Integer instanceNumber)
	{
		this.instanceNumber = instanceNumber;
	}

	private void setNumberOfFrames(String numberOfFrames)
	{
		this.numberOfFrames = numberOfFrames;
	}

	private void setPixelSpacing(String pixelSpacing)
	{
		this.pixelSpacing = pixelSpacing;
	}

	private void setRows(Integer rows)
	{
		this.rows = rows;
	}

	private void setSopInstanceUid(String sopInstanceUid)
	{
		this.sopInstanceUid = sopInstanceUid;
	}

	private void setUrl(String url)
	{
		this.url = url;
	}
}
