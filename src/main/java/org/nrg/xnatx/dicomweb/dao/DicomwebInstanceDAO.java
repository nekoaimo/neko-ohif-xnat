/********************************************************************
 * Copyright (c) 2023, Institute of Cancer Research
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
package org.nrg.xnatx.dicomweb.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Restrictions;
import org.nrg.xnatx.dicomweb.entity.DwInstance;
import org.nrg.xnatx.dicomweb.entity.DwSeries;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DicomwebInstanceDAO extends DicomwebAbstractDAO<DwInstance>
{
	public static final String[] EXCLUSION_PROPERTIES =
		{"encodedAttributes", "encodedMetadata",
			"enabled", "created", "timestamp", "disabled"};

	public static final String[] PROJECTION_PROPERTIES =
		{"sopInstanceUid", "sopClassUid", "instanceNumber",
			"contentDate", "contentTime", "numberOfFrames",
			"filename", "transferSyntaxUid", "series"};

	private static final String QUERY_DELETE_ALL_INSTANCES_OF_SERIES =
		"DELETE FROM DwInstance i WHERE i.series=:series";

	public static final ProjectionList projectionList =
		DicomwebAbstractDAO.getProjectionList(PROJECTION_PROPERTIES);

	public List<DwInstance> getAll(DwInstance example, boolean isEager)
	{
		List<Criterion> extraCriteria = new ArrayList<>();
		if (example.getSeries() != null)
		{
			extraCriteria.add(
				Restrictions.eq("series", example.getSeries()));
		}

		return super.getAll(example, EXCLUSION_PROPERTIES, projectionList,
			isEager, extraCriteria);
	}

	public int deleteAll(DwSeries series)
	{
		Session session = getSession();
		session.flush();
		session.clear();
		Query query = session.createQuery(QUERY_DELETE_ALL_INSTANCES_OF_SERIES);
		query.setParameter("series", series);

		int deletedCount = query.executeUpdate();

		return deletedCount;
	}
}
