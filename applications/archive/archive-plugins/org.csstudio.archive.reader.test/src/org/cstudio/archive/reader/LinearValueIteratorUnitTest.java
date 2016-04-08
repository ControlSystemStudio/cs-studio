/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.cstudio.archive.reader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.time.Duration;
import java.time.Instant;

import org.csstudio.archive.reader.LinearValueIterator;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVString;
import org.csstudio.archive.vtype.VTypeHelper;
import org.diirt.util.time.TimeDuration;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VString;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.junit.Test;

/** JUnit test of the {@link LinearValueIterator}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LinearValueIteratorUnitTest
{
    /** Create test value */
    private VType testValue(final long secs, final double value, final AlarmSeverity severity, final String message)
    {
        return new ArchiveVNumber(Instant.ofEpochSecond(secs, 0), severity, message, ValueFactory.displayNone(), value);
    }

    /** Create test value */
    private VType testValue(final long secs, final double value)
    {
        if (Double.isNaN(value)  ||  Double.isInfinite(value))
            return testValue(secs, value, AlarmSeverity.INVALID, "NaN");
        else
            return testValue(secs, value, AlarmSeverity.NONE, "peachy");
    }

    /** Have some samples, ask for linear values in between */
    @Test
    public void testNiceCase() throws Exception
    {
        final VType[] data = new VType[]
        {
            testValue( 5, 0.5),
            // Linr.  10, 1.0
            testValue(15, 1.5),
            testValue(16, 1.6),
            testValue(17, 1.7),
            testValue(19, 1.9),
            // Linr.  20, 2.0
            testValue(21, 2.1),
            // Last value as is
        };
        final ValueIterator raw = new DemoDataIterator(data);

        final ValueIterator linear = new LinearValueIterator(raw, TimeDuration.ofSeconds(10));

        // Linr.  10, 1.0
        assertThat(linear.hasNext(), equalTo(true));
        VType value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.toDouble(value), equalTo(1.0));
        assertThat(VTypeHelper.getTimestamp(value).getEpochSecond(), equalTo(10l));

        // Linr.  20, 2.0
        assertThat(linear.hasNext(), equalTo(true));
        value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.toDouble(value), equalTo(2.0));
        assertThat(VTypeHelper.getTimestamp(value).getEpochSecond(), equalTo(20l));

        // Last value as is
        assertThat(linear.hasNext(), equalTo(true));
        value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.toDouble(value), equalTo(2.1));
        assertThat(VTypeHelper.getTimestamp(value).getEpochSecond(), equalTo(21l));

        // No more
        assertThat(linear.hasNext(), equalTo(false));

        linear.close();
    }

    /** Have nothing, get nothing */
    @Test
    public void testNothing() throws Exception
    {
        final ValueIterator raw = new DemoDataIterator(new VType[0]);
        final ValueIterator linear = new LinearValueIterator(raw, TimeDuration.ofSeconds(10));

        assertThat(linear.hasNext(), equalTo(false));

        linear.close();
    }

    /** Have gap, interpolation jumps over it */
    @Test
    public void testGap() throws Exception
    {
        final VType[] data = new VType[]
        {
            testValue( 5, 0.5),
            // Linr.  10, 1.0

            // This is a rather long interpolation onto the next
            // sample at 79.
            // The next interpolation bins for 20, ..., are skipped
            testValue(79, 7.9),
            // Linr.  80, 8.0
            testValue(81, 8.1),
        };
        final ValueIterator raw = new DemoDataIterator(data);

        final ValueIterator linear = new LinearValueIterator(raw, TimeDuration.ofSeconds(10));

        // Linr.  10, 1.0
        assertThat(linear.hasNext(), equalTo(true));
        VType value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.toDouble(value), equalTo(1.0));
        assertThat(VTypeHelper.getTimestamp(value).getEpochSecond(), equalTo(10l));

        // The next interpolation bins for 20, ..., are skipped

        // Linr.  80, 8.0
        assertThat(linear.hasNext(), equalTo(true));
        value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.toDouble(value), equalTo(8.0));
        assertThat(VTypeHelper.getTimestamp(value).getEpochSecond(), equalTo(80l));

        // Dump the rest
        while (linear.hasNext())
            System.out.println(linear.next());

        linear.close();
    }

    /** Invalid/NaN/.. which cannot be interpolated */
    @Test
    public void testInvalids() throws Exception
    {
        final VType[] data = new VType[]
        {
            testValue( 5, 0.5),
            // Linr.  10, 1.0
            testValue(15, 1.5),
            // Bad number, ..
            testValue(16, Double.NaN),
            // but then a good number just
            // before the next interpolation time stamp
            testValue(18, 1.8),
            // Linr.  20, 2.0, but the alarm indicates NaN
            testValue(25, 2.5),

            // Bad number, ..
            testValue(27, Double.NaN),
            // Would be 30, 3.0, but there was a NaN
            testValue(35, 3.5),
        };
        final ValueIterator raw = new DemoDataIterator(data);

        final ValueIterator linear = new LinearValueIterator(raw, TimeDuration.ofSeconds(10));

        // Linr.  10, 1.0
        assertThat(linear.hasNext(), equalTo(true));
        VType value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.toDouble(value), equalTo(1.0));
        assertThat(VTypeHelper.getTimestamp(value).getEpochSecond(), equalTo(10l));
        assertThat(VTypeHelper.getSeverity(value), equalTo(AlarmSeverity.NONE));

        // Linr.  20, 2.0, with alarm
        assertThat(linear.hasNext(), equalTo(true));
        value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.toDouble(value), equalTo(2.0));
        assertThat(VTypeHelper.getTimestamp(value).getEpochSecond(), equalTo(20l));
        assertThat(VTypeHelper.getSeverity(value), equalTo(AlarmSeverity.INVALID));
        assertThat(VTypeHelper.getMessage(value), equalTo("NaN"));

        // Would be 30, 3.0, but there was a NaN
        assertThat(linear.hasNext(), equalTo(true));
        value = linear.next();
        System.out.println(value);
        assertThat(Double.isNaN(VTypeHelper.toDouble(value)), equalTo(true));
        assertThat(VTypeHelper.getTimestamp(value).getEpochSecond(), equalTo(30l));
        assertThat(VTypeHelper.getSeverity(value), equalTo(AlarmSeverity.INVALID));
        assertThat(VTypeHelper.getMessage(value), equalTo("NaN"));

        // End of interpolation, final value as-is
        assertThat(linear.hasNext(), equalTo(true));
        value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.toDouble(value), equalTo(3.5));
        assertThat(VTypeHelper.getTimestamp(value).getEpochSecond(), equalTo(35l));

        assertThat(linear.hasNext(), equalTo(false));

        linear.close();
    }

    /** Have 'archive off' interruption, interpolation jumps over it */
    @Test
    public void testInterruption() throws Exception
    {
        final VType[] data = new VType[]
        {
            testValue( 5, 0.5),
            // UNDEFINED values within interpolation interval
            testValue( 6, 99.5, AlarmSeverity.UNDEFINED, "Disconnected"),
            // but returns to normal, so ignored
            testValue( 7, 0.7),
            // Linr.  10, 1.0
            testValue(15, 1.5),

            testValue(18, 1.5, AlarmSeverity.UNDEFINED, "Write_Error"),
            new ArchiveVString(Instant.ofEpochSecond(17L, 0), AlarmSeverity.UNDEFINED, "Archive_Off", "Turned off"),
            // Reported with time stamp 20, UNDEFINED, Archive_Off

            // .. nothing for a long time, then archive back on
            // Linr. 100, 10.0
            testValue(95, 9.5),
            testValue(105, 10.5),
        };
        final ValueIterator raw = new DemoDataIterator(data);

        final ValueIterator linear = new LinearValueIterator(raw, TimeDuration.ofSeconds(10));

        // Linr.  10, 1.0
        assertThat(linear.hasNext(), equalTo(true));
        VType value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.toDouble(value), equalTo(1.0));
        assertThat(VTypeHelper.getTimestamp(value).getEpochSecond(), equalTo(10l));

        // Want to see the last UNDEFINED value in interval
        assertThat(linear.hasNext(), equalTo(true));
        value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.getTimestamp(value).getEpochSecond(), equalTo(20l));
        assertThat(VTypeHelper.getSeverity(value), equalTo(AlarmSeverity.UNDEFINED));
        assertThat(VTypeHelper.getMessage(value), equalTo("Archive_Off"));
        assertThat(value, instanceOf(VString.class));

        // Resume interpolation after gap, Linr. 100, 10.0
        assertThat(linear.hasNext(), equalTo(true));
        value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.toDouble(value), equalTo(10.0));
        assertThat(VTypeHelper.getTimestamp(value).getEpochSecond(), equalTo(100l));

        // Dump the rest
        while (linear.hasNext())
            System.out.println(linear.next());

        linear.close();
    }

    /** Sparse input data */
    @Test
    public void testSparse() throws Exception
    {
        final long ten_min = Duration.ofMinutes(10).getSeconds();
        final VType[] data = new VType[]
        {
            testValue( 0*ten_min + 5, 0.0),
            // Interp at 10 minutes
            testValue( 1*ten_min + 5, 1.0),
            // Interp at 20 min
            testValue(10*ten_min + 5, 2.0),
            // Jump to 11 * 10min
            testValue(20*ten_min + 5, 2.0),
        };
        final ValueIterator raw = new DemoDataIterator(data);

        final ValueIterator linear = new LinearValueIterator(raw, TimeDuration.ofMinutes(10));

        // Interp at 10 minutes
        assertThat(linear.hasNext(), equalTo(true));
        VType value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.getTimestamp(value).getEpochSecond(), equalTo(ten_min));

        // Interp at 20 min
        assertThat(linear.hasNext(), equalTo(true));
        value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.getTimestamp(value).getEpochSecond(), equalTo(2*ten_min));

        // Jump to 11 * 10min
        assertThat(linear.hasNext(), equalTo(true));
        value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.getTimestamp(value).getEpochSecond(), equalTo(11*ten_min));

        // Dump the rest
        while (linear.hasNext())
            System.out.println(linear.next());

        linear.close();
    }

}
