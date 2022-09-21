package org.nrg.xnatx.ohifviewer.inputcreator;

import icr.etherj.dicom.SopInstance;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.nrg.xnatx.ohifviewer.ViewerUtils;

import java.util.Arrays;


/**
 *
 * @author malsad
 */
public class OhifViewerInputInstanceMetadata extends OhifViewerInputItem
{
    private int AcquisitionNumber;
    private int BitsAllocated;
    private int BitsStored;
    private int Columns;
    private String FrameOfReferenceUID;
    private int HighBit;
    private double[] ImageOrientationPatient;
    private double[] ImagePositionPatient;
    private String[] ImageType;
    private int InstanceNumber;
    private int NumberOfFrames;
    private String PhotometricInterpretation;
    private int PixelRepresentation;
    private double[] PixelSpacing;
    private double RescaleIntercept;
    private double RescaleSlope;
    private String RescaleType;
    private int Rows;
    private int SamplesPerPixel;
    private String SOPClassUID;
    private String SOPInstanceUID;
    private double[] WindowWidth;
    private double[] WindowCenter;

    public OhifViewerInputInstanceMetadata(SopInstance sop)
    {
        Columns = sop.getColumnCount();
        FrameOfReferenceUID = sop.getFrameOfReferenceUid();
        ImageOrientationPatient = sop.getImageOrientationPatient();
        for (int i = 0; i < ImageOrientationPatient.length; i++)
        {
            if (Double.isNaN(ImageOrientationPatient[i]))
                ImageOrientationPatient[i] = 0.0;
        }
        ImagePositionPatient = sop.getImagePositionPatient();
        for (int i = 0; i < ImagePositionPatient.length; i++)
        {
            if (Double.isNaN(ImagePositionPatient[i]))
                ImagePositionPatient[i] = 0.0;
        }
        InstanceNumber = sop.getInstanceNumber();
        NumberOfFrames = sop.getNumberOfFrames();
        PixelSpacing = sop.getPixelSpacing();
        for (int i = 0; i < PixelSpacing.length; i++)
        {
            if (Double.isNaN(PixelSpacing[i]))
                PixelSpacing[i] = 1.0;
        }
        Rows = sop.getRowCount();
        SOPClassUID = sop.getSopClassUid();
        SOPInstanceUID = sop.getUid();

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
            WindowWidth = DataUtils.getDSArray(dcm, Tag.WindowWidth, 0);
            WindowCenter = DataUtils.getDSArray(dcm, Tag.WindowCenter, 0);
            RescaleIntercept = DataUtils.getDSValue(dcm, Tag.RescaleIntercept, 0.0);
            RescaleSlope = DataUtils.getDSValue(dcm, Tag.RescaleSlope, 1.0);
            RescaleType = dcm.getString(Tag.RescaleType, "");
        }
    }

    public int getAcquisitionNumber() {
        return AcquisitionNumber;
    }

    public int getBitsAllocated() {
        return BitsAllocated;
    }

    public int getBitsStored() {
        return BitsStored;
    }

    public int getColumns() {
        return Columns;
    }

    public String getFrameOfReferenceUID() {
        return FrameOfReferenceUID;
    }

    public int getHighBit() {
        return HighBit;
    }

    public double[] getImageOrientationPatient() {
        return ImageOrientationPatient;
    }

    public double[] getImagePositionPatient() {
        return ImagePositionPatient;
    }

    public String[] getImageType() {
        return ImageType;
    }

    public int getInstanceNumber() {
        return InstanceNumber;
    }

    public int getNumberOfFrames() {
        return NumberOfFrames;
    }

    public String getPhotometricInterpretation() {
        return PhotometricInterpretation;
    }

    public int getPixelRepresentation() {
        return PixelRepresentation;
    }

    public double[] getPixelSpacing() {
        return PixelSpacing;
    }

    public int getRows() {
        return Rows;
    }

    public int getSamplesPerPixel() {
        return SamplesPerPixel;
    }

    public String getSOPInstanceUID() {
        return SOPInstanceUID;
    }

    public double[] getWindowWidth() {
        return WindowWidth;
    }

    public double[] getWindowCenter() {
        return WindowCenter;
    }
}


/**
 *
 * @author malsad
 */
class DataUtils
{
    public static double[] getDSArray(DicomObject dcm, int tag, double defaultValue)
    {
        double[] dsArray;

        try
        {
            String[] strarray = dcm.getStrings(tag);

            if (strarray.length > 0)
            {
                dsArray = Arrays.stream(strarray).mapToDouble(Double::parseDouble).toArray();
            }
            else
            {
                throw new Exception("Empty value");
            }
        }
        catch (Exception ex)
        {
            dsArray = new double[]{defaultValue};
        }

        return dsArray;
    }

    public static double getDSValue(DicomObject dcm, int tag, double defaultValue)
    {
        double dsValue;

        try
        {
            String strValue = dcm.getString(tag);

            if (strValue.length() > 0)
            {
                dsValue = Double.parseDouble(strValue);
            }
            else
            {
                throw new Exception("Empty value");
            }
        }
        catch (Exception ex)
        {
            dsValue = defaultValue;
        }

        return dsValue;
    }
}