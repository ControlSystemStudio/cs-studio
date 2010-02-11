package org.csstudio.trends.databrowser.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** JUnit test of PlotSampleMerger
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PlotSampleMergerTest
{
    /** Check if samples are monotonous in time */
    private boolean check(final PlotSample samples[])
    {
        for (int i = 0; i < samples.length; i++)
        {
            System.out.println(String.format("%3d: ", i) + samples[i]);
            if (i > 0  &&
                samples[i].getTime().isLessThan(samples[i-1].getTime()))
                return false;
        }
        return true;
    }

    @Test
    public void addToNothing()
    {
        System.out.println("addToNothing()");
        // Start with nothing, add new samples
        final PlotSample orig[] = null;
        final PlotSample add[] = TestSampleBuilder.makePlotSamples(0, 10);
        PlotSample result[] = PlotSampleMerger.merge(orig, add);
        assertTrue(check(result));
        assertArrayEquals(add, result);
        
        // Add nothing to existing samples
        result = PlotSampleMerger.merge(add, orig);
        assertTrue(check(result));
        assertArrayEquals(add, result);
    }

    @Test
    public void addBeforeOrAfter()
    {
        System.out.println("addBeforeOrAfter()");
        final PlotSample earlier[] = TestSampleBuilder.makePlotSamples(0, 10);
        final PlotSample later[] = TestSampleBuilder.makePlotSamples(10, 20);
        // Time line: old ... new
        PlotSample result[] = PlotSampleMerger.merge(earlier, later);
        assertTrue(check(result));
        assertEquals(20, result.length);
        assertEquals(earlier[0], result[0]);
        assertEquals(later[9], result[19]);

        // Timeline: new samples ... old samples
        result = PlotSampleMerger.merge(later, earlier);
        assertTrue(check(result));
        assertEquals(20, result.length);
        assertEquals(earlier[0], result[0]);
        assertEquals(later[9], result[19]);
    }

    @Test
    public void addOverlapsStartOfOldData()
    {
        System.out.println("addOverlapsStartOfOldData()");
        // 0, 1, ...10
        final PlotSample add[] = TestSampleBuilder.makePlotSamples(0, 10);
        // 5, 10, 15, 20, ..
        final PlotSample existing[] = new PlotSample[10];
        for (int i=0; i<10; ++i) 
            existing[i] = TestSampleBuilder.makePlotSample(5 * (i+1));
        
        PlotSample result[] = PlotSampleMerger.merge(existing, add);
        assertTrue(check(result));
        assertEquals(19, result.length);
        assertEquals( 0, result[0].getYValue(), 0.1);
        assertEquals( 1, result[1].getYValue(), 0.1);
        assertEquals(10, result[10].getYValue(), 0.1);
        assertEquals(15, result[11].getYValue(), 0.1);
        assertEquals(20, result[12].getYValue(), 0.1);
    }

    @Test
    public void addOverlapsEndOfOldData()
    {
        System.out.println("addOverlapsEndOfOldData()");
        // 45, 46, ..., 54
        final PlotSample add[] = TestSampleBuilder.makePlotSamples(45, 55);
        // 5, 10, 15, 20, .., 50
        final PlotSample existing[] = new PlotSample[10];
        for (int i=0; i<10; ++i) 
            existing[i] = TestSampleBuilder.makePlotSample(5 * (i+1));
        
        PlotSample result[] = PlotSampleMerger.merge(existing, add);
        assertTrue(check(result));
        assertEquals(18, result.length);
        assertEquals( 5, result[0].getYValue(), 0.1);
        assertEquals(10, result[1].getYValue(), 0.1);
        assertEquals(15, result[2].getYValue(), 0.1);
        assertEquals(45, result[8].getYValue(), 0.1);
        assertEquals(46, result[9].getYValue(), 0.1);
        assertEquals(47, result[10].getYValue(), 0.1);
        assertEquals(48, result[11].getYValue(), 0.1);
        assertEquals(54, result[17].getYValue(), 0.1);
    }

    @Test
    public void addWithinExistingData()
    {
        System.out.println("addWithinExistingData()");
        // 15, 16, ..., 21
        final PlotSample add[] = TestSampleBuilder.makePlotSamples(14, 22);
        // 5, 10, 15, 20, .., 50
        final PlotSample existing[] = new PlotSample[10];
        for (int i=0; i<10; ++i) 
            existing[i] = TestSampleBuilder.makePlotSample(5 * (i+1));
        
        PlotSample result[] = PlotSampleMerger.merge(existing, add);
        assertTrue(check(result));
        assertEquals(16, result.length);

        assertEquals( 5, result[0].getYValue(), 0.1);
        assertEquals(10, result[1].getYValue(), 0.1);

        assertEquals(14, result[2].getYValue(), 0.1);
        assertEquals(21, result[9].getYValue(), 0.1);

        assertEquals(25, result[10].getYValue(), 0.1);
        assertEquals(50, result[15].getYValue(), 0.1);
    }

    @Test
    public void newDataCompletelyReplacesOld()
    {
        System.out.println("newDataCompletelyReplacesOld()");
        final PlotSample existing[] = TestSampleBuilder.makePlotSamples(15, 21);
        final PlotSample add[] = TestSampleBuilder.makePlotSamples(1, 30);
        
        PlotSample result[] = PlotSampleMerger.merge(existing, add);
        assertTrue(check(result));
        assertArrayEquals(add, result);
    }
}
