/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.influxdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.csstudio.archive.influxdb.InfluxDBUtil;
import org.csstudio.archive.influxdb.InfluxDBUtil.ConnectionInfo;
import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.influxdb.raw.InfluxDBRawReader;
import org.diirt.vtype.VType;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** JUnit test of the InfluxDBArchiveServer
 *  <p>
 *  Will only work when suitable archived data is available.
 *  @author Megan Grodowitz
 */
@SuppressWarnings("nls")
public class InfluxDBRawReaderTest
{
    private InfluxDBRawReader reader;
    private String dbname;

    @Before
    public void connect() throws Exception
    {
        String archive_url = "http://localhost:8086";
        // String archive_url = "http://diane.ornl.gov:8086";
        String user = null;
        String password = null;

        dbname = "InfluxDBRawReaderTest-DB";

        if (user == null  ||  password == null)
        {
            System.out.println("Trying connections with no username or password....");
            user = null;
            password = null;
        }

        try
        {
            reader = new InfluxDBRawReader(archive_url, user, password, dbname);
            reader.getQueries().initDatabases(reader.getConnectionInfo().influxdb);

        }
        catch (Exception e)
        {
            System.err.println("Could not create archive reader");
            e.printStackTrace();
        }
    }

    @After
    public void close()
    {
        if (reader != null)
        {
            // try {
            // reader.getConnectionInfo().influxdb.deleteDatabase(dbname);
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            reader.close();
        }
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
        assertEquals("InfluxDB-Raw", reader.getServerName());
        System.out.println(reader.getDescription());
        for (ArchiveInfo arch : reader.getArchiveInfos())
            System.out.println(arch);

        ConnectionInfo ci = reader.getConnectionInfo();
        System.out.println(ci);
        System.out.println("Databases: ");
        for (String db : ci.dbs) {
            System.out.println("\t" + db);
        }
    }

    private void makeData() throws Exception {
        InfluxDB influxdb = reader.getConnectionInfo().influxdb;
        Random rand = new Random();

        int num_hosts = 3;
        String[] hosts = { "apple", "bottom", "jeans" };
        String[] disk_vend = { "Seagate", "WD", "Unknown=Area51" };
        long[] ids = { 100, 200, 300 };
        long[] ramps = { 50, 100, 150 };
        List<BatchPoints> bps = new ArrayList<BatchPoints>();

        for (String host : hosts) {
            BatchPoints batchPoints = BatchPoints.database(dbname).tag("async", "true").tag("host", host)
                    .retentionPolicy("autogen").consistency(ConsistencyLevel.ALL).build();
            bps.add(batchPoints);
        }

        for (int idx = 0; idx < 250; idx++) {
            Thread.sleep(100);

            for (int hdx = 0; hdx < num_hosts; hdx++) {
                Instant stamp = Instant.now().plusNanos(1);

                ramps[hdx] = (ramps[hdx] + 1) % ids[hdx];
                double used = (ids[hdx]) / 10.0 + rand.nextDouble();

                Point point1 = Point.measurement("cpu").time(InfluxDBUtil.toNanoLong(stamp), TimeUnit.NANOSECONDS)
                        .addField("idle", 90L).addField("user,pi", 3.14159).addField("system ramp", ramps[hdx]).build();
                Point point2 = Point.measurement("disk,sda").time(InfluxDBUtil.toNanoLong(stamp), TimeUnit.NANOSECONDS)
                        .addField("vendor name", disk_vend[hdx]).addField("used%", used)
                        .addField("free space", "42.42GB")
                        .build();
                bps.get(hdx).point(point1);
                bps.get(hdx).point(point2);
            }
        }

        for (int hdx = 0; hdx < num_hosts; hdx++) {
            // System.out.println("Line Protocol for points: " +
            // batchPoints.lineProtocol());
            try {
                // Any failure response from the influx server will thrown as an
                // exception
                // with the exception error message set to the server response
                influxdb.write(bps.get(hdx));
            } catch (Exception e) {
                System.err.println("Write Failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Test
    public void demoClearMakeData() throws Exception {
        try {
            reader.getConnectionInfo().influxdb.deleteDatabase(dbname);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            reader.getQueries().initDatabases(reader.getConnectionInfo().influxdb);
        } catch (Exception e) {
            System.err.println("Could not create archive reader");
            e.printStackTrace();
        }

        makeData();
    }

    private void printValues(final ValueIterator values, final int max) throws Exception {
        int count = 0;
        while (values.hasNext()) {
            VType value = values.next();
            System.out.println(value);

            ++count;
            if (count > max) {
                System.out.println("Skipping rest...");
                break;
            }
        }
    }

    @Test
    public void testRead() throws Exception {
        makeData();

        final Instant end = Instant.now();
        final Instant start = Instant.EPOCH;

        ValueIterator values = reader.getRawValues(0, "cpu,host=apple idle", start, end);
        printValues(values, 10);
        values.close();

        values = reader.getRawValues(0, "cpu user\\,pi", start, end);
        printValues(values, 10);
        values.close();

        values = reader.getRawValues(0, "cpu,host=apple system\\ ramp", start, end);
        printValues(values, 10);
        values.close();

        values = reader.getRawValues(0, "disk\\,sda,host=jeans vendor\\ name", start, end);
        printValues(values, 10);
        values.close();

        values = reader.getRawValues(0, "disk\\,sda,host=bottom vendor\\ name", start, end);
        printValues(values, 10);
        values.close();

        values = reader.getRawValues(0, "disk\\,sda,host=bottom used%", start, end);
        printValues(values, 10);
        values.close();

    }

    /** Locate channels by pattern */
    @Test
    public void testChannelByPattern() throws Exception {
        if (reader == null)
            return;
        final String pattern = "c*";
        System.out.println("Channels matching a pattern: " + pattern);
        final String[] names = reader.getNamesByPattern(1, pattern);
        for (String name : names)
            System.out.println(name);
        assertTrue(names.length > 0);
    }
    //
    // /** Locate channels by pattern */
    // @Test
    // public void testChannelByRegExp() throws Exception
    // {
    // if (reader == null)
    // return;
    // final String pattern = "." + channel_name.replace("(",
    // "\\(").substring(1, channel_name.length()-3) + ".*";
    // System.out.println("Channels matching a regular expression: " + pattern);
    // final String[] names = reader.getNamesByRegExp(1, pattern);
    // for (String name : names)
    // System.out.println(name);
    // assertTrue(names.length > 0);
    // }
}
