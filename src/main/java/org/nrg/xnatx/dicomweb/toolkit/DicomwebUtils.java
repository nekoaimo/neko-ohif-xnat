package org.nrg.xnatx.dicomweb.toolkit;

import org.dcm4che3.data.*;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xnatx.plugin.PluginUtils;

import java.io.*;

public class DicomwebUtils
{
	public static Attributes decodeAttributes(byte[] b) throws IOException
	{
		if (b == null || b.length == 0)
		{
			return new Attributes(0);
		}

		Attributes result = new Attributes();
		ByteArrayInputStream is = new ByteArrayInputStream(b);
		DicomInputStream dis = null;
		try
		{
			dis = new DicomInputStream(is);
			dis.readFileMetaInformation();
			dis.readAttributes(result, -1, -1);
		}
		catch (IOException e)
		{
			throw new IOException("Could not decode attributes", e);
		}
		return result;
	}

	public static Attributes decodeMetadata(byte[] b) throws IOException
	{
		if (b == null || b.length == 0)
		{
			return new Attributes(0);
		}

		Attributes result = new Attributes();
		ByteArrayInputStream bin = new ByteArrayInputStream(b);
		try (ObjectInputStream in = new ObjectInputStream(bin))
		{
			return (Attributes) in.readObject();
		}
		catch (IOException | ClassNotFoundException e)
		{
			throw new IOException("Could not decode attributes", e);
		}
	}

	public static byte[] encodeAttributes(Attributes attrs)
		throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream(512);
		DicomOutputStream dos = null;
		try
		{
			dos = new DicomOutputStream(out, UID.ExplicitVRLittleEndian);
			dos.writeDataset(null, attrs);
		}
		catch (IOException e)
		{
			throw new IOException("Could not encode attributes", e);
		}
		return out.toByteArray();
	}

	public static byte[] encodeMetadata(Attributes attrs)
		throws IOException
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
		try (ObjectOutputStream out = new ObjectOutputStream(bout))
		{
			out.writeObject(attrs);
			out.writeUTF("EOF");
		}
		catch (IOException e)
		{
			throw new IOException("Could not encode attributes", e);
		}
		return bout.toByteArray();
	}

	public static Integer getInt(Attributes attrs, int tag, String defVal)
	{
		String val = attrs.getString(tag, defVal);
		if (val != null)
		{
			try
			{
				return Integer.valueOf(val);
			}
			catch (NumberFormatException ignored)
			{
			}
		}
		return null;
	}

	public static boolean isImage(Attributes attrs)
	{
		String sopClassUID = attrs.getString(Tag.SOPClassUID);
		return attrs.contains(Tag.BitsAllocated) &&
						 !sopClassUID.equals(UID.RTDoseStorage);
	}
}
