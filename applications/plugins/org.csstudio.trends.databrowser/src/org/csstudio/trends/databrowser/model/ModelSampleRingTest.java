package org.csstudio.trends.databrowser.model;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.archive.util.TimestampUtil;
import org.csstudio.value.DoubleValue;
import org.csstudio.value.Value;

@SuppressWarnings("nls")
public class ModelSampleRingTest extends TestCase
{
    private Value create(double tick)
    {
        return new DoubleValue(TimestampUtil.fromDouble(tick),
                        SeverityFactory.getInvalid(),
                        "",
                        MetaDataFactory.getNumeric(),
                        new double[] { 0.0 });
    }
    
    
    public void testContainer() throws Exception
    {
        ModelSampleRing c = new ModelSampleRing(5);
        Assert.assertEquals(5, c.getCapacity());
        Assert.assertEquals(0, c.size());
        
        double value = 0;

        c.add(create(++value));
        System.out.println("Initial element");
        for (int i=0; i<c.size(); ++i)
            System.err.println(c.get(i).getX());
        Assert.assertEquals(5, c.getCapacity());
        Assert.assertEquals(1, c.size());
        Assert.assertEquals(1.0, c.get(0).getX(), 0.1);
        
        // These should all fit
        for (int i=0; i<4; ++i)
            c.add(create(++value));
        System.out.println("5 elements");
        for (int i=0; i<c.size(); ++i)
            System.err.println(c.get(i).getX());
        Assert.assertEquals(5, c.getCapacity());
        Assert.assertEquals(5, c.size());
        Assert.assertEquals(1.0, c.get(0).getX(), 0.1);
        Assert.assertEquals(5.0, c.get(4).getX(), 0.1);

        // One more
        c.add(create(++value));
        System.out.println("sixt elements");
        for (int i=0; i<c.size(); ++i)
            System.err.println(c.get(i).getX());
        Assert.assertEquals(5, c.getCapacity());
        Assert.assertEquals(5, c.size());
        Assert.assertEquals(2.0, c.get(0).getX(), 0.1);
        Assert.assertEquals(6.0, c.get(4).getX(), 0.1);

        // Up to 100
        for (int i=0; i<100-6; ++i)
            c.add(create(++value));
        System.out.println("Total of 100 added");
        for (int i=0; i<c.size(); ++i)
            System.err.println(c.get(i).getX());
        Assert.assertEquals(5, c.getCapacity());
        Assert.assertEquals(5, c.size());
        Assert.assertEquals(96.0, c.get(0).getX(), 0.1);
        Assert.assertEquals(100.0, c.get(4).getX(), 0.1);
        try
        {
            c.get(5);
            Assert.fail("Didn't generate exception");
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            Assert.assertEquals("Array index out of range: 5", e.getMessage());
        }
        
        // Clear
        c.setCapacity(5);
        System.out.println("Cleared");
        for (int i=0; i<c.size(); ++i)
            System.err.println(c.get(i).getX());
        Assert.assertEquals(5, c.getCapacity());
        Assert.assertEquals(0, c.size());
    }
}
