/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.writer.rdb;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.util.Arrays;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.csstudio.archive.vtype.ArchiveVString;
import org.csstudio.archive.writer.WriteChannel;
import org.diirt.util.text.NumberFormats;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.ValueFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
//import org.junit.Ignore;

/** JUnit test of the archive writer
 *
 *  <p>Main purpose of these tests is to run in debugger, step-by-step,
 *  so verify if correct RDB entries are made.
 *  The sources don't include anything to check the raw RDB data.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBArchiveWriterTest
{
    final Display display = ValueFactory.newDisplay(0.0, 1.0, 2.0, "a.u.", NumberFormats.format(2), 8.0, 9.0, 10.0, 0.0, 10.0);
    private RDBArchiveWriter writer = null;
    private String name, array_name;

    @Before
    public void connect() throws Exception
    {
        final TestProperties settings = new TestProperties();
        final String url = settings.getString("archive_rdb_url");
        final String user = settings.getString("archive_rdb_user");
        final String password = settings.getString("archive_rdb_password");
        final String schema = settings.getString("archive_rdb_schema");
        name = settings.getString("archive_channel");
        array_name = settings.getString("archive_array_channel");
        if (url == null  ||  user == null  ||  password == null  ||  name == null)
        {
            System.out.println("Skipping test, no archive_rdb_url, user, password");
            return;
        }
        final boolean use_blob = Boolean.parseBoolean(settings.getString("archive_use_blob"));
        if (use_blob)
            System.out.println("Running write test with BLOB");
        else
            System.out.println("Running write test with old array_val table");
        writer = new RDBArchiveWriter(url, user, password, schema, use_blob);
    }

    @After
    public void close()
    {
        if (writer != null)
            writer.close();
    }

    @Test
    public void testChannelLookup() throws Exception
    {
        if (writer == null)
            return;
        WriteChannel channel = writer.getChannel(name);
        System.out.println(channel);
        assertThat(channel, not(nullValue()));
        assertThat(name, equalTo(channel.getName()));

        if (array_name == null)
            return;
        channel = writer.getChannel(array_name);
        System.out.println(channel);
        assertThat(channel, not(nullValue()));
        assertThat(array_name, equalTo(channel.getName()));
    }

    @Test
    public void testWriteDouble() throws Exception
    {
        if (writer == null)
            return;
        System.out.println("Writing double sample for channel " + name);
        final WriteChannel channel = writer.getChannel(name);
        // Write double
        writer.addSample(channel, new ArchiveVNumber(Instant.now(), AlarmSeverity.NONE, "OK", display, 3.14));
        // .. double that could be int
        writer.addSample(channel, new ArchiveVNumber(Instant.now(), AlarmSeverity.NONE, "OK", display, 3.00));
        writer.flush();
    }

    @Test
    public void testWriteDoubleArray() throws Exception
    {
        if (writer == null  ||  array_name == null)
            return;
        System.out.println("Writing double array sample for channel " + array_name);
        final WriteChannel channel = writer.getChannel(array_name);
        writer.addSample(channel, new ArchiveVNumberArray(Instant.now(), AlarmSeverity.NONE, "OK", display,
                3.14, 6.28, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        writer.flush();
    }

    @Test
    public void testWriteLongEnumText() throws Exception
    {
        if (writer == null)
            return;
        final WriteChannel channel = writer.getChannel(name);

        // Enum, sets enumerated meta data
        writer.addSample(channel, new ArchiveVEnum(Instant.now(), AlarmSeverity.MINOR, "OK", Arrays.asList("Zero", "One"), 1));
        writer.flush();

        // Writing string leaves the enumerated meta data untouched
        writer.addSample(channel, new ArchiveVString(Instant.now(), AlarmSeverity.MAJOR, "OK", "Hello"));
        writer.flush();

        // Integer, sets numeric meta data
        writer.addSample(channel, new ArchiveVNumber(Instant.now(), AlarmSeverity.MINOR, "OK", display, 42));
        writer.flush();
    }

    final private static int TEST_DURATION_SECS = 60;
    final private static long FLUSH_COUNT = 500;

    /* PostgreSQL 9 Test Results:
     *
     * HP Compact 8000 Elite Small Form Factor,
     * Intel Core Duo, 3GHz, Windows 7, 32 bit,
     * Hitachi Hds721025cla382 250gb Sata 7200rpm
     *
     * Flush Count  100, 500, 1000: ~7000 samples/sec, no big difference
     *
     * After deleting the constraints of sample.channel_id to channel,
     * severity_id and status_id to sev. and status tables: ~12000 samples/sec,
     * i.e. almost twice as much.
     *
     * JProfiler shows most time spent in 'flush', some in addSample()'s call to setTimestamp(),
     * but overall time is in RDB, not Java.
     *
     *
     * MySQL Test Results (same w/ original IValue and update to VType):
     *
     * iMac8,1    2.8GHz Intel Core 2 Duo, 4GB RAM
     *
     * Without rewriteBatchedStatements=true:  ~7000 samples/sec
     * With rewriteBatchedStatements=true   : ~21000 samples/sec
     */
     // @Ignore
    @Test
    public void testWriteSpeedDouble() throws Exception
    {
        if (writer == null)
            return;

        System.out.println("Write test: Adding samples to " + name + " for " + TEST_DURATION_SECS + " secs");
        final WriteChannel channel = writer.getChannel(name);

        long count = 0;
        final long start = System.currentTimeMillis();
        final long end = start + TEST_DURATION_SECS*1000L;
        do
        {
            ++count;
            writer.addSample(channel, new ArchiveVNumber(Instant.now(), AlarmSeverity.NONE, "OK", display, 3.14));
            if (count % FLUSH_COUNT == 0)
                writer.flush();
        }
        while (System.currentTimeMillis() < end);
        writer.flush();

        System.out.println("Wrote " + count + " samples, i.e. "
                         + ((double)count / TEST_DURATION_SECS) + " samples/sec.");
    }
}
