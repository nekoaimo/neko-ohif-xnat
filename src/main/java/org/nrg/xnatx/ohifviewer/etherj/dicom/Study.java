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

import java.util.List;
import org.nrg.xnatx.ohifviewer.etherj.Displayable;

/**
 *
 * @author jamesd
 */
public interface Study extends Displayable
{

	/**
	 *
	 * @param series
	 * @return 
	 */
	public Series addSeries(Series series);

	/**
	 *
	 * @return
	 */
	public String getAccession();

	/**
	 *
	 * @return
	 */
	public String getDate();

	/**
	 *
	 * @return
	 */
	public String getDescription();

	/**
	 *
	 * @return
	 */
	public String getId();

	/**
	 *
	 * @return
	 */
	public String getModality();

	/**
	 *
	 * @param uid
	 * @return
	 */
	public Series getSeries(String uid);

	/**
	 *
	 * @return
	 */
	public int getSeriesCount();

	/**
	 *
	 * @return
	 */
	public List<Series> getSeriesList();

	/**
	 *
	 * @return
	 */
	public String getUid();

	/**
	 *
	 * @param uid
	 * @return
	 */
	public boolean hasSeries(String uid);

	/**
	 *
	 * @param uid
	 * @return
	 */
	public Series removeSeries(String uid);

	/**
	 *
	 * @param accession
	 */
	public void setAccession(String accession);

	/**
	 *
	 * @param date
	 */
	public void setDate(String date);

	/**
	 *
	 * @param description
	 */
	public void setDescription(String description);

	/**
	 *
	 * @param id
	 */
	public void setId(String id);

	/**
	 *
	 * @param uid
	 */
	public void setUid(String uid);

}
