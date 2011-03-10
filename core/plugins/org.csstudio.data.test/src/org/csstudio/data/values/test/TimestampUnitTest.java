/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values.test;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.internal.Timestamp;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests of the {@link Timestamp} class.
 *  @author Kay Kasemir
 */
public final class TimestampUnitTest
{
    /** Basic timestamp construction. */
    @SuppressWarnings("nls")
    @Test
    public void createStamps()
    {
        ITimestamp stamp;

        // long, long
        stamp = new Timestamp(0L, 0L);
        assertEquals(0L, stamp.seconds());
        assertEquals(0L, stamp.nanoseconds());
        assertEquals(false, stamp.isValid());

        // double
        stamp = new Timestamp(0.0);
        assertEquals(0L, stamp.seconds());
        assertEquals(0L, stamp.nanoseconds());
        assertEquals(false, stamp.isValid());

        // nanos roll over
        stamp = new Timestamp(1L, 2500000000L);
        assertEquals(3L, stamp.seconds());
        assertEquals(500000000L, stamp.nanoseconds());
        assertEquals(true, stamp.isValid());

        // To string
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2007, 1, 18);
        stamp = new Timestamp(cal.getTimeInMillis()/1000L, 0L);
        // Note: Cannot check seconds, since those would be based on UTC,
        //       while the calendar is in 'local' time.
        // But the end result, the local time string, should
        // match what we put in.
        System.out.println(stamp.toString());
        assertEquals("2007/02/18 00:00:00.000000000", stamp.toString());

        // Does factory give the same result?
        ITimestamp stamp2 = TimestampFactory.fromCalendar(cal);
        assertEquals(stamp, stamp2);

        // Down to seconds
        cal.set(2007, 1, 18, 13, 45, 59);
        stamp = new Timestamp(cal.getTimeInMillis()/1000L, 0L);
        System.out.println(stamp.toString());
        assertEquals("2007/02/18 13:45:59.000000000", stamp.toString());

        stamp2 = TimestampFactory.fromCalendar(cal);
        assertEquals(stamp, stamp2);

        // Down to nanoseconds
        stamp = new Timestamp(cal.getTimeInMillis()/1000L, 123456789L);
        System.out.println(stamp.toString());
        assertEquals("2007/02/18 13:45:59.123456789", stamp.toString());
    }

    /** Comparisons */
    @Test
    public void compareStamps()
    {
        // Basic conversions from/to pieces and strings
        Calendar cal = Calendar.getInstance();
        cal.set(1990, 1, 18, 13, 30, 20);
        ITimestamp a = TimestampFactory.fromCalendar(cal);

        cal.set(1990, 1, 18, 13, 30, 20);
        ITimestamp b = TimestampFactory.fromCalendar(cal);

        ITimestamp c = TimestampFactory.now();

        assertTrue(a.equals(a));
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));

        assertTrue(a.isGreaterOrEqual(a));
        assertTrue(a.isGreaterOrEqual(b));
        assertTrue(b.isGreaterOrEqual(a));

        assertTrue(c.isGreaterOrEqual(a));
        assertTrue(c.isGreaterOrEqual(b));
        assertTrue(c.isGreaterOrEqual(c));

        assertTrue(c.isGreaterThan(a));
        assertTrue(c.isGreaterThan(b));
        assertFalse(c.isGreaterThan(c));

        assertFalse(a.equals(c));
        assertFalse(b.equals(c));
        assertFalse(c.equals(a));
    }

}
