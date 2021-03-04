/*********************************************************************
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
package org.nrg.xnatx.roi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.model.CatEntryI;
import org.nrg.xdat.om.IcrRoicollectiondata;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.security.UserI;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;

/**
 *
 * @author jamesd
 */
public class RoiUtils
{

	public static XnatResourcecatalog getCollectionCatalog(UserI user,
		IcrRoicollectiondata collectData, String collectType)
		throws PluginException
	{
		List<XnatResource> list = collectData.getOut_file();
		for (XnatResource resource : list)
		{
			try
			{
				if (resource.getItem().instanceOf("xnat:resourceCatalog") &&
					collectType.equals(resource.getLabel()))
				{
					return (XnatResourcecatalog) resource;
				}
			}
			catch (ElementNotFoundException ex)
			{
				throw new PluginException("Error locating catalog",
					PluginCode.HttpInternalError, ex);
			}
		}
		return null;
	}

	/**
	 *
	 * @param idOrLabel
	 * @return
	 */
	public static IcrRoicollectiondata getCollectionData(String idOrLabel)
	{
		IcrRoicollectiondata data = getCollectionDataById(idOrLabel);
		return (data != null) ? data : getCollectionDataByLabel(idOrLabel);
	}

	/**
	 *
	 * @param projectId
	 * @param idOrLabel
	 * @return
	 */
	public static IcrRoicollectiondata getCollectionData(String projectId,
		String idOrLabel)
	{
		IcrRoicollectiondata data = getCollectionDataById(projectId, idOrLabel);
		return (data != null) ? data : getCollectionDataByLabel(projectId, idOrLabel);
	}

	/**
	 *
	 * @param collectId
	 * @return
	 */
	public static IcrRoicollectiondata getCollectionDataById(String collectId)
	{
		return IcrRoicollectiondata.getIcrRoicollectiondatasById(collectId, null,
			false);
	}

	/**
	 *
	 * @param projectId
	 * @param collectId
	 * @return
	 */
	public static IcrRoicollectiondata getCollectionDataById(String projectId,
		String collectId)
	{
		CriteriaCollection projectCc = new CriteriaCollection("OR");
		projectCc.addClause("icr:roiCollectionData/project", projectId);
		projectCc.addClause("icr:roiCollectionData/sharing/share/project",
			projectId);
		CriteriaCollection cc = new CriteriaCollection("AND");
		cc.addClause(projectCc);
		cc.addClause("icr:roiCollectionData/ID", collectId);
		List<IcrRoicollectiondata> roiCollectList =
			IcrRoicollectiondata.getIcrRoicollectiondatasByField(cc, null, false);
		if ((roiCollectList == null) || roiCollectList.isEmpty())
		{
			return null;
		}
		return roiCollectList.get(0);
	}

	/**
	 *
	 * @param label
	 * @return
	 */
	public static IcrRoicollectiondata getCollectionDataByLabel(String label)
	{
		CriteriaCollection cc = new CriteriaCollection("OR");
		cc.addClause("icr:roiCollectionData/label", label);
		cc.addClause("icr:roiCollectionData/sharing/share/label", label);
		List<IcrRoicollectiondata> roiCollectList =
			IcrRoicollectiondata.getIcrRoicollectiondatasByField(cc, null, false);
		if ((roiCollectList == null) || roiCollectList.isEmpty())
		{
			return null;
		}
		return roiCollectList.get(0);
	}

	/**
	 *
	 * @param projectId
	 * @param label
	 * @return
	 */
	public static IcrRoicollectiondata getCollectionDataByLabel(String projectId,
		String label)
	{
		CriteriaCollection primaryCc = new CriteriaCollection("AND");
		primaryCc.addClause("icr:roiCollectionData/project", projectId);
		primaryCc.addClause("icr:roiCollectionData/label", label);
		CriteriaCollection sharedCc = new CriteriaCollection("AND");
		sharedCc.addClause("icr:roiCollectionData/sharing/share/project", projectId);
		sharedCc.addClause("icr:roiCollectionData/sharing/share/label", label);
		CriteriaCollection cc = new CriteriaCollection("OR");
		cc.addClause(primaryCc);
		cc.addClause(sharedCc);
		List<IcrRoicollectiondata> roiCollectList =
			IcrRoicollectiondata.getIcrRoicollectiondatasByField(cc, null, false);
		if ((roiCollectList == null) || roiCollectList.isEmpty())
		{
			return null;
		}
		return roiCollectList.get(0);
	}

	/**
	 *
	 * @param projectId
	 * @param sessionId
	 * @return
	 */
	public static List<IcrRoicollectiondata> getCollectionDataBySession(
		String projectId, String sessionId)
	{
		CriteriaCollection projectCc = new CriteriaCollection("OR");
		projectCc.addClause("icr:roiCollectionData/project", projectId);
		projectCc.addClause("icr:roiCollectionData/sharing/share/project",
			projectId);
		CriteriaCollection cc = new CriteriaCollection("AND");
		cc.addClause(projectCc);
		cc.addClause("icr:roiCollectionData/imageSession_ID", sessionId);
		List<IcrRoicollectiondata> roiCollectList =
			IcrRoicollectiondata.getIcrRoicollectiondatasByField(cc, null, false);
		return (roiCollectList != null) ? roiCollectList : new ArrayList<>();
	}

	/**
	 *
	 * @param user
	 * @param collectData
	 * @param collectType
	 * @return
	 * @throws PluginException
	 */
	public static File getCollectionFile(UserI user,
		IcrRoicollectiondata collectData, String collectType)
		throws PluginException
	{
		XnatResourcecatalog catalog = getCollectionCatalog(user, collectData,
			collectType);
		if (catalog == null)
		{
			return null;
		}
		String catalogUri = catalog.getUri();
		CatCatalogBean bean = catalog.getCatalog(catalogUri);
		if (bean == null)
		{
			return null;
		}
		List<CatEntryI> entries = bean.getEntries_entry();
		if (entries.size() != 1)
		{
			return null;
		}
		File catFile = new File(catalogUri).getParentFile();
		return new File(
			catFile.getAbsolutePath() + File.separator + entries.get(0).getUri());
	}

	private RoiUtils()
	{}
}
