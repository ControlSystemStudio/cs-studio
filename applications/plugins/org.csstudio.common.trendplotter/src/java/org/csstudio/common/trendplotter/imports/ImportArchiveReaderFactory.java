/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.imports;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;
import org.csstudio.common.trendplotter.model.ArchiveDataSource;

/** Factory for {@link ArchiveReader} that imports data from file
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ImportArchiveReaderFactory implements ArchiveReaderFactory
{
    /** Prefix used by this reader */
    final public static String PREFIX = "import:";

    /** Map URLs to ImportArchiveReader for the URL
     *
     *  <p>The reader will be invoked whenever data for a new time range is
     *  requested.
     *
     *  <p>Since the underlying file doesn't change, cache the readers by URL,
     *  and the reader will only parse the file once, then always return
     *  the remembered values.
     *
     *  <p>To prevent running out of memory, Model#stop() will
     *  remove cache data for items in model.
     */
    final private static Map<String, ImportArchiveReader> cache = new HashMap<String, ImportArchiveReader>();

    /** Create URL
     *  @param type File type
     *  @param path Path to file
     *  @return Import URL
     */
    public static String createURL(final String type, final String path)
    {
        return "import:" + type + ":/" + path;
    }

    /** Parse URL
     *  @param url Import URL
     *  @return String[] with type, path
     *  @throws Exception if URL doesn't parse
     */
    public static String[] parseURL(final String url) throws Exception
    {
        final int prefix_len = PREFIX.length();
        if (! url.startsWith("import:"))
            throw new Exception("URL does not start with 'import': " + url);
        final int sep = url.substring(prefix_len).indexOf(":/");
        if (sep < 0)
            throw new Exception("Missing import data type from URL: " + url);
        final String type = url.substring(prefix_len, prefix_len+sep);
        final String path = url.substring(prefix_len+sep+2);
        return new String[] { type, path };
    }

    /** {@inheritDoc} */
    @Override
    public ArchiveReader getArchiveReader(final String url) throws Exception
    {
       // Avoid duplicate readers for same URL
        synchronized (cache)
        {
            ArchiveReader reader = cache.get(url);
            if (reader != null)
                return reader;
        }
        // Get path, importer from URL
        final String[] type_path = parseURL(url);
        final String type = type_path[0];
        final String path = type_path[1];
        final SampleImporterInfo importer = SampleImporters.getImporter(type);
        if (importer == null)
            throw new Exception("Unknown import data type " + type);
        final ImportArchiveReader reader = new ImportArchiveReader(url, path, importer);
        // Cache the reader by URL
        synchronized (cache)
        {
            cache.put(url, reader);
        }
        return reader;
    }

    /** Removed cached data for given archive data sources
     *  @param sources {@link ArchiveDataSource}[]
     */
    public static void removeCachedArchives(final ArchiveDataSource[] sources)
    {
        for (ArchiveDataSource source : sources)
        {
            synchronized (cache)
            {
                cache.remove(source.getUrl());
            }
        }
    }
}
