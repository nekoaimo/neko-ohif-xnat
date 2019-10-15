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

import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A comparator for ordering studies.
 * @author jamesd
 */
public class StudyComparator implements Comparator<Study>
{
	/** Natural order for Studies is date, ID. */
	public static final StudyComparator Natural = new StudyComparator();

	@Override
	public int compare(Study a, Study b)
	{
		if (a.equals(b))
		{
			return 0;
		}
		int dateA = Integer.MAX_VALUE;
		int dateB = Integer.MAX_VALUE;
		try
		{
			dateA = Integer.parseInt(a.getDate());
		}
		catch (NumberFormatException ex)
		{}
		try
		{
			dateB = Integer.parseInt(b.getDate());
		}
		catch (NumberFormatException ex)
		{}
		int date = (int) Math.signum(dateA-dateB);
		if (date != 0)
		{
			return date;
		}
		String idA = a.getId();
		String idB = b.getId();
		if (idA.equals(idB))
		{
			return 0;
		}
		List<String> idList = Arrays.asList(idA, idB);
		Collator collator = Collator.getInstance();
		collator.setStrength(Collator.TERTIARY);
		Collections.sort(idList, collator);
		return (idList.get(0).equals(idA)) ? -1 : 1;
	}

}
