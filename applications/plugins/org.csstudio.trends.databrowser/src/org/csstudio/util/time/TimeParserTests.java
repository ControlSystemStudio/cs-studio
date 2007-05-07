package org.csstudio.util.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import junit.framework.TestCase;

import org.junit.Test;

@SuppressWarnings("nls")
public class TimeParserTests extends TestCase
{
    private static final DateFormat format = 
                      new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    
    @Test
    public void testAbsoluteTimeParser() throws Exception
    {
        Calendar cal;
        
        // Full time, with extra spaces
        cal = AbsoluteTimeParser.parse("   2007/01/18    12:10:13.123     ");
        System.out.println(cal.getTime());
        assertEquals(2007, cal.get(Calendar.YEAR));
        assertEquals(1, cal.get(Calendar.MONTH) + 1); // Jan == 0
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
        assertEquals(cal, cal3);
        
        // Only date, no time
        cal = AbsoluteTimeParser.parse("2007/01/18");
        System.out.println(cal.getTime());
        assertEquals(2007, cal.get(Calendar.YEAR));
        assertEquals(1, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals(18, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));

        // Only time, keep the date
        cal2 = AbsoluteTimeParser.parse(cal, "13:45");
        // Should return new instance, not change the cal we passed in.
        assertNotSame(cal, cal2);
        System.out.println(cal2.getTime());
        assertEquals(2007, cal2.get(Calendar.YEAR));
        assertEquals(1, cal2.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals(18, cal2.get(Calendar.DAY_OF_MONTH));
        assertEquals(13, cal2.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, cal2.get(Calendar.MINUTE));
        assertEquals(0, cal2.get(Calendar.SECOND));

        // Only month and day, but no year
        cal = AbsoluteTimeParser.parse(cal, "02/15 13:45");
        System.out.println(cal.getTime());
        assertEquals(2007, cal.get(Calendar.YEAR));
        assertEquals(2, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(13, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));
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
        
        result = RelativeTimeParser.parse("   30 minutes -2 mon     ");
        System.out.println(result);
        assertEquals(20, result.getOffsetOfNextChar());
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.YEARS));
        assertEquals(-2, result.getRelativeTime().get(RelativeTime.MONTHS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.DAYS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.HOURS));
        assertEquals(30, result.getRelativeTime().get(RelativeTime.MINUTES));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.SECONDS));

        result = RelativeTimeParser.parse("+2M 3m 08:00");
        System.out.println(result);
        assertEquals( 6, result.getOffsetOfNextChar());
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.YEARS));
        assertEquals( 2, result.getRelativeTime().get(RelativeTime.MONTHS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.DAYS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.HOURS));
        assertEquals( 3, result.getRelativeTime().get(RelativeTime.MINUTES));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.SECONDS));
        
        // now
        result = RelativeTimeParser.parse("   NoW ");
        assertEquals(true, result.getRelativeTime().isNow());
    }
    
    @Test
    public void testTimeParser() throws Exception
    {
        // abs, abs
        String start = "2006/01/02 03:04:05";
        String end = "2007/05/06 07:08:09";
        StartEndTimeParser start_end = new StartEndTimeParser(start, end);
        String start_time = format.format(start_end.getStart().getTime());
        String end_time = format.format(start_end.getEnd().getTime());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        assertEquals(start, start_time);
        assertEquals(end, end_time);

        // rel, abs
        start = "-2 month +10 min";
        end = "2007/05/06 23:45:09";
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime());
        end_time = format.format(start_end.getEnd().getTime());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        assertEquals("2007/03/06 23:55:09", start_time);
        assertEquals("2007/05/06 23:45:09", end_time);

        // rel, abs with an absolute time in the relative part
        start = "-2 days 08:15";
        end = "2007/05/06 23:45:09";
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime());
        end_time = format.format(start_end.getEnd().getTime());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        assertEquals("2007/05/04 08:15:00", start_time);
        assertEquals("2007/05/06 23:45:09", end_time);
        
        // abs, rel. Also hours that roll over into next day.
        start = "2006/01/29 12:00:00";
        end = "6M 12h";
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime());
        end_time = format.format(start_end.getEnd().getTime());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        assertEquals("2006/01/29 12:00:00", start_time);
        assertEquals("2006/07/30 00:00:00", end_time);

        // rel, rel
        start = "-6h";
        end = "-2h";
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime());
        end_time = format.format(start_end.getEnd().getTime());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        long now = Calendar.getInstance().getTimeInMillis() / 1000;
        // Get seconds to 'now'
        double start_diff_sec = now - start_end.getStart().getTimeInMillis()/1000;
        double end_diff_sec = now - start_end.getEnd().getTimeInMillis()/1000;
        assertEquals(8*60*60.0, start_diff_sec, 10.0);
        assertEquals(2*60*60.0, end_diff_sec, 10.0);

        // rel, now
        start = "-6h";
        end = "now";
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime());
        end_time = format.format(start_end.getEnd().getTime());
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
        start_time = format.format(start_end.getStart().getTime());
        end_time = format.format(start_end.getEnd().getTime());
        System.out.println("   " + start + " ... " + end + "\n-> " +
                           start_time + " ... " + end_time);
        now = Calendar.getInstance().getTimeInMillis() / 1000;
        // Get seconds to 'now'
        start_diff_sec = now - start_end.getStart().getTimeInMillis()/1000;
        end_diff_sec = now - start_end.getEnd().getTimeInMillis()/1000;
        assertEquals(6*60*60.0, start_diff_sec, 10.0);
        assertEquals(0.0, end_diff_sec, 10.0);
    }
}
