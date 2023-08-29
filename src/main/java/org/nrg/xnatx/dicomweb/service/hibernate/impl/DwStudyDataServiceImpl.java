package org.nrg.xnatx.dicomweb.service.hibernate.impl;

import org.hibernate.ObjectNotFoundException;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.xnatx.dicomweb.entity.DwStudy;
import org.nrg.xnatx.dicomweb.dao.DicomwebStudyDAO;
import org.nrg.xnatx.dicomweb.entity.util.EntityProperties;
import org.nrg.xnatx.dicomweb.service.hibernate.DwStudyDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DwStudyDataServiceImpl
	extends AbstractHibernateEntityService<DwStudy,DicomwebStudyDAO>
	implements DwStudyDataService
{
	@Override
	@Transactional
	public DwStudy createOrUpdate(DwStudy study) throws IOException
	{
		DwStudy existing = this.get("sessionId",
			study.getSessionId(), false);;
		if (existing == null)
		{
			return create(study);
		}

		existing.setData(study.getAttributes());
		existing.setPatient(study.getPatient());
		updateQueryAttributes(existing, study);
		update(existing);

		return existing;
	}

	@Override
	@Transactional(readOnly = true)
	public DwStudy get(DwStudy example, boolean isEager)
	{
		List<DwStudy> matches = getAll(example, false, isEager);

		return matches.isEmpty() ? null : matches.get(0);
	}

	@Override
	@Transactional(readOnly = true)
	public DwStudy get(String propertyName, Object propertyValue, boolean isEager)
	{
		DwStudy example = new DwStudy();
		EntityProperties.setExampleProp(example, propertyName, propertyValue);

		return get(example, isEager);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DwStudy> getAll(DwStudy example, boolean matchAny,
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
	public void updateQueryAttributes(DwStudy study)
	{
		DwStudy existing = this.get("sessionId",
			study.getSessionId(), false);;

		if (existing != null)
		{
			updateQueryAttributes(existing, study);
			update(existing);
		}
	}

	private void updateQueryAttributes(DwStudy existing, DwStudy study)
	{
		existing.setNumberOfStudyRelatedInstances(
			study.getNumberOfStudyRelatedInstances());
		existing.setNumberOfStudyRelatedSeries(
			study.getNumberOfStudyRelatedSeries());
		existing.setModalitiesInStudy(study.getModalitiesInStudy());
		existing.setSopClassesInStudy(study.getModalitiesInStudy());
	}
}
