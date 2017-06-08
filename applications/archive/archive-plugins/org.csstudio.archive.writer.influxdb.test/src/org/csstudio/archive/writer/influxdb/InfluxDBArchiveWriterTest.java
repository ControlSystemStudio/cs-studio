/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.writer.influxdb;

import java.time.Instant;
import java.util.Arrays;
import java.util.Random;

import org.csstudio.archive.influxdb.InfluxDBResults;
//import org.junit.Ignore;
import org.csstudio.archive.influxdb.InfluxDBUtil.ConnectionInfo;
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

/** JUnit test of the archive writer
 *
 *  <p>Main purpose of these tests is to run in debugger, step-by-step,
 *  so verify if correct DB entries are made.
 *  The sources don't include anything to check the raw DB data.
 *
 *  @author Megan Grodowitz
 */
@SuppressWarnings("nls")
public class InfluxDBArchiveWriterTest
{
    final Display display = ValueFactory.newDisplay(0.0, 1.0, 2.0, "a.u.", NumberFormats.format(2), 8.0, 9.0, 10.0, 0.0, 10.0);
    private InfluxDBArchiveWriter writer = null;
    private String channel_name, array_channel_name;


    @Before
    public void connect() throws Exception
    {
        //        final TestProperties settings = new TestProperties();
        //        final String url = settings.getString("archive_influxdb_url");
        //        final String user = settings.getString("archive_influxdb_user");
        //        final String password = settings.getString("archive_influxdb_password");
        //        name = settings.getString("archive_channel");
        //        array_name = settings.getString("archive_array_channel");

        String archive_url = "http://localhost:8086";
        // String archive_url = "http://diane.ornl.gov:8086";
        String user = null;
        String password = null;

        channel_name = "testPV";
        array_channel_name = "testPV_Array";

        if (archive_url == null  ||  channel_name == null)
        {
            System.out.println("Skipping test, missing one of: archive_url, channel_name");
            writer = null;
            return;
        }

        if (user == null  ||  password == null)
        {
            System.out.println("Trying connections with no username or password....");
            user = null;
            password = null;
        }

        try
        {
            writer = new InfluxDBArchiveWriter(archive_url, user, password);
        }
        catch (Exception e)
        {
            System.err.println("Could not create archive writer");
            e.printStackTrace();
        }

        writer.getQueries().initDatabases(writer.getConnectionInfo().influxdb);
    }

    @After
    public void close()
    {
        if (writer != null)
            writer.close();
    }

    /** Basic connection */
    @Test
    public void testBasicInfo() throws Exception
    {
        if (writer == null)
            return;

        ConnectionInfo ci = writer.getConnectionInfo();
        System.out.println(ci);
    }

    private WriteChannel getMakeChannel(final String channel_name) throws Exception
    {
        InfluxDBWriteChannel channel;
        try
        {
            channel = (InfluxDBWriteChannel) writer.getChannel(channel_name);
            System.out.println("Channel " + channel_name + " is stored as " + channel.toLongString());
        }
        catch (Exception e)
        {
            System.out.println("Failed to get channel, trying to make new...");
            try
            {
                channel = (InfluxDBWriteChannel) writer.makeNewChannel(channel_name);
            }
            catch (Exception e1)
            {
                e.printStackTrace();
                throw new Exception("Failed to find or make new channel " + channel_name, e1);
            }
        }
        return channel;
    }

    private void printSomePoints(final String name) throws Exception
    {
        System.out.println(InfluxDBResults.toString(writer.getQueries().get_all_meta_data(name)));
        System.out.println(InfluxDBResults.toString(writer.getQueries().get_newest_channel_samples(name, null, null, 8L)));

    }

    @Test
    public void testWriteDouble() throws Exception
    {
        if (writer == null)
            return;
        System.out.println("Writing double sample for channel " + channel_name);

        WriteChannel channel = getMakeChannel(channel_name);
        // Write double
        writer.addSample(channel, new ArchiveVNumber(Instant.now(), AlarmSeverity.NONE, "OK", display, 3.14));
        // .. double that could be int
        writer.addSample(channel, new ArchiveVNumber(Instant.now(), AlarmSeverity.NONE, "OK", display, 3.00));
        writer.addSample(channel, new ArchiveVNumber(Instant.now(), AlarmSeverity.NONE, "OK", display, Double.NaN));
        writer.flush();

        printSomePoints(channel.getName());

    }

    @Test
    public void testWriteDoubleArray() throws Exception
    {
        if (writer == null  ||  array_channel_name == null)
            return;
        System.out.println("Writing double array sample for channel " + array_channel_name);
        final WriteChannel channel = getMakeChannel(array_channel_name);
        writer.addSample(channel, new ArchiveVNumberArray(Instant.now(), AlarmSeverity.NONE, "OK", display,
                Double.NaN, 6.28, 1.0, 2.0, 3.0, Double.NaN, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        writer.flush();

        printSomePoints(channel.getName());
    }

    @Test
    public void testWriteText() throws Exception
    {
        if (writer == null)
            return;
        final WriteChannel channel = getMakeChannel(channel_name);

        writer.addSample(channel, new ArchiveVString(Instant.now(), AlarmSeverity.MAJOR, "OK", "Foo"));
        writer.addSample(channel, new ArchiveVString(Instant.now(), AlarmSeverity.MAJOR, "OK", "Bar"));
        writer.flush();

        printSomePoints(channel.getName());
    }

    @Test
    public void testWriteLongEnumText() throws Exception
    {
        if (writer == null)
            return;
        final WriteChannel channel = getMakeChannel(channel_name);

        // Enum, sets enumerated meta data
        writer.addSample(channel, new ArchiveVEnum(Instant.now(), AlarmSeverity.MINOR, "OK", Arrays.asList("Zero", "One"), 1));
        writer.flush();

        // Writing string
        writer.addSample(channel, new ArchiveVString(Instant.now(), AlarmSeverity.MAJOR, "OK", "Hello"));
        writer.flush();

        // Integer, sets numeric meta data
        writer.addSample(channel, new ArchiveVNumber(Instant.now(), AlarmSeverity.MINOR, "OK", display, 1485370052974000001L));
        writer.flush();

        printSomePoints(channel.getName());
    }

    final private static int TEST_DURATION_SECS = 10;
    final private static long FLUSH_COUNT = 10000;
    final private static long MAX_SAMPLES = 100000;

    /**
     *
     * @throws Exception
     *
     * Results from testing:
     * Server:
     *    - RHEL 7
     *    - 8 core Intel i7-4790 3.6GHz
     *    - 16GB 1600MHz DDR3 RAM
     *    - 500GB 7200 RPM SATA Disk
     *
     *  Client:
     *    - OSX 10.11
     *    - 2.5 GHz Intel Core i7
     *    - 16 GB 1600 MHz DDR3
     *    - 512GB SSD storage
     *
     *   Client -> Server over LAN:
     *   - FLUSH_COUNT =    500 ->  12,000 samples per second
     *   - FLUSH_COUNT =   5000 ->  75,000 samples per second
     *   - FLUSH_COUNT =  10000 -> 110,000 samples per second
     *   - FLUSH_COUNT =  20000 -> 140,000 samples per second
     *   - FLUSH_COUNT =  50000 -> 170,000 samples per second
     *   - FLUSH_COUNT = 100000 -> 180,000 samples per second
     *   - FLUSH_COUNT = 200000 -> 185,000 samples per second
     *
     *   According to the documentation, they do not recommend writing more than 5000 points
     *   at a time to avoid timing out during the HTTP POST. So the higher flush counts may get
     *   higher speeds, but might encounter problems. These tests did not have any such problems.
     */

    @Test
    public void demoWriteSpeedDouble() throws Exception
    {
        if (writer == null)
            return;

        System.out.println("Write test: Adding samples to " + channel_name + " for " + TEST_DURATION_SECS + " secs");
        final WriteChannel channel = getMakeChannel(channel_name);

        long count = 0;

        double[] vals = new double[(int) FLUSH_COUNT];
        Random randval = new Random();
        for (int f = 0; f < FLUSH_COUNT; f++)
        {
            vals[f] = randval.nextDouble();
        }

        final long start = System.currentTimeMillis();
        long end = start;
        final long to_end = start + TEST_DURATION_SECS*1000L;
        Instant stamp = Instant.now().minusMillis(MAX_SAMPLES * 100).plusNanos(100);
        do
        {
            for (int f = 0; f < FLUSH_COUNT; f++)
            {
                stamp = stamp.plusMillis(100);
                writer.addSample(channel, new ArchiveVNumber(stamp, AlarmSeverity.NONE, "OK", display, vals[f]));
            }
            count += FLUSH_COUNT;
            writer.flush();
            end = System.currentTimeMillis();
        }
        while ((end < to_end) && (count < MAX_SAMPLES));
        writer.flush();

        final double duration_secs = (end-start) / 1000.0;

        System.out.println("Wrote " + count + " samples, i.e. "
                + (count / duration_secs) + " samples/sec.");
    }

    @Test
    public void demoWriteRampPV() throws Exception {
        if (writer == null)
            return;

        final String test_channel = "rampPV0";
        final int sample_count = 1000;

        final WriteChannel channel = getMakeChannel(test_channel);

        Instant stamp = Instant.now().minusSeconds(sample_count);

        double min = 0.0, max = 200.0, step = 2.0;
        double val = min;
        int flush = 0;
        for (int i = 0; i < sample_count; i++) {
            writer.addSample(channel, new ArchiveVNumber(stamp, AlarmSeverity.NONE, "OK", display, val));
            stamp = stamp.plusSeconds(1);

            val += step;
            if ((val > max) || (val < min)) {
                step = -step;
            }

            flush++;
            if (flush >= FLUSH_COUNT) {
                writer.flush();
                flush = 0;
            }
        }
        writer.flush();
    }

    @Test
    public void demoUpdateRampPV() throws Exception {
        if (writer == null)
            return;

        final String test_channel = "rampPV0";
        final int sample_count = 1000;

        final WriteChannel channel = getMakeChannel(test_channel);

        double min = 0.0, max = 200.0, step = 2.0;
        double val = min;

        for (int i = 0; i < sample_count; i++) {
            Instant stamp = Instant.now();
            writer.addSample(channel, new ArchiveVNumber(stamp, AlarmSeverity.NONE, "OK", display, val));

            val += step;
            if ((val > max) || (val < min)) {
                step = -step;
            }
            writer.flush();
            if ((i % 10) == 0) {
                System.out.println("Wrote: " + i);
            }

            Thread.sleep(100);

        }
        writer.flush();
    }

}
