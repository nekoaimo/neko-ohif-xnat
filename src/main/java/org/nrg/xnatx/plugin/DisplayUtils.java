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
package org.nrg.xnatx.plugin;

import java.io.PrintStream;
import java.util.List;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.model.CatEntryI;
import org.nrg.xdat.om.IcrRoicollectiondata;
import org.nrg.xdat.om.IcrRoicollectiondataSeriesuid;
import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.om.base.BaseXnatExperimentdata;
import org.nrg.xft.utils.ResourceFile;

/**
 *
 * @author jamesd
 */
public class DisplayUtils
{
	public static void display(IcrRoicollectiondata collectData)
	{
		display(collectData, "", System.out);
	}

	public static void display(IcrRoicollectiondata collectData,
		PrintStream out)
	{
		display(collectData, "", out);
	}

	public static void display(IcrRoicollectiondata collectData, String indent)
	{
		display(collectData, indent, System.out);
	}

	public static void display(IcrRoicollectiondata collectData, String indent,
		PrintStream out)
	{
		out.println(indent+collectData.getClass().getName());
		out.println(indent+"* CollectionType: "+collectData.getCollectiontype());
		out.println(indent+"* Description: "+collectData.getDescription());
		out.println(indent+"* ID: "+collectData.getId());
		out.println(indent+"* SessionID: "+collectData.getImagesessionId());
		out.println(indent+"* Label: "+collectData.getLabel());
		out.println(indent+"* Name: "+collectData.getName());
		out.println(indent+"* ProjectID: "+collectData.getProject());
		out.println(indent+"* SubjectID: "+collectData.getSubjectid());
		out.println(indent+"* UID: "+collectData.getUid());
		List<IcrRoicollectiondataSeriesuid> seriesUidList =
			collectData.getReferences_seriesuid();
		out.println(indent+"* SeriesUIDs:");
		for (IcrRoicollectiondataSeriesuid seriesUid : seriesUidList)
		{
			out.println(indent+"  "+seriesUid.getSeriesuid());
		}
		try
		{
			String rootPath = collectData.getArchiveRootPath();
			out.println(indent+"* ArchiveRootPath: "+rootPath);
			XnatImagesessiondata sessionData = collectData.getImageSessionData();
			if (sessionData != null)
			{
				String expPath = PluginUtils.getExperimentPath(
					collectData.getImageSessionData());
				List<ResourceFile> fileResources = collectData.getFileResources(
					expPath, true);
				out.println(indent+"* File Resources ("+fileResources.size()+")");
				for (ResourceFile rf : fileResources)
				{
					display(rf, indent+"  ", out);
				}
			}
		}
		catch (BaseXnatExperimentdata.UnknownPrimaryProjectException ex)
		{
			out.println("UnknownPrimaryProjectException: "+ex.getMessage());
		}
		List<XnatResource> outFileList = collectData.getOut_file();
		out.println(indent+"* Out Files ("+outFileList.size()+")");
		for (XnatResource outFile : outFileList)
		{
			display(outFile, indent+"  ", out);
		}
		List<XnatResource> resources = collectData.getResources_resource();
		out.println(indent+"* Resources ("+resources.size()+")");
		for (XnatResource resource : resources)
		{
			display(resource, indent+"  ", out);
		}
	}

	public static void display(ResourceFile rf)
	{
		display(rf, "", System.out);
	}

	public static void display(ResourceFile rf, PrintStream out)
	{
		display(rf, "", out);
	}

	public static void display(ResourceFile rf, String indent)
	{
		display(rf, indent, System.out);
	}

	public static void display(ResourceFile rf, String indent, PrintStream out)
	{
		out.println(indent+rf.getClass().getName());
		out.println(indent+"* AbsolutePath: "+rf.getAbsolutePath());
		out.println(indent+"* XdatPath: "+rf.getXdatPath());
		out.println(indent+"* XPath: "+rf.getXpath());
		out.println(indent+"* Size: "+rf.getSize());
	}

	public static void display(XnatImageassessordata assessorData)
	{
		display(assessorData, "", System.out);
	}

	public static void display(XnatImageassessordata assessorData,
		PrintStream out)
	{
		display(assessorData, "", out);
	}

	public static void display(XnatImageassessordata assessorData, String indent)
	{
		display(assessorData, indent, System.out);
	}

	public static void display(XnatImageassessordata assessorData, String indent,
		PrintStream out)
	{
		out.println(indent+assessorData.getClass().getName());
		out.println(indent+"* Description: "+assessorData.getDescription());
		out.println(indent+"* ID: "+assessorData.getId());
		out.println(indent+"* SessionID: "+assessorData.getImagesessionId());
		out.println(indent+"* Label: "+assessorData.getLabel());
		out.println(indent+"* ProjectID: "+assessorData.getProject());
		try
		{
			String rootPath = assessorData.getArchiveRootPath();
			out.println(indent+"* ArchiveRootPath: "+rootPath);
		}
		catch (BaseXnatExperimentdata.UnknownPrimaryProjectException ex)
		{
			out.println("UnknownPrimaryProjectException: "+ex.getMessage());
		}
		XnatImagesessiondata sessionData = assessorData.getImageSessionData();
		if (sessionData != null)
		{
			String expPath = PluginUtils.getExperimentPath(
				assessorData.getImageSessionData());
			List<ResourceFile> fileResources = assessorData.getFileResources(
				expPath, true);
			out.println(indent+"* File Resources ("+fileResources.size()+")");
			for (ResourceFile rf : fileResources)
			{
				display(rf, indent+"  ", out);
			}
		}
		List<XnatResource> outFileList = assessorData.getOut_file();
		out.println(indent+"* Out Files ("+outFileList.size()+")");
		for (XnatResource outFile : outFileList)
		{
			display(outFile, indent+"  ", out);
		}
		List<XnatResource> resources = assessorData.getResources_resource();
		out.println(indent+"* Resources ("+resources.size()+")");
		for (XnatResource resource : resources)
		{
			display(resource, indent+"  ", out);
		}
	}

	public static void display(XnatResource resource)
	{
		display(resource, "", System.out);
	}

	public static void display(XnatResource resource, PrintStream out)
	{
		display(resource, "", out);
	}

	public static void display(XnatResource resource, String indent)
	{
		display(resource, indent, System.out);
	}

	public static void display(XnatResource resource, String indent,
		PrintStream out)
	{
		out.println(indent+resource.getClass().getName());
		out.println(indent+"* BaseURI: "+resource.getBaseURI());
		out.println(indent+"* CachePath: "+resource.getCachepath());
		out.println(indent+"* Content: "+resource.getContent());
		out.println(indent+"* Description: "+resource.getDescription());
		out.println(indent+"* FileCount: "+resource.getFileCount());
		out.println(indent+"* FileSize: "+resource.getFileSize());
		out.println(indent+"* Format: "+resource.getFormat());
		out.println(indent+"* Label: "+resource.getLabel());
		out.println(indent+"* Note: "+resource.getNote());
		out.println(indent+"* URI: "+resource.getUri());
		out.println(indent+"* XnatAbstractresourceId: "+
			resource.getXnatAbstractresourceId());
		out.println(indent+"* XSIType: "+resource.getXSIType());
	}

	public static void display(XnatResourcecatalog catalog)
	{
		display(catalog, "", System.out);
	}

	public static void display(XnatResourcecatalog catalog, PrintStream out)
	{
		display(catalog, "", out);
	}

	public static void display(XnatResourcecatalog catalog, String indent)
	{
		display(catalog, indent, System.out);
	}

	public static void display(XnatResourcecatalog catalog, String indent,
		PrintStream out)
	{
		out.println(indent+catalog.getClass().getName());
		out.println(indent+"* BaseURI: "+catalog.getBaseURI());
		out.println(indent+"* CachePath: "+catalog.getCachepath());
		out.println(indent+"* Content: "+catalog.getContent());
		out.println(indent+"* Description: "+catalog.getDescription());
		out.println(indent+"* FileCount: "+catalog.getFileCount());
		out.println(indent+"* FileSize: "+catalog.getFileSize());
		out.println(indent+"* Format: "+catalog.getFormat());
		out.println(indent+"* Label: "+catalog.getLabel());
		out.println(indent+"* Note: "+catalog.getNote());
		out.println(indent+"* URI: "+catalog.getUri());
		out.println(indent+"* XnatAbstractresourceId: "+
			catalog.getXnatAbstractresourceId());
		out.println(indent+"* XSIType: "+catalog.getXSIType());

		CatCatalogBean bean = catalog.getCatalog(catalog.getUri());
		if (bean == null)
		{
			return;
		}
		List<CatEntryI> entries = bean.getEntries_entry();
		out.println(indent+"* Catalog Entries ("+entries.size()+")");
		String pad = indent+"  ";
		for (CatEntryI entry : entries)
		{
			out.println(indent+"* Entry");
			out.println(pad+"* Content: "+entry.getContent());
			out.println(pad+"* Description: "+entry.getDescription());
			out.println(pad+"* Format: "+entry.getFormat());
			out.println(pad+"* ID: "+entry.getId());
			out.println(pad+"* Name: "+entry.getName());
			out.println(pad+"* URI: "+entry.getUri());
		}
	}

	private DisplayUtils()
	{}
}
