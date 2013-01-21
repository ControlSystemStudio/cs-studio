/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.cstudio.archive.reader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.csstudio.archive.reader.LinearValueIterator;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.VTypeHelper;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;
import org.junit.Test;

/** JUnit test of the {@link LinearValueIterator}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LinearValueIteratorUnitTest
{
    /** Create test value */
    private VType testValue(final int secs, final double value)
    {
        return new ArchiveVNumber(Timestamp.of((long)secs, 0), AlarmSeverity.NONE, "peachy", ValueFactory.displayNone(), value);
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
        assertThat(VTypeHelper.getTimestamp(value).getSec(), equalTo(10l));

        // Linr.  20, 2.0
        assertThat(linear.hasNext(), equalTo(true));
        value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.toDouble(value), equalTo(2.0));
        assertThat(VTypeHelper.getTimestamp(value).getSec(), equalTo(20l));
        
        // Last value as is
        assertThat(linear.hasNext(), equalTo(true));
        value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.toDouble(value), equalTo(2.1));
        assertThat(VTypeHelper.getTimestamp(value).getSec(), equalTo(21l));
        
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
        assertThat(VTypeHelper.getTimestamp(value).getSec(), equalTo(10l));
        
        // The next interpolation bins for 20, ..., are skipped

        // Linr.  80, 8.0
        assertThat(linear.hasNext(), equalTo(true));
        value = linear.next();
        System.out.println(value);
        assertThat(VTypeHelper.toDouble(value), equalTo(8.0));
        assertThat(VTypeHelper.getTimestamp(value).getSec(), equalTo(80l));

        // Dump the rest
        while (linear.hasNext())
            System.out.println(linear.next());
        
        linear.close();
    }
}
