package org.nrg.xnatx.dicomweb.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;
import org.nrg.xnatx.dicomweb.entity.DwInstance;
import org.nrg.xnatx.dicomweb.entity.DwSeries;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class DicomwebInstanceDAO extends DicomwebAbstractDAO<DwInstance>
{
	public static final String[] EXCLUSION_PROPERTIES = AbstractHibernateEntity.getExcludedProperties(
		"encodedAttributes", "encodedMetadata");

	public static final String[] PROJECTION_PROPERTIES =
		{"sopInstanceUid", "sopClassUid", "instanceNumber",
			"contentDate", "contentTime", "numberOfFrames",
			"storagePath", "dataOffsets", "dataLengths",
			"laterality", "series"};

	private static final String QUERY_DELETE_ALL_INSTANCES_OF_SERIES =
		"DELETE FROM DwInstance i WHERE i.series=:series";

	public static final ProjectionList projectionList =
		DicomwebAbstractDAO.getProjectionList(PROJECTION_PROPERTIES);


	public List<DwInstance> getAll(DwInstance example, boolean matchAny,
		boolean isEager)
	{
		return super.getAll(example, matchAny, EXCLUSION_PROPERTIES, projectionList,
			isEager);
	}

	public List<DwInstance> getAllOrdered(DwInstance example, boolean matchAny,
		String orderByProperty, boolean ascending, boolean isEager)
	{
		return super.getAllOrdered(example, matchAny, EXCLUSION_PROPERTIES,
			projectionList, orderByProperty, ascending,
			isEager);
	}

	public int deleteAll(DwSeries series)
	{
		Session session = getSession();
		session.flush();
		session.clear();
		Query query = session.createQuery(QUERY_DELETE_ALL_INSTANCES_OF_SERIES);
		query.setParameter("series", series);

		int deletedCount = query.executeUpdate();

		return deletedCount;
	}
}
