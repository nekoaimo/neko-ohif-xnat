package org.nrg.xnatx.dicomweb.toolkit;

import org.dcm4che3.data.Tag;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.util.StringUtils;
import org.nrg.xnatx.dicomweb.entity.DwInstance;
import org.nrg.xnatx.dicomweb.entity.DwPatient;
import org.nrg.xnatx.dicomweb.entity.DwSeries;
import org.nrg.xnatx.dicomweb.entity.DwStudy;

import java.io.IOException;
import java.util.*;

@SuppressWarnings("FieldMayBeFinal")
public class DicomwebInput
{
	private final Map<String,String> xnatIds;

	private boolean isValid = false;
	private Set<String> modalitiesInStudy = new HashSet<>();
	private DwPatient patient;
	private Map<String,List<DwInstance>> seriesInstancesMap = new LinkedHashMap<>();
	private List<DwSeries> seriesList = new ArrayList<>();
	private Map<String,Set<String>> sopClassesInSeriesMap = new LinkedHashMap<>();
	private Set<String> sopClassesInStudy = new HashSet<>();
	private Set<String> sopInstanceUids = new HashSet<>();
	private DwStudy study;
	private Map<String,Set<String>> tsuidsInSeriesMap = new LinkedHashMap<>();

	public DicomwebInput(Map<String,String> xnatIds)
	{
		this.xnatIds = xnatIds;
	}

	public void addInstance(Attributes attrs, String instPath) throws IOException
	{
		String instanceUid = attrs.getString(Tag.SOPInstanceUID);
		if (sopInstanceUids.contains(instanceUid))
		{
			return;
		}

		Attributes attrsWithoutBulkData = new Attributes(attrs);
		attrsWithoutBulkData.removeAllBulkData();

		newOrUpdatePatient(attrsWithoutBulkData);
		newOrUpdateStudy(attrsWithoutBulkData);

		DwSeries series = newOrGetSeries(attrsWithoutBulkData);

		DwInstance instance = newInstance(attrsWithoutBulkData, attrs, series);
		instance.setStoragePath(instPath);

		sopInstanceUids.add(instanceUid);
	}

	public DwPatient getPatient()
	{
		return patient;
	}

	public Map<String,List<DwInstance>> getSeriesInstancesMap()
	{
		return seriesInstancesMap;
	}

	public List<DwSeries> getSeriesList()
	{
		return seriesList;
	}

	public DwStudy getStudy()
	{
		return study;
	}

	public boolean isValid()
	{
		return isValid;
	}

	public void validateAndUpdateQueryAttributes()
	{
		isValid = false;

		if (patient == null || study == null)
		{
			return;
		}

		if (seriesList.isEmpty() || seriesInstancesMap.isEmpty())
		{
			return;
		}

		for (String key : seriesInstancesMap.keySet())
		{
			// Instance list
			if (seriesInstancesMap.get(key).isEmpty())
			{
				return;
			}
		}

		// Update Query Attributes
		// Patient - We have one study
		patient.incrementNumberOfStudies();

		// Study
		study.setNumberOfStudyRelatedInstances(sopInstanceUids.size());
		study.setNumberOfStudyRelatedSeries(seriesList.size());
		study.setSopClassesInStudy(StringUtils.concat(sopClassesInStudy, '\\'));
		study.setModalitiesInStudy(StringUtils.concat(modalitiesInStudy, '\\'));

		// Series
		for (DwSeries series : seriesList)
		{
			String seriesUid = series.getSeriesInstanceUid();
			series.setNumberOfSeriesRelatedInstances(
				seriesInstancesMap.get(seriesUid).size());
			series.setAvailableTransferSyntaxUid(
				StringUtils.concat(tsuidsInSeriesMap.get(seriesUid), '\\'));
			series.setAvailableTransferSyntaxUid(
				StringUtils.concat(sopClassesInSeriesMap.get(seriesUid), '\\'));
		}

		isValid = true;
	}

	private DwInstance newInstance(Attributes attrs, Attributes metadata,
		DwSeries series) throws IOException
	{
		String seriesUid = series.getSeriesInstanceUid();

		List<DwInstance> instanceList = seriesInstancesMap.computeIfAbsent(
			seriesUid, k -> new ArrayList<>());

		DwInstance instance = new DwInstance();
		instance.setData(attrs);
		instance.setMetadata(metadata);

		instanceList.add(instance);

		String cuid = attrs.getString(Tag.SOPClassUID);
		Set<String> seriesCuids = sopClassesInSeriesMap.computeIfAbsent(seriesUid,
			k -> new HashSet<>());

		seriesCuids.add(cuid);
		sopClassesInStudy.add(cuid);
		modalitiesInStudy.add(attrs.getString(Tag.Modality));

		String tsuid = attrs.getString(Tag.TransferSyntaxUID);
		instance.setTransferSyntaxUid(tsuid);
		Set<String> seriesTsuids = tsuidsInSeriesMap.computeIfAbsent(seriesUid,
			k -> new HashSet<>());
		seriesTsuids.add(tsuid);

		return instance;
	}

	private DwSeries newOrGetSeries(Attributes attrs) throws IOException
	{
		for (DwSeries series : seriesList)
		{
			if (series.getSeriesInstanceUid().equals(
				attrs.getString(Tag.SeriesInstanceUID)))
			{
				return series;
			}
		}

		DwSeries series = new DwSeries();
		series.setData(attrs);

		seriesList.add(series);

		return series;
	}

	private void newOrUpdatePatient(Attributes attrs) throws IOException
	{
		if (patient != null)
		{
			return;
		}

		patient = new DwPatient();
		patient.setData(attrs);
		patient.setSubjectId(xnatIds.get(DicomwebConstants.XNAT_SUBJECT_ID));
	}

	private void newOrUpdateStudy(Attributes attrs) throws IOException
	{
		if (study != null)
		{
			return;
		}

		study = new DwStudy();
		study.setData(attrs);
		study.setSessionId(xnatIds.get(DicomwebConstants.XNAT_SESSION_ID));
	}
}
