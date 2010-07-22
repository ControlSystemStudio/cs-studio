package org.csstudio.util.stats;

import org.junit.Test;
import static org.junit.Assert.*;

public class AverageTest
{
    private static final double EPS = 0.001;

    @Test
    public void testAverage()
    {
        Average average = new Average();
        assertEquals(0.0, average.get(), EPS);
        
        average.update(100.0);
        assertEquals(100.0, average.get(), EPS);

        average.update(50.0);
        assertEquals(100.0 * 0.9 + 50.0 * 0.1, average.get(), EPS);

        average.update(50.0);
        assertEquals((100.0 * 0.9 + 50.0 * 0.1)*0.9 + 50.0 * 0.1,
                     average.get(), EPS);
    }
}
