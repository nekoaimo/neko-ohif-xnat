/* ********************************************************************
 * Copyright (c) 2018, Institute of Cancer Research
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
package org.nrg.xnatx.roi.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import icr.xnat.plugin.roi.entity.DicomSpatialData;
import org.nrg.xnatx.roi.data.DicomSpatialDataRepository;

import java.util.List;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jamesd
 */
@Service
@JsonIgnoreProperties(value = {"created"})
public class HibernateDicomSpatialDataService
        extends AbstractHibernateEntityService<DicomSpatialData, DicomSpatialDataRepository>
        implements DicomSpatialDataService {
    private final static Logger logger = LoggerFactory.getLogger(HibernateDicomSpatialDataService.class);

    @Transactional
    @Override
    public void create(List<DicomSpatialData> list) {
        logger.debug("DicomSpatialData deprecated - NO-OP");
    }

    @Transactional
    @Override
    public void deleteForSeries(String seriesUid) {
        logger.debug("Deleting DicomSpatialData for seriesUid=" + seriesUid);
        List<DicomSpatialData> list = getDao().findByProperty("seriesUid", seriesUid);
        if (list == null) {
            logger.warn("No spatial data found for series UID: " + seriesUid);
            return;
        }
        list.forEach(this::delete);
    }

    @Transactional
    @Override
    public List<DicomSpatialData> findForSeries(String seriesUid) {
        logger.debug("Fetching DicomSpatialData for seriesUid=" + seriesUid);
        return getDao().findByProperty("seriesUid", seriesUid);
    }

}
