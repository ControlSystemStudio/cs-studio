package org.csstudio.diag.pvfields;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.csstudio.diag.pvfields.model.PVModel;
import org.csstudio.diag.pvfields.model.PVModelListener;
import org.junit.Before;
import org.junit.Test;

public class PVModelUnitTest implements PVModelListener
{
    final private CountDownLatch updates = new CountDownLatch(2);

    @Before
    public void setup() throws Exception
    {
        TestSetup.setup();
    }
    
    @Override
    public void updateProperties(final Map<String, String> properties)
    {
        System.out.println("Properties");
    	for (String prop : properties.keySet())
            System.out.println(prop + " = " + properties.get(prop));
        updates.countDown();
    }

    @Override
    public void updateFields(final List<PVField> fields)
    {
        System.out.println("Fields");
        for (PVField field : fields)
            System.out.println(field);
        updates.countDown();
    }

    @Test
    public void testPVModel() throws Exception
    {
    	final PVModel model = new PVModel(this);
        model.setPVName(TestSetup.CHANNEL_NAME);
        updates.await();
        
        assertThat(TestSetup.CHANNEL_NAME, equalTo(model.getPVName()));
        
        final Map<String, String> properties = model.getProperties();
        assertTrue(properties.size() > 0);
    }
}
