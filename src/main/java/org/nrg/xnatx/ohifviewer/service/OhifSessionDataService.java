package org.nrg.xnatx.ohifviewer.service;

import org.nrg.framework.orm.hibernate.BaseHibernateService;
import org.nrg.xnatx.ohifviewer.entity.OhifSessionData;
import org.nrg.xnatx.plugin.PluginException;

import java.io.Writer;

public interface OhifSessionDataService extends BaseHibernateService<OhifSessionData> {
    OhifSessionData createOrUpdate(OhifSessionData ohifSessionData);

    OhifSessionData getSessionData(String sessionId);

    void transferSessionJson(String sessionId, Writer writer) throws PluginException;
}
