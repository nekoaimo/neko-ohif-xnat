package org.nrg.xnatx.ohifviewer.inputcreator;

import icr.etherj.PathScanContext;
import icr.etherj.dicom.*;
import org.dcm4che2.data.DicomObject;
import org.nrg.xnatx.ohifviewer.ViewerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class CustomDicomReceiver implements PathScanContext<DicomObject> {
    private static final Logger logger = LoggerFactory.getLogger(
            CustomDicomReceiver.class);

    private final String xnatExperimentScanUrl;
    private final Map<String,String> seriesUidToScanIdMap;

    private Map<String, OhifViewerInputStudy> studyMap;
    private Map<String, OhifViewerInputInstance> sopInstMap;
    private OhifViewerInput ohifViewerInput;

    private final DicomToolkit toolkit;

    public CustomDicomReceiver(String transactionId, String xnatExperimentScanUrl, Map<String,String> seriesUidToScanIdMap) {
        this.xnatExperimentScanUrl = xnatExperimentScanUrl;
        this.seriesUidToScanIdMap = seriesUidToScanIdMap;

        ohifViewerInput = new OhifViewerInput();
        ohifViewerInput.setTransactionId(transactionId);

        this.toolkit = DicomToolkit.getToolkit();
    }

    public OhifViewerInput getOhifViewerInput() {
        return ohifViewerInput;
    }

    @Override
    public void notifyItemFound(File file, DicomObject dcm) {
        processSopInst(toolkit.createSopInstance(file, dcm));
    }

    @Override
    public void notifyScanFinish() {
        List<OhifViewerInputStudy> oviStudyList = new ArrayList<>(studyMap.values());

        ohifViewerInput.setStudies(oviStudyList);
    }

    @Override
    public void notifyScanStart() {
        studyMap = new LinkedHashMap<>();
        sopInstMap = new HashMap<>();
    }

    private void processSopInst(SopInstance sopInst)
    {
        String uid = sopInst.getUid();

        if (sopInstMap.containsKey(uid) ||
                !ViewerUtils.isDisplayableSopClass(sopInst.getSopClassUid())) {
            return;
        }

        String studyUid = sopInst.getStudyUid();
        OhifViewerInputStudy study = studyMap.get(studyUid);
        if (study == null) {
            study = new OhifViewerInputStudy(toolkit.createStudy(sopInst),
                    toolkit.createPatient(sopInst));
            study.allocateStudyTime(sopInst);
            studyMap.put(studyUid, study);
        }

        String seriesUid = sopInst.getSeriesUid();
        OhifViewerInputSeries series = study.getSeries(seriesUid);
        if (series == null) {
            series = new OhifViewerInputSeries(toolkit.createSeries(sopInst));
            study.addSeries(series);
        }

        String scanId = seriesUidToScanIdMap.get(seriesUid);
        if ((scanId == null) || scanId.isEmpty())
        {
            logger.warn("Series UID "+seriesUid+" has a null or empty scan ID");
            return;
        }

        OhifViewerInputInstance instance = new OhifViewerInputInstance(sopInst, xnatExperimentScanUrl, scanId);
        series.addInstances(instance);
        sopInstMap.put(uid, instance);
    }
}
