package org.csstudio.archive.reader.influxdb;

import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.csstudio.archive.influxdb.InfluxDBResults;
import org.csstudio.archive.influxdb.InfluxDBUtil;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.influxdb.dto.QueryResult.Series;

public class InfluxDBJavaTest
{
    String dbName = "aTimeSeries";
    InfluxDB influxDB;
    final private static int TEST_DURATION_SECS = 60;
    final private static long FLUSH_COUNT = 500;

    public void printInfo(InfluxDB influxdb)
    {
        List<String> dbs = influxdb.describeDatabases();
        String ver = influxdb.version();

        System.out.println("Connected to database version: " + ver);
        System.out.println("Contains " + dbs.size() + " databases: ");

        for (String db : dbs)
        {
            System.out.println("\t" + db);
        }
    }


    @Before
    public void connect() throws Exception
    {
        try
        {
            influxDB = InfluxDBFactory.connect("http://diane.ornl.gov:8086");
            printInfo(influxDB);
        }
        catch (Exception e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        }


        influxDB.createDatabase(dbName);

    }

    /** Basic connection */
    @Test
    public void LongValueProblem()
    {
        long millis = System.currentTimeMillis();
        long innano = millis * 1000000 + 1;
        Instant instamp = Instant.ofEpochMilli(millis).plusNanos(1);

        long lval = 1485370052974000001L;

        System.out.println("Timestamp: " + innano + " = " + instamp);

        BatchPoints batchPoints = BatchPoints
                .database(dbName)
                .tag("async", "true")
                .retentionPolicy("autogen")
                .consistency(ConsistencyLevel.ALL)
                .build();
        Point point = Point.measurement("testProblems")
                .time(innano, TimeUnit.NANOSECONDS)
                .addField("double", 3.14)
                .addField("long", lval)
                .build();

        batchPoints.point(point);

        try
        {
            // Any failure response from the influx server will thrown as an exception
            // with the exception error message set to the server response
            influxDB.write(batchPoints);
        }
        catch (Exception e)
        {
            System.err.println("Write Failed: " + e.getMessage());
            e.printStackTrace();
        }

        Query query = new Query("SELECT * FROM testProblems ORDER BY time DESC LIMIT 1", dbName);

        System.out.println("Testing query using rfc3339 time/date: ");
        QueryResult result = influxDB.query(query);
        System.out.println(InfluxDBResults.toString(result));

        Series series0 = result.getResults().get(0).getSeries().get(0);
        List<String> cols = series0.getColumns();
        List<Object> val0 = series0.getValues().get(0);

        String tsstr = (String) val0.get(cols.indexOf("time"));
        Instant outstamp = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(tsstr));

        if (!outstamp.equals(instamp))
        {
            System.err.println("Got bad timestamp value back as string [" + tsstr + "] -> " + outstamp + " != " + instamp);
        }

        Object outlval_obj = val0.get(cols.indexOf("long"));
        if (outlval_obj instanceof Double)
        {
            long outlval = ((Double)outlval_obj).longValue();
            if (outlval != lval)
            {
                System.err.println("Got bad lval back as double [" + (outlval_obj) + "] -> " + outlval + " != " + lval);
            }
        }
        else
        {
            System.err.println("Expected Double output for long value... got " + outlval_obj.getClass().getName() + " = " + outlval_obj.toString());
        }

        System.out.println("Testing query using epoch=n time/date: ");
        result = influxDB.query(query, TimeUnit.NANOSECONDS);
        System.out.println(InfluxDBResults.toString(result));

        series0 = result.getResults().get(0).getSeries().get(0);
        cols = series0.getColumns();
        val0 = series0.getValues().get(0);

        Double tsnano = (Double) val0.get(cols.indexOf("time"));
        long outnano = tsnano.longValue();
        long outmillis = outnano / 1000000;
        outstamp = Instant.ofEpochMilli(outmillis).plusNanos(outnano - (outmillis * 1000000));
        if ((outnano != innano) || (!outstamp.equals(instamp)))
        {
            System.err.println("Got bad long nanos back as double [" + tsnano + "] -> " + outnano + " ?= " + innano);
            System.err.println("Got bad timestamp back as double [" + tsnano + "] -> " + outstamp + " ?= " + instamp);
        }

        outlval_obj = val0.get(cols.indexOf("long"));
        if (outlval_obj instanceof Double)
        {
            long outlval = ((Double)outlval_obj).longValue();
            if (outlval != lval)
            {
                System.err.println("Got bad lval back as double [" + outlval_obj + "] -> " + outlval + " != " + lval);
            }
        }

    }

    /** Basic connection */
    @Test
    public void demoBasicConnect() throws Exception
    {
        long millis = System.currentTimeMillis();
        long tnano = millis * 1000000 + 1;
        Instant stamp = Instant.ofEpochMilli(millis).plusNanos(1);

        System.out.println("Timestamp: " + tnano + " = " + stamp + " = " + InfluxDBUtil.toInfluxDBTimeFormat(stamp));

        double tricky = -Double.MAX_VALUE;
        //double tricky = Double.NaN;
        byte[] trickybytes = InfluxDBUtil.toByteArray(tricky);
        System.out.println("Tricky: " + tricky + " = "+ InfluxDBUtil.bytesToHex(trickybytes));

        BatchPoints batchPoints = BatchPoints
                .database(dbName)
                .tag("async", "true")
                .retentionPolicy("autogen")
                .consistency(ConsistencyLevel.ALL)
                .build();
        Point point1 = Point.measurement("cpu1")
                .time(tnano, TimeUnit.NANOSECONDS)
                .addField("idle", 90L)
                .addField("user", tricky)
                .addField("system", 1L)
                .build();
        Point point2 = Point.measurement("disk")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("used", 80L)
                .addField("free", 1L)
                .build();
        batchPoints.point(point1);
        batchPoints.point(point2);
        System.out.println("Line Protocol for points: " + batchPoints.lineProtocol());

        try
        {
            // Any failure response from the influx server will thrown as an exception
            // with the exception error message set to the server response
            influxDB.write(batchPoints);
        }
        catch (Exception e)
        {
            System.err.println("Write Failed: " + e.getMessage());
            e.printStackTrace();
        }
        //Query query = new Query("SELECT idle FROM cpu", dbName);
        //Select 3 most recent points
        Query query = new Query("SELECT * FROM cpu1 ORDER BY time DESC LIMIT 2", dbName);
        System.out.println("Sending query: " + query.getCommandWithUrlEncoded());

        QueryResult result = influxDB.query(query);
        System.out.println(InfluxDBResults.toString(result));


    }

    @Test
    public void testWriteSpeedDouble() throws Exception
    {
        final String channel_name = "testDouble";

        System.out.println("Write test: Adding samples to " + channel_name + " for " + TEST_DURATION_SECS + " secs");

        long count = 0;
        final long start = System.currentTimeMillis();
        final long to_end = start + TEST_DURATION_SECS*1000L;
        long end;

        BatchPoints batchPoints = BatchPoints
                .database(dbName)
                .retentionPolicy("autogen")
                .consistency(ConsistencyLevel.ALL)
                .build();

        do
        {
            ++count;
            Point point = Point.measurement(channel_name)
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("a", "isA")
                    .tag("b", "isB")
                    .addField("double", 3.1)
                    .build();

            batchPoints.point(point);

            if (count % FLUSH_COUNT == 0)
            {
                influxDB.write(batchPoints);
                batchPoints = BatchPoints
                        .database(dbName)
                        .retentionPolicy("autogen")
                        .consistency(ConsistencyLevel.ALL)
                        .build();
            }
            end = System.currentTimeMillis();
        }
        while (end < to_end);

        double secs = (end-start) / 1000.0;
        System.out.println("Wrote " + count + " samples, i.e. "
                + (count / secs) + " samples/sec.");

        influxDB.write(batchPoints);
    }
}
