/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.time;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import org.csstudio.java.time.TimestampFormats;
import org.junit.Test;

import junit.framework.TestCase;

/** JUnit tests of the TimeParser
 *
 *  The tests "work", but everything relative to 'now' could
 *  fail around times of daylight saving transitions.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimeParserUnitTest extends TestCase
{

    @Test
    public void testAbsoluteTimeParser() throws Exception
    {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnnnnnX");
        ZonedDateTime zdt = ZonedDateTime.parse("2014-09-02 08:05:23.653123544+02", f);
        ZoneId zone = zdt.getZone();

        Calendar cal;
        // Full time with nanoseconds
        String forrmatedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnnnnn").withZone(zone).format(zdt);
        System.out.println(forrmatedTime);
        cal = AbsoluteTimeParser.parse(forrmatedTime);
        assertEquals(2014, cal.get(Calendar.YEAR));
        assertEquals( 9, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals( 2, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(8, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals( 5, cal.get(Calendar.MINUTE));
        assertEquals(23, cal.get(Calendar.SECOND));
        assertEquals(653, cal.get(Calendar.MILLISECOND));

        // Full ISO time with nanoseconds
        forrmatedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnnnnnX").withZone(zone).format(zdt);
        System.out.println(forrmatedTime);
        cal = AbsoluteTimeParser.parse(forrmatedTime);
        assertEquals(2014, cal.get(Calendar.YEAR));
        assertEquals( 9, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals( 2, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(8, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals( 5, cal.get(Calendar.MINUTE));
        assertEquals(23, cal.get(Calendar.SECOND));
        assertEquals(653, cal.get(Calendar.MILLISECOND));

        //Full time with miliseconds
        forrmatedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(zone).format(zdt);
        System.out.println(forrmatedTime);
        cal = AbsoluteTimeParser.parse(forrmatedTime);
        assertEquals(2014, cal.get(Calendar.YEAR));
        assertEquals( 9, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals( 2, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(8, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals( 5, cal.get(Calendar.MINUTE));
        assertEquals(23, cal.get(Calendar.SECOND));
        assertEquals(653, cal.get(Calendar.MILLISECOND));

        //Full ISO time with miliseconds
        forrmatedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(zone).format(zdt);
        System.out.println(forrmatedTime);
        cal = AbsoluteTimeParser.parse(forrmatedTime);
        assertEquals(2014, cal.get(Calendar.YEAR));
        assertEquals( 9, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals( 2, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(8, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals( 5, cal.get(Calendar.MINUTE));
        assertEquals(23, cal.get(Calendar.SECOND));
        assertEquals(653, cal.get(Calendar.MILLISECOND));

        //Full time with seconds
        forrmatedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(zone).format(zdt);
        System.out.println(forrmatedTime);
        cal = AbsoluteTimeParser.parse(forrmatedTime);
        assertEquals(2014, cal.get(Calendar.YEAR));
        assertEquals( 9, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals( 2, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(8, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals( 5, cal.get(Calendar.MINUTE));
        assertEquals(23, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MILLISECOND));

        //Full ISO time with seconds
        forrmatedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX").withZone(zone).format(zdt);
        System.out.println(forrmatedTime);
        cal = AbsoluteTimeParser.parse(forrmatedTime);
        assertEquals(2014, cal.get(Calendar.YEAR));
        assertEquals( 9, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals( 2, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(8, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals( 5, cal.get(Calendar.MINUTE));
        assertEquals(23, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MILLISECOND));


        // Full time, with extra spaces
        cal = AbsoluteTimeParser.parse("   2007-01-18    12:10:13.123     ");
        System.out.println(cal.getTime());
        assertEquals(2007, cal.get(Calendar.YEAR));
        assertEquals( 1, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals(18, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(10, cal.get(Calendar.MINUTE));
        assertEquals(13, cal.get(Calendar.SECOND));
        assertEquals(123, cal.get(Calendar.MILLISECOND));

        // Full time, with extra spaces and zone
        cal = AbsoluteTimeParser.parse("   2007-01-18T    12:10:13.123+02    ");
        System.out.println(cal.getTime());
        assertEquals(2007, cal.get(Calendar.YEAR));
        assertEquals( 1, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals(18, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(10, cal.get(Calendar.MINUTE));
        assertEquals(13, cal.get(Calendar.SECOND));
        assertEquals(123, cal.get(Calendar.MILLISECOND));

        // Empty string should not change the passed in calendar
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(cal.getTimeInMillis());
        Calendar cal3 = AbsoluteTimeParser.parse(cal2, "     ");
        // Copy, or same instance?
        // But in any case should the values match what we had in cal.
        assertEquals(cal2, cal3);

        // Only date, no time
        cal = AbsoluteTimeParser.parse("2007-01-18");
        System.out.println(cal.getTime());
        assertEquals(2007, cal.get(Calendar.YEAR));
        assertEquals( 1, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals(18, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals( 0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals( 0, cal.get(Calendar.MINUTE));
        assertEquals( 0, cal.get(Calendar.SECOND));
        assertEquals( 0, cal.get(Calendar.MILLISECOND));



        // Only time, keep the date
        cal2 = AbsoluteTimeParser.parse(cal, "13:45");
        // Should return new instance, not change the cal we passed in.
        assertNotSame(cal, cal2);
        System.out.println(cal2.getTime());
        assertEquals(2007, cal2.get(Calendar.YEAR));
        assertEquals( 1, cal2.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals(18, cal2.get(Calendar.DAY_OF_MONTH));
        assertEquals(13, cal2.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, cal2.get(Calendar.MINUTE));
        assertEquals( 0, cal2.get(Calendar.SECOND));
        assertEquals( 0, cal.get(Calendar.MILLISECOND));

          // Only time, with millisecs
        cal2 = AbsoluteTimeParser.parse(cal, "14:00:24.156959772");
        // Should return new instance, not change the cal we passed in.
        assertNotSame(cal, cal2);
        System.out.println(cal2.getTime());
        assertEquals(2007, cal2.get(Calendar.YEAR));
        assertEquals( 1, cal2.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals(18, cal2.get(Calendar.DAY_OF_MONTH));
        assertEquals(14, cal2.get(Calendar.HOUR_OF_DAY));
        assertEquals(00, cal2.get(Calendar.MINUTE));
        assertEquals(24, cal2.get(Calendar.SECOND));
        assertEquals(156, cal2.get(Calendar.MILLISECOND));


        // Only month and day, but no year
        cal = AbsoluteTimeParser.parse(cal, "02-15 13:45");
        System.out.println(cal.getTime());
        assertEquals(2007, cal.get(Calendar.YEAR));
        assertEquals( 2, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(13, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, cal.get(Calendar.MINUTE));
        assertEquals( 0, cal.get(Calendar.SECOND));
        assertEquals( 0, cal.get(Calendar.MILLISECOND));
    }

    @Test
    public void testRelativeTimeParser() throws Exception
    {
        RelativeTimeParserResult result = RelativeTimeParser.parse("-2 Months +5 years");
        System.out.println(result);
        assertEquals(18, result.getOffsetOfNextChar());
        assertEquals(+5, result.getRelativeTime().get(RelativeTime.YEARS));
        assertEquals(-2, result.getRelativeTime().get(RelativeTime.MONTHS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.DAYS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.HOURS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.MINUTES));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.SECONDS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.MILLISECONDS));

        result = RelativeTimeParser.parse("   30 minutes -2 mon     ");
        System.out.println(result);
        assertEquals(20, result.getOffsetOfNextChar());
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.YEARS));
        assertEquals(-2, result.getRelativeTime().get(RelativeTime.MONTHS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.DAYS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.HOURS));
        assertEquals(30, result.getRelativeTime().get(RelativeTime.MINUTES));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.SECONDS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.MILLISECONDS));

        result = RelativeTimeParser.parse("+2M 3m 08:00");
        System.out.println(result);
        assertEquals( 6, result.getOffsetOfNextChar());
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.YEARS));
        assertEquals( 2, result.getRelativeTime().get(RelativeTime.MONTHS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.DAYS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.HOURS));
        assertEquals( 3, result.getRelativeTime().get(RelativeTime.MINUTES));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.SECONDS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.MILLISECONDS));

        // Parse the fractional seconds, and roll over from
        // 3600sec to 60min to 1hour
        result = RelativeTimeParser.parse("-3600.123 seconds");
        System.out.println(result);
        assertEquals( 17, result.getOffsetOfNextChar());
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.YEARS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.MONTHS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.DAYS));
        assertEquals(-1, result.getRelativeTime().get(RelativeTime.HOURS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.MINUTES));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.SECONDS));
        assertEquals(-123, result.getRelativeTime().get(RelativeTime.MILLISECONDS));

        // now
        result = RelativeTimeParser.parse("   NoW ");
        assertEquals(true, result.getRelativeTime().isNow());
    }

    @Test
    public void testTimeParser() throws Exception
    {

        final DateTimeFormatter format = TimestampFormats.SECONDS_FORMAT;
        // abs, abs
        String start = "2006-01-02 03:04:05";
        String end = "2007-05-06 07:08:09";
        StartEndTimeParser start_end = new StartEndTimeParser(start, end);
        String start_time = format.format(start_end.getStart().toInstant());
        String end_time = format.format(start_end.getEnd().getTime().toInstant());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        assertEquals(start, start_time);
        assertEquals(end, end_time);

        // rel, abs
        start = "-2 month +10 min";
        end = "2007-05-06 23:45:09";
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime().toInstant());
        end_time = format.format(start_end.getEnd().getTime().toInstant());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        assertEquals("2007-03-06 23:55:09", start_time);
        assertEquals("2007-05-06 23:45:09", end_time);

        // rel, abs with an absolute time in the relative part
        start = "-2 days 08:15";
        end = "2007-05-06 23:45:09";
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime().toInstant());
        end_time = format.format(start_end.getEnd().getTime().toInstant());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        assertEquals("2007-05-04 08:15:00", start_time);
        assertEquals("2007-05-06 23:45:09", end_time);

        // abs, rel. Also hours that roll over into next day.
        start = "2006-01-29 12:00:00";
        end = "6M 12h";
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime().toInstant());
        end_time = format.format(start_end.getEnd().getTime().toInstant());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        assertEquals("2006-01-29 12:00:00", start_time);
        assertEquals("2006-07-30 00:00:00", end_time);

        // abs, rel==now
        start = "2006-01-29 12:00:00";
        end = "now";
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime().toInstant());
        end_time = format.format(start_end.getEnd().getTime().toInstant());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        assertEquals("2006-01-29 12:00:00", start_time);
        long now = Calendar.getInstance().getTimeInMillis() / 1000;
        double end_diff_sec = now - start_end.getEnd().getTimeInMillis()/1000;
        assertEquals(0.0, end_diff_sec, 10.0);

        // rel (back), rel(back)
        start = "-6h";
        end = "-2h";
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime().toInstant());
        end_time = format.format(start_end.getEnd().getTime().toInstant());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        now = Calendar.getInstance().getTimeInMillis() / 1000;
        // Get seconds to 'now'
        double start_diff_sec = now - start_end.getStart().getTimeInMillis()/1000;
        end_diff_sec = now - start_end.getEnd().getTimeInMillis()/1000;
        assertEquals(8*60*60.0, start_diff_sec, 10.0);
        assertEquals(2*60*60.0, end_diff_sec, 10.0);

        // rel(back), rel(forward)
        start = "-60 days";
        end = "+60 days";
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime().toInstant());
        end_time = format.format(start_end.getEnd().getTime().toInstant());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        now = Calendar.getInstance().getTimeInMillis() / 1000;
        // Get seconds to 'now'
        start_diff_sec = now - start_end.getStart().getTimeInMillis()/1000;
        end_diff_sec = now - start_end.getEnd().getTimeInMillis()/1000;
        assertEquals(0.0, start_diff_sec, 10.0);
        final double error = end_diff_sec + 60.0*24.0*60*60.0;
        if (error == -60.0 * 60.0)
            System.out.println("Looks like we crossed daylight saving time");
        // An "error" of one hour is allowed in this test in case we cross daylight saving time
        assertEquals(-60.0*24.0*60*60.0, end_diff_sec, 10.0 + 60.0*60.0);

        // rel, now
        start = "-6h";
        end = "now";
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime().toInstant());
        end_time = format.format(start_end.getEnd().getTime().toInstant());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        now = Calendar.getInstance().getTimeInMillis() / 1000;
        // Get seconds to 'now'
        start_diff_sec = now - start_end.getStart().getTimeInMillis()/1000;
        end_diff_sec = now - start_end.getEnd().getTimeInMillis()/1000;
        assertEquals(6*60*60.0, start_diff_sec, 10.0);
        assertEquals(0.0, end_diff_sec, 10.0);

        // rel, now
        start = "-6h";
        end = "";
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime().toInstant());
        end_time = format.format(start_end.getEnd().getTime().toInstant());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        now = Calendar.getInstance().getTimeInMillis() / 1000;
        // Get seconds to 'now'
        start_diff_sec = now - start_end.getStart().getTimeInMillis()/1000;
        end_diff_sec = now - start_end.getEnd().getTimeInMillis()/1000;
        assertEquals(6*60*60.0, start_diff_sec, 10.0);
        assertEquals(0.0, end_diff_sec, 10.0);

        // rel - days back -, now
        start = "-30 days";
        end = "";
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime().toInstant());
        end_time = format.format(start_end.getEnd().getTime().toInstant());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        now = Calendar.getInstance().getTimeInMillis() / 1000;
        // Get seconds to 'now'
        start_diff_sec = now - start_end.getStart().getTimeInMillis()/1000;
        end_diff_sec = now - start_end.getEnd().getTimeInMillis()/1000;
        // In case that 1 month covers a daylight saving time change,
        // the difference could be 1 hour off what we think it should be
        // in this naive check.
        assertEquals(30*24*60*60.0, start_diff_sec, 1.2*60*60.0);
        assertEquals(0.0, end_diff_sec, 10.0);
    }

    @Test
    public void testPerformance() throws Exception
    {
        // rel, now
        final String start = "-6h";
        final String end = "now";
        final int N = 1000;
        BenchmarkTimer bench = new BenchmarkTimer();
        int i;
        for (i=0; i<N; ++i)
        {
            new StartEndTimeParser(start, end);
        }
        bench.stop();
        System.out.println("Parse and eval: "
                        + (double)bench.getMilliseconds()/N + " millisec/run");

        StartEndTimeParser start_end = new StartEndTimeParser(start, end);
        bench.start();
        for (i=0; i<N; ++i)
        {
            start_end.eval();
        }
        bench.stop();
        System.out.println("Only eval: "
                        + (double)bench.getMilliseconds()/N + " millisec/run");
    }
}
