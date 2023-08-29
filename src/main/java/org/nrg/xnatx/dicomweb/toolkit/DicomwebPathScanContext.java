package org.nrg.xnatx.dicomweb.toolkit;

import icr.etherj.PathScanContext;
import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.nrg.xnatx.dicomweb.conf.DicomwebDeviceConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
public class DicomwebPathScanContext implements PathScanContext<Attributes>
{
	private final Map<String,String> xnatIds;
	private final String xnatExperimentScanUrl;
	private final Map<String,String> seriesUidToScanIdMap;
	private final URI experimentUri;
	private final DicomwebInput dicomwebInput;

	public DicomwebPathScanContext(final Map<String,String> xnatIds,
		String xnatExperimentScanUrl, Map<String,String> seriesUidToScanIdMap,
		String experimentPath)
	{
		this.xnatIds = xnatIds;
		this.xnatExperimentScanUrl = xnatExperimentScanUrl;
		this.seriesUidToScanIdMap = seriesUidToScanIdMap;
		this.experimentUri = Paths.get(experimentPath).toUri();
		this.dicomwebInput = new DicomwebInput(xnatIds);
	}

	public DicomwebInput getDicomwebInput()
	{
		return dicomwebInput;
	}

	public void notifyItemFound(File file, Attributes attrs)
	{
		URI fileUri = file.toURI();
		String fileRelativePath = experimentUri.relativize(fileUri).getPath();
		processInstance(attrs, fileRelativePath);
	}

	@Override
	public void notifyScanFinish()
	{
		dicomwebInput.validateAndUpdateQueryAttributes();
	}

	@Override
	public void notifyScanStart() {}

	private void processInstance(Attributes instAttrs, String instPath)
	{
		String cuid = instAttrs.getString(Tag.SOPClassUID);

		if (!DicomwebDeviceConfiguration.isDicomwebSopClass(cuid))
		{
			return;
		}

		try
		{
			dicomwebInput.addInstance(instAttrs, instPath);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
