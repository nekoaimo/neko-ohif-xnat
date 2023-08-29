package org.nrg.xnatx.dicomweb.dao;

import org.hibernate.criterion.ProjectionList;
import org.nrg.framework.orm.hibernate.AbstractHibernateDAO;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;
import org.nrg.xnatx.dicomweb.entity.DwPatient;
import org.nrg.xnatx.dicomweb.entity.DwSeries;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DicomwebSeriesDAO extends DicomwebAbstractDAO<DwSeries>
{
	public static final String[] EXCLUSION_PROPERTIES = AbstractHibernateEntity.getExcludedProperties(
		"encodedAttributes");

	public static final String[] PROJECTION_PROPERTIES =
		{"seriesInstanceUid", "seriesNumber", "seriesDescription",
			"modality", "sopClassUid", "institutionName",
			"institutionalDepartmentName", "stationName", "bodyPartExamined",
			"laterality", "performedProcedureStepInstanceUid", "performedProcedureStepClassUid",
			"performedProcedureStepStartDate", "performedProcedureStepStartTime", "study",
			"numberOfSeriesRelatedInstances", "availableTransferSyntaxUid", "sopClassesInSeries"};

	public static final ProjectionList projectionList =
		DicomwebAbstractDAO.getProjectionList(PROJECTION_PROPERTIES);


	public List<DwSeries> getAll(DwSeries example, boolean matchAny,
		boolean isEager)
	{
		return super.getAll(example, matchAny, EXCLUSION_PROPERTIES, projectionList,
			isEager);
	}

	public List<DwSeries> getAllOrdered(DwSeries example, boolean matchAny,
		String orderByProperty, boolean ascending, boolean isEager)
	{
		return super.getAllOrdered(example, matchAny, EXCLUSION_PROPERTIES,
			projectionList, orderByProperty, ascending,
			isEager);
	}
}
