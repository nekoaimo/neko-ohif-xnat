package org.nrg.xnatx.dicomweb.service.hibernate;

import org.nrg.xnatx.dicomweb.entity.DwPatient;
import org.nrg.xnatx.dicomweb.entity.DwSeries;
import org.nrg.xnatx.dicomweb.entity.DwStudy;
import org.nrg.xnatx.dicomweb.service.qido.QidoRsContext;
import org.nrg.xnatx.dicomweb.toolkit.DicomwebInput;

import java.io.IOException;
import java.util.List;

public interface DicomwebDataService
{
	void createOrUpdate(DicomwebInput dwi) throws IOException;

	void deletePatient(DwPatient patient);

	void deleteSeries(DwSeries series);

	void deleteStudy(DwStudy study);

	DwPatient deleteStudyAndUpdatePatient(DwPatient patient, DwStudy study);

	List<DwSeries> getAllSeries(DwStudy study, boolean isEager);

	List<DwStudy> getAllStudies(DwPatient patient, boolean isEager);

	DwPatient getPatientBySubjectId(String subjectId, boolean isEager);

	DwStudy getStudyBySessionId(String sessionId, boolean isEager);

	void runQidoQuery(QidoRsContext ctx);

	/*
	////////////////////
	void checkAndDeleteLeaves(DwPatient patient);

	DwPatient createOrUpdatePatient(DwPatient patient) throws IOException;

	DwStudy createOrUpdateStudy(DwStudy study) throws IOException;

	DwInstance createOrUpdateInstance(DwInstance instance)
		throws IOException;

	DwSeries createOrUpdateSeries(DwSeries series) throws IOException;

	void deletePatient(DwPatient patient);

	void deleteSeries(DwSeries series);

	void deleteStudy(DwStudy study);

	DwInstance getInstance(DwSeries series, String sopInstanceUid);

	DwPatient getPatient(String subjectId);

	List<DwStudy> getPatientStudies(DwPatient patient);

	DwSeries getSeries(DwStudy study, String seriesInstanceUid);

	List<DwInstance> getSeriesInstances(DwSeries series);

	DwStudy getStudy(String sessionId);

	List<DwSeries> getStudySeries(DwStudy study);

	void updatePatientQueryAttributes(DwPatient patient);

	void updateSeriesQueryAttributes(DwSeries series);

	void updateStudyQueryAttributes(DwStudy study);
	*/
}
