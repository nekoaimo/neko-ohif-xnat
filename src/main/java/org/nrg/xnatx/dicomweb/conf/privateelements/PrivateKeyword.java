package org.nrg.xnatx.dicomweb.conf.privateelements;

public class PrivateKeyword
{

	public static final String PrivateCreator = "";

	public static String valueOf(int tag)
	{
		switch (tag & 0xFFFF00FF)
		{
			case PrivateTag.XNATProjectID:
				return "XNATProjectID";
			case PrivateTag.XNATSubjectID:
				return "XNATSubjectID";
			case PrivateTag.XNATExperimentID:
				return "XNATExperimentID";
			case PrivateTag.DataOffsets:
				return "DataOffsets";
			case PrivateTag.DataLengths:
				return "DataLengths";
		}
		return "";
	}

}
