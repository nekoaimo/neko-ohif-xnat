package org.nrg.xnatx.ohifviewer.entity;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.sql.Clob;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sessionId"})})
public class OhifSessionData extends AbstractHibernateEntity {
    private String sessionId;

    private String revision;

    @Lob
    private Clob sessionJson;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String projectId) {
        this.sessionId = projectId;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Clob getSessionJson() {
        return sessionJson;
    }

    public void setSessionJson(Clob sessionJson) {
        this.sessionJson = sessionJson;
    }
}
