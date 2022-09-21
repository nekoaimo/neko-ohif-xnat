/*********************************************************************
 * Copyright (c) 2022, Institute of Cancer Research
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
package icr.etherj.meas;

import icr.etherj.AbstractDisplayable;
import java.io.PrintStream;

/**
 *
 * @author mo.alsad
 */
public class User extends AbstractDisplayable
{
	private String name = "";
	private String loginName = "";
	private int numberWithinRoleOfClinicalTrial = 0;
	private String roleInTrial = "";

	/**
	 *
	 */
	public User()
	{}

	/**
	 *
	 * @param name
	 * @param loginName
	 */
	public User(String name, String loginName)
	{
		this.name = (name == null) ? "" : name;
		this.loginName = (loginName == null) ? "" : loginName;
	}

	@Override
	public void display(PrintStream ps, String indent, boolean recurse)
	{
		ps.println(indent+getClass().getName());
		String pad = indent+"  * ";
		ps.println(pad+"Name: "+name);
		ps.println(pad+"LoginName: "+loginName);
		if (!roleInTrial.isEmpty())
		{
			ps.println(pad+"RoleInTrial: "+roleInTrial);
		}
		if (numberWithinRoleOfClinicalTrial > 0)
		{
			ps.println(pad+"NumberWithinRoleOfClinicalTrial: "+
				numberWithinRoleOfClinicalTrial);
		}
	}

	/**
	 * @return the loginName
	 */
	public String getLoginName()
	{
		return loginName;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the numberWithinRoleOfClinicalTrial
	 */
	public int getNumberWithinRoleOfClinicalTrial()
	{
		return numberWithinRoleOfClinicalTrial;
	}

	/**
	 * @return the roleInTrial
	 */
	public String getRoleInTrial()
	{
		return roleInTrial;
	}

	/**
	 * @param loginName the loginName to set
	 */
	public void setLoginName(String loginName)
	{
		this.loginName = (loginName == null) ? "" : loginName;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = (name == null) ? "" : name;
	}

	/**
	 * @param numberWithinRoleOfClinicalTrial the numberWithinRoleOfClinicalTrial to set
	 */
	public void setNumberWithinRoleOfClinicalTrial(int numberWithinRoleOfClinicalTrial)
	{
		this.numberWithinRoleOfClinicalTrial = numberWithinRoleOfClinicalTrial;
	}

	/**
	 * @param roleInTrial the roleInTrial to set
	 */
	public void setRoleInTrial(String roleInTrial)
	{
		this.roleInTrial = (roleInTrial == null) ? "" : roleInTrial;
	}
}
