/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.channelarchiver.file;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ValueIterator;
import org.diirt.vtype.VType;

/** Demo of Channel Archiver Data File Reader
 *
 *  @author Amanda Carpenter
 *  @author Kay Kasemir
 */
public class ArchiveReaderDemo
{
    private final static DateTimeFormatter parser =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnnnnn").withZone(ZoneId.systemDefault());

    private static void dump(final ArchiveReader reader) throws Exception
    {
        // 2005-01-11 02:56:59.988856666    148.50 gpm

        for (String name : reader.getNamesByPattern(0, "CCL_HPRF:KlyCol3:Flw"))
        {
            System.out.println("Channel: " + name);
            final ValueIterator values = reader.getRawValues(0, name,
                    // Instant.ofEpochMilli(0),
                    // Instant.from(parser.parse("2005-01-05 10:31:59.863343666")),
                    Instant.from(parser.parse("2005-01-11 02:56:59.988856600")),
                    Instant.now());
            try
            {
                while (values.hasNext())
                {
                    final VType sample = values.next();
                    System.out.println(sample);
                }
            }
            finally
            {
                values.close();
            }
        }
    }

    /** Demonstrates ArchiveFileReader.
     *
     *  Given a list of index files, prints all channels and data
     *  for each file.
     *
     *  @param args List of index files
     *  @throws Exception on error
     */
    public static void main(final String[] args) throws Exception
    {
        final Logger logger = Logger.getLogger("");
        logger.setLevel(Level.ALL);
        for (Handler handler : logger.getHandlers())
            handler.setLevel(Level.ALL);

        // /home/ky9/archdata/hprf/2005/01_05/index
        // /home/ky9/archdata/hprf/test/index
        for (String arg : args)
        {
            System.out.println("Index file " + arg);
            ArchiveReader reader = new ArchiveFileReader(arg);
            dump(reader);
        }
    }
}
