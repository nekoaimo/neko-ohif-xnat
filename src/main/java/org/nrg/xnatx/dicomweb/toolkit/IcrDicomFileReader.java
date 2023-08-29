package org.nrg.xnatx.dicomweb.toolkit;

import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.data.*;
import org.dcm4che3.imageio.codec.ImageDescriptor;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomStreamException;
import org.dcm4che3.util.SafeClose;
import org.nrg.xnatx.dicomweb.conf.DicomwebDeviceConfiguration;
import org.nrg.xnatx.dicomweb.conf.privateelements.PrivateTag;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
public class IcrDicomFileReader
{
	private File file;
	private DicomInputStream dis;
	private Attributes attrs;
	private long dataOffset[];
	private long dataLength[];

	public IcrDicomFileReader(File file) throws IOException
	{
		this.file = file;
	}

	public Attributes read() throws IOException
	{
		try
		{
			dis = new DicomInputStream(file);
			dis.setBulkDataDescriptor(
				DicomwebDeviceConfiguration.getBulkDataDescriptor());
			dis.setIncludeBulkData(DicomInputStream.IncludeBulkData.URI);
			dis.setURI("");
			attrs = dis.readDataset();

			attrs.setString(Tag.TransferSyntaxUID, VR.UI, dis.getTransferSyntax());

			checkForAndAddPixelDataOffsets();

			Object pixelData = attrs.getValue(Tag.PixelData);
			if (pixelData != null)
			{
				// Set PixelData to BulkData
				VR vr = attrs.getVR(Tag.PixelData);
				attrs.setValue(Tag.PixelData, vr,
					new BulkData(null, "", attrs.bigEndian()));
			}

			// Set BulkData to a relative retrieve URL
			try
			{
				StringBuffer sb = new StringBuffer();
				sb.append("/studies/")
					.append(attrs.getString(Tag.StudyInstanceUID))
					.append("/series/")
					.append(attrs.getString(Tag.SeriesInstanceUID))
					.append("/instances/")
					.append(attrs.getString(Tag.SOPInstanceUID));
				final String retrieveURL = sb.toString();

				attrs.accept(new Attributes.ItemPointerVisitor()
				{
					@Override
					public boolean visit(Attributes attrs, int tag, VR vr,
						Object value)
					{
						if (value instanceof BulkData)
						{
							BulkData bulkData = (BulkData) value;
							if (tag == Tag.PixelData && itemPointers.isEmpty())
							{
								bulkData.setURI(retrieveURL);
							}
							else
							{
								bulkData.setURI(
									retrieveURL + "/bulkdata" + DicomInputStream.toAttributePath(
										itemPointers, tag));
							}
						}
						return true;
					}
				}, true);
			}
			catch (Exception e)
			{
				log.debug("Could not set retrieveURL: {}", file.getPath());
				attrs = null;
			}
		}
		catch (DicomStreamException exDC)
		{
			// Dcm4Che throws this on scanning some non-DICOM files
			log.debug(
				"DicomStreamException reading non-DICOM file: {}",
				file.getPath());
			attrs = null;
		}
		catch (FileNotFoundException ex)
		{
			// This can be thrown if the file is locked e.g. by Excel
			log.debug(
				"FileNotFoundException reading file: " + file.getPath() +
					" (" + ex.getMessage() + ")");
			attrs = null;
		}
		catch (EOFException exEOF)
		{
			// Dcm4Che throws this on scanning some non-DICOM files
			log.debug("EOFException reading non-DICOM file: {}",
				file.getPath());
			attrs = null;
		}
		catch (IndexOutOfBoundsException exIOOB)
		{
			// Dcm4Che throws this on scanning some non-DICOM files
			log.debug(
				"IndexOutOfBoundsException reading non-DICOM file: {}",
				file.getPath());
			attrs = null;
		}
		catch (NegativeArraySizeException exNAS)
		{
			// Dcm4Che throws this on scanning some non-DICOM files
			log.debug(
				"NegativeArraySizeException reading non-DICOM file: {}",
				file.getPath());
			attrs = null;
		}
		catch (NumberFormatException exNF)
		{
			// Dcm4Che throws this on scanning some non-DICOM files
			log.debug(
				"NumberFormatException reading non-DICOM file: {}",
				file.getPath());
			attrs = null;
		}
		catch (UnsupportedOperationException exUO)
		{
			// Dcm4Che throws this on scanning some non-DICOM files
			log.debug(
				"UnsupportedOperationException reading non-DICOM file: {}",
				file.getPath());
			attrs = null;
		}
		catch (OutOfMemoryError erOOM)
		{
			attrs = null;
			// Dcm4Che can throw this on scanning some non-DICOM files by
			// trying to allocate a stupidly large byte array. Try and recover.
			// Generally not an advisable thing to catch :(
			log.error("OutOfMemoryError reading file: {}",
				file.getPath());
		}
		catch (InvalidPixelDataException e)
		{
			attrs = null;
			log.error(e.getMessage() + " File: " + file.getPath());
		}
		catch (Error er)
		{
			attrs = null;
			// Shouldn't happen, log in case it's swallowed
			log.error("Error reading file: " + file.getPath(), er);
			throw er;
		}
		finally
		{
			if (dis != null)
			{
				SafeClose.close(dis);
			}
		}

		if ((attrs != null) && attrs.isEmpty())
		{
			attrs = null;
			log.debug("Zero Attributes found: {}", file.getPath());
		}

		return attrs;
	}

	private void checkForAndAddPixelDataOffsets()
		throws InvalidPixelDataException
	{
		if (!DicomwebUtils.isImage(attrs))
		{
			return;
		}

		ImageDescriptor imageDescriptor = new ImageDescriptor(attrs);

		int numFrames = imageDescriptor.getFrames();
		if (numFrames == 1)
		{
			return;
		}

		Object pixelData = attrs.getValue(Tag.PixelData);

		if (pixelData instanceof Fragments)
		{
			Fragments fragments = (Fragments) pixelData;
			int numFragments = fragments.size() - 1;
			if (numFragments < numFrames)
			{
				throw new InvalidPixelDataException(
					"Invalid fragmentation of frames");
			}

			dataOffset = new long[numFragments];
			dataLength = new long[numFragments];
			int i = 0;
			for (Object fragment : fragments.subList(1, fragments.size()))
			{
				BulkData bulkData = (BulkData) fragment;
				dataOffset[i] = bulkData.offset();
				dataLength[i++] = bulkData.longLength();
			}
		}
		else
		{
			if (pixelData instanceof BulkData)
			{
				BulkData bulkData = (BulkData) pixelData;

				long frameLength = imageDescriptor.getFrameLength();
				long expectedLength = numFrames * frameLength;
				if (bulkData.longLength() != expectedLength)
				{
					throw new InvalidPixelDataException(
						"Invalid length of PixelData");
				}

				long offset = bulkData.offset();
				dataOffset = new long[numFrames];
				dataLength = new long[1];
				for (int i = 0; i < numFrames; i++)
				{
					dataOffset[i] = offset + offset * i;
				}
				dataLength[0] = frameLength;
			}
		}

		if (dataOffset != null && dataOffset.length > 0)
		{
			attrs.setLong(PrivateTag.PrivateCreator, PrivateTag.DataOffsets,
				VR.UL, dataOffset);
			attrs.setLong(PrivateTag.PrivateCreator, PrivateTag.DataLengths,
				VR.UL, dataLength);
		}
	}
}
