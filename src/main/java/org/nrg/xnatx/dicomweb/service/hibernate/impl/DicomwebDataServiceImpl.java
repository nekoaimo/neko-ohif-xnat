package org.nrg.xnatx.dicomweb.service.hibernate.impl;

import org.nrg.xnatx.dicomweb.entity.DwInstance;
import org.nrg.xnatx.dicomweb.entity.DwPatient;
import org.nrg.xnatx.dicomweb.entity.DwSeries;
import org.nrg.xnatx.dicomweb.entity.DwStudy;
import org.nrg.xnatx.dicomweb.service.hibernate.*;
import org.nrg.xnatx.dicomweb.service.qido.QidoRsContext;
import org.nrg.xnatx.dicomweb.toolkit.DicomwebInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class DicomwebDataServiceImpl implements DicomwebDataService
{
	private final DwInstanceDataService instanceDataService;
	private final DwPatientDataService patientDataService;
	private final DwSeriesDataService seriesDataService;
	private final DwStudyDataService studyDataService;

	@Autowired
	public DicomwebDataServiceImpl(DwInstanceDataService instanceDataService,
		DwPatientDataService patientDataService,
		DwSeriesDataService seriesDataService, DwStudyDataService studyDataService)
	{
		this.instanceDataService = instanceDataService;
		this.patientDataService = patientDataService;
		this.seriesDataService = seriesDataService;
		this.studyDataService = studyDataService;
	}

	@Override
	@Transactional
	public void createOrUpdate(DicomwebInput dwi) throws IOException
	{
		DwPatient patient = dwi.getPatient();
		DwStudy study = dwi.getStudy();
		List<DwSeries> seriesList = dwi.getSeriesList();
		Map<String,List<DwInstance>> seriesInstancesMap =
			dwi.getSeriesInstancesMap();

		// Delete existing study, its series and all instances
		DwPatient prevPatient = deleteStudyAndUpdatePatient(patient, study);
		DwPatient newPatient = prevPatient != null ? prevPatient
														 : patientDataService.createOrUpdate(patient);

		study.setPatient(newPatient);
		DwStudy newStudy = studyDataService.createOrUpdate(study);

		for (DwSeries series : seriesList)
		{
			series.setStudy(newStudy);
			DwSeries newSeries = seriesDataService.createOrUpdate(series);
			// Create instances
			List<DwInstance> instanceList = seriesInstancesMap.get(
				series.getSeriesInstanceUid());
			for (DwInstance instance : instanceList)
			{
				instance.setSeries(newSeries);
				instanceDataService.createOrUpdate(instance);
			}
		}
	}

	@Override
	@Transactional
	public void deletePatient(DwPatient patient)
	{
		List<DwStudy> matches = getAllStudies(patient, false);
		for (DwStudy entity : matches)
		{
			deleteStudy(entity);
		}
		patientDataService.delete(patient);
	}

	@Override
	@Transactional
	public void deleteSeries(DwSeries series)
	{
		instanceDataService.deleteAll(series);
		seriesDataService.delete(series);
	}

	@Override
	@Transactional
	public void deleteStudy(DwStudy study)
	{
		List<DwSeries> matches = getAllSeries(study, false);
		for (DwSeries entity : matches)
		{
			deleteSeries(entity);
		}
		studyDataService.delete(study);
	}

	@Override
	@Transactional
	public DwPatient deleteStudyAndUpdatePatient(DwPatient patient,
		DwStudy study)
	{
		DwPatient prevPatient = getPatientBySubjectId(patient.getSubjectId(),
			false);

		if (prevPatient == null)
		{
			return null;
		}

		List<DwStudy> studyList = getAllStudies(patient, false);
		if (studyList.isEmpty())
		{
			patientDataService.delete(patient);
			return null;
		}

		int numberOfStudies = studyList.size();
		for (DwStudy prevStudy : studyList)
		{
			if (prevStudy.getSessionId().equals(study.getSessionId()))
			{
				deleteStudy(prevStudy);
				numberOfStudies--;
				break;
			}
		}

		if (numberOfStudies < 1)
		{
			patientDataService.delete(patient);
			return null;
		}

		prevPatient.decrementNumberOfStudies();
		patientDataService.updateQueryAttributes(prevPatient);

		return prevPatient;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DwSeries> getAllSeries(DwStudy study, boolean isEager)
	{
		DwSeries example = new DwSeries();
		example.setStudy(study);

		return seriesDataService.getAll(example, false, isEager);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DwStudy> getAllStudies(DwPatient patient, boolean isEager)
	{
		DwStudy example = new DwStudy();
		example.setPatient(patient);

		return studyDataService.getAll(example, false, isEager);
	}

	@Override
	@Transactional(readOnly = true)
	public DwPatient getPatientBySubjectId(String subjectId, boolean isEager)
	{
		return patientDataService.get("subjectId", subjectId, isEager);
	}

	@Override
	@Transactional(readOnly = true)
	public DwStudy getStudyBySessionId(String sessionId, boolean isEager)
	{
		return studyDataService.get("sessionId", sessionId, isEager);
	}

	@Override
	@Transactional(readOnly = true)
	public void runQidoQuery(QidoRsContext ctx)
	{
		QidoQuery qidoQuery = getQidoQuery(ctx);


	}

	private QidoQuery getQidoQuery(QidoRsContext ctx)
	{
		QidoQuery qidoQuery;

		switch (ctx.getQueryRetrieveLevel()) {
			case PATIENT:
				qidoQuery = QidoQuery.PATIENT;
			case STUDY:
				qidoQuery = QidoQuery.STUDY;
			case SERIES:
				qidoQuery = QidoQuery.SERIES;
			default: // case IMAGE
				qidoQuery = QidoQuery.INSTANCE;
		}

		qidoQuery.ctx = ctx;

		return qidoQuery;
	}
}
