package org.csstudio.diag.pvfields;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.jca.JCADataSource;
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
    public void updateFields(final PVField[] fields)
    {
        System.out.println("Fields");
        for (PVField field : fields)
            System.out.println(field);
        updates.countDown();
    }

    @Test
    public void testPVModel() throws Exception
    {
        PVManager.setDefaultDataSource(new JCADataSource());
        
        PVModel model = new PVModel(this);
        
        model.setPVName("DTL_LLRF:IOC1:Load"); // TestSetup.CHANNEL_NAME);
        updates.await();
        
        assertThat(TestSetup.CHANNEL_NAME, equalTo(model.getPVName()));
        
        Map<String, String> properties = model.getProperties();
        assertTrue(properties.size() > 0);
    
        model.close();
    }
}
