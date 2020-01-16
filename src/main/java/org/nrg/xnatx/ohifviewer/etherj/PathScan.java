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

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A PathScan searches a path for files containing objects of type T, 
 * notifying any {@link PathScanContext}s when found.
 * @author jamesd
 * @param <T> the type of objects searched for
 */
public interface PathScan<T extends Object>
{

	/**
	 * Adds a {@link PathScanContext} to the collection of contexts to be notified when an
	 * object of type T is found.
	 * @param context the PathScanContext to be added
	 * @return true if the context is successfully added
	 */
	boolean addContext(PathScanContext<T> context);

	/**
	 * Returns the list of currently registered {@link PathScanContext}s.
	 * @return list of currently registered contexts
	 */
	List<PathScanContext<T>> getContextList();

	/**
	 * Removes a {@link PathScanContext} from the collection of contexts to be notified
	 * when an object of type T is found.
	 * @param context the context to be removed
	 * @return true if the context is successfully found and removed
	 */
	boolean removeContext(PathScanContext<T> context);

	/**
	 * Search the path for objects of type T.
	 * @param path the path to search
	 * @throws IOException if an I/O error occurs
	 */
	void scan(String path) throws IOException;

	/**
	 * Search the path for objects of type T, optionally descending into child
	 * directories.
	 * @param path the path to search
	 * @param recurse whether to descend into child directories
	 * @throws IOException if an I/O error occurs
	 */
	void scan(String path, boolean recurse) throws IOException;

	/**
	 * Attempts to read an object of type T from the file.
	 * @param file the file to read
	 * @return the object of type T found or null
	 * @throws IOException if an I/O error occurs
	 */
	T scanFile(File file) throws IOException;

}
