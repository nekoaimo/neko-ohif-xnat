package org.nrg.xnatx.dicomweb.service.qido;

import org.dcm4che3.data.*;
import org.nrg.xnatx.dicomweb.toolkit.QueryRetrieveLevel2;

public enum QidoRsModel
{
	PATIENT(QueryRetrieveLevel2.PATIENT,
		UID.PatientRootQueryRetrieveInformationModelFind)
		{
			@Override
			public void addRetrieveURL(String requestURL, Attributes match)
			{}
		},
	STUDY(QueryRetrieveLevel2.STUDY,
		UID.StudyRootQueryRetrieveInformationModelFind)
		{
			@Override
			public StringBuffer retrieveURL(String requestURL, Attributes match)
			{
				return super.retrieveURL(requestURL, match)
										.append("/studies/")
										.append(match.getString(Tag.StudyInstanceUID));
			}
		},
	SERIES(QueryRetrieveLevel2.SERIES,
		UID.StudyRootQueryRetrieveInformationModelFind)
		{
			@Override
			StringBuffer retrieveURL(String requestURL, Attributes match)
			{
				return STUDY.retrieveURL(requestURL, match)
										.append("/series/")
										.append(match.getString(Tag.SeriesInstanceUID));
			}
		},
	INSTANCE(QueryRetrieveLevel2.IMAGE,
		UID.StudyRootQueryRetrieveInformationModelFind)
		{
			@Override
			StringBuffer retrieveURL(String requestURL, Attributes match)
			{
				return SERIES.retrieveURL(requestURL, match)
										 .append("/instances/")
										 .append(match.getString(Tag.SOPInstanceUID));
			}
		};

	final QueryRetrieveLevel2 qrLevel;
	// For attributes coercion - left to be implemented in the future
	final String sopClassUid;
	Attributes returnKeys;
	boolean includeAll;

	QidoRsModel(QueryRetrieveLevel2 qrLevel, String sopClassUid)
	{
		this.qrLevel = qrLevel;
		this.sopClassUid = sopClassUid;
	}

	void addRetrieveURL(String requestURL, Attributes match)
	{
		match.setString(Tag.RetrieveURL, VR.UR,
			retrieveURL(requestURL, match).toString());
	}

	QueryRetrieveLevel2 getQueryRetrieveLevel()
	{
		return qrLevel;
	}

	String getSOPClassUid()
	{
		return sopClassUid;
	}

	StringBuffer retrieveURL(String requestURL, Attributes match)
	{
		StringBuffer sb = new StringBuffer(requestURL);
		sb.setLength(sb.lastIndexOf("/rs/") + 3);
		return sb;
	}

	void setIncludeAll(boolean includeAll)
	{
		this.includeAll = includeAll;
	}

	void setReturnKeys(Attributes returnKeys)
	{
		this.returnKeys = returnKeys;
	}
}

