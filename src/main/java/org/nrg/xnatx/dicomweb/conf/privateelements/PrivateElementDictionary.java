package org.nrg.xnatx.dicomweb.conf.privateelements;

import org.dcm4che3.data.ElementDictionary;
import org.dcm4che3.data.VR;

public class PrivateElementDictionary extends ElementDictionary
{

	public static final String PrivateCreator = "";

	public PrivateElementDictionary()
	{
		super(PrivateTag.PrivateCreator, PrivateTag.class);
	}

	@Override
	public String keywordOf(int tag)
	{
		return PrivateKeyword.valueOf(tag);
	}

	@Override
	public VR vrOf(int tag)
	{
		switch (tag & 0xFFFF00FF)
		{
			case PrivateTag.XNATProjectID:
			case PrivateTag.XNATSubjectID:
			case PrivateTag.XNATExperimentID:
				return VR.LO;
			case PrivateTag.DataOffsets:
			case PrivateTag.DataLengths:
				return VR.UL;
		}
		return VR.UN;
	}
}