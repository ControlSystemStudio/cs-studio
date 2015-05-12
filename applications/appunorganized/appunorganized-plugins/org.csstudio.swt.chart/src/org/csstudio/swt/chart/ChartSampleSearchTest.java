/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ChartSampleSearchTest
{
    private ChartSampleSequence samples;

    @Before
    public void setUp() throws Exception
    {
        final ChartSampleSequenceContainer sc = new ChartSampleSequenceContainer();
        for (int i=1; i<10; ++i) {
            sc.add(2*i, i);      // y = 1, 2, 3, ..., 9
        }
        samples = sc;
    }

    @Test
    public void testEmpty() throws Exception
    {
        // Empty container
        final ChartSampleSequenceContainer sc = new ChartSampleSequenceContainer();

        // Should get -1 (nothing found) for empty container
        int i = ChartSampleSearch.findClosestSample(sc, 10.0);
        assertEquals(-1, i);
        i = ChartSampleSearch.findSampleGreaterOrEqual(sc, 10.0);
        assertEquals(-1, i);
        i = ChartSampleSearch.findSampleLessOrEqual(sc, 10.0);
        assertEquals(-1, i);
    }

    @Test
    public void testFindClosest() throws Exception
    {
        double x;
        int i;

        // Exact find
        x = 4.0;
        i = ChartSampleSearch.findClosestSample(samples, x);
        //System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() == x);
        assertEquals(1, i);

        // Still '4'
        i = ChartSampleSearch.findClosestSample(samples, 4.1);
        assertEquals(1, i);
        i = ChartSampleSearch.findClosestSample(samples, 4.4);
        assertEquals(1, i);

        // '6'
        i = ChartSampleSearch.findClosestSample(samples, 5.2);
        assertEquals(2, i);
        i = ChartSampleSearch.findClosestSample(samples, 6.0);
        assertEquals(2, i);
        i = ChartSampleSearch.findClosestSample(samples, 6.9);
        assertEquals(2, i);

        // First
        i = ChartSampleSearch.findClosestSample(samples, 2.0);
        assertEquals(0, i);
        i = ChartSampleSearch.findClosestSample(samples, 1.0);
        assertEquals(0, i);
        i = ChartSampleSearch.findClosestSample(samples, -2.0);
        assertEquals(0, i);

        // Last
        i = ChartSampleSearch.findClosestSample(samples, 17.8);
        assertEquals(8, i);
        i = ChartSampleSearch.findClosestSample(samples, 18.0);
        assertEquals(8, i);
        i = ChartSampleSearch.findClosestSample(samples, 200.0);
        assertEquals(8, i);
    }

    @Test
    public void testFindSampleLessOrEqual() throws Exception
    {
        double x;
        int i;

        // Exact find
        x = 4.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        //System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() == x);

        // Samples contain 4 and 6, but not 5
        x = 5.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        //System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() < x);

        // Lower end
        x = 2.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        //System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() == x);

        // Upper end
        x = 18.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        //System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() == x);

        // Below lower end
        x = 1.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        assertEquals(-1, i);
        //System.out.println("Looking for " + x + ", found nothing");

        // Beyond upper end
        x = 20.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        //System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() < x);
   }

    @Test
    public void testFindSampleGreaterOrEqual() throws Exception
    {
        double x;
        int i;

        // Exact find
        x = 4.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        //System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() == x);


        // Samples contain 4 and 6, but not 5
        x = 5.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        //System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() > x);

        // Lower end
        x = 2.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        //System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() == x);

        // Upper end
        x = 18.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        //System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() == x);

        // Below lower end
        x = 1.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        //System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() > x);

        // Beyond upper end
        x = 20.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        assertEquals(-1, i);
        //System.out.println("Looking for " + x + ", found nothing");
   }
}
