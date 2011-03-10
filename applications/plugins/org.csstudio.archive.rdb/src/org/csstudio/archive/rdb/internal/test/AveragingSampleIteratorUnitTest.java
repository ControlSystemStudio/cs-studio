/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.internal.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.csstudio.archive.rdb.AveragingSampleIterator;
import org.csstudio.archive.rdb.SampleIterator;
import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.data.values.ValueUtil;
import org.junit.Test;

/** Test of the AveragingSampleIterator.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AveragingSampleIteratorUnitTest
{
    /** SampleIterator that provides a sine-wave. */
    static class SinewaveSamples implements SampleIterator
    {
        /** Total sequence length in seconds */
        final public static int LENGTH = 100;

        /** Period of one sine in seconds */
        final public static double PERIOD = 10;

        final private static String DISCONNECTED = "Disconnected";

        final private ArrayList<IValue> samples = new ArrayList<IValue>();

        /** Index of 'current' sample for <code>next()</code> */
        private int index = 0;

        public SinewaveSamples(final boolean with_disconnects)
        {
            final INumericMetaData meta =
                ValueFactory.createNumericMetaData(0.0, 10.0, 2.0, 8.0,
                                                   1.0, 10.0, 2, "a.u.");
            for (int i=0; i<LENGTH; ++i)
            {
                final ITimestamp time = TimestampFactory.createTimestamp(i, 0);
                final double value = Math.sin(2.0*Math.PI*i/PERIOD);
                ISeverity severity;
                if (value > 0.9)
                    severity = ValueFactory.createMajorSeverity();
                else if (value > 0.5)
                    severity = ValueFactory.createMinorSeverity();
                else
                    severity = ValueFactory.createOKSeverity();
                samples.add(ValueFactory.createDoubleValue(time, severity, "OK", meta,
                                IValue.Quality.Original,
                                new double[] { value }));
                if (with_disconnects  &&  Math.abs(value) < 0.01)
                    samples.add(ValueFactory.createStringValue(time,
                            ValueFactory.createInvalidSeverity(),
                            DISCONNECTED,
                            IValue.Quality.Original,
                            new String[] { DISCONNECTED }));
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean hasNext()
        {
            return index < samples.size();
        }

        /** {@inheritDoc} */
        @Override
        public IValue next() throws Exception
        {
            return samples.get(index++);
        }
    }

    /** SampleIterator that provides text and ennum. */
    static class NonNumericSamples implements SampleIterator
    {
        final private ArrayList<IValue> samples = new ArrayList<IValue>();

        /** Index of 'current' sample for <code>next()</code> */
        private int index = 0;

        public NonNumericSamples()
        {
            final IEnumeratedMetaData meta =
                ValueFactory.createEnumeratedMetaData(new String []
            {
                        "One", "Two", "Three"
            });

            samples.add(ValueFactory.createStringValue(
                    TimestampFactory.createTimestamp(0, 0),
                    ValueFactory.createOKSeverity(),
                    "OK",
                    IValue.Quality.Original,
                    new String[] { "Hello" }));
            samples.add(ValueFactory.createStringValue(
                    TimestampFactory.createTimestamp(1, 0),
                    ValueFactory.createOKSeverity(),
                    "OK",
                    IValue.Quality.Original,
                    new String[] { "Dolly" }));
            samples.add(ValueFactory.createEnumeratedValue(
                    TimestampFactory.createTimestamp(2, 0),
                    ValueFactory.createOKSeverity(),
                    "OK", meta,
                    IValue.Quality.Original,
                    new int[] { 0 }));
            samples.add(ValueFactory.createEnumeratedValue(
                    TimestampFactory.createTimestamp(3, 0),
                    ValueFactory.createOKSeverity(),
                    "OK", meta,
                    IValue.Quality.Original,
                    new int[] { 2 }));
        }

        /** {@inheritDoc} */
        @Override
        public boolean hasNext()
        {
            return index < samples.size();
        }

        /** {@inheritDoc} */
        @Override
        public IValue next() throws Exception
        {
            return samples.get(index++);
        }
    }

    /** Basic Average over the 10 second periods, should give ~0,
     *  and the maximized severity is MAJOR.
     */
    @Test
    public void testFullCycleAveraging() throws Exception
    {
        final SampleIterator base = new SinewaveSamples(false);
        final AveragingSampleIterator avg =
            new AveragingSampleIterator(base, 10.0);
        int count = 0;
        while (avg.hasNext())
        {
            final IValue next = avg.next();
            System.out.println("----> " + next.toString());
            assertEquals(0.0, ValueUtil.getDouble(next), 0.01);
            assertTrue(next.getSeverity().isMajor());
            assertEquals(Quality.Interpolated, next.getQuality());
            ++count;
        }
        System.out.println("Samples: " + count);
        assertEquals(SinewaveSamples.LENGTH / SinewaveSamples.PERIOD, count, 0.1);
    }

    /** Basic Average over the 5 second half-wave, should give positive and
     *  negative cycle averages around +-0.62,
     *  and the maximized severity is MAJOR resp. OK.
     */
    @Test
    public void testHalfCycleAveraging() throws Exception
    {
        final SampleIterator base = new SinewaveSamples(false);
        final AveragingSampleIterator avg =
            new AveragingSampleIterator(base, 5.0);
        int count = 0;
        boolean positive_cycle = true;
        while (avg.hasNext())
        {
            final IValue next = avg.next();
            System.out.println("----> " + next.toString());
            if (positive_cycle)
            {
                assertEquals(+0.62, ValueUtil.getDouble(next), 0.01);
                assertTrue(next.getSeverity().isMajor());
            }
            else
            {
                assertEquals(-0.62, ValueUtil.getDouble(next), 0.01);
                assertTrue(next.getSeverity().isOK());
            }
            assertEquals(Quality.Interpolated, next.getQuality());
            positive_cycle = ! positive_cycle;
            ++count;
        }
        System.out.println("Samples: " + count);
        assertEquals(SinewaveSamples.LENGTH / SinewaveSamples.PERIOD * 2, count, 0.1);
    }

    /** Average over 1 second, i.e. there's only one sample per period.
     *  Gives the original data.
     */
    @Test
    public void testTooFineAveraging() throws Exception
    {
        final SampleIterator base = new SinewaveSamples(false);
        final AveragingSampleIterator avg =
            new AveragingSampleIterator(base, 1.0);
        int count = 0;
        while (avg.hasNext())
        {
            final IValue next = avg.next();
            System.out.println("----> " + next.toString());
            assertEquals(Quality.Original, next.getQuality());
            ++count;
        }
        System.out.println("Samples: " + count);
        assertEquals(SinewaveSamples.LENGTH, count);
    }

    /** ... with some 'disconnected' samples */
    @Test
    public void testFullCycleAveragingWithDisconnects() throws Exception
    {
        final SampleIterator base = new SinewaveSamples(true);
        final AveragingSampleIterator avg =
            new AveragingSampleIterator(base, 10.0);
        int count = 0;
        while (avg.hasNext())
        {
            final IValue next = avg.next();
            System.out.println("----> " + next.toString());
            //assertEquals(0.0, ValueUtil.getDouble(next), 0.01);
            //assertTrue(next.getSeverity().isMajor());
            ++count;
        }
        // 10 sine waves.
        // For each we get 0, invalid, high avg, invalid, low avg
        System.out.println("Samples: " + count);
        assertEquals(5 * SinewaveSamples.LENGTH / SinewaveSamples.PERIOD, count, 0.1);
    }

    /** ... with some 'disconnected' samples */
    @Test
    public void testNonNumericSamples() throws Exception
    {
        final SampleIterator base = new NonNumericSamples();
        final AveragingSampleIterator avg =
            new AveragingSampleIterator(base, 10.0);
        int count = 0;
        while (avg.hasNext())
        {
            final IValue next = avg.next();
            System.out.println("----> " + next.toString());
            assertEquals(Quality.Original, next.getQuality());
            ++count;
        }
        System.out.println("Samples: " + count);
        assertEquals(4, count);
    }
}
