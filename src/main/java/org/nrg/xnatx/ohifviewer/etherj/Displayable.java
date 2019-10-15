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
 * Interface providing the display() method that MATLAB looks for in order to
 * output an object to the console. Useful for simple debugging in Java IDE.
 * @author jamesd
 */
public interface Displayable
{

	/**
	 * Display the object on the console.
	 */
	public void display();

	/**
	 * Display the object on the console, optionally calling display() any member
	 * objects that also implement Displayable.
	 * @param recurse whether to call display() on relevant member objects.
	 */
	public void display(boolean recurse);

	/**
	 * Display the object on the console with each line prefixed by the supplied
	 * indent string.
	 * @param indent string to prefix each line with
	 */
	public void display(String indent);

	/**
	 * Display the object on the console with each line prefixed by the supplied
	 * indent string, optionally calling display() any member
	 * objects that also implement Displayable;
	 * @param indent string to prefix each line with
	 * @param recurse whether to call display() on relevant member objects
	 */
	public void display(String indent, boolean recurse);

	/**
	 * Display the object on the print stream.
	 * @param ps the print stream
	 */
	public void display(PrintStream ps);

	/**
	 * Display the object on the print stream, optionally calling display() any
	 * member objects that also implement Displayable.
	 * @param ps the print stream
	 * @param recurse whether to call display() on relevant member objects.
	 */
	public void display(PrintStream ps, boolean recurse);

	/**
	 * Display the object on the print stream with each line prefixed by the
	 * supplied indent string.
	 * @param ps the print stream
	 * @param indent string to prefix each line with
	 */
	public void display(PrintStream ps, String indent);

	/**
	 * Display the object on the print stream with each line prefixed by the
	 * supplied indent string, optionally calling display() any member
	 * objects that also implement Displayable;
	 * @param ps the print stream
	 * @param indent string to prefix each line with
	 * @param recurse whether to call display() on relevant member objects
	 */
	public void display(PrintStream ps, String indent, boolean recurse);
}
