package org.csstudio.archive.util;

import java.util.Calendar;

import junit.framework.TestCase;

import org.csstudio.platform.util.ITimestamp;
import org.junit.Test;

/** Test for some of the TimestampUtil methods.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimestampUtilTest extends TestCase
{
    @Test
    public void testConversions()
    {
        Calendar cal = Calendar.getInstance();
        // Note that cal's month is 0-based
        cal.set(2007, 2-1, 3, 13, 14, 15);
        ITimestamp stamp = TimestampUtil.fromCalendar(cal);
        assertEquals("2007/02/03 13:14:15",
                     stamp.format(ITimestamp.FMT_DATE_HH_MM_SS));
    }
    
    @Test
    public void testParseSeconds() throws Exception
    {
        assertEquals(42.0, TimestampUtil.parseSeconds("42"), 0.01);
        assertEquals(10.0, TimestampUtil.parseSeconds(" 10  "), 0.01);
        assertEquals(3600.2, TimestampUtil.parseSeconds("  3600.2 "), 0.01);

        assertEquals(60.0, TimestampUtil.parseSeconds("1:0"), 0.01);
        assertEquals(120.0, TimestampUtil.parseSeconds(" 02:000"), 0.01);
        assertEquals(90.0, TimestampUtil.parseSeconds("   1:30"), 0.01);

        assertEquals(60.0*60.0, TimestampUtil.parseSeconds(" 1: 0:0"), 0.01);
        assertEquals(60.0*60.0 + 30*60 + 20, TimestampUtil.parseSeconds("1:30:20"), 0.01);
        assertEquals(24*60.0*60.0, TimestampUtil.parseSeconds("24:0:  0"), 0.01);
    }

    @Test
    public void testParseOddballs() throws Exception
    {
        assertEquals(-42.0, TimestampUtil.parseSeconds("-42"), 0.01);

        assertEquals(-90.0, TimestampUtil.parseSeconds("-1:30"), 0.01);

        assertEquals(-24*60.0*60.0, TimestampUtil.parseSeconds("-24:0:0"), 0.01);

        boolean caught = false;
        try
        {
            TimestampUtil.parseSeconds("    ");
        }
        catch (Exception e)
        {
            caught = true;
        }
        assertTrue(caught);

        caught = false;
        try
        {
            TimestampUtil.parseSeconds("  1:2:3:4  ");
        }
        catch (Exception e)
        {
            caught = true;
        }
        assertTrue(caught);
    }
}
