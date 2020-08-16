package org.nrg.xnatx.ohifviewer.inputcreator;

import icr.etherj.dicom.SopInstance;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.nrg.xnatx.ohifviewer.ViewerUtils;

import java.util.Arrays;


class DataUtils
{
    public static double[] getDSArray(DicomObject dcm, int tag, double defaultValue)
    {
        double[] dsArray;
        String[] strarray = dcm.getStrings(tag);

        if (strarray.length > 0)
        {
            dsArray = Arrays.stream(strarray).mapToDouble(Double::parseDouble).toArray();
        }
        else
        {
            dsArray = new double[]{defaultValue};
        }

        return dsArray;
    }
}

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

    private int AcquisitionNumber;
    private String PhotometricInterpretation;
    private int BitsAllocated;
    private int BitsStored;
    private int PixelRepresentation;
    private int SamplesPerPixel;
    private int HighBit;
    private String[] ImageType;
    private double[] WindowWidth;
    private double[] WindowCenter;
    private String SOPClassUID;
    private String SeriesDate;
    private String SeriesTime;
    private String StudyDate;
    private String StudyTime;

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
        SOPClassUID = sop.getSopClassUid();
        SeriesDate = sop.getSeriesDate();
        SeriesTime = sop.getSeriesTime();
        StudyDate = sop.getStudyDate();
        StudyTime = sop.getStudyTime();

        if (ViewerUtils.isDisplayableSopClass(sop.getSopClassUid())) {
            DicomObject dcm = sop.getDicomObject();
            String[] emptyStrArray = new String[]{};
            AcquisitionNumber = dcm.getInt(Tag.AcquisitionNumber, 0);
            PhotometricInterpretation = dcm.getString(Tag.PhotometricInterpretation, "");
            BitsAllocated = dcm.getInt(Tag.BitsAllocated, 16);
            BitsStored = dcm.getInt(Tag.BitsStored, 16);
            PixelRepresentation = dcm.getInt(Tag.PixelRepresentation, 1);
            SamplesPerPixel = dcm.getInt(Tag.SamplesPerPixel, 1);
            HighBit = dcm.getInt(Tag.HighBit, 15);
            ImageType = dcm.getStrings(Tag.ImageType, emptyStrArray);
            WindowWidth = DataUtils.getDSArray(dcm, Tag.WindowWidth, 400);
            WindowCenter = DataUtils.getDSArray(dcm, Tag.WindowCenter, 40);
        }
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

    public int getAcquisitionNumber() {
        return AcquisitionNumber;
    }

    public String getPhotometricInterpretation() {
        return PhotometricInterpretation;
    }

    public int getBitsAllocated() {
        return BitsAllocated;
    }

    public int getBitsStored() {
        return BitsStored;
    }

    public int getPixelRepresentation() {
        return PixelRepresentation;
    }

    public int getSamplesPerPixel() {
        return SamplesPerPixel;
    }

    public int getHighBit() {
        return HighBit;
    }

    public String[] getImageType() {
        return ImageType;
    }

    public double[] getWindowWidth() {
        return WindowWidth;
    }

    public double[] getWindowCenter() {
        return WindowCenter;
    }
}
