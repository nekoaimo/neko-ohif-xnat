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

import icr.etherj.dicom.Series;
import icr.etherj.dicom.SopInstance;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jpetts
 */
public class OhifViewerInputInstanceSingle extends OhifViewerInputInstance {
  private String url;
  
  private static final Logger logger = LoggerFactory.getLogger(OhifViewerInputInstanceSingle.class);
  
  public OhifViewerInputInstanceSingle(SopInstance sop, Series ser, String xnatScanUrl, String scanId)
  {
    super(sop, ser, xnatScanUrl, scanId);
    String file = new File(sop.getPath()).getName();
    String sopClassUid = sop.getSopClassUid();    
    String resource = getResourceType(sopClassUid);
    
    xnatScanUrl = selectCorrectProtocol(xnatScanUrl, sopClassUid);
    
    String urlString = xnatScanUrl + scanId + RESOURCES + resource + FILES + file;
    
    setUrl(urlString);
  }
  
  private String selectCorrectProtocol(String xnatScanUrl, String sopClassUid)
  {
    try
    {
      xnatScanUrl = selectProtocol(xnatScanUrl, sopClassUid);
    }
    catch (Exception ex)
    {
      logger.error(ex.getMessage());
    }
    
    return xnatScanUrl;
  }
  
  private String selectProtocol(String xnatScanUrl, String sopClassUid)
  throws Exception
  {
    //Elegance please James...
    //TODO: Use URL type and just replace protocol with dicomweb

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
      throw new Exception("unrecognised protocol in xnat url");
    }

  }
  
  public String getUrl()
	{
		return url;
	}

	private void setUrl(String url)
	{
		this.url = url;
	}
  
}
