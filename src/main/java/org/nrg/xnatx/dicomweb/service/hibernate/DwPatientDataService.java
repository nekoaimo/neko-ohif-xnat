package org.nrg.xnatx.dicomweb.service.hibernate;

import org.nrg.framework.orm.hibernate.BaseHibernateService;
import org.nrg.xnatx.dicomweb.entity.DwPatient;

import java.io.IOException;
import java.util.List;

public interface DwPatientDataService extends BaseHibernateService<DwPatient>
{
	DwPatient createOrUpdate(DwPatient patient) throws IOException;

	DwPatient get(DwPatient example, boolean isEager);

	DwPatient get(String propertyName, Object propertyValue, boolean isEager);

	List<DwPatient> getAll(DwPatient example, boolean matchAny, boolean isEager);

	void updateQueryAttributes(DwPatient patient);
}
