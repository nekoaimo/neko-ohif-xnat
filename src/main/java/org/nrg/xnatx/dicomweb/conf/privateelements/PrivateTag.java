package org.nrg.xnatx.dicomweb.conf.privateelements;

public class PrivateTag
{
	public static final String PrivateCreator = "ICR XNAT Viewer";

	/**
	 * (7777,xx10) VR=LO VM=1 XNAT Project ID
	 */
	public static final int XNATProjectID = 0x77770010;

	/**
	 * (7777,xx11) VR=LO VM=1 XNAT Subject ID
	 */
	public static final int XNATSubjectID = 0x77770011;

	/**
	 * (7777,xx12) VR=LO VM=1 XNAT Experiment ID
	 */
	public static final int XNATExperimentID = 0x77770012;

	/**
	 * (7777,xxFF) VR=UL VM=1-n Data Offsets
	 */
	public static final int DataOffsets = 0x777700FE;

	/**
	 * (7777,xxFF) VR=UL VM=1-n Data Lengths
	 */
	public static final int DataLengths = 0x777700FF;
}
