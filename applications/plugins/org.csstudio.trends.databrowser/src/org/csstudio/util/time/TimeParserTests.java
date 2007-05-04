package org.csstudio.util.time;

import java.util.Calendar;

import junit.framework.TestCase;

import org.junit.Test;

@SuppressWarnings("nls")
public class TimeParserTests extends TestCase
{
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
        // Unclear if cal2 == cal3, i.e. do we get the exact instance back?
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
        cal = AbsoluteTimeParser.parse(cal, "13:45");
        System.out.println(cal.getTime());
        assertEquals(2007, cal.get(Calendar.YEAR));
        assertEquals(1, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals(18, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(13, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));

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
        int pieces[] = RelativeTimeParser.parse("-2 Months");
        assertEquals(-2, pieces[1]);
        pieces = RelativeTimeParser.parse("-2 mon");
        assertEquals(-2, pieces[1]);
        pieces = RelativeTimeParser.parse("-2M");
        assertEquals(-2, pieces[1]);
    }
}
