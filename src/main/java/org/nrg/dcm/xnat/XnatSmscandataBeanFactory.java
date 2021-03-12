/*********************************************************************
 * Copyright (c) 2021, Institute of Cancer Research
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
package org.nrg.dcm.xnat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.SetMultimap;
import java.util.Collections;
import java.util.Set;
import org.dcm4che2.data.Tag;
import org.nrg.dcm.Attributes;
import org.nrg.dcm.DicomAttributeIndex;
import org.nrg.dcm.DicomMetadataStore;
import org.nrg.dcm.FixedDicomAttributeIndex;
import org.nrg.xdat.bean.XnatImagescandataBean;
import org.nrg.xdat.bean.XnatSmscandataBean;
import org.springframework.stereotype.Component;

/**
 *
 * @author jamesd
 */
@Component
public class XnatSmscandataBeanFactory extends XnatImagescandataBeanFactory
{
	@Override
	public XnatImagescandataBean create(Series series, DicomMetadataStore store)
	{
		logger.info("XnatSmscandataBeanFactory::create()");
		DicomAttributeIndex modalityAttr =
			new FixedDicomAttributeIndex(Tag.Modality);
		SetMultimap<DicomAttributeIndex,String> values = getValues(store,
			ImmutableMap.of(Attributes.SeriesInstanceUID, series.getUID()),
			Collections.singleton(modalityAttr));
		if (null == values)
		{
			return null;
		}
		Set<String> modalities = values.get(modalityAttr);
		if (modalities != null && modalities.size() == 1 &&
			 modalities.contains("SM"))
		{
			logger.info("SM scan bean created");
			return new XnatSmscandataBean();
		}
		return null;
	}

}
