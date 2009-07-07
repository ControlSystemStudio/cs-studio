package org.csstudio.utility.pv.simu;

import org.junit.Test;

@SuppressWarnings("nls")
public class DynamicValueTest implements ValueListener
{
    
    public void changed(Value value)
    {
        System.out.println("Update: " + value.toString());
    }

    @Test
    public void testUpdate() throws Exception
    {
        DynamicValue value = new NoiseValue("noise(0, 10, 1.0)");
        value.addListener(this);
        value.start();
        Thread.sleep(3000);
        value.stop();
    }
}
