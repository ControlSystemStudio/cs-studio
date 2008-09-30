package org.csstudio.trends.databrowser.model;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.apputil.ringbuffer.RingBuffer;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.junit.Test;

@SuppressWarnings("nls")
public class ModelSampleRingTest extends TestCase
{
    private static final String SOURCE = "test";

    private IValue create(double tick)
    {
        return ValueFactory.createDoubleValue(TimestampFactory.fromDouble(tick),
                        ValueFactory.createInvalidSeverity(),
                        "",
                        ValueFactory.createNumericMetaData(0, 0, 0, 0, 0, 0, 0, ""),
                        IValue.Quality.Original,
                        new double[] { 0.0 });
    }
    
    @Test
    public void testContainer() throws Exception
    {
        RingBuffer<ModelSample> c = new RingBuffer<ModelSample>(5);
        Assert.assertEquals(5, c.getCapacity());
        Assert.assertEquals(0, c.size());
        
        double value = 0;

        c.add(new ModelSample(create(++value), SOURCE));
        System.out.println("Initial element");
        for (int i=0; i<c.size(); ++i)
            System.err.println(c.get(i).getX());
        assertEquals(5, c.getCapacity());
        assertEquals(1, c.size());
        assertEquals(1.0, c.get(0).getX(), 0.1);
        
        // These should all fit
        for (int i=0; i<4; ++i)
            c.add(new ModelSample(create(++value), SOURCE));
        System.out.println("5 elements");
        for (int i=0; i<c.size(); ++i)
            System.err.println(c.get(i).getX());
        assertEquals(5, c.getCapacity());
        assertEquals(5, c.size());
        assertEquals(1.0, c.get(0).getX(), 0.1);
        assertEquals(5.0, c.get(4).getX(), 0.1);

        // One more
        c.add(new ModelSample(create(++value), SOURCE));
        System.out.println("sixt elements");
        for (int i=0; i<c.size(); ++i)
            System.err.println(c.get(i).getX());
        assertEquals(5, c.getCapacity());
        assertEquals(5, c.size());
        assertEquals(2.0, c.get(0).getX(), 0.1);
        assertEquals(6.0, c.get(4).getX(), 0.1);

        // Up to 100
        for (int i=0; i<100-6; ++i)
            c.add(new ModelSample(create(++value), SOURCE));
        System.out.println("Total of 100 added");
        for (int i=0; i<c.size(); ++i)
            System.err.println(c.get(i).getX());
        assertEquals(5, c.getCapacity());
        assertEquals(5, c.size());
        assertEquals(96.0, c.get(0).getX(), 0.1);
        assertEquals(100.0, c.get(4).getX(), 0.1);
        try
        {
            c.get(5);
            fail("Didn't generate exception");
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            assertEquals("Array index out of range: 5", e.getMessage());
        }
        
        // Grow
        c.setCapacity(10);
        c.add(new ModelSample(create(++value), SOURCE));
        System.out.println("Extended to 10");
        for (int i=0; i<c.size(); ++i)
            System.err.println(c.get(i).getX());
        assertEquals(10, c.getCapacity());
        assertEquals(6, c.size());

        // Shrink
        c.setCapacity(3);
        c.add(new ModelSample(create(++value), SOURCE));
        System.out.println("Shrink to 3");
        for (int i=0; i<c.size(); ++i)
            System.err.println(c.get(i).getX());
        assertEquals(3, c.getCapacity());
        assertEquals(3, c.size());
    }
}
