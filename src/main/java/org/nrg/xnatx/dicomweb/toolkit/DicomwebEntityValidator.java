package org.nrg.xnatx.dicomweb.toolkit;

import icr.etherj.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xnatx.dicomweb.conf.DicomwebDeviceConfiguration;
import org.nrg.xnatx.dicomweb.entity.DwEntity;

@Slf4j
public class DicomwebEntityValidator
{
	public static boolean isValidEntity(DwEntity entity)
	{
		if (entity == null)
		{
			log.debug("DICOMweb entity not found");
			return false;
		}

		String revision = entity.getRevision();
		if (StringUtils.isNullOrEmpty(revision))
		{
			log.debug("DICOMweb entity revision empty");
			return false;
		}
		else
		{
			log.debug("Stored DICOMweb entity revision: {}", revision);
			if (!revision.equalsIgnoreCase(
				DicomwebDeviceConfiguration.DICOMWEB_DATA_REVISION))
			{
				log.debug("Stored DICOMweb entity revision {} mismatches {}",
					revision, DicomwebDeviceConfiguration.DICOMWEB_DATA_REVISION);
				return false;
			}
		}

		return true;
	}
}
