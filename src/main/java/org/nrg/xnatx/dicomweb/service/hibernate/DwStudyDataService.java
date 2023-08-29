package org.nrg.xnatx.dicomweb.service.hibernate;

import org.nrg.framework.orm.hibernate.BaseHibernateService;
import org.nrg.xnatx.dicomweb.entity.DwStudy;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DwStudyDataService extends BaseHibernateService<DwStudy>
{
	DwStudy createOrUpdate(DwStudy study) throws IOException;

	DwStudy get(DwStudy example, boolean isEager);

	DwStudy get(String propertyName, Object propertyValue, boolean isEager);

	List<DwStudy> getAll(DwStudy example, boolean matchAny, boolean isEager);

	void updateQueryAttributes(DwStudy study);
}
