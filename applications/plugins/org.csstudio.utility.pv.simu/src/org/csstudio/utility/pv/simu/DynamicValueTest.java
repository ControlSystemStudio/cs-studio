package org.csstudio.utility.pv.simu;

import org.junit.Test;

/** JUnit test of the dynamic values
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DynamicValueTest implements ValueListener
{
    private volatile int updates = 0;
    
    public void changed(Value value)
    {
        ++updates;
        System.out.println("Update: " + value.toString());
    }

    @Test
    public void testNoise() throws Exception
    {
        final DynamicValue value = new NoiseValue("noise(0, 10, 1.0)");
        value.addListener(this);
        updates = 0;
        value.start();
        while (updates < 3)
            Thread.sleep(100);
        value.stop();
        value.removeListener(this);
    }

    @Test
    public void testSine() throws Exception
    {
        final DynamicValue value = new SineValue("sine(0, 10, 1.0)");
        value.addListener(this);
        updates = 0;
        value.start();
        while (updates < 20)
            Thread.sleep(100);
        value.stop();
        value.removeListener(this);
    }

}
