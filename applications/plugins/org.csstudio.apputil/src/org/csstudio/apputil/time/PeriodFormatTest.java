package org.csstudio.apputil.time;

import static org.junit.Assert.*;

import org.junit.Test;

/** Unit test of the <code>PeriodParser</code>
 *  @author Kay Kasemir
 */
public class PeriodFormatTest
{
    @SuppressWarnings("nls")
    @Test
    public void testGetSeconds()
    {
        assertEquals(3.14, PeriodFormat.parseSeconds("3.14"), 0.01);
        assertEquals(3.14, PeriodFormat.parseSeconds("3.14 seconds"), 0.01);
        assertEquals(3.14, PeriodFormat.parseSeconds("3.14 sec"), 0.01);

        assertEquals(30.0*60.0, PeriodFormat.parseSeconds("30 min"), 0.01);

        assertEquals(2.5*60.0*60.0, PeriodFormat.parseSeconds("2.5 hours"), 0.01);

        assertEquals(2.5*24.0*60.0*60.0, PeriodFormat.parseSeconds("2.5 days"), 0.01);
    }

    @SuppressWarnings("nls")
    @Test
    public void testFormatSeconds()
    {
        assertEquals("3.14 sec", PeriodFormat.formatSeconds(3.14));
        assertEquals("59.00 sec", PeriodFormat.formatSeconds(59));

        assertEquals("1.00 min", PeriodFormat.formatSeconds(60));
        assertEquals("1.50 min", PeriodFormat.formatSeconds(90));

        assertEquals("1.00 h", PeriodFormat.formatSeconds(60*60));
        assertEquals("1.50 h", PeriodFormat.formatSeconds(90*60));

        assertEquals("1.50 days", PeriodFormat.formatSeconds(90*24*60));
    }
}
