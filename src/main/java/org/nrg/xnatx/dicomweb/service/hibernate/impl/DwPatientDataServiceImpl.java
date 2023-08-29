package org.nrg.xnatx.dicomweb.service.hibernate.impl;

import org.hibernate.ObjectNotFoundException;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.xnatx.dicomweb.entity.DwPatient;
import org.nrg.xnatx.dicomweb.dao.DicomwebPatientDAO;
import org.nrg.xnatx.dicomweb.entity.util.EntityProperties;
import org.nrg.xnatx.dicomweb.service.hibernate.DwPatientDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DwPatientDataServiceImpl
	extends AbstractHibernateEntityService<DwPatient,DicomwebPatientDAO>
	implements DwPatientDataService
{
	@Override
	@Transactional
	public DwPatient createOrUpdate(DwPatient patient) throws IOException
	{
		DwPatient existing = this.get("subjectId",
			patient.getSubjectId(), false);
		if (existing == null)
		{
			return create(patient);
		}

		existing.setData(patient.getAttributes());
		updateQueryAttributes(existing, patient);
		update(existing);

		return existing;
	}

	@Override
	@Transactional(readOnly = true)
	public DwPatient get(DwPatient example, boolean isEager)
	{
		List<DwPatient> matches = getAll(example, false, isEager);

		return matches.isEmpty() ? null : matches.get(0);
	}

	@Override
	@Transactional(readOnly = true)
	public DwPatient get(String propertyName, Object propertyValue,
		boolean isEager)
	{
		DwPatient example = new DwPatient();
		EntityProperties.setExampleProp(example, propertyName, propertyValue);

		return get(example, isEager);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DwPatient> getAll(DwPatient example, boolean matchAny,
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
	public void updateQueryAttributes(DwPatient patient)
	{
		DwPatient existing = this.get("subjectId",
			patient.getSubjectId(), false);
		if (existing != null)
		{
			updateQueryAttributes(existing, patient);
			update(existing);
		}
	}

	private void updateQueryAttributes(DwPatient existing, DwPatient patient)
	{
		existing.setNumberOfStudies(patient.getNumberOfStudies());
	}
}
