package org.csstudio.diag.pvfields;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.csstudio.diag.pvfields.model.PVModel;
import org.csstudio.diag.pvfields.model.PVModelListener;
import org.junit.Before;
import org.junit.Test;

public class PVModelUnitTest implements PVModelListener
{
	// Expect updates on properties, fields overall and at least two individual fields
    final private CountDownLatch updates = new CountDownLatch(4);

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
    
    @Override
	public void updateField(final PVField field)
    {
    	System.out.println("Update from field " + field);
        updates.countDown();
	}

	@Test
    public void testPVModel() throws Exception
    {
    	final PVModel model = new PVModel(TestSetup.CHANNEL_NAME, this);
        updates.await();
        
        assertThat(TestSetup.CHANNEL_NAME, equalTo(model.getPVName()));
        
        final Map<String, String> properties = model.getProperties();
        assertTrue(properties.size() > 0);
        
        // Wait a little longer to allow more field updates
        System.out.println("Allowing more updates...");
        TimeUnit.SECONDS.sleep(1);
        System.out.println("Stopping");
        
        model.stop();
    }
}
