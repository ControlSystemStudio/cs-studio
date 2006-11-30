package org.csstudio.swt.chart.axes;

import junit.framework.TestCase;
import junit.framework.Assert;

/** Test of the linear transformation.
 * 
 * @author Kay Kasemir
 */
public class LinearTransformTest extends TestCase
{
    /*
     * Test method for 'kay.swt.plot.LinearTransform.transform(double)'
     */
    public void testTransform()
    {
        ITransform t = new LinearTransform();
        
        // Default 1:1
        Assert.assertEquals(0.0, t.transform(0.0), 0.01);
        Assert.assertEquals(1.0, t.transform(1.0), 0.01);

        t.config(0.0, 10.0, -1.0, 0.0);
        Assert.assertEquals(-1.0, t.transform(0.0), 0.01);
        Assert.assertEquals(0.0, t.transform(10.0), 0.01);
        Assert.assertEquals(1.0, t.transform(20.0), 0.01);
    }

}
