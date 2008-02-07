package org.csstudio.platform.internal.data;

import static org.junit.Assert.*;

import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.junit.Test;

public class DoubleValueTest
{
    @Test
    public void specialComparisons()
    {
        // This one is obvious
        assertEquals(3.14, 3.14, 0.001);
        
        // These are also the same
        assertTrue("+inf comparison",
                Double.POSITIVE_INFINITY == Double.POSITIVE_INFINITY);
        assertTrue("-inf comparison",
                Double.NEGATIVE_INFINITY == Double.NEGATIVE_INFINITY);

        // Beware of this one!!
        assertTrue("NaN differs from anything", Double.NaN != Double.NaN);
    }

    @Test
    public void arrayCompare()
    {
        final double a[] = 
            new double[] { 3.14, Double.NaN, Double.POSITIVE_INFINITY, 10.0 };
        final double b[] =
            new double[] { 3.14, Double.NaN, Double.POSITIVE_INFINITY, 10.0 };
        
        // Compare individual values the same way as DoubleValue.equals()
        for (int i=0; i<a.length; ++i)
        {   
            if (Double.isNaN(a[i])   &&   Double.isNaN(b[i]))
                continue; // OK, we use NaN == NaN
            if (a[i] != b[i])
                assertTrue("Error", false);
        }
        
        // Full DoubleValue comparison
        final ITimestamp time = TimestampFactory.now();
        final INumericMetaData meta =
            new NumericMetaData(0.0, 10.0, 2.0, 8.0, 1.0, 9.0, 2, "a.u.");
        final ISeverity sevr = SeverityInstances.ok;
        final DoubleValue va =
            new DoubleValue(time, sevr, "OK", meta, IValue.Quality.Original, a);
        final DoubleValue vb =
            new DoubleValue(time, sevr, "OK", meta, IValue.Quality.Original, b);
        
        assertTrue(va.equals(vb));
    }
}
