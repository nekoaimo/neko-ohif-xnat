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
package org.nrg.xnatx.ohifviewer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;

/**
 *
 * @author jamesd
 */
public class PluginUtils
{
	private static final Set<String> displayableSopClassUids = new HashSet<>();

	static
	{
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.1"); // CR Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.1.1"); // Digital X-Ray Image Storage – for Presentation
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.1.2"); // Digital Mammography X-Ray Image Storage – for Presentation
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.1.3"); //	Digital Intra – oral X-Ray Image Storage – for Presentation
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.11.1"); //	Grayscale Softcopy Presentation State Storage SOP Class
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.11.2"); //	Color Softcopy Presentation State Storage SOP Class
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.11.3"); //	Pseudocolor Softcopy Presentation Stage Storage SOP Class
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.11.4"); //	Blending Softcopy Presentation State Storage SOP Class
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.12.1"); //	X-Ray Angiographic Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.12.1.1"); //	Enhanced XA Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.12.2"); //	X-Ray Radiofluoroscopic Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.12.2.1"); //	Enhanced XRF Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.12.3"); //	X-Ray Angiographic Bi-plane Image Storage 	Retired
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.128"); //	Positron Emission Tomography Curve Storage 	Retired
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.129"); //	Standalone Positron Emission Tomography Curve Storage 	Retired
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.13.1.3"); // Breast Tomosynthesis Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.14.1"); // Intravascular Optical Coherence Tomography Image Storage - For Presentation
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.2"); //	CT Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.2.1"); //	Enhanced CT Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.2.2"); // Legacy Converted Enhanced CT Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.20"); //	NM Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.3"); //	Ultrasound Multiframe Image Storage 	Retired
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.3.1"); //	Ultrasound Multiframe Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.4"); //	MR Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.4.1"); //	Enhanced MR Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.4.2"); //	MR Spectroscopy Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.4.3"); // Enhanced MR Color Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.4.4"); // Legacy Converted Enhanced MR Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.6"); //	Ultrasound Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.6.1"); //	Ultrasound Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.7"); // Secondary Capture Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.7.1"); // Multiframe Single Bit Secondary Capture Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.7.2"); // Multiframe Grayscale Byte Secondary Capture Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.7.3"); // Multiframe Grayscale Word Secondary Capture Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.7.4"); // Multiframe True Color Secondary Capture Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.77.1"); //	VL (Visible Light) Image Storage 	Retired
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.77.1.1"); //	VL endoscopic Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.77.1.1.1"); //	Video Endoscopic Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.77.1.2"); //	VL Microscopic Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.77.1.2.1"); //	Video Microscopic Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.77.1.4"); //	VL Photographic Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.77.1.4.1"); // Video Photographic Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.77.1.5.1"); // Ophthalmic Photography 8-Bit Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.77.1.5.2"); // Ophthalmic Photography 16-Bit Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.77.1.5.4"); // Ophthalmic Tomography Image Storage
		displayableSopClassUids.add("1.2.840.10008.5.1.4.1.1.77.2"); // VL Multiframe Image Storage
	}

	/**
	 *
	 * @param experimentId
	 * @return
	 */
	public static Map<String,String> getDirectoryInfo(String experimentId)
	{
		// Get Experiment data and Project data from the experimentId
		XnatExperimentdata expData = XnatExperimentdata.getXnatExperimentdatasById(
			experimentId, null, false);
		XnatProjectdata projData = expData.getProjectData();
		XnatImagesessiondata sessionData = (XnatImagesessiondata) expData;
		// Get the subject data
		XnatSubjectdata subjData = XnatSubjectdata.getXnatSubjectdatasById(
			sessionData.getSubjectId(), null, false);

		// Get the required info
		String expLabel = expData.getArchiveDirectoryName();
		String proj = projData.getId();
		String subj = subjData.getLabel();

		// Construct a HashMap to return data
		Map<String, String> result = new HashMap<>();
		result.put("expLabel", expLabel);
		result.put("proj", proj);
		result.put("subj", subj);

		return result;
	}

	public static boolean isDisplayableSopClass(String uid)
	{
		return displayableSopClassUids.contains(uid);
	}

	private PluginUtils()
	{}
}
