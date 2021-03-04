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
package org.nrg.xnatx.roi.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import icr.xnat.plugin.roi.entity.Roi;
import org.nrg.xnatx.roi.data.RoiRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jamesd
 */
@Service
@JsonIgnoreProperties(value = { "created" })
public class HibernateRoiService
	extends AbstractHibernateEntityService<Roi,RoiRepository>
	implements RoiService
{
	private final static Logger logger =
		LoggerFactory.getLogger(HibernateRoiService.class);

	@Transactional
	@Override
	public void deleteCollectionRois(String collectionId)
	{
		logger.debug("Deleting ROIs for roiCollectionId="+collectionId);
		List<Roi> list = getDao().findByProperty("roiCollectionId", collectionId);
		if (list == null)
		{
			logger.warn("ROI list is null for ROI collection {}", collectionId);
			return;
		}
		for (Roi roi : list)
		{
			delete(roi.getId());
		}
	}

	@Transactional
	@Override
	public List<Roi> getCollectionRois(String collectionId)
	{
		logger.debug("Fetching ROIs for roiCollectionId="+collectionId);
		List<Roi> list = getDao().findByProperty("roiCollectionId", collectionId);
		if (list == null)
		{
			return new ArrayList<>();
		}
		list.sort(new Comparator<Roi>()
		{
			@Override
			public int compare(Roi a, Roi b)
			{
				if (a == null)
				{
					return (b == null) ? 0 : 1;
				}
				else
				{
					if (b == null)
					{
						return -1;
					}
				}
				String aName = a.getName();
				String bName = b.getName();
				if (aName == null)
				{
					return (bName == null) ? 0 : 1;
				}
				else
				{
					if (bName == null)
					{
						return -1;
					}
				}
				return aName.compareTo(bName);
			}
		});
		
		return list;
	}
}
