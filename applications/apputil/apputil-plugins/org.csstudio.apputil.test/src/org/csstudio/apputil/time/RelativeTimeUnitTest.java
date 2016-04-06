/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.time;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit test of RelativeTime
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RelativeTimeUnitTest
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

        rel = new RelativeTime(3.14);
        assertEquals("3.14 seconds", rel.toString());

        rel = new RelativeTime(0.001);
        assertEquals("0.001 seconds", rel.toString());
    }
}
