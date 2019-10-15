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

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A skeletal implementation of the PathScan interface. The subclass need only
 * implement scanFile(File).
 * @author jamesd
 * @param <T> the type of item to search for
 */
public abstract class AbstractPathScan<T> implements PathScan<T>
{
	private static final Logger logger =
		LoggerFactory.getLogger(AbstractPathScan.class);

	// CopyOnWriteArrayList to prevent concurrent modification errors during
	// iteration
	private final List<PathScanContext<T>> contexts = new CopyOnWriteArrayList<>();
	private volatile boolean stopScanning = false;

	@Override
	public boolean addContext(PathScanContext<T> context)
	{
		if (contexts.contains(context))
		{
			logger.warn(
				"PathScanContext #"+context.hashCode()+" is already registered.");
			return false;
		}
		return this.contexts.add(context);
	}

	@Override
	public List<PathScanContext<T>> getContextList()
	{
		return ImmutableList.copyOf(contexts);
	}

	@Override
	public boolean removeContext(PathScanContext<T> context)
	{
		return contexts.remove(context);
	}

	@Override
	public void scan(String path) throws IOException
	{
		scan(path, true);
	}

	@Override
	public void scan(String path, boolean recurse)
		throws IOException
	{
		File searchRoot = new File(path);

		// Bail out if it's not a directory or not readable
		if (!searchRoot.isDirectory() || !searchRoot.canRead())
		{
			logger.warn("Not a directory or cannot be read: {}",
				searchRoot.getPath());
			return;
		}

		stopScanning = false;
		logger.info("Building import list...");
		SortedMap<String,List<File>> tree = new TreeMap<>();
		buildTree(tree, searchRoot, recurse);
		FileCountTreeWalker fileWalker = new FileCountTreeWalker();
		walkTree(tree, fileWalker);
		logger.info("Scanning "+Integer.toString(fileWalker.getFileCount())+
			" files from "+searchRoot.getPath()+"...");
		ContextTreeWalker contextWalker = new ContextTreeWalker(contexts);
		Iterator<PathScanContext<T>> iter = contexts.iterator();
		while (iter.hasNext())
		{
			iter.next().notifyScanStart();
		}
		walkTree(tree, contextWalker);
		iter = contexts.iterator();
		while (iter.hasNext())
		{
			iter.next().notifyScanFinish();
		}
		logger.info("Scan complete {}", searchRoot.getPath());
	}

	private void buildTree(SortedMap<String,List<File>> tree, File path,
		boolean recurse)
	{
		logger.debug("Listing directory {}...", path.getPath());
		List<File> fileList = new ArrayList<>();
		tree.put(path.getPath(), fileList);

		if (!path.canRead())
		{
			logger.warn("Directory cannot be read: {}", path.getPath());
			return;
		}
		File[] contents = path.listFiles();
		if (contents == null)
		{
			logger.debug("Directory is empty: {}", path.getPath());
			return;
		}
		for (int i=0; i<contents.length && !stopScanning; i++)
		{
			if (contents[i].isDirectory())
			{
				if (recurse)
				{
					buildTree(tree, contents[i], recurse);
				}
				continue;
			}
			fileList.add(contents[i]);
		}
	}

	private void walkTree(SortedMap<String,List<File>> tree, TreeWalker walker)
		throws IOException
	{
		Set<String> keys = tree.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext() && !stopScanning)
		{
			String path = iter.next();
			walker.onDirectory(new File(path));
			List<File> pathContents = tree.get(path);
			Iterator<File> listIter = pathContents.iterator();
			while (listIter.hasNext() && !stopScanning)
			{
				File file = listIter.next();
				walker.onFile(file);
			}
		}
	}

		interface TreeWalker
	{
		public void onDirectory(File directory) throws IOException;

		public void onFile(File file) throws IOException;
	}

	/**
	 *
	 */
	private class FileCountTreeWalker implements TreeWalker
	{
		int nFiles = 0;
		int nDirs = 0;

		public int getDirectoryCount()
		{
			return nDirs;
		}

		public int getFileCount()
		{
			return nFiles;
		}

		@Override
		public void onDirectory(File directory) throws IOException
		{
			nDirs++;
		}

		@Override
		public void onFile(File file) throws IOException
		{
			nFiles++;
		}
	}

	/**
	 *
	 */
	private class ContextTreeWalker implements TreeWalker
	{
		private List<PathScanContext<T>> contexts = null;

		ContextTreeWalker(List<PathScanContext<T>> contexts)
		{
			this.contexts = contexts;
		}

		@Override
		public void onDirectory(File directory) throws IOException
		{
			logger.trace("Scanning directory: {}", directory.getPath());
		}

		@Override
		public void onFile(File file) throws IOException
		{
			logger.trace("Scanning file: {}", file.getPath());
			T item = scanFile(file);
			if (item != null)
			{
				Iterator<PathScanContext<T>> iter = contexts.iterator();
				while (iter.hasNext())
				{
					// Notify contexts but protect ourselves if one throws
					try
					{
						iter.next().notifyItemFound(file, item);
					}
					catch (Exception ex)
					{
						logger.warn("Exception in client PathScanContext", ex);
					}
				}
			}
		}
	}
}
