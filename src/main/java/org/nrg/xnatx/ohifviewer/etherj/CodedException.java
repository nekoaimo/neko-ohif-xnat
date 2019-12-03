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
package org.nrg.xnatx.ohifviewer.etherj;

import java.io.PrintStream;

/**
 * Base class for exceptions that stores information about the error in a
 * five letter state code. Modelled after Java's SQLException.
 * @author jamesd
 */
public class CodedException extends DisplayableException
{
	private final ExceptionCode code;

	/**
	 * Creates a new instance of <code>CodedException</code>.
	 */
	public CodedException()
	{
		super();
		code = null;
	}

	/**
	 * Constructs an instance of <code>CodedException</code> with the given
	 * <code>reason</code>.
	 *
	 * @param reason a description of the exception.
	 */
	public CodedException(String reason)
	{
		super(reason);
		code = null;
	}

	/**
	 * Constructs an instance of <code>CodedException</code> with the given
	 * <code>state</code>.
	 *
	 * @param code a descriptor of the exception.
	 */
	public CodedException(ExceptionCode code)
	{
		this(code.getMessage(), code);
	}

	/**
	 * Constructs an instance of <code>CodedException</code> with the given
	 * <code>reason</code> and <code>code</code>.
	 *
	 * @param reason a description of the exception.
	 * @param code a five letter code identifying the exception.
	 */
	public CodedException(String reason, ExceptionCode code)
	{
		super(reason);
		this.code = code;
	}

	/**
	 * Constructs an instance of <code>CodedException</code> with the given
	 * <code>reason</code>, <code>code</code> and <code>cause</code>.
	 *
	 * @param reason a description of the exception.
	 * @param code a descriptor of the exception.
	 * @param cause the cause.
	 */
	public CodedException(String reason, ExceptionCode code, Throwable cause)
	{
		super(reason, cause);
		this.code = code;
	}

	/**
	 * Constructs an instance of <code>CodedException</code> with the given
	 * <code>reason</code> and <code>cause</code>.
	 *
	 * @param reason a description of the exception.
	 * @param cause the cause.
	 */
	public CodedException(String reason, Throwable cause)
	{
		super(reason, cause);
		this.code = null;
	}

	/**
	 * Constructs an instance of <code>CodedException</code> with the given
	 * <code>code</code> and <code>cause</code>.
	 *
	 * @param code a descriptor of the exception.
	 * @param cause the cause.
	 */
	public CodedException(ExceptionCode code, Throwable cause)
	{
		super(cause);
		this.code = code;
	}

	/**
	 * Constructs an instance of <code>CodedException</code> with the given
	 * <code>cause</code>.
	 *
	 * @param cause the cause.
	 */
	public CodedException(Throwable cause)
	{
		super(cause);
		this.code = null;
	}

	@Override
	public void display(PrintStream ps, String indent, boolean recurse)
	{
		ps.println(indent+getClass().getName());
		String pad = indent+"  * ";
		ps.println(pad+"Message: "+getMessage());
		ps.println(pad+"Code: "+code);
		Throwable cause = getCause();
		if (cause != null)
		{
			ps.println(pad+"Cause: "+cause.getClass().getName());
		}
		StackTraceElement[] stackTrace = getStackTrace();
		for (StackTraceElement element : stackTrace)
		{
			ps.println(indent+"      "+element.toString());
		}
	}

	/**
	 * Returns the code descriptor for this <code>CodedException</code>.
	 * @return the code
	 */
	public ExceptionCode getCode()
	{
		return code;
	}

}
