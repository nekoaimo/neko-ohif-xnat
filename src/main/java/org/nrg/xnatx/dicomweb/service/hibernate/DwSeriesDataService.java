package org.nrg.xnatx.dicomweb.service.hibernate;

import org.nrg.framework.orm.hibernate.BaseHibernateService;
import org.nrg.xnatx.dicomweb.entity.DwSeries;

import java.io.IOException;
import java.util.List;

public interface DwSeriesDataService extends BaseHibernateService<DwSeries>
{
	DwSeries createOrUpdate(DwSeries series) throws IOException;

	DwSeries get(DwSeries example, boolean isEager);

	DwSeries get(String propertyName, Object propertyValue, boolean isEager);

	List<DwSeries> getAll(DwSeries example, boolean matchAny, boolean isEager);

	void updateQueryAttributes(DwSeries series);
}
