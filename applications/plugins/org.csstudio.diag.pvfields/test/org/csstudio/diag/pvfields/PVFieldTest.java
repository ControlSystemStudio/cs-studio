package org.csstudio.diag.pvfields;

import java.util.concurrent.CountDownLatch;

import org.csstudio.diag.pvfields.model.PVFieldListener;
import org.junit.Before;
import org.junit.Test;

public class PVFieldTest implements PVFieldListener
{
    final private CountDownLatch updates = new CountDownLatch(1);

    @Before
    public void setup() throws Exception
    {
        TestSetup.setup();
    }
    
    @Override
    public void updateField(final PVField field)
    {
    	System.out.println("Field update: " + field);
    	updates.countDown();
    }

    @Test
    public void testPVField() throws Exception
    {
    	final PVField field = new PVField(TestSetup.CHANNEL_NAME + ".VAL", "test");
    	field.start(this);
        updates.await();
        field.stop();
    }
}
