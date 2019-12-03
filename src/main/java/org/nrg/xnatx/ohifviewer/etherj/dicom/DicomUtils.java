/*********************************************************************
 * Copyright (c) 2017, Institute of Cancer Research
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
package org.nrg.xnatx.ohifviewer.etherj.dicom;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomCodingException;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.DicomOutputStream;
import org.nrg.xnatx.ohifviewer.etherj.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for <code>ether.dicom</code> package.
 * @author jamesd
 */
public class DicomUtils
{
	private static final Logger logger = LoggerFactory.getLogger(DicomUtils.class);
	private static final Set<String> imageSopClasses = new HashSet<>();
	private static final Set<String> multiframeSopClasses = new HashSet<>();

	static
	{
		// Image
		imageSopClasses.add(UID.EnhancedMRImageStorage);
		imageSopClasses.add(UID.MRImageStorage);
		imageSopClasses.add(UID.MRSpectroscopyStorage);
		imageSopClasses.add(UID.PositronEmissionTomographyImageStorage);
		imageSopClasses.add(UID.CTImageStorage);
		imageSopClasses.add(UID.EnhancedCTImageStorage);
		imageSopClasses.add(UID.UltrasoundImageStorage);
		imageSopClasses.add(UID.DigitalMammographyXRayImageStorageForProcessing);
		imageSopClasses.add(UID.DigitalMammographyXRayImageStorageForPresentation);
		imageSopClasses.add(UID.DigitalXRayImageStorageForProcessing);
		imageSopClasses.add(UID.DigitalXRayImageStorageForPresentation);
		imageSopClasses.add(UID.XRayRadiofluoroscopicImageStorage);
		imageSopClasses.add(UID.ComputedRadiographyImageStorage);
		imageSopClasses.add(UID.SecondaryCaptureImageStorage);
		imageSopClasses.add(UID.NuclearMedicineImageStorage);
		// Multiframe
		multiframeSopClasses.add(UID.EnhancedMRImageStorage);
		multiframeSopClasses.add(UID.EnhancedCTImageStorage);
	}

	/**
	 * Returns a <code>double</code> array from the supplied <code>byte</code>
	 * array with {@link ByteOrder#LITTLE_ENDIAN} byte ordering.
	 * @param bytes the byte array
	 * @return the double array
	 */
	public static double[] bytesToDoubles(byte[] bytes)
	{
		return bytesToDoubles(bytes, ByteOrder.LITTLE_ENDIAN);
	}

	/**
	 * Returns a <code>double</code> array from the supplied <code>byte</code>
	 * array and byte ordering.
	 * @param bytes the byte array
	 * @param order the byte ordering
	 * @return the double array
	 */
	public static double[] bytesToDoubles(byte[] bytes, ByteOrder order)
	{
		int nDoubles = bytes.length/8;
		double[] result = new double[nDoubles];
		ByteBuffer bb = ByteBuffer.wrap(bytes).order(order);
		for (int i=0; i<nDoubles; i++)
		{
			result[i] = bb.getDouble(i*8);
		}
		return result;
	}

	/**
	 * Returns an <code>int</code> representing the supplied date.
	 * @param date the date
	 * @return the integer
	 */
	public static int dateToInt(String date)
	{
		if (date.length() != 8)
		{
			throw new NumberFormatException("DA must be 8 characters");
		}
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(4, 6));
		int day = Integer.parseInt(date.substring(6, 8));
	
		return year*10000+month*100+day;
	}

	/**
	 * Display a text representation of the <code>DicomObject</code> using
	 * <code>System.out</code>.
	 * @param dcm the DICOM object
	 */
	public static void display(DicomObject dcm)
	{
		print(dcm, System.out, "");
	}

	/**
	 * Returns the number of items in the sequence at the tag path, or -1 if the
	 * sequence doesn't exist.
	 * 
	 * The tag path is zero or more pairs of sequence tag and item index within
	 * that sequence followed by a sequence tag. As such the tag path must be of
	 * odd length.
	 * 
	 * @param inDcm the DICOM object containing the sequence
	 * @param tagPath the tag path
	 * @return the number of items
	 * @throws IllegalArgumentException if the tag path is null or of even length
	 */
	public static int getSequenceItemCount(DicomObject inDcm, int[] tagPath)
		throws IllegalArgumentException
	{
		if ((tagPath == null) || ((tagPath.length % 2) == 0))
		{
			throw new IllegalArgumentException("Tag path must have odd length");
		}
		DicomObject dcm = inDcm;
		DicomElement element;
		int nLevels = tagPath.length / 2;
		for (int i = 0; i < nLevels - 1; i++, i++)
		{
			int sqTag = tagPath[i];
			int sqIdx = tagPath[i + 1];
			element = dcm.get(sqTag);
			if ((element == null) || (sqIdx < 0) ||
				 (sqIdx >= element.countItems()) ||
				 !element.hasDicomObjects())
			{
				return -1;
			}
			dcm = element.getDicomObject(sqIdx);
			if (dcm == null)
			{
				return -1;
			}
		}
		element = dcm.get(tagPath[tagPath.length - 1]);
		return (element != null) ? element.countItems() : -1;
	}

	/**
	 * Returns the item in the sequence at the tag path, or null if the
	 * sequence or item doesn't exist.
	 * 
	 * The tag path is zero or more pairs of sequence tag and item index within
	 * that sequence. As such the tag path must be of even length.
	 * 
	 * @param inDcm the DICOM object containing the sequence
	 * @param tagPath the tag path
	 * @return the item
	 * @throws IllegalArgumentException if the tag path is null or of odd length
	 * @throws IllegalArgumentException if an item index in the tag path is less
	 * than zero
	 */
	public static DicomObject getSequenceObject(DicomObject inDcm, int[] tagPath)
		throws IllegalArgumentException
	{
		if ((tagPath == null) || ((tagPath.length % 2) == 1))
		{
			throw new IllegalArgumentException("Tag path must have even length");
		}
		DicomObject dcm = inDcm;
		int nLevels = tagPath.length / 2;
		for (int i = 0; i < nLevels; i++)
		{
			int sqTag = tagPath[2 * i];
			int sqIdx = tagPath[2 * i + 1];
			if (sqIdx < 0)
			{
				throw new IllegalArgumentException(
					"Item index must be greater than 0 for SQ ("+
					String.format("%x08", sqTag) +")");
			}
			DicomElement element = dcm.get(sqTag);
			if ((element == null) || (sqIdx >= element.countItems()) || !element.hasDicomObjects())
			{
				return null;
			}
			dcm = element.getDicomObject(sqIdx);
			if (dcm == null)
			{
				return null;
			}
		}
		return dcm;
	}

	/**
	 * Returns the item in the sequence at the tag path, if the sequence or item
	 * doesn't exist optionally create it or return null.
	 * 
	 * The tag path is zero or more pairs of sequence tag and item index within
	 * that sequence. As such the tag path must be of even length.
	 * 
	 * @param inDcm the DICOM object containing the sequence
	 * @param tagPath the tag path
	 * @param create whether to create the objects along the tag path
	 * @return the item
	 * @throws IllegalArgumentException if the tag path is null or of odd length
	 * @throws IllegalArgumentException if an item index in the tag path is less
	 * than zero
	 */
	public static DicomObject getSequenceObject(DicomObject inDcm, int[] tagPath,
		boolean create) throws IllegalArgumentException
	{
		if (!create)
		{
			return getSequenceObject(inDcm, tagPath);
		}
		if ((tagPath == null) || ((tagPath.length % 2) == 1))
		{
			throw new IllegalArgumentException("Tag path must have even length");
		}
		DicomObject dcm = inDcm;
		int nLevels = tagPath.length / 2;
		for (int i = 0; i < nLevels; i++)
		{
			int sqTag = tagPath[2 * i];
			int sqIdx = tagPath[2 * i + 1];
			if (sqIdx < 0)
			{
				throw new IllegalArgumentException(
					"Item index must be greater than 0 for SQ ("+
					String.format("%x08", sqTag)+")");
			}
			DicomElement element = dcm.get(sqTag);
			if (element == null)
			{
				element = dcm.putSequence(sqTag);
			}
			if (sqIdx >= element.countItems())
			{
				// Pad SQ with empty DicomObjects
				int nItems = element.countItems();
				logger.debug("Padding SQ ("+String.format("%x08", sqTag)+
					") with {} empty DicomObjects", sqIdx-nItems+1);
				for (int j=nItems; j<=sqIdx; j++)
				{
					element.addDicomObject(new BasicDicomObject());
				}
			}
			dcm = element.getDicomObject(sqIdx);
		}
		return dcm;
	}

	/**
	 * Returns the value representation of the tag within the
	 * <code>DicomObject</code>.
	 * @param dcm the DICOM object
	 * @param tag the tag
	 * @return the VR
	 */
	public static String getVr(DicomObject dcm, int tag)
	{
		DicomElement element = dcm.get(tag);
		if (element == null)
		{
			return "UN";
		}
		VR vr = element.vr();
		return (vr != null) ? vrCodeAsString(vr) : "UN";
	}

	/**
	 * Returns the <code>Coordinate3D</code> of the point defined by the position,
	 * direction cosines and (x,y) coordinates within the image plane.
	 * @param pos the image position patient
	 * @param row the direction cosines of the image row
	 * @param col the direction cosines of the image column
	 * @param pixDims the pixel dimensions
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the 3D coordinate
	 * @throws IllegalArgumentException if any of the position or direction
	 * cosines are not three element arrays
	 * @throws IllegalArgumentException if pixel dimensions is not a two element
	 * array
	 */
	public static Coordinate3D imageCoordToPatientCoord3D(double[] pos,
		double[] row, double[] col, double[] pixDims, double x, double y)
		throws IllegalArgumentException
	{
		if ((pos == null) || (row == null) || (col == null) ||
			 (pos.length != 3) || (row.length != 3) || (col.length != 3))
		{
			throw new IllegalArgumentException(
				"Position and direction cosines must be double[3]");
		}
		if ((pixDims == null) || (pixDims.length != 2))
		{
			throw new IllegalArgumentException(
				"Pixel dimensions must be double[2]");
		}
		double magRow = row[0]*row[0]+row[1]*row[1]+row[2]*row[2];
		double magCol = col[0]*col[0]+col[1]*col[1]+col[2]*col[2];
		if ((Math.abs(1.0-magRow) >= 0.01) || (Math.abs(1.0-magCol) >= 0.01))
		{
			throw new IllegalArgumentException(
				"Direction cosines must form unit vectors");
		}

		double x3D = pos[0]+x*row[0]*pixDims[0]+y*col[0]*pixDims[1];
		double y3D = pos[1]+x*row[1]*pixDims[0]+y*col[1]*pixDims[1];
		double z3D = pos[2]+x*row[2]*pixDims[0]+y*col[2]*pixDims[1];

		return new Coordinate3D(x3D, y3D, z3D);
	}

	/**
	 * Returns true if the UID is a known image SOP class.
	 * @param sopClassUid the UID
	 * @return true if an image SOP class
	 */
	public static boolean isImageSopClass(String sopClassUid)
	{
		return imageSopClasses.contains(sopClassUid);
	}

	/**
	 * Returns true if the UID is a known multiframe SOP class.
	 * @param sopClassUid the UID
	 * @return true if a multiframe SOP class
	 */
	public static boolean isMultiframeImageSopClass(String sopClassUid)
	{
		return multiframeSopClasses.contains(sopClassUid);
	}

	/**
	 * Returns true if age is a valid DICOM age string.
	 * @param age
	 * @return
	 */
	public static boolean isValidAgeString(String age)
	{
		if (age == null)
		{
			return false;
		}
		String trimmed = age.trim();
		if (trimmed.length() != 4)
		{
			return false;
		}
		try
		{
			Integer.parseInt(trimmed.substring(0, 2));
		}
		catch (NumberFormatException ex)
		{
			return false;
		}
		switch (trimmed.substring(3))
		{
			case "D":
			case "W":
			case "M":
			case "Y":
				return true;
			default:
				return false;
		}
	}

	/**
	 * Returns the key corresponding to the <code>Patient</code>'s name, birth
	 * date and ID.
	 * @param patient the patient
	 * @return the key
	 */
	public static String makePatientKey(Patient patient)
	{
		return patient.getName()+"_"+patient.getBirthDate()+"_"+patient.getId();
	}

	/**
	 * Returns the key corresponding to the <code>Patient</code>'s name, birth
	 * date and ID in the supplied <code>SopInstance</code>.
	 * @param sopInst the instance
	 * @return the key
	 */
	public static String makePatientKey(SopInstance sopInst)
	{
		DicomObject dcm = sopInst.getDicomObject();
		String patName = dcm.getString(Tag.PatientName);
		patName = (patName == null) ? "" : patName.replace(' ', '_');
		String birthDate = dcm.getString(Tag.PatientBirthDate);
		if ((birthDate == null) || birthDate.isEmpty())
		{
			birthDate = "00000000";
		}
		String patId = dcm.getString(Tag.PatientID);
		if (patId == null)
		{
			patId = "";
		}
		return patName+"_"+birthDate+"_"+patId;
	}	

	/**
	 * Returns a <code>Date</code> representing the date from the supplied string.
	 * @param s the string
	 * @return the date
	 * @throws IllegalArgumentException if the date does not have 8 digits
	 */
	public static Date parseDate(String s) throws IllegalArgumentException
	{
		if ((s == null) || (s.length() < 8))
		{
			throw new IllegalArgumentException("Invalid DICOM DA: "+s);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = null;
		try
		{
			date = sdf.parse(s);
		}
		catch (ParseException ex)
		{
			logger.warn("Invalid date: {}", s, ex);
		}
		return date;
	}

	/**
	 * Returns a <code>Date</code> representing the time from the supplied string.
	 * @param s the string
	 * @return the time
	 * @throws IllegalArgumentException if the time has an invalid format
	 */
	public static Date parseTime(String s) throws IllegalArgumentException
	{
		if (s == null)
		{
			throw new IllegalArgumentException("Invalid DICOM TM: "+s);
		}
		int length = s.length();
		if (length < 6)
		{
			throw new IllegalArgumentException("Invalid DICOM TM: "+s);
		}
		SimpleDateFormat sdf;
		int dotIdx = s.indexOf('.');
		if (dotIdx < 0)
		{
			sdf = new SimpleDateFormat("HHmmss");
		}
		else
		{
			if (dotIdx != 6)
			{
				throw new IllegalArgumentException("Invalid DICOM TM: "+s);
			}
			int nSubSec = length-7;
			char[] chars = new char[nSubSec];
			Arrays.fill(chars, 'S');
			sdf = new SimpleDateFormat("HHmmss."+new String(chars));
		}
		Date date = null;
		try
		{
			date = sdf.parse(s);
		}
		catch (ParseException ex)
		{
			logger.warn("Invalid time: {}", s, ex);
		}
		return date;
	}

	/**
	 * Returns the 2D coordinate within the image plane defined by the 3D patient
	 * coordinate, position, direction cosines and pixel dimensions.
	 * @param patCoord the coordinate in the patient coordinate system
	 * @param pos the image position patient
	 * @param row the direction cosines of the image row
	 * @param col the direction cosines of the image column
	 * @param pixDims the pixel dimensions
	 * @return the coordinate
	 * @throws IllegalArgumentException if any of the patient coordinate,
	 * position or direction cosines are not three element arrays
	 * @throws IllegalArgumentException if pixel dimensions is not a two element
	 * array
	 */
	public static double[] patientCoordToImageCoord(double[] patCoord, 
		double[] pos, double[] row, double[] col, double[] pixDims)
		throws IllegalArgumentException
	{
		if ((patCoord == null) || (pos == null) || (row == null) || (col == null) ||
			 (patCoord.length != 3) || (pos.length != 3) || (row.length != 3) ||
			 (col.length != 3))
		{
			throw new IllegalArgumentException(
				"Patient coordinate, image position and direction cosines must be double[3]");
		}
		if ((pixDims == null) || (pixDims.length != 2))
		{
			throw new IllegalArgumentException(
				"Pixel dimensions must be double[2]");
		}

		// Translated from James Petts' Javascript implementation
		double magRow = row[0]*row[0]+row[1]*row[1]+row[2]*row[2];
		double magCol = col[0]*col[0]+col[1]*col[1]+col[2]*col[2];
		if ((Math.abs(1.0-magRow) >= 0.01) || (Math.abs(1.0-magCol) >= 0.01))
		{
			throw new IllegalArgumentException(
				"Direction cosines must form unit vectors");
		}

		// 9 sets of simulataneous equations to choose from, choose which set to
		// solve based on the largest component of each direction cosine. This
		// avoids NaNs or floating point errors caused by dividing by very small
		// numbers and ensures a safe mapping.
		int xMaxIdx = findIndexOfMax(row);
		int yMaxIdx = findIndexOfMax(col);
		// Subtract ImagePositionPatient from coordinate
		double[] r = new double[]
		{
			patCoord[0]-pos[0], patCoord[1]-pos[1], patCoord[2]-pos[2]
		};
		// Create array to select the two simultaneous equations to solve
		double[] c = new double[]
		{
			r[xMaxIdx], col[xMaxIdx], row[xMaxIdx],
			r[yMaxIdx], row[yMaxIdx], col[yMaxIdx]
		};
		// General case: Solves the two choosen simulataneous equations to go from
		// the patient coordinate system to the image plane coordinates.
		double i = (c[0] - c[1]*c[3]/c[5]) /
			(c[2]*pixDims[0] * (1 - (c[1]*c[4])/(c[2]*c[5])));
		double j = (c[3] - c[4]*i*pixDims[0]) / (c[5]*pixDims[1]);

		return new double[] {i, j};
	}

	public static void print(DicomObject dcm, PrintStream ps)
	{
		print(dcm, ps, "");
	}

	/**
	 * Returns the <code>DicomObject</code> parsed from the supplied path.
	 * @param path the path
	 * @return the DICOM object
	 * @throws IOException if an I/O error occurs
	 */
	public static DicomObject readDicomFile(String path) throws IOException
	{
		return readDicomFile(new File(path));
	}

	/**
	 * Returns the <code>DicomObject</code> parsed from the supplied file.
	 * @param file the file
	 * @return the DICOM object
	 * @throws IOException if an I/O error occurs
	 */
	public static DicomObject readDicomFile(File file) throws IOException
	{
		DicomObject dcm = new BasicDicomObject();
		DicomInputStream dcmIS = null;
		try
		{
			dcmIS = new DicomInputStream(file);
			dcmIS.readDicomObject(dcm, -1);
		}
		catch (DicomCodingException exDC)
		{
			// Dcm4Che throws this on scanning some non-DICOM files
			logger.debug("DicomCodingException reading non-DICOM file: {}",
				file.getPath());
			dcm = null;
		}
		catch (FileNotFoundException ex)
		{
			// This can be thrown if the file is locked e.g. by Excel
			logger.debug("FileNotFoundException reading file: "+file.getPath()+
				" ("+ex.getMessage()+")");
			dcm = null;
		}
		catch (EOFException exEOF)
		{
			// Dcm4Che throws this on scanning some non-DICOM files
			logger.debug("EOFException reading non-DICOM file: {}", file.getPath());
			dcm = null;
		}
		catch (IndexOutOfBoundsException exIOOB)
		{
			// Dcm4Che throws this on scanning some non-DICOM files
			logger.debug("IndexOutOfBoundsException reading non-DICOM file: {}",
				file.getPath());
			dcm = null;
		}
		catch (NegativeArraySizeException exNAS)
		{
			// Dcm4Che throws this on scanning some non-DICOM files
			logger.debug("NegativeArraySizeException reading non-DICOM file: {}",
				file.getPath());
			dcm = null;
		}
		catch (NumberFormatException exNF)
		{
			// Dcm4Che throws this on scanning some non-DICOM files
			logger.debug("NumberFormatException reading non-DICOM file: {}",
				file.getPath());
			dcm = null;
		}
		catch (UnsupportedOperationException exUO)
		{
			// Dcm4Che throws this on scanning some non-DICOM files
			logger.debug("UnsupportedOperationException reading non-DICOM file: {}",
				file.getPath());
			dcm = null;
		}
		catch (OutOfMemoryError erOOM)
		{
			dcm = null;
			// Dcm4Che can throw this on scanning some non-DICOM files by
			// trying to allocate a stupidly large byte array. Try and recover.
			// Generally not an advisable thing to catch :(
			logger.error("OutOfMemoryError reading file: {}", file.getPath());
		}
		catch (Error er)
		{
			dcm = null;
			// Shouldn't happen, log in case it's swallowed
			logger.error("Error reading file: "+file.getPath(), er);
			throw er;
		}
		finally
		{
			IoUtils.safeClose(dcmIS);
		}
		if ((dcm != null) && dcm.isEmpty())
		{
			dcm = null;
			logger.debug("Zero DicomElements found: {}", file.getPath());
		}

		return dcm;
	}

	/**
	 * Returns the <code>DicomObject</code> parsed from the supplied stream.
	 * @param is the stream
	 * @return the DICOM object
	 * @throws IOException if an I/O error occurs
	 */
	public static DicomObject readDicomObject(InputStream is) throws IOException
	{
		return readDicomObject(is, null);
	}

	/**
	 * Returns the <code>DicomObject</code> parsed from the supplied stream using
	 * the transfer syntax.
	 * @param is the stream
	 * @param xferSyntax the transfer syntax
	 * @return the DICOM object
	 * @throws IOException if an I/O error occurs
	 */
	public static DicomObject readDicomObject(InputStream is, String xferSyntax)
		throws IOException
	{
		DicomObject dcm = new BasicDicomObject();
		DicomInputStream dcmIS = null;
		try
		{
			dcmIS = (xferSyntax != null)
				? new DicomInputStream(is, xferSyntax)
				: new DicomInputStream(is);
			dcmIS.readDicomObject(dcm, -1);
		}
		catch (DicomCodingException exDC)
		{
			// Dcm4Che throws this on scanning some non-DICOM streams
			logger.debug("DicomCodingException in stream", exDC);
			dcm = null;
		}
		catch (EOFException | NegativeArraySizeException | NumberFormatException |
				 UnsupportedOperationException ex)
		{
			// Dcm4Che throws this on scanning some non-DICOM streams
			logger.debug("Non-DICOM object", ex);
			dcm = null;
		}
		catch (IndexOutOfBoundsException exIOOB)
		{
			// Dcm4Che throws this on scanning some non-DICOM streams
			logger.debug("Non-DICOM file object", exIOOB);
			dcm = null;
		}
		catch (OutOfMemoryError erOOM)
		{
			dcm = null;
			// Dcm4Che can throw this on scanning some non-DICOM streams by
			// trying to allocate a stupidly large byte array. Try and recover.
			// Generally not an advisable thing to catch :(
			logger.error("OutOfMemoryError reading object", erOOM);
		}
		catch (Error er)
		{
			dcm = null;
			// Shouldn't happen, log in case it's swallowed
			logger.error("Error reading object", er);
			throw er;
		}
		finally
		{
			IoUtils.safeClose(dcmIS);
		}

		return dcm;
	}

	/**
	 * Converts the number of seconds after midnight to a DICOM TM string.
	 * @param seconds the value
	 * @return the TM string
	 */
	public static String secondsToTm(double seconds)
	{
		if (seconds > 86400.0)
		{
			throw new IllegalArgumentException("Value in excess of 24h");
		}
		double tm = seconds;
		StringBuilder sb = new StringBuilder();
		int hh = (int) Math.floor(tm/3600.0);
		tm -= 3600.0*hh;
		int mm = (int) Math.floor(tm/60.0);
		tm -= 60*mm;
		int ss = (int) Math.floor(tm);
		tm -= ss;
		sb.append(String.format("%02d", hh)).append(String.format("%02d", mm))
			.append(String.format("%02d", ss));
		if (tm > 0)
		{
			String sec = String.format("%f", tm);
			sb.append(sec.substring(1, sec.length()));
		}
		return sb.toString();
	}

	public static double sliceLocation(double[] imagePosition,
		double[] imageOrientation) throws IllegalArgumentException
	{
		if ((imagePosition == null) || (imagePosition.length != 3))
		{
			throw new IllegalArgumentException(
				"ImagePositionPatient is not double[3]");
		}
		if ((imageOrientation == null) || (imageOrientation.length != 6))
		{
			throw new IllegalArgumentException(
				"ImageOrientationPatient is not double[6]");
		}
		double[] normal = cross(Arrays.copyOfRange(imageOrientation, 0, 3),
			Arrays.copyOfRange(imageOrientation, 3, 6));
		int idx = findIndexOfMax(normal);
		return  Math.signum(normal[idx])*dot(normal, imagePosition);
	}

	/**
	 * Returns the name of the tag, or null if unknown.
	 * @param tag the tag
	 * @return the name
	 */
	public static String tagName(int tag)
	{
		for (Field field : Tag.class.getDeclaredFields())
		{
			try
			{
				if (field.getInt(null) == tag)
				{
					return field.getName();
				}
			}
			catch (IllegalArgumentException | IllegalAccessException ignore)
			{}
		}
		return null;
	}

	/**
	 * Converts a DICOM TM string to seconds after midnight as a double.
	 * @param tm the TM string
	 * @return the number of seconds
	 */
	public static double tmToSeconds(String tm)
	{
		if (tm == null)
		{
			throw new NumberFormatException("TM invalid (null)");
		}
		int nTM = tm.length();
		if (nTM < 2)
		{
			throw new NumberFormatException("TM invalid: "+tm);
		}
		double hh = 3600*Double.parseDouble(tm.substring(0,2));
		double mm = 0;
		double ss = 0;
		if (nTM >= 4)
		{
			mm = 60*Double.parseDouble(tm.substring(2,4));
			if (nTM > 4)
			{
				ss = Double.parseDouble(tm.substring(4,nTM));
			}
		}
		else
		{
			throw new NumberFormatException("TM invalid: "+tm);
		}
		return hh+mm+ss;
	}

	/**
	 * Writes a <code>DicomObject</code> to the supplied path.
	 * @param dcm the DICOM object
	 * @param path the path
	 * @throws IOException if an I/O error occurs
	 */
	public static void writeDicomFile(DicomObject dcm, String path)
		throws IOException
	{
		writeDicomFile(dcm, new File(path));
	}

	/**
	 * Writes a <code>DicomObject</code> to the supplied file.
	 * @param dcm the DICOM object
	 * @param file the file
	 * @throws IOException if an I/O error occurs
	 */
	public static void writeDicomFile(DicomObject dcm, File file)
		throws IOException
	{
		DicomOutputStream dcmOS = null;
		try
		{
			dcmOS = new DicomOutputStream(file);
			dcmOS.writeDicomFile(dcm);
		}
		finally
		{
			IoUtils.safeClose(dcmOS);
		}
	}

	private static double[] cross(double[] row, double[] col)
	{
		double[] product = new double[3];
		product[0] = row[1]*col[2]-row[2]*col[1];
		product[1] = row[2]*col[0]-row[0]*col[2];
		product[2] = row[0]*col[1]-row[1]*col[0];
		return product;
	}

	private static double dot(double[] u, double[] v)
	{
		return u[0]*v[0]+u[1]*v[1]+u[2]*v[2];
	}

	private static int findIndexOfMax(double[] array)
	{
		if ((array == null) || (array.length == 0))
		{
			return -1;
		}
		if (array.length == 1)
		{
			return 0;
		}
		int idx = 0;
		double max = Math.abs(array[0]);
		for (int i=1; i<array.length; i++)
		{
			double test = Math.abs(array[i]);
			if (test > max)
			{
				idx = i;
				max = test;
			}
		}
		return idx;
	}

	private static void print(DicomElement element, PrintStream ps, String prefix)
	{
		for (int i=0; i<element.countItems(); i++)
		{
			print(element.getDicomObject(i), ps, prefix);
		}
	}

	private static void print(DicomObject dcm, PrintStream ps, String prefix)
	{
		Iterator<DicomElement> iter = dcm.iterator();
		while (iter.hasNext())
		{
			DicomElement element = iter.next();
			ps.println(prefix+element.toString()+" "+dcm.nameOf(element.tag()));
			if (element.hasItems())
			{
				print(element, ps, prefix+">");
			}
		}
	}

	private static String vrCodeAsString(VR vr)
	{
		int code = vr.code();
		byte b1 = (byte) ((code & 65280) >> 8);
		byte b2 = (byte) (code & 255);
		return Character.toString((char) b1) + Character.toString((char) b2);
	}

	/*
	 *	Private constructor to prevent direct instantiation
	 */
	private DicomUtils()
	{}
}
