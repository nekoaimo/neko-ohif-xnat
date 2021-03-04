/*********************************************************************
 * Copyright (c) 2018, Institute of Cancer Research
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
package org.nrg.xnatx.plugin;

/**
 *
 * @author jamesd
 */
public class ElementPermissions
{

	private String element = "";
	private boolean create;
	private boolean delete;
	private boolean edit;
	private boolean read;

	public ElementPermissions()
	{}

	public ElementPermissions(String element, boolean create, boolean read,
		boolean edit, boolean delete)
	{
		this.element = element;
		this.create = create;
		this.read = read;
		this.edit = edit;
		this.delete = delete;
	}

	/**
	 * @return the element
	 */
	public String getElement()
	{
		return element;
	}

	/**
	 * @return
	 */
	public boolean isCreate()
	{
		return create;
	}

	/**
	 * @return
	 */
	public boolean isDelete()
	{
		return delete;
	}

	/**
	 * @return
	 */
	public boolean isEdit()
	{
		return edit;
	}

	/**
	 * @return
	 */
	public boolean isRead()
	{
		return read;
	}
	
	/**
	 * @param create the create to set
	 */
	public void setCreate(boolean create)
	{
		this.create = create;
	}

	/**
	 * @param delete the delete to set
	 */
	public void setDelete(boolean delete)
	{
		this.delete = delete;
	}

	/**
	 * @param edit the edit to set
	 */
	public void setEdit(boolean edit)
	{
		this.edit = edit;
	}

	/**
	 * @param element the element to set
	 */
	public void setElement(String element)
	{
		this.element = element;
	}

	/**
	 * @param read the read to set
	 */
	public void setRead(boolean read)
	{
		this.read = read;
	}
}
