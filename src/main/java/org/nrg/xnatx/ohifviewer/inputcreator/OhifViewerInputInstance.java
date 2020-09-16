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

import icr.etherj.dicom.SopInstance;
import java.io.File;

import org.dcm4che2.data.DicomObject;
import org.nrg.dcm.SOPModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jpetts
 */
public class OhifViewerInputInstance extends OhifViewerInputItem
{
	private static final String FILES = "/files/";
	private static final String RESOURCES = "/resources/";
	private static final Logger logger =
		LoggerFactory.getLogger(OhifViewerInputInstance.class);

	private OhifViewerInputInstanceMetadata metadata;
	private String url;

	public OhifViewerInputInstance(SopInstance sop, String xnatScanUrl,
		String scanId)
	{
		if (sop == null)
		{
			logger.error("SopInstance is null");
			return;
		}

		metadata = new OhifViewerInputInstanceMetadata(sop);

		String file = new File(sop.getPath()).getName();
		String SOPClassUID = sop.getSopClassUid();
		String resource = getResourceType(SOPClassUID);
		xnatScanUrl = selectCorrectProtocol(xnatScanUrl);
		url = xnatScanUrl+scanId+RESOURCES+resource+FILES+file;
	}

	public int getColumns()
	{
		return metadata.getColumns();
	}

	public String getFrameOfReferenceUID()
	{
		return metadata.getFrameOfReferenceUID();
	}

	public String getImageOrientationPatient()
	{
		return dbl2DcmString(metadata.getImageOrientationPatient());
	}

	public String getImagePositionPatient()
	{
		return dbl2DcmString(metadata.getImagePositionPatient());
	}

	public int getInstanceNumber()
	{
		return metadata.getInstanceNumber();
	}

	public String getNumberOfFrames()
	{
		return Integer.toString(metadata.getNumberOfFrames());
	}

	public String getPixelSpacing()
	{
		return dbl2DcmString(metadata.getPixelSpacing());
	}

	public int getRows()
	{
		return metadata.getRows();
	}

	public String getSopInstanceUid()
	{
		return metadata.getSOPInstanceUID();
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
		  logger.error("Unrecognised protocol in XNAT url");
		}

		return xnatScanUrl;
	}

}
