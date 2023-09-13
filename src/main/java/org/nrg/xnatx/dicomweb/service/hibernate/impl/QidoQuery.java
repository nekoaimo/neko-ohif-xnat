package org.nrg.xnatx.dicomweb.service.hibernate.impl;

import org.dcm4che3.data.Attributes;
import org.nrg.xnatx.dicomweb.entity.DwEntity;
import org.nrg.xnatx.dicomweb.service.hibernate.DicomwebDataService;
import org.nrg.xnatx.dicomweb.service.qido.QidoRsContext;

import java.util.List;

public enum QidoQuery
{
	PATIENT() {
		@Override
		void executeQuery(DicomwebDataService service)
		{

		}

		@Override
		long fetchCount(DicomwebDataService service)
		{
			return 0;
		}

		@Override
		List<Attributes> toAttributes(List<DwEntity> matches)
		{
			return null;
		}
	},
	STUDY() {
		@Override
		void executeQuery(DicomwebDataService service)
		{
//			Attributes keys = ctx.
		}

		@Override
		long fetchCount(DicomwebDataService service)
		{
			return 0;
		}

		@Override
		List<Attributes> toAttributes(List<DwEntity> matches)
		{
			return null;
		}
	},
	SERIES() {
		@Override
		void executeQuery(DicomwebDataService service)
		{

		}

		@Override
		long fetchCount(DicomwebDataService service)
		{
			return 0;
		}

		@Override
		List<Attributes> toAttributes(List<DwEntity> matches)
		{
			return null;
		}
	},
	INSTANCE() {
		@Override
		void executeQuery(DicomwebDataService service)
		{

		}

		@Override
		long fetchCount(DicomwebDataService service)
		{
			return 0;
		}

		@Override
		List<Attributes> toAttributes(List<DwEntity> matches)
		{
			return null;
		}
	};

	protected QidoRsContext ctx;
	// ToDo - NOTE: limit, size and fetchSize are not used
	private int offset;
	private int limit;
	private int fetchSize;
	private int matches;

	QidoQuery() {}

	Attributes adjust(Attributes match)
	{
		if (match == null)
		{
			return null;
		}

		Attributes returnKeys = ctx.getReturnKeys();
		if (returnKeys == null)
		{
			return match;
		}

		Attributes filtered = new Attributes(returnKeys.size());
		filtered.addSelected(match, returnKeys);
		filtered.supplementEmpty(returnKeys);
		return filtered;
	}

	abstract void executeQuery(DicomwebDataService service);

	abstract long fetchCount(DicomwebDataService service);

	abstract List<Attributes> toAttributes(List<DwEntity> matches);
}
