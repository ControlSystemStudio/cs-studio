/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.data.values.TimestampFactory;
import org.junit.Before;
import org.junit.Test;

/** JUnit test of PlotSampleSearch
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PlotSampleSearchUnitTest
{
    private PlotSample samples[];

    /** Create samples with 'time stamp' 2, 4, 6, ..., 18
     *  and 'values' 1, 2, 3, ..., 9
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        samples = new PlotSample[9];
        for (int i=1; i<10; ++i) {
            samples[i-1] = new PlotSample(2.0*i, i); // y = 1, 2, 3, ..., 9
        }
    }

    @Test
    public void testEmpty() throws Exception
    {
        // Empty container
        samples = new PlotSample[0];

        // Should get -1 (nothing found) for empty container
        int i = PlotSampleSearch.findClosestSample(samples, TimestampFactory.fromDouble(10.0));
        assertEquals(-1, i);
        i = PlotSampleSearch.findSampleGreaterOrEqual(samples, TimestampFactory.fromDouble(10.0));
        assertEquals(-1, i);
        i = PlotSampleSearch.findSampleLessOrEqual(samples, TimestampFactory.fromDouble(10.0));
        assertEquals(-1, i);
    }

    @Test
    public void testFindClosest() throws Exception
    {
        double x;
        int i;

        // Exact find
        x = 4.0;
        i = PlotSampleSearch.findClosestSample(samples, TimestampFactory.fromDouble(x));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertEquals(x, samples[i].getTime().toDouble(), 0.01);
        assertEquals(1, i);

        // Still '4'
        i = PlotSampleSearch.findClosestSample(samples, TimestampFactory.fromDouble(4.1));
        assertEquals(1, i);
        i = PlotSampleSearch.findClosestSample(samples, TimestampFactory.fromDouble(4.4));
        assertEquals(1, i);

        // '6'
        i = PlotSampleSearch.findClosestSample(samples, TimestampFactory.fromDouble(5.2));
        assertEquals(2, i);
        i = PlotSampleSearch.findClosestSample(samples, TimestampFactory.fromDouble(6.0));
        assertEquals(2, i);
        i = PlotSampleSearch.findClosestSample(samples, TimestampFactory.fromDouble(6.9));
        assertEquals(2, i);

        // First
        i = PlotSampleSearch.findClosestSample(samples, TimestampFactory.fromDouble(2.0));
        assertEquals(0, i);
        i = PlotSampleSearch.findClosestSample(samples, TimestampFactory.fromDouble(1.0));
        assertEquals(0, i);
        i = PlotSampleSearch.findClosestSample(samples, TimestampFactory.fromDouble(-2.0));
        assertEquals(0, i);

        // Last
        i = PlotSampleSearch.findClosestSample(samples, TimestampFactory.fromDouble(17.8));
        assertEquals(8, i);
        i = PlotSampleSearch.findClosestSample(samples, TimestampFactory.fromDouble(18.0));
        assertEquals(8, i);
        i = PlotSampleSearch.findClosestSample(samples, TimestampFactory.fromDouble(200.0));
        assertEquals(8, i);
    }

    @Test
    public void testFindSampleLessOrEqual() throws Exception
    {
        double x;
        int i;

        // Exact find
        x = 4.0;
        i = PlotSampleSearch.findSampleLessOrEqual(samples, TimestampFactory.fromDouble(x));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertEquals(x, samples[i].getTime().toDouble(), 0.01);

        // Samples contain 4 and 6, but not 5
        x = 5.0;
        i = PlotSampleSearch.findSampleLessOrEqual(samples, TimestampFactory.fromDouble(x));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().toDouble() < x);

        // Lower end
        x = 2.0;
        i = PlotSampleSearch.findSampleLessOrEqual(samples, TimestampFactory.fromDouble(x));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().toDouble() == x);

        // Upper end
        x = 18.0;
        i = PlotSampleSearch.findSampleLessOrEqual(samples, TimestampFactory.fromDouble(x));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().toDouble() == x);

        // Below lower end
        x = 1.0;
        i = PlotSampleSearch.findSampleLessOrEqual(samples, TimestampFactory.fromDouble(x));
        assertEquals(-1, i);
        System.out.println("Looking for " + x + ", found nothing");

        // Beyond upper end
        x = 20.0;
        i = PlotSampleSearch.findSampleLessOrEqual(samples, TimestampFactory.fromDouble(x));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().toDouble() < x);
   }

    @Test
    public void testFindSampleGreaterOrEqual() throws Exception
    {
        double x;
        int i;

        // Exact find
        x = 4.0;
        i = PlotSampleSearch.findSampleGreaterOrEqual(samples, TimestampFactory.fromDouble(x));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().toDouble() == x);


        // Samples contain 4 and 6, but not 5
        x = 5.0;
        i = PlotSampleSearch.findSampleGreaterOrEqual(samples, TimestampFactory.fromDouble(x));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().toDouble() > x);

        // Lower end
        x = 2.0;
        i = PlotSampleSearch.findSampleGreaterOrEqual(samples, TimestampFactory.fromDouble(x));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().toDouble() == x);

        // Upper end
        x = 18.0;
        i = PlotSampleSearch.findSampleGreaterOrEqual(samples, TimestampFactory.fromDouble(x));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().toDouble() == x);

        // Below lower end
        x = 1.0;
        i = PlotSampleSearch.findSampleGreaterOrEqual(samples, TimestampFactory.fromDouble(x));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().toDouble() > x);

        // Beyond upper end
        x = 20.0;
        i = PlotSampleSearch.findSampleGreaterOrEqual(samples, TimestampFactory.fromDouble(x));
        assertEquals(-1, i);
        System.out.println("Looking for " + x + ", found nothing");
   }

    @Test
    public void testFindSampleLessThan() throws Exception
    {
        System.out.println("testFindSampleLessThan()");

        // Patch samples so times are 2, 4, 6, 6, 6, 12, 14
        samples[3] = new PlotSample(6.0, 4);
        samples[4] = new PlotSample(6.0, 5);

        for (int i=0; i<samples.length; ++i)
            System.out.println(i + " " + samples[i]);

        int i = PlotSampleSearch.findSampleLessThan(samples, TimestampFactory.fromDouble(4.5));
        assertEquals(1, i);

        i = PlotSampleSearch.findSampleLessThan(samples, TimestampFactory.fromDouble(12.0));
        assertEquals(4, i);

        i = PlotSampleSearch.findSampleLessThan(samples, TimestampFactory.fromDouble(6.0));
        assertEquals(1, i);

        // Beyond end
        i = PlotSampleSearch.findSampleLessThan(samples, TimestampFactory.fromDouble(18.1));
        assertEquals(8, i);

        // Before start
        i = PlotSampleSearch.findSampleLessThan(samples, TimestampFactory.fromDouble(2.0));
        assertEquals(-1, i);
    }

    @Test
    public void findSampleGreaterThan() throws Exception
    {
        System.out.println("findSampleGreaterThan()");

        // Patch samples so times are 2, 4, 6, 8, 8, 8, 14
        samples[4] = new PlotSample(8.0, 5);
        samples[5] = new PlotSample(8.0, 6);

        for (int i=0; i<samples.length; ++i)
            System.out.println(i + " " + samples[i]);

        int i = PlotSampleSearch.findSampleGreaterThan(samples, TimestampFactory.fromDouble(4.5));
        assertEquals(2, i);

        i = PlotSampleSearch.findSampleGreaterThan(samples, TimestampFactory.fromDouble(8.0));
        assertEquals(6, i);

        // Beyond end
        i = PlotSampleSearch.findSampleGreaterThan(samples, TimestampFactory.fromDouble(18.0));
        assertEquals(-1, i);

        // Before start
        i = PlotSampleSearch.findSampleGreaterThan(samples, TimestampFactory.fromDouble(1.0));
        assertEquals(0, i);
    }
}
