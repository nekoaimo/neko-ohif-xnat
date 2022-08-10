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


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import icr.etherj.PathScan;
import icr.etherj.dicom.DicomReceiver;
import icr.etherj.dicom.DicomToolkit;
import icr.etherj.dicom.Patient;
import icr.etherj.dicom.PatientRoot;
import icr.etherj.dicom.Series;
import icr.etherj.dicom.SopInstance;
import icr.etherj.dicom.Study;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.dcm4che2.data.DicomObject;
import org.nrg.xnatx.ohifviewer.ViewerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author simond
 * @author jpetts
 */
public class CreateOhifViewerMetadata
{
	private static final Logger logger = LoggerFactory.getLogger(
		CreateOhifViewerMetadata.class);
	private static final DicomToolkit dcmTk = DicomToolkit.getToolkit();

	private final String xnatScanPath;
	private final String xnatExperimentScanUrl;
	private final Map<String,String> seriesUidToScanIdMap;

	public CreateOhifViewerMetadata(final String xnatScanPath,
		final String xnatExperimentScanUrl,
		final Map<String,String> seriesUidToScanIdMap)
	{
		this.xnatScanPath = xnatScanPath;
		this.xnatExperimentScanUrl = xnatExperimentScanUrl;
		this.seriesUidToScanIdMap = seriesUidToScanIdMap;
	}

	/**
	 * Create the serialized JSON file and return its Path object
	 * @param transactionId
	 * @returnPath to file that contains the serialized JSON created
	 * @throws IOException
	 */
	public Path jsonify(final String transactionId) throws IOException
	{
		OhifViewerInput ovi = scanPathAndCreateInput(transactionId, xnatScanPath, xnatExperimentScanUrl, seriesUidToScanIdMap);

		// Convert the Java object to a JSON string
		Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
		Path jsonFilePath = Files.createTempFile("ohif", null);
		logger.debug("Temp file named {} created for storing serialized JSON", jsonFilePath.toString());
		try (JsonWriter writer = new JsonWriter(new FileWriter(jsonFilePath.toFile()))) {
			gson.toJson(ovi, OhifViewerInput.class, writer);
		}
		return jsonFilePath;
	}

	private OhifViewerInput scanPathAndCreateInput(String transactionId, String xnatScanPath, String xnatExperimentScanUrl, Map<String,String> seriesUidToScanIdMap) throws IOException {
		logger.info("DICOM search: {}", xnatScanPath);

		CustomDicomReceiver dcmRec = new CustomDicomReceiver(transactionId, xnatExperimentScanUrl, seriesUidToScanIdMap);
		PathScan<DicomObject> pathScan = dcmTk.createPathScan();
		pathScan.addContext(dcmRec);
		pathScan.scan(xnatScanPath, true);
		OhifViewerInput ovi = dcmRec.getOhifViewerInput();

		return ovi;
	}
}
