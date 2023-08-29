package org.nrg.xnatx.dicomweb.service.qido;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xnatx.dicomweb.service.hibernate.DicomwebDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QidoRsService
{
	private final DicomwebDataService dwDataService;

	@Autowired
	public QidoRsService(final DicomwebDataService dwDataService)
	{
		this.dwDataService = dwDataService;
	}


}
