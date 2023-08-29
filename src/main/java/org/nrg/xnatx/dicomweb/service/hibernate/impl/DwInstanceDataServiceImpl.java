package org.nrg.xnatx.dicomweb.service.hibernate.impl;

import org.hibernate.ObjectNotFoundException;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.xnatx.dicomweb.entity.DwInstance;
import org.nrg.xnatx.dicomweb.dao.DicomwebInstanceDAO;
import org.nrg.xnatx.dicomweb.entity.DwSeries;
import org.nrg.xnatx.dicomweb.entity.util.EntityProperties;
import org.nrg.xnatx.dicomweb.service.hibernate.DwInstanceDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DwInstanceDataServiceImpl
	extends AbstractHibernateEntityService<DwInstance,DicomwebInstanceDAO>
	implements DwInstanceDataService
{
	@Override
	@Transactional
	public DwInstance createOrUpdate(DwInstance instance)
		throws IOException
	{
		DwInstance example = new DwInstance();
		EntityProperties.setExampleProps(example, EntityProperties.newPropsMap(
			new String[]{"series", "sopInstanceUid"},
			new Object[]{instance.getSeries(), instance.getSopInstanceUid()}));

		DwInstance existing = this.get(example, false);
		if (existing == null)
		{
			return create(instance);
		}

		existing.setData(instance.getAttributes());
		existing.setSeries(instance.getSeries());
		update(existing);

		return existing;
	}

	@Override
	public int deleteAll(DwSeries series)
	{
		return getDao().deleteAll(series);
	}

	@Override
	@Transactional(readOnly = true)
	public DwInstance get(DwInstance example, boolean isEager)
	{
		List<DwInstance> matches = getAll(example, false, isEager);

		return matches.isEmpty() ? null : matches.get(0);
	}

	@Override
	@Transactional(readOnly = true)
	public DwInstance get(String propertyName, Object propertyValue,
		boolean isEager)
	{
		DwInstance example = new DwInstance();
		EntityProperties.setExampleProp(example, propertyName, propertyValue);

		return get(example, isEager);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DwInstance> getAll(DwInstance example, boolean matchAny,
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
}
