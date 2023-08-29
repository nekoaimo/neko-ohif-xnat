package org.nrg.xnatx.dicomweb.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.nrg.framework.generics.GenericUtils;
import org.nrg.framework.orm.hibernate.AbstractHibernateDAO;
import org.nrg.framework.orm.hibernate.BaseHibernateEntity;
import org.nrg.xnatx.dicomweb.entity.DwPatient;

import java.util.List;

public abstract class DicomwebAbstractDAO<E extends BaseHibernateEntity> extends AbstractHibernateDAO<E>
{
	public static ProjectionList getProjectionList(final String... properties)
	{
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("id"));
		projectionList.add(Projections.property("revision"));

		for (String prop : properties)
		{
			projectionList.add(Projections.property(prop));
		}

		return projectionList;
	}

	public List<E> getAll(final E example, boolean matchAny,
		final String[] excludeProperty, final ProjectionList projectionList,
		boolean isEager)
	{
		if (isEager)
		{
			return getAllByExample(example, excludeProperty, matchAny,
				null, null);
		}
		else
		{
			return getAllByExample(example, excludeProperty, matchAny,
				null, projectionList);
		}
	}

	protected List<E> getAllByExample(final E exampleInstance,
		final String[] excludeProperty, boolean matchAny, Order orderBy,
		final ProjectionList projectionList)
	{
		final Criteria criteria = getCriteriaForType();
		// Create Example
		final Example example = Example.create(exampleInstance);
		for (final String exclude : excludeProperty)
		{
			example.excludeProperty(exclude);
		}

		if (matchAny)
		{
			example.ignoreCase().enableLike(MatchMode.ANYWHERE);
		}
		example.excludeNone().excludeZeroes();
		criteria.add(example);

		// Add projection
		if (projectionList != null)
		{
			// ToDo: Projection is inactive; fix criteria returns empty result
			// criteria.setProjection(projectionList);
		}

		// Add Order by
		if (orderBy != null)
		{
			criteria.addOrder(orderBy);
		}

		return GenericUtils.convertToTypedList(criteria.list(),
			getParameterizedType());
	}

	public List<E> getAllOrdered(E example, boolean matchAny,
		final String[] excludeProperty, final ProjectionList projectionList,
		String orderByProperty, boolean ascending, boolean isEager)
	{
		Order order = ascending ? Order.asc(orderByProperty) : Order.desc(
			orderByProperty);

		if (isEager)
		{
			return getAllByExample(example, excludeProperty, matchAny,
				order, null);
		}
		else
		{
			return getAllByExample(example, excludeProperty, matchAny,
				order, projectionList);
		}
	}
}
