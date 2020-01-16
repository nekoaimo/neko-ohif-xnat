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
package org.nrg.xnatx.ohifviewer;

import org.nrg.xnatx.ohifviewer.etherj.CodedException;
import org.nrg.xnatx.ohifviewer.etherj.ExceptionCode;

/**
 *
 * @author jamesd
 */
public class PluginException extends CodedException
{
	/**
	 * Creates a new instance of <code>PluginException</code> without detail
	 * message.
	 */
	public PluginException()
	{
		super();
	}

	/**
	 * Constructs an instance of <code>PluginException</code> with the specified
	 * detail message.
	 *
	 * @param msg the detail message.
	 */
	public PluginException(String msg)
	{
		super(msg);
	}

	/**
	 * Constructs an instance of <code>PluginException</code> with the given
	 * <code>reason</code> and <code>code</code>.
	 *
	 * @param reason a description of the exception.
	 * @param code a five letter code identifying the exception.
	 */
	public PluginException(String reason, ExceptionCode code)
	{
		super(reason, code);
	}

	/**
	 * Constructs an instance of <code>PluginException</code> with the given
	 * <code>reason</code>, <code>code</code> and <code>cause</code>.
	 *
	 * @param reason a description of the exception.
	 * @param code a descriptor of the exception.
	 * @param cause the cause.
	 */
	public PluginException(String reason, ExceptionCode code, Throwable cause)
	{
		super(reason, code, cause);
	}

	/**
	 * Constructs an instance of <code>PluginException</code> with the specified
	 * detail message and cause.
	 *
	 * @param msg the detail message.
	 * @param cause the cause.
	 */
	public PluginException(String msg, Throwable cause)
	{
		super(msg, cause);
	}

	/**
	 * Constructs an instance of <code>PluginException</code> with the given
	 * <code>code</code> and <code>cause</code>.
	 *
	 * @param code a descriptor of the exception.
	 * @param cause the cause.
	 */
	public PluginException(ExceptionCode code, Throwable cause)
	{
		super(code, cause);
	}

	/**
	 * Constructs an instance of <code>PluginException</code> with the specified
	 * cause.
	 *
	 * @param cause the cause.
	 */
	public PluginException(Throwable cause)
	{
		super(cause);
	}

	
}
