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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Utility class for I/O-related methods.
 * @author jamesd
 */
public class IoUtils
{

	/**
	 * Checks if the named directory exists, creating it if it does not. An
	 * {@link IOException} will be thrown if creation fails, named directory is
	 * not a directory or is not both readable and writable.
	 * @param dir the directory to check
	 * @throws IOException if an I/O error occurs, named directory is not a
	 * directory or is not both readable and writable 
	 */
	public static void ensureDirExists(File dir) throws IOException
	{
		if (!dir.exists())
		{
			dir.mkdirs();
			return;
		}
		if (!dir.isDirectory())
		{
			throw new IOException("Not a diretory: "+dir.getAbsolutePath());
		}
		if (!(dir.canRead() && dir.canWrite()))
		{
			throw new IOException(
				"Permission error, must be readable and writable: "+
					dir.getAbsolutePath());
		}
	}

	/**
	 * Converts the content of an {@link InputStream} into a string. Can be
	 * useful for debugging HTTP/S responses.
	 * @param is the input stream
	 * @return the String representation of the input stream's content
	 */
	public static String toString(InputStream is)
	{
		return toString(is, "UTF-8");
	}

	/**
	 * Converts the content of an {@link InputStream} into a string using
	 * the specified encoding. Can be useful for debugging HTTP/S responses.
	 * @param is the input stream
	 * @param encoding the encoding to use
	 * @return the String representation of the input stream's content
	 */
	public static String toString(InputStream is, String encoding)
	{
		Scanner scanner = new Scanner(is, encoding).useDelimiter("\\A");
		return scanner.hasNext() ? scanner.next() : null;
	}

	/**
	 *	Safely close {@link Closeable} c without throwing.
	 * @param c the object to close
	 */
	public static void safeClose(Closeable c)
	{
		try
		{
			if (c != null)
			{
				c.close();
			}
		}
		catch (IOException exIgnore)
		{}
	}

	private IoUtils()
	{}
}
