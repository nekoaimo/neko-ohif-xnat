package org.nrg.xnatx.ohifviewer.inputcreator;

import icr.etherj2.PathScanContext;
import icr.etherj2.dicom.DicomToolkit;
import icr.etherj2.dicom.SopInstance;
import org.dcm4che3.data.Attributes;
import org.nrg.xnatx.ohifviewer.ViewerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomDicomReceiver implements PathScanContext<Attributes> {
    private static final Logger logger = LoggerFactory.getLogger(
            CustomDicomReceiver.class);

    private final String xnatExperimentScanUrl;
    private final Map<String,String> seriesUidToScanIdMap;

    private Map<String, OhifViewerInputStudy> studyMap;
    private Map<String, OhifViewerInputInstance> sopInstMap;
    private final OhifViewerInput ohifViewerInput;

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
    public void notifyItemFound(Path path, Attributes dcm)
    {
        processSopInst(toolkit.createSopInstance(path.toFile(), dcm));
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
            study = new OhifViewerInputStudy(toolkit.createStudy(sopInst), toolkit.createPatient(sopInst));
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
