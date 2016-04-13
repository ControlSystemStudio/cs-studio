/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.TimestampHelper;
import org.diirt.util.time.TimeDuration;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** JUnit test of the RDBArchiveServer
 *  <p>
 *  Will only work when suitable archived data is available.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBArchiveReaderTest
{
    final private static Duration TIMERANGE = Duration.ofHours(10);
    final private static Duration WAVEFORM_TIMERANGE = Duration.ofMinutes(20);

    final private static int BUCKETS = 50;

    final private static boolean dump = true;

    @SuppressWarnings("unused")
    final private static SimpleDateFormat parser = new SimpleDateFormat("yyyy/MM/dd");

    private RDBArchiveReader reader;
    private String proc, name, array_name;

    @Before
    public void connect() throws Exception
    {
        final TestProperties settings = new TestProperties();
        final String url = settings.getString("archive_rdb_url");
        final String user = settings.getString("archive_rdb_user");
        final String password = settings.getString("archive_rdb_password");
        final String schema = settings.getString("archive_rdb_schema");
        proc = settings.getString("archive_rdb_stored_procedure");
        if (proc == null)
            proc = "";
        name = settings.getString("archive_channel");
        array_name = settings.getString("archive_array_channel");
        if (url == null  ||  user == null  ||  password == null  ||  name == null)
        {
            System.out.println("Skipping test, no archive_rdb_url, user, password, name");
            reader = null;
            return;
        }
        final boolean use_blob = Boolean.parseBoolean(settings.getString("archive_use_blob"));
        if (use_blob)
            System.out.println("Running read test with BLOB");
        else
            System.out.println("Running read test with old array_val table");
        reader = new RDBArchiveReader(url, user, password, schema, proc, use_blob);

        assertEquals(use_blob, reader.useArrayBlob());
    }

    @After
    public void close()
    {
        if (reader != null)
            reader.close();
    }

    /** Schedule a call to 'cancel()'
     *  @param archive ArchiveReader to cance
     *  @param seconds Seconds until cancellation
     */
    @SuppressWarnings("unused")
    private void scheduleCancellation(final ArchiveReader archive, final double seconds)
    {
        new Timer("CancellationTest").schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                System.out.println("Cancelling ongoing requests!");
                archive.cancel();
            }
        }, 2000);
    }

    /** Basic connection */
    @Test
    public void testBasicInfo() throws Exception
    {
        if (reader == null)
            return;
        assertEquals("RDB", reader.getServerName());
        System.out.println(reader.getDescription());
        for (ArchiveInfo arch : reader.getArchiveInfos())
            System.out.println(arch);
    }

    /** Locate channels by pattern */
    @Test
    public void testChannelByPattern() throws Exception
    {
        if (reader == null)
            return;
        final String pattern = name.substring(0, name.length()-1) + "?";
        System.out.println("Channels matching a pattern: " + pattern);
        final String[] names = reader.getNamesByPattern(1, pattern);
        for (String name : names)
            System.out.println(name);
        assertTrue(names.length > 0);
    }

    /** Locate channels by pattern */
    @Test
    public void testChannelByRegExp() throws Exception
    {
        if (reader == null)
            return;
        final String pattern = "." + name.replace("(", "\\(").substring(1, name.length()-3) + ".*";
        System.out.println("Channels matching a regular expression: " + pattern);
        final String[] names = reader.getNamesByRegExp(1, pattern);
        for (String name : names)
            System.out.println(name);
        assertTrue(names.length > 0);
    }

    /** Get raw data for scalar */
    @Test
    public void testRawData() throws Exception
    {
        if (reader == null)
            return;
        System.out.println("Raw samples for " + name + ":");
        final Instant end = Instant.now();
        final Instant start = end.minus(TIMERANGE);

        final BenchmarkTimer timer = new BenchmarkTimer();
        final ValueIterator values = reader.getRawValues(0, name, start, end);

        if (dump)
        {
            int count = 0;
            Display display = null;
            while (values.hasNext())
            {
                VType value = values.next();
                System.out.println(value);
                if (display == null)
                    display = ValueUtil.displayOf(value);
                ++count;
                if (count > 10)
                {
                    System.out.println("Skipping rest...");
                    break;
                }
            }
            values.close();
            System.out.println("Meta data: " + display);
        }
        else
        {
            int count = 0;
            while (values.hasNext())
            {
                final VType value = values.next();
                // System.out.println(value);
                assertNotNull(value);
                ++count;
            }
            timer.stop();
            /* PostgreSQL 9 Test Results without the System.out in the loop:
             *
             * HP Compact 8000 Elite Small Form Factor,
             * Intel Core Duo, 3GHz, Windows 7, 32 bit,
             * Hitachi Hds721025cla382 250gb Sata 7200rpm
             *
             * No constraints on sample table.
             * 723000 samples in 7.547 seconds
             * 95796.79727732475 samples/sec
             *
             * JProfiler shows that time is spent in ResultSet.next(),
             * which fetches new data according to the 'fetch size'
             * of 100000.
             * But overall about 3 times that much CPU is spent
             * in ResultSet.getTimestamp() because it needs to
             * deal with Calendar
             */
            System.out.println(count + " samples in " + timer);
            System.out.println(count/timer.getSeconds() + " samples/sec");
        }
    }

    /** Get raw data for waveform */
    @Test
    public void testRawWaveformData() throws Exception
    {
        if (reader == null  ||  array_name == null)
            return;
        System.out.println("Raw samples for waveform " + array_name + ":");

        if (reader.useArrayBlob())
            System.out.println(".. using BLOB");
        else
            System.out.println(".. using non-BLOB array table");

        final Instant end = Instant.now();
        final Instant start = end.minus(WAVEFORM_TIMERANGE);

        // Cancel after 10 secs
        // scheduleCancellation(reader, 10.0);
        final ValueIterator values = reader.getRawValues(0, array_name, start, end);
        while (values.hasNext())
        {
            final VType value = values.next();
            System.out.println(value);
        }
        values.close();
    }

    /** Get optimized data for scalar, using the client-side {@link AveragedValueIterator} */
    @Test
    public void testJavaOptimizedScalarData() throws Exception
    {
        if (reader == null)
            return;
        System.out.println("Optimized samples for " + name + ":");
        System.out.println("-- Java implementation --");

        final Instant end = Instant.now();
        final Instant start = end.minus(TIMERANGE);

        final ValueIterator raw = reader.getRawValues(0, name, start, end);
        final double seconds = TimeDuration.toSecondsDouble(Duration.between(start, end)) / BUCKETS;
        System.out.println("Time range: "
                + TimestampHelper.format(start) + " ... " + TimestampHelper.format(end)
                + ", " + BUCKETS + " bins, "
                + "i.e. every " + seconds + " seconds");
        final ValueIterator values = new AveragedValueIterator(raw, seconds);
        while (values.hasNext())
        {
            final VType value = values.next();
            System.out.println(value);
        }
        values.close();
    }

    /** Get optimized data for scalar, using the server-side {@link StoredProcedureValueIterator} */
    @Test
    public void testStoredProcedure() throws Exception
    {
        if (reader == null)
            return;
        if (proc.isEmpty())
            System.out.println("No stored procedure available");
        final int channel_id = reader.getChannelID(name);
        System.out.println("Optimized samples for " + name + " (" + channel_id + "):");
        System.out.println("-- Stored procedure --");

        final Instant end = Instant.now();
        final Instant start = end.minus(TIMERANGE);
        final ValueIterator values = new StoredProcedureValueIterator(reader, proc, channel_id, start, end, BUCKETS);
        while (values.hasNext())
        {
            final VType value = values.next();
            System.out.println(value);
        }
        values.close();
    }

//    /** Directly call the stored procedure */
//    @Test
//    @Ignore
//    public void testSQL() throws Exception
//    {
//        final RDBUtil rdb = RDBUtil.connect(URL, USER, PASSWORD, false);
//        final PreparedStatement statement = rdb.getConnection().prepareStatement(
//            "SELECT smpl_time, severity_id, status_id, num_val, float_val, str_val" +
//            " FROM chan_arch.sample" +
//            " WHERE channel_id=?" +
//            "   AND smpl_time BETWEEN ? AND ?"
//            // + "   ORDER BY smpl_time"
//            );
//        statement.setInt(1, 58418);
//        statement.setTimestamp(2, new Timestamp(parser.parse("2009/07/01").getTime()));
//        statement.setTimestamp(3, new Timestamp(parser.parse("2009/11/01").getTime()));
//        statement.setFetchSize(100000);
//        final long bench_start = System.currentTimeMillis();
//        final ResultSet result = statement.executeQuery();
//        // - Network sniffer:
//        // Client -> Oracle: SELECT ....
//        // Oracle -> Client: Result contains columns time SMPL_TIME, int SEVERITY_ID, ...
//        // Client -> Oracle: OK, send me the data
//        // - then a long pause, like 180 seconds.
//        // - No network traffic, client waits in executeQuery(), until suddenly
//        // Oracle -> Client: data
//        // Oracle -> Client: data
//        // Oracle -> Client: data
//        // Client -> Oracle: Acknowledge
//        // Oracle -> Client: data
//        // Oracle -> Client: data
//        // Oracle -> Client: data
//        // Client -> Oracle: Acknowledge
//        // - about 4000 network packages with data in maybe 4 seconds
//        // - executeQuery() returns
//        final long bench_lap1 = System.currentTimeMillis();
//        // While fetching the rows of data, most calls to next()
//        // do not cause any network traffic.
//        // With setFetchSize(10), the default, there is another request for data
//        // about every 10 rows. With setFetchSize(100000) there is no network
//        // traffic after about 100000 rows, so maybe that fetch size is too big
//        // and the JDBC library uses its own idea of how many rows to transfer
//        // in a network packet.
//        // These bursts data read over the network triggered by next()
//        // take almost no time compared to the initial executeQuery(),
//        // but a bigger setFetchSize() reduces the number of network requests
//        // and is of course faster.
//        int count = 0;
//        Timestamp last = null;
//        while (result.next())
//        {
//            final Timestamp time = result.getTimestamp(1);
//            if (last != null &&  time.before(last))
//                System.out.println("Time error!");
//            last = time;
//            ++count;
//        }
//        final long bench_lap2 = System.currentTimeMillis();
//        final double secs_query = (bench_lap1 - bench_start) / 1000.0;
//        final double secs_total = (bench_lap2 - bench_start) / 1000.0;
//        System.out.println(count + " samples");
//        System.out.println("Query: " + secs_query);
//        System.out.println("Total: " + secs_total);
//        statement.close();
//        rdb.close();
//    }
}
