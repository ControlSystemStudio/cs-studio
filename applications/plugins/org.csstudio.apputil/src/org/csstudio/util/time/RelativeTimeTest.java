package org.csstudio.util.time;

import static org.junit.Assert.*;

import org.junit.Test;

public class RelativeTimeTest
{
    @Test
    public void testRelativeTimeDoubleArray()
    {
        // Zero is zero
        double ymdhms[] = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
        RelativeTime rel = new RelativeTime(ymdhms);
        assertEquals(0, rel.get(RelativeTime.YEARS));
        assertEquals(0, rel.get(RelativeTime.MONTHS));
        assertEquals(0, rel.get(RelativeTime.DAYS));
        assertEquals(0, rel.get(RelativeTime.HOURS));
        assertEquals(0, rel.get(RelativeTime.MINUTES));
        assertEquals(0, rel.get(RelativeTime.SECONDS));

        // Some plus, some minus
        ymdhms = new double[] { 1.0, -2.0, 3.0, -4.0, 5.0, -6.0 };
        rel = new RelativeTime(ymdhms);
        assertEquals(1, rel.get(RelativeTime.YEARS));
        assertEquals(-2, rel.get(RelativeTime.MONTHS));
        assertEquals(3, rel.get(RelativeTime.DAYS));
        assertEquals(-4, rel.get(RelativeTime.HOURS));
        assertEquals(5, rel.get(RelativeTime.MINUTES));
        assertEquals(-6, rel.get(RelativeTime.SECONDS));

        // Fractional days -> hours
        ymdhms = new double[] { 0.0, 0.0, 10.5, 0.0, 0.0, 0.0 };
        rel = new RelativeTime(ymdhms);
        assertEquals(0, rel.get(RelativeTime.YEARS));
        assertEquals(0, rel.get(RelativeTime.MONTHS));
        assertEquals(10, rel.get(RelativeTime.DAYS));
        assertEquals(12, rel.get(RelativeTime.HOURS));
        assertEquals(0, rel.get(RelativeTime.MINUTES));
        assertEquals(0, rel.get(RelativeTime.SECONDS));
        assertEquals(0, rel.get(RelativeTime.MILLISECONDS));

        // Negative fractional days -> hours
        ymdhms = new double[] { 0.0, 0.0, -10.5, 0.0, 0.0, 0.0 };
        rel = new RelativeTime(ymdhms);
        assertEquals(0, rel.get(RelativeTime.YEARS));
        assertEquals(0, rel.get(RelativeTime.MONTHS));
        assertEquals(-10, rel.get(RelativeTime.DAYS));
        assertEquals(-12, rel.get(RelativeTime.HOURS));
        assertEquals(0, rel.get(RelativeTime.MINUTES));
        assertEquals(0, rel.get(RelativeTime.SECONDS));
        assertEquals(0, rel.get(RelativeTime.MILLISECONDS));
    
        // Negative fractional days & hours  ->  hours & minutes
        ymdhms = new double[] { 0.0, 0.0, -10.5, -1.5, 0.0, 0.0 };
        rel = new RelativeTime(ymdhms);
        assertEquals(0, rel.get(RelativeTime.YEARS));
        assertEquals(0, rel.get(RelativeTime.MONTHS));
        assertEquals(-10, rel.get(RelativeTime.DAYS));
        assertEquals(-13, rel.get(RelativeTime.HOURS));
        assertEquals(-30, rel.get(RelativeTime.MINUTES));
        assertEquals(0, rel.get(RelativeTime.SECONDS));
        assertEquals(0, rel.get(RelativeTime.MILLISECONDS));

        // Fractional seconds  ->  millis
        ymdhms = new double[] { 0.0, 0.0, -10.5, -1.5, 0.0, 23.321 };
        rel = new RelativeTime(ymdhms);
        assertEquals(0, rel.get(RelativeTime.YEARS));
        assertEquals(0, rel.get(RelativeTime.MONTHS));
        assertEquals(-10, rel.get(RelativeTime.DAYS));
        assertEquals(-13, rel.get(RelativeTime.HOURS));
        assertEquals(-30, rel.get(RelativeTime.MINUTES));
        assertEquals(23, rel.get(RelativeTime.SECONDS));
        assertEquals(321, rel.get(RelativeTime.MILLISECONDS));

    }
}
