package org.csstudio.swt.chart;

import junit.framework.TestCase;
import junit.framework.Assert;


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

    public void testFindClosest() throws Exception
    {
        double x;
        int i;
        
        // Exact find
        x = 4.0;
        i = ChartSampleSearch.findClosestSample(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        Assert.assertTrue(i >= 0);
        Assert.assertTrue(i < samples.size());
        Assert.assertTrue(samples.get(i).getX() == x);        
        Assert.assertEquals(1, i);

        // Still '4'
        i = ChartSampleSearch.findClosestSample(samples, 4.1);
        Assert.assertEquals(1, i);
        i = ChartSampleSearch.findClosestSample(samples, 4.4);
        Assert.assertEquals(1, i);

        // '6'
        i = ChartSampleSearch.findClosestSample(samples, 5.2);
        Assert.assertEquals(2, i);
        i = ChartSampleSearch.findClosestSample(samples, 6.0);
        Assert.assertEquals(2, i);
        i = ChartSampleSearch.findClosestSample(samples, 6.9);
        Assert.assertEquals(2, i);

        // First
        i = ChartSampleSearch.findClosestSample(samples, 2.0);
        Assert.assertEquals(0, i);
        i = ChartSampleSearch.findClosestSample(samples, 1.0);
        Assert.assertEquals(0, i);
        i = ChartSampleSearch.findClosestSample(samples, -2.0);
        Assert.assertEquals(0, i);

        // Last
        i = ChartSampleSearch.findClosestSample(samples, 17.8);
        Assert.assertEquals(8, i);
        i = ChartSampleSearch.findClosestSample(samples, 18.0);
        Assert.assertEquals(8, i);
        i = ChartSampleSearch.findClosestSample(samples, 200.0);
        Assert.assertEquals(8, i);
    }
    
    public void testFindSampleLessOrEqual() throws Exception
    {
        double x;
        int i;
        
        // Exact find
        x = 4.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        Assert.assertTrue(i >= 0);
        Assert.assertTrue(i < samples.size());
        Assert.assertTrue(samples.get(i).getX() == x);        

        // Samples contain 4 and 6, but not 5
        x = 5.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        Assert.assertTrue(i >= 0);
        Assert.assertTrue(i < samples.size());
        Assert.assertTrue(samples.get(i).getX() < x);        

        // Lower end
        x = 2.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        Assert.assertTrue(i >= 0);
        Assert.assertTrue(i < samples.size());
        Assert.assertTrue(samples.get(i).getX() == x);        

        // Upper end
        x = 18.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        Assert.assertTrue(i >= 0);
        Assert.assertTrue(i < samples.size());
        Assert.assertTrue(samples.get(i).getX() == x);        

        // Below lower end
        x = 1.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        Assert.assertEquals(-1, i);        
        System.out.println("Looking for " + x + ", found nothing");

        // Beyond upper end
        x = 20.0;
        i = ChartSampleSearch.findSampleLessOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        Assert.assertTrue(i >= 0);
        Assert.assertTrue(i < samples.size());
        Assert.assertTrue(samples.get(i).getX() < x);        
   }

    public void testFindSampleGreaterOrEqual() throws Exception
    {
        double x;
        int i;
        
        // Exact find
        x = 4.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        Assert.assertTrue(i >= 0);
        Assert.assertTrue(i < samples.size());
        Assert.assertTrue(samples.get(i).getX() == x);        

        
        // Samples contain 4 and 6, but not 5
        x = 5.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        Assert.assertTrue(i >= 0);
        Assert.assertTrue(i < samples.size());
        Assert.assertTrue(samples.get(i).getX() > x);        

        // Lower end
        x = 2.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        Assert.assertTrue(i >= 0);
        Assert.assertTrue(i < samples.size());
        Assert.assertTrue(samples.get(i).getX() == x);        

        // Upper end
        x = 18.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        Assert.assertTrue(i >= 0);
        Assert.assertTrue(i < samples.size());
        Assert.assertTrue(samples.get(i).getX() == x);        

        // Below lower end
        x = 1.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        System.out.println("Looking for " + x + ", found " + samples.get(i).getX());
        Assert.assertTrue(i >= 0);
        Assert.assertTrue(i < samples.size());
        Assert.assertTrue(samples.get(i).getX() > x);        

        // Beyond upper end
        x = 20.0;
        i = ChartSampleSearch.findSampleGreaterOrEqual(samples, x);
        Assert.assertEquals(-1, i);        
        System.out.println("Looking for " + x + ", found nothing");
   }
}
