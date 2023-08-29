package org.nrg.xnatx.dicomweb.service.hibernate;

import org.nrg.framework.orm.hibernate.BaseHibernateService;
import org.nrg.xnatx.dicomweb.entity.DwInstance;
import org.nrg.xnatx.dicomweb.entity.DwSeries;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DwInstanceDataService extends BaseHibernateService<DwInstance>
{
	DwInstance createOrUpdate(DwInstance instance) throws IOException;

	int deleteAll(DwSeries series);

	DwInstance get(DwInstance example, boolean isEager);

	DwInstance get(String propertyName, Object propertyValue, boolean isEager);

	List<DwInstance> getAll(DwInstance example, boolean matchAny,
		boolean isEager);
}
