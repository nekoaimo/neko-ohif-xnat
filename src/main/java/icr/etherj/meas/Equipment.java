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
public class Equipment extends AbstractDisplayable
{
	private String deviceSerialNumber = "";
	private String manufacturerName = "";
	private String manufacturerModelName = "";
	private String softwareVersion = "";

	public Equipment()
	{}

	public Equipment(String manufacturer, String modelName)
	{
		manufacturerName = (manufacturer == null) ? "" : manufacturer;
		manufacturerModelName = (modelName == null) ? "" : modelName;
	}

	@Override
	public void display(PrintStream ps, String indent, boolean recurse)
	{
		ps.println(indent+getClass().getName());
		String pad = indent+"  * ";
		ps.println(pad+"ManufacturerName: "+manufacturerName);
		ps.println(pad+"ManufacturerModelName: "+manufacturerModelName);
		if (!deviceSerialNumber.isEmpty())
		{
			ps.println(pad+"DeviceSerialNumber: "+deviceSerialNumber);
		}
		if (!softwareVersion.isEmpty())
		{
			ps.println(pad+"SoftwareVersion: "+softwareVersion);
		}
	}

	/**
	 * @return the deviceSerialNumber
	 */
	public String getDeviceSerialNumber()
	{
		return deviceSerialNumber;
	}

	/**
	 * @return the manufacturerName
	 */
	public String getManufacturerName()
	{
		return manufacturerName;
	}

	/**
	 * @return the manufacturerModelName
	 */
	public String getManufacturerModelName()
	{
		return manufacturerModelName;
	}

	/**
	 * @return the softwareVersion
	 */
	public String getSoftwareVersion()
	{
		return softwareVersion;
	}

	/**
	 * @param deviceSerialNumber the deviceSerialNumber to set
	 */
	public void setDeviceSerialNumber(String deviceSerialNumber)
	{
		this.deviceSerialNumber = (deviceSerialNumber == null) ?
			"" : deviceSerialNumber;
	}

	/**
	 * @param manufacturerName the manufacturerName to set
	 */
	public void setManufacturerName(String manufacturerName)
	{
		this.manufacturerName = (manufacturerName == null) ?
			"" : manufacturerName;
	}

	/**
	 * @param manufacturerModelName the manufacturerModelName to set
	 */
	public void setManufacturerModelName(String manufacturerModelName)
	{
		this.manufacturerModelName = (manufacturerModelName == null) ?
			"" : manufacturerModelName;
	}

	/**
	 * @param softwareVersion the softwareVersion to set
	 */
	public void setSoftwareVersion(String softwareVersion)
	{
		this.softwareVersion = (softwareVersion == null) ? "" : softwareVersion;
	}
}
