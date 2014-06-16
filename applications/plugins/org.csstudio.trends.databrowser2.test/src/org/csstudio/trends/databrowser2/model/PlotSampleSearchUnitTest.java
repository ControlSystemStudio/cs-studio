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

import org.epics.util.time.Timestamp;
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
        int i = PlotSampleSearch.findClosestSample(samples, Timestamp.of(10, 0));
        assertEquals(-1, i);
        i = PlotSampleSearch.findSampleGreaterOrEqual(samples, Timestamp.of(10, 0));
        assertEquals(-1, i);
        i = PlotSampleSearch.findSampleLessOrEqual(samples, Timestamp.of(10, 0));
        assertEquals(-1, i);
    }

    @Test
    public void testFindClosest() throws Exception
    {
        double x;
        int i;

        // Exact find
        x = 4.0;
        i = PlotSampleSearch.findClosestSample(samples, Timestamp.of((int)x, 0));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertEquals(x, samples[i].getTime().getSec(), 0.01);
        assertEquals(1, i);

        // Still '4'
        i = PlotSampleSearch.findClosestSample(samples, Timestamp.of(4, 100000000));
        assertEquals(1, i);
        i = PlotSampleSearch.findClosestSample(samples, Timestamp.of(4, 400000000));
        assertEquals(1, i);

        // '6'
        i = PlotSampleSearch.findClosestSample(samples, Timestamp.of(5, 200000000));
        assertEquals(2, i);
        i = PlotSampleSearch.findClosestSample(samples, Timestamp.of(6, 0));
        assertEquals(2, i);
        i = PlotSampleSearch.findClosestSample(samples, Timestamp.of(6, 900000000));
        assertEquals(2, i);

        // First
        i = PlotSampleSearch.findClosestSample(samples, Timestamp.of(2, 0));
        assertEquals(0, i);
        i = PlotSampleSearch.findClosestSample(samples, Timestamp.of(1, 0));
        assertEquals(0, i);
        i = PlotSampleSearch.findClosestSample(samples, Timestamp.of(-2, 0));
        assertEquals(0, i);

        // Last
        i = PlotSampleSearch.findClosestSample(samples, Timestamp.of(17, 800000000));
        assertEquals(8, i);
        i = PlotSampleSearch.findClosestSample(samples, Timestamp.of(18, 0));
        assertEquals(8, i);
        i = PlotSampleSearch.findClosestSample(samples, Timestamp.of(200, 0));
        assertEquals(8, i);
    }

    @Test
    public void testFindSampleLessOrEqual() throws Exception
    {
        int x;
        int i;

        // Exact find
        x = 4;
        i = PlotSampleSearch.findSampleLessOrEqual(samples, Timestamp.of(x, 0));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertEquals(x, samples[i].getTime().getSec());

        // Samples contain 4 and 6, but not 5
        x = 5;
        i = PlotSampleSearch.findSampleLessOrEqual(samples, Timestamp.of(x, 0));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().getSec() < x);

        // Lower end
        x = 2;
        i = PlotSampleSearch.findSampleLessOrEqual(samples, Timestamp.of(x, 0));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().getSec() == x);

        // Upper end
        x = 18;
        i = PlotSampleSearch.findSampleLessOrEqual(samples, Timestamp.of(x, 0));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().getSec() == x);

        // Below lower end
        x = 1;
        i = PlotSampleSearch.findSampleLessOrEqual(samples, Timestamp.of(x, 0));
        assertEquals(-1, i);
        System.out.println("Looking for " + x + ", found nothing");

        // Beyond upper end
        x = 20;
        i = PlotSampleSearch.findSampleLessOrEqual(samples, Timestamp.of(x, 0));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().getSec() < x);
   }

    @Test
    public void testFindSampleGreaterOrEqual() throws Exception
    {
        int x;
        int i;

        // Exact find
        x = 4;
        i = PlotSampleSearch.findSampleGreaterOrEqual(samples, Timestamp.of(x, 0));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().getSec() == x);


        // Samples contain 4 and 6, but not 5
        x = 5;
        i = PlotSampleSearch.findSampleGreaterOrEqual(samples, Timestamp.of(x, 0));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().getSec() > x);

        // Lower end
        x = 2;
        i = PlotSampleSearch.findSampleGreaterOrEqual(samples, Timestamp.of(x, 0));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().getSec() == x);

        // Upper end
        x = 18;
        i = PlotSampleSearch.findSampleGreaterOrEqual(samples, Timestamp.of(x, 0));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().getSec() == x);

        // Below lower end
        x = 1;
        i = PlotSampleSearch.findSampleGreaterOrEqual(samples, Timestamp.of(x, 0));
        System.out.println("Looking for " + x + ", found " + samples[i].getXValue());
        assertTrue(i >= 0);
        assertTrue(i < samples.length);
        assertTrue(samples[i].getTime().getSec() > x);

        // Beyond upper end
        x = 20;
        i = PlotSampleSearch.findSampleGreaterOrEqual(samples, Timestamp.of(x, 0));
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

        int i = PlotSampleSearch.findSampleLessThan(samples, Timestamp.of(4, 500000000));
        assertEquals(1, i);

        i = PlotSampleSearch.findSampleLessThan(samples, Timestamp.of(12, 0));
        assertEquals(4, i);

        i = PlotSampleSearch.findSampleLessThan(samples, Timestamp.of(6, 0));
        assertEquals(1, i);

        // Beyond end
        i = PlotSampleSearch.findSampleLessThan(samples, Timestamp.of(18, 100000000));
        assertEquals(8, i);

        // Before start
        i = PlotSampleSearch.findSampleLessThan(samples, Timestamp.of(2, 0));
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

        int i = PlotSampleSearch.findSampleGreaterThan(samples, Timestamp.of(4, 500000000));
        assertEquals(2, i);

        i = PlotSampleSearch.findSampleGreaterThan(samples, Timestamp.of(8, 0));
        assertEquals(6, i);

        // Beyond end
        i = PlotSampleSearch.findSampleGreaterThan(samples, Timestamp.of(18, 0));
        assertEquals(-1, i);

        // Before start
        i = PlotSampleSearch.findSampleGreaterThan(samples, Timestamp.of(1, 0));
        assertEquals(0, i);
    }
}
