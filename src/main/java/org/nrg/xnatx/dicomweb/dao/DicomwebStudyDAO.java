package org.nrg.xnatx.dicomweb.dao;

import org.hibernate.criterion.ProjectionList;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;
import org.nrg.xnatx.dicomweb.entity.DwStudy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DicomwebStudyDAO extends DicomwebAbstractDAO<DwStudy>
{
	public static final String[] EXCLUSION_PROPERTIES =
		AbstractHibernateEntity.getExcludedProperties("encodedAttributes");

	public static final String[] PROJECTION_PROPERTIES =
		{"sessionId", "studyInstanceUid", "studyId",
			"studyDate", "studyTime", "accessionNumber",
			"studyDescription", "patient", "numberOfStudyRelatedInstances",
			"numberOfStudyRelatedSeries", "modalitiesInStudy", "sopClassesInStudy",
			"projectId"};

	public static final ProjectionList projectionList =
		DicomwebAbstractDAO.getProjectionList(PROJECTION_PROPERTIES);


	public List<DwStudy> getAll(DwStudy example, boolean matchAny,
		boolean isEager)
	{
		return super.getAll(example, matchAny, EXCLUSION_PROPERTIES, projectionList,
			isEager);
	}

	public List<DwStudy> getAllOrdered(DwStudy example, boolean matchAny,
		String orderByProperty, boolean ascending, boolean isEager)
	{
		return super.getAllOrdered(example, matchAny, EXCLUSION_PROPERTIES,
			projectionList, orderByProperty, ascending,
			isEager);
	}
}
