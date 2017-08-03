package org.csstudio.archive.reader.influxdb;

import static org.junit.Assert.assertTrue;

import java.time.Instant;

import org.csstudio.archive.influxdb.InfluxDBDataSource;
import org.csstudio.archive.influxdb.InfluxDBQueries;
import org.csstudio.archive.influxdb.InfluxDBSeriesInfo;
import org.csstudio.archive.influxdb.InfluxDBUtil;
import org.junit.Test;

public class InfluxDBUtilTest {

    /** Time/Date formatting */
    @Test
    public void testTimeDateFormat() throws Exception {
        // long millis = System.currentTimeMillis();
        // long tnano = millis * 1000000 + 555;
        // Instant stamp = Instant.ofEpochMilli(millis).plusNanos(555);
        //
        // System.out.println("Timestamp: " + tnano + " = " + stamp + " = " +
        // InfluxDBUtil.toInfluxDBTimeFormat(stamp));

        final Instant time1 = Instant.ofEpochMilli(1484845031986L).plusNanos(1);
        final String timestr1 = "2017-01-19T16:57:11.986000001Z";

        final Instant time2 = Instant.ofEpochMilli(1484845213540L).plusNanos(555);
        final String timestr2 = "2017-01-19T17:00:13.540000555Z";

        assertTrue(InfluxDBUtil.toInfluxDBTimeFormat(time1).equals(timestr1));
        assertTrue(InfluxDBUtil.toInfluxDBTimeFormat(time2).equals(timestr2));

        assertTrue(InfluxDBUtil.fromInfluxDBTimeFormat(timestr1).equals(time1));
        assertTrue(InfluxDBUtil.fromInfluxDBTimeFormat(timestr2).equals(time2));
    }

    @Test
    public void testSeriesParsing() throws Exception {

        InfluxDBSeriesInfo series;

        series = InfluxDBSeriesInfo.decodeLineProtocol("wea\\ ther,location=us-midwest temperature");
        System.out.println(InfluxDBQueries.get_series_points(series, null, null, null));

        series = InfluxDBSeriesInfo.decodeLineProtocol("weather,location=us-midwest temp\\=rature");
        System.out.println(InfluxDBQueries.get_series_points(series, Instant.EPOCH, Instant.ofEpochMilli(1001), null));

        series = InfluxDBSeriesInfo
                .decodeLineProtocol("weather,location\\ place=us-midwest,warnings=tstorm temperature");
        System.out.println(InfluxDBQueries.get_series_points(series, null, Instant.EPOCH, null));
    }

    @Test
    public void testDataSourceParsing() throws Exception {
        InfluxDBDataSource ds = InfluxDBDataSource
                .decodeURL("infludb-raw://localhost:8086?db=mytestdb&user=me&password=badpassword");

        System.out.println("url = " + ds.getURL());
        System.out.println("db = " + ds.getArgRequired("db"));
        System.out.println("user = " + ds.getArgRequired("user"));
        System.out.println("password = " + ds.getArgRequired("password"));

        ds = InfluxDBDataSource.decodeURL("infludb://somehost.somewhere.com:8086?db=mytestdb");
        System.out.println("url = " + ds.getURL());
        System.out.println("db = " + ds.getArgRequired("db"));
    }

}
