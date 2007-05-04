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
        
        // now
        cal = AbsoluteTimeParser.parse("   NoW ");
        System.out.println(cal.getTime());
        long diff = cal.getTimeInMillis()
            - Calendar.getInstance().getTimeInMillis();
        assertTrue("Got 'now' within a second", diff < 1000);

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
        int pieces[] = RelativeTimeParser.parse("-2 Months +5 years");
        assertEquals(18, pieces[0]);
        assertEquals(+5, pieces[RelativeTimeParser.YEARS]);
        assertEquals(-2, pieces[RelativeTimeParser.MONTHS]);
        assertEquals( 0, pieces[RelativeTimeParser.DAYS]);
        assertEquals( 0, pieces[RelativeTimeParser.HOURS]);
        assertEquals( 0, pieces[RelativeTimeParser.MINUTES]);
        assertEquals( 0, pieces[RelativeTimeParser.SECONDS]);
        
        pieces = RelativeTimeParser.parse("   30 minutes -2 mon     ");
        assertEquals(20, pieces[0]);
        assertEquals( 0, pieces[RelativeTimeParser.YEARS]);
        assertEquals(-2, pieces[RelativeTimeParser.MONTHS]);
        assertEquals( 0, pieces[RelativeTimeParser.DAYS]);
        assertEquals( 0, pieces[RelativeTimeParser.HOURS]);
        assertEquals(30, pieces[RelativeTimeParser.MINUTES]);
        assertEquals( 0, pieces[RelativeTimeParser.SECONDS]);

        pieces = RelativeTimeParser.parse("+2M 3m 08:00");
        assertEquals( 6, pieces[0]);
        assertEquals( 0, pieces[RelativeTimeParser.YEARS]);
        assertEquals( 2, pieces[RelativeTimeParser.MONTHS]);
        assertEquals( 0, pieces[RelativeTimeParser.DAYS]);
        assertEquals( 0, pieces[RelativeTimeParser.HOURS]);
        assertEquals( 3, pieces[RelativeTimeParser.MINUTES]);
        assertEquals( 0, pieces[RelativeTimeParser.SECONDS]);
    }
    
    @Test
    public void testTimeParser() throws Exception
    {
        Calendar start_end[];
        start_end = StartEndTimeParser.parse("2006/01/02 03:04:05",
                                             "2007/05/06 07:08:09");
        String start = format.format(start_end[0].getTime());
        String end = format.format(start_end[1].getTime());
        System.out.println(start + " ... " + end);
        assertEquals("2006/01/02 03:04:05", start);
        assertEquals("2007/05/06 07:08:09", end);

        start_end = StartEndTimeParser.parse("-2 month +10 min",
                                             "2007/05/06 23:45:09");
        start = format.format(start_end[0].getTime());
        end = format.format(start_end[1].getTime());
        System.out.println(start + " ... " + end);
        assertEquals("2007/03/06 23:55:09", start);
        assertEquals("2007/05/06 23:45:09", end);
    }
}
