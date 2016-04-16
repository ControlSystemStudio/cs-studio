/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.writer.rdb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;

import org.csstudio.archive.config.EngineConfig;
import org.csstudio.archive.config.rdb.RDBArchiveConfig;
import org.csstudio.archive.config.rdb.RDBGroupConfig;
import org.csstudio.archive.config.rdb.RDBSampleMode;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.rdb.RDBArchiveReader;
import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.archive.writer.ArchiveWriter;
import org.csstudio.archive.writer.WriteChannel;
import org.diirt.vtype.VType;
import org.junit.Test;

/** JUnit-based demo of copying data
 *
 *  <p>Not a test, not a great tool, but a quick hack
 *  that was actually used to copy data from a backup MySQL
 *  setup to an Oracle setup that had partition issue,
 *  INSERTing data from the backup after the partition issues had been
 *  resolved.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CopyDemo
{
    // Create new config (for test),
    // or write to existing config where channels are already defined
    // (as expected in production setup)?
    final private static boolean create_config = false;

    private ArchiveReader getReader() throws Exception
    {
        return new RDBArchiveReader("jdbc:mysql://server1/archive", "archive", "$archive", "", "");
    }

    private RDBArchiveConfig getTargetConfig() throws Exception
    {
        return new RDBArchiveConfig("jdbc:mysql://server2/archive", "archive", "$archive", "");
    }

    private ArchiveWriter getWriter() throws Exception
    {
        return new RDBArchiveWriter("jdbc:mysql://server2/archive", "archive", "$archive", "", true);
    }

    @Test
    public void demoCopy() throws Exception
    {
        final DateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final Instant start = parser.parse("2013-05-17 17:00").toInstant();
        final Instant end = parser.parse("2013-05-18 00:00").toInstant();

        final ArchiveReader reader = getReader();
        final ArchiveWriter writer = getWriter();

        RDBArchiveConfig config;
        EngineConfig engine;
        RDBGroupConfig group;
        RDBSampleMode mode;
        if (create_config)
        {
            config = getTargetConfig();
            engine = config.createEngine("demo", "demo", "http://localhost:4812");
            group = config.addGroup(engine, "demo");
            mode = config.getSampleMode(false, 0.0, 30.0);
        }

        final String[] names = reader.getNamesByPattern(1, "*");
        for (String name : names)
        {
            System.out.print(name + " ...");
            System.out.flush();
            final ValueIterator values = reader.getRawValues(1, name, start, end);

            if (create_config)
                config.addChannel(group, name, mode);

            long count = 0;
            final WriteChannel channel = writer.getChannel(name);
            while (values.hasNext())
            {
                final VType sample = values.next();
                if (VTypeHelper.getTimestamp(sample).compareTo(start) < 0)
                {
                    System.out.println("Skip " + sample);
                    continue;
                }
                if (VTypeHelper.getTimestamp(sample).compareTo(end) >= 0)
                {
                    System.out.println("Skip " + sample);
                    continue;
                }
                try
                {
                    writer.addSample(channel, sample);
                    writer.flush();
                    ++count;
                }
                catch (Exception ex)
                {
                    System.out.println("Error inserting " + channel + " : " + sample);
                    ex.printStackTrace();
                }
            }
            writer.flush();
            System.out.println(count + " samples");
        }

        writer.close();
        reader.close();
    }
}
