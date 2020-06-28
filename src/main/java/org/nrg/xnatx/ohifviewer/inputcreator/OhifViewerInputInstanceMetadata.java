package org.nrg.xnatx.ohifviewer.inputcreator;

import icr.etherj.dicom.SopInstance;

/**
 *
 * @author malsad
 */
public class OhifViewerInputInstanceMetadata extends OhifViewerInputItem
{
    private int Columns;
    private int Rows;
    private int InstanceNumber;
    private double[] PixelSpacing;
    private double[] ImageOrientationPatient;
    private double[] ImagePositionPatient;
    private String FrameOfReferenceUID;
    private String Modality;
    private String SOPInstanceUID;
    private String SeriesInstanceUID;
    private String StudyInstanceUID;
    private int NumberOfFrames;

    public OhifViewerInputInstanceMetadata(SopInstance sop)
    {
        Columns = sop.getColumnCount();
        Rows = sop.getRowCount();
        InstanceNumber = sop.getInstanceNumber();
        PixelSpacing = sop.getPixelSpacing();
        ImageOrientationPatient = sop.getImageOrientationPatient();
        ImagePositionPatient = sop.getImagePositionPatient();
        FrameOfReferenceUID = sop.getFrameOfReferenceUid();
        Modality = sop.getModality();
        SOPInstanceUID = sop.getUid();
        SeriesInstanceUID = sop.getSeriesUid();
        StudyInstanceUID = sop.getStudyUid();
        NumberOfFrames = sop.getNumberOfFrames();
    }

    public int getColumns() {
        return Columns;
    }

    public int getRows() {
        return Rows;
    }

    public int getInstanceNumber() {
        return InstanceNumber;
    }

    public double[] getPixelSpacing() {
        return PixelSpacing;
    }

    public double[] getImageOrientationPatient() {
        return ImageOrientationPatient;
    }

    public double[] getImagePositionPatient() {
        return ImagePositionPatient;
    }

    public String getFrameOfReferenceUID() {
        return FrameOfReferenceUID;
    }

    public String getModality() {
        return Modality;
    }

    public String getSOPInstanceUID() {
        return SOPInstanceUID;
    }

    public String getSeriesInstanceUID() {
        return SeriesInstanceUID;
    }

    public String getStudyInstanceUID() {
        return StudyInstanceUID;
    }

    public int getNumberOfFrames() {
        return NumberOfFrames;
    }
}
