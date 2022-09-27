/*********************************************************************
 * Copyright (c) 2017, Institute of Cancer Research
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * (2) Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *
 * (3) Neither the name of the Institute of Cancer Research nor the
 *     names of its contributors may be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *********************************************************************/
package org.nrg.xnatx.ohifviewer.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.ObjectNotFoundException;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.xnatx.ohifviewer.data.OhifSessionDataRepository;
import org.nrg.xnatx.ohifviewer.entity.OhifSessionData;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.SQLException;

@Service
@JsonIgnoreProperties(value = { "created" })
public class HibernateOhifSessionDataService
	extends AbstractHibernateEntityService<OhifSessionData, OhifSessionDataRepository>
	implements OhifSessionDataService
{
	private final static Logger logger =
		LoggerFactory.getLogger(HibernateOhifSessionDataService.class);

    @Override
    @Transactional
    public OhifSessionData createOrUpdate(OhifSessionData ohifSessionData) {
        OhifSessionData existing = this.getSessionData(ohifSessionData.getSessionId());
        if (existing == null) {
            return create(ohifSessionData);
        }

        existing.setRevision(ohifSessionData.getRevision());
        existing.setSessionJson(ohifSessionData.getSessionJson());
        update(existing);
        return existing;
    }
    
	@Override
	@Transactional(readOnly = true)
	public OhifSessionData getSessionData(String sessionId) {
		try {
			return getDao().findByUniqueProperty("sessionId", sessionId);
		} catch(ObjectNotFoundException e) {
			return null;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public void transferSessionJson(String sessionId, Writer writer) throws PluginException {
		Reader reader = getSessionJsonReader(sessionId);
		try {
			IOUtils.copyLarge(reader, writer);
		} catch (IOException e) {
			if (StringUtils.contains(e.getClass().getName(), "ClientAbortException")) {
				logger.debug("Client aborted request");
				return;
			}
			throw new PluginException("Failed to copy stream,", e);
		}
	}

	private Reader getSessionJsonReader(String sessionId) throws PluginException {
		logger.debug("Fetching session JSON for sessionId={}", sessionId);
		OhifSessionData ohifSessionData = getDao().findByUniqueProperty("sessionId", sessionId);
		if (ohifSessionData == null) {
			throw new PluginException("JSON not found", PluginCode.FileNotFound);
		}

		Reader reader = null;
		try {
			reader = ohifSessionData.getSessionJson().getCharacterStream();
		} catch (SQLException e) {
			throw new PluginException("Failed to obtain stream", e);
		}
		return reader;
	}
}
