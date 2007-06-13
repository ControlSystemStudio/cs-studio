package org.csstudio.swt.chart;

import junit.framework.TestCase;


@SuppressWarnings("nls")
public class ChartSampleSearchTest extends TestCase
{
    private ChartSampleSequence samples;

    @Override
    protected void setUp() throws Exception
    {
        ChartSampleSequenceContainer sc = new ChartSampleSequenceContainer();
        for (int i=1; i<10; ++i) // x = 2, 4, 6, ..., 18
            sc.add(2*i, i);      // y = 1, 2, 3, ..., 9
        samples = sc;
    }

    public void testEmpty() throws Exception
    {
        // Empty container
        ChartSampleSequenceContainer sc = new ChartSampleSequenceContainer();
        
        // Should get -1 (nothing found) for empty container
        int i = ChartSampleSearch.findClosestSample(sc, 10.0);
        assertEquals(-1, i);
        i = ChartSampleSearch.findSampleGreaterOrEqual(sc, 10.0);
        assertEquals(-1, i);
        i = ChartSampleSearch.findSampleLessOrEqual(sc, 10.0);
        assertEquals(-1, i);
    }

    
    public void testFindClosest() throws Exception
    {
        double x;
        int i;
        
        // Exact find
        x = 4.0;
        i = ChartSampleSearch.findClosestSample(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
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
    
    public void testFindSampleLessOrEqual() throws Exception
    {
        double x;
        int i;
        
        // Exact find
        x = 4.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() == x);        

        // Samples contain 4 and 6, but not 5
        x = 5.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() < x);        

        // Lower end
        x = 2.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() == x);        

        // Upper end
        x = 18.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() == x);        

        // Below lower end
        x = 1.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        assertEquals(-1, i);        
        System.out.println("Looking for " + x + ", found nothing");

        // Beyond upper end
        x = 20.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() < x);        
   }

    public void testFindSampleGreaterOrEqual() throws Exception
    {
        double x;
        int i;
        
        // Exact find
        x = 4.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() == x);        

        
        // Samples contain 4 and 6, but not 5
        x = 5.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() > x);        

        // Lower end
        x = 2.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() == x);        

        // Upper end
        x = 18.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() == x);        

        // Below lower end
        x = 1.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        assertTrue(i >= 0);
        assertTrue(i < samples.size());
        assertTrue(samples.get(i).getX() > x);        

        // Beyond upper end
        x = 20.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        assertEquals(-1, i);        
        System.out.println("Looking for " + x + ", found nothing");
   }
}
