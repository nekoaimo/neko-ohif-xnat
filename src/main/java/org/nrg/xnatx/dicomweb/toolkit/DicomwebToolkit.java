package org.nrg.xnatx.dicomweb.toolkit;

import icr.etherj.PathScan;
import org.dcm4che3.data.Attributes;

public class DicomwebToolkit
{
	private static final DicomwebToolkit toolkit;

	static {
		toolkit = new DicomwebToolkit();
	}

	protected DicomwebToolkit() {}

	public static DicomwebToolkit getToolkit() {
		return toolkit;
	}

	public PathScan<Attributes> createPathScan()
	{
		return new DicomwebPathScan();
	}
}
