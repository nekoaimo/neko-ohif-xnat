package org.nrg.xnatx.dicomweb.toolkit;

import icr.etherj.AbstractPathScan;
import lombok.extern.slf4j.Slf4j;
import org.dcm4che2.data.Tag;
import org.dcm4che3.data.Attributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class DicomwebPathScan extends AbstractPathScan<Attributes>
{
	@Override
	public Attributes scanFile(Path file) throws IOException
	{
		if (Files.isDirectory(file) || !Files.isReadable(file))
		{
			log.warn("Not a file or cannot be read: {}", file);
			return null;
		}

		Attributes attrs = null;
		try
		{
			IcrDicomFileReader reader = new IcrDicomFileReader(
				file.toFile());
			attrs = reader.read();
		}
		catch (IOException exIO)
		{
			log.warn("Cannot scan file: " + file, exIO);
			throw exIO;
		}

		// Ignore null SOPClassUID
		if (attrs != null)
		{
			String cuid = attrs.getString(Tag.SOPClassUID);
			if (cuid == null)
			{
				return null;
			}
		}

		return attrs;
	}
}
