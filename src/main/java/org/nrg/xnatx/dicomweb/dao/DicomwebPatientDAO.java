package org.nrg.xnatx.dicomweb.dao;

import org.hibernate.criterion.ProjectionList;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;
import org.nrg.xnatx.dicomweb.entity.DwPatient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DicomwebPatientDAO extends DicomwebAbstractDAO<DwPatient>
{
	public static final String[] EXCLUSION_PROPERTIES =
		AbstractHibernateEntity.getExcludedProperties("encodedAttributes");
	public static final String[] PROJECTION_PROPERTIES =
		{"subjectId", "patientId", "patientName",
			"patientBirthDate", "patientSex", "numberOfStudies"};

	public static final ProjectionList projectionList =
		DicomwebAbstractDAO.getProjectionList(PROJECTION_PROPERTIES);


	public List<DwPatient> getAll(DwPatient example, boolean matchAny,
		boolean isEager)
	{
		return super.getAll(example, matchAny, EXCLUSION_PROPERTIES, projectionList,
			isEager);
	}

	public List<DwPatient> getAllOrdered(DwPatient example, boolean matchAny,
		String orderByProperty, boolean ascending, boolean isEager)
	{
		return super.getAllOrdered(example, matchAny, EXCLUSION_PROPERTIES,
			projectionList, orderByProperty, ascending,
			isEager);
	}
}
