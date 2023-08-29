package org.nrg.xnatx.dicomweb.service.hibernate.impl;

import org.hibernate.ObjectNotFoundException;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.xnatx.dicomweb.entity.DwSeries;
import org.nrg.xnatx.dicomweb.dao.DicomwebSeriesDAO;
import org.nrg.xnatx.dicomweb.entity.util.EntityProperties;
import org.nrg.xnatx.dicomweb.service.hibernate.DwSeriesDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DwSeriesDataServiceImpl
	extends AbstractHibernateEntityService<DwSeries,DicomwebSeriesDAO>
	implements DwSeriesDataService
{
	@Override
	@Transactional
	public DwSeries createOrUpdate(DwSeries series) throws IOException
	{
		DwSeries example = new DwSeries();
		EntityProperties.setExampleProps(example, EntityProperties.newPropsMap(
			new String[]{"study", "seriesInstanceUid"},
			new Object[]{series.getStudy(), series.getSeriesInstanceUid()}));

		DwSeries existing = this.get(example, false);
		if (existing == null)
		{
			return create(series);
		}

		existing.setData(series.getAttributes());
		existing.setStudy(series.getStudy());
		updateQueryAttributes(existing, series);
		update(existing);

		return existing;
	}

	@Override
	@Transactional(readOnly = true)
	public DwSeries get(DwSeries example, boolean isEager)
	{
		List<DwSeries> matches = getAll(example, false, isEager);

		return matches.isEmpty() ? null : matches.get(0);
	}

	@Override
	@Transactional(readOnly = true)
	public DwSeries get(String propertyName, Object propertyValue,
		boolean isEager)
	{
		DwSeries example = new DwSeries();
		EntityProperties.setExampleProp(example, propertyName, propertyValue);

		return get(example, isEager);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DwSeries> getAll(DwSeries example, boolean matchAny,
		boolean isEager)
	{
		try
		{
			return getDao().getAll(example, matchAny, isEager);
		}
		catch (ObjectNotFoundException e)
		{
			return new ArrayList<>();
		}
	}

	@Override
	@Transactional
	public void updateQueryAttributes(DwSeries series)
	{
		DwSeries example = new DwSeries();
		EntityProperties.setExampleProps(example, EntityProperties.newPropsMap(
			new String[]{"study", "seriesInstanceUid"},
			new Object[]{series.getStudy(), series.getSeriesInstanceUid()}));

		DwSeries existing = this.get(example,false);
		if (existing != null)
		{
			updateQueryAttributes(existing, series);
			update(existing);
		}
	}

	private void updateQueryAttributes(DwSeries existing, DwSeries series)
	{
		existing.setNumberOfSeriesRelatedInstances(
			series.getNumberOfSeriesRelatedInstances());
		existing.setAvailableTransferSyntaxUid(
			series.getAvailableTransferSyntaxUid());
		existing.setSopClassesInSeries(series.getSopClassesInSeries());
	}
}
