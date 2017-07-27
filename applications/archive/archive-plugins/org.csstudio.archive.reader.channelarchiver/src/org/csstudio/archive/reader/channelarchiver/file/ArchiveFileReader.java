/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.channelarchiver.file;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.csstudio.apputil.text.RegExHelper;
import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;

/** ArchiveReader for Channel Archiver index & data files.
 *  @author Amanda Carpenter
 */
public class ArchiveFileReader implements ArchiveReader
{
    public static final Logger logger = Logger.getLogger(ArchiveFileReader.class.getName());

    private final String index_name;
    private final ArchiveFileIndexReader indexReader;

    /** Construct an ArchiveFileReader.
     *  @param index Path to  Channel Archiver index file
     *  @throws IOException
     */
    public ArchiveFileReader(final String index) throws IOException
    {
        index_name = index;
        indexReader = new ArchiveFileIndexReader(new File(index));
    }

    @Override
    public String getServerName()
    {
        return "Channel Archiver";
    }

    @Override
    public String getURL()
    {
        return ArchiveFileReaderFactory.PREFIX + index_name;
    }

    @Override
    public String getDescription()
    {
        return "Data File";
    }

    @Override
    public int getVersion()
    {
        return 0;
    }

    @Override
    public ArchiveInfo[] getArchiveInfos()
    {
        return new ArchiveInfo[] { new ArchiveInfo(getServerName(), getDescription(), 0)};
    }

    @Override
    public String[] getNamesByPattern(final int key, final String glob_pattern) throws Exception
    {
        return getNamesByRegExp(key, RegExHelper.fullRegexFromGlob(glob_pattern));
    }

    @Override
    public String[] getNamesByRegExp(final int key, final String reg_exp) throws Exception
    {
        final Pattern pattern = Pattern.compile(reg_exp, Pattern.CASE_INSENSITIVE);
        final List<String> result = new ArrayList<>();
        for (String name : indexReader.getChannelNames())
            if (pattern.matcher(name).matches())
                result.add(name);
        return result.toArray(new String [result.size()]);
    }

    @Override
    public ValueIterator getRawValues(int key, String name, Instant start, Instant end)
            throws UnknownChannelException, Exception
    {
        final List<DataFileEntry> entries = indexReader.getEntries(name, start, end);
        return new ArchiveFileSampleReader(start, end, entries);
    }

    @Override
    public ValueIterator getOptimizedValues(int key, String name, Instant start, Instant end, int count)
            throws UnknownChannelException, Exception
    {
        // Not implemented, falling back to raw values
        return getRawValues(key, name, start, end);
    }

    @Override
    public void cancel()
    {
        //no-op
    }

    @Override
    public void close()
    {
        try
        {
            indexReader.close();
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, "Cannot close index", ex);
        }
    }
}
