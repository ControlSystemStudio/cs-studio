package org.csstudio.diag.pvfields;

import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vType;
import static org.epics.util.time.TimeDuration.ofSeconds;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VType;
import org.junit.Before;
import org.junit.Test;

public class PVInfoTest
{
    final private CountDownLatch updates = new CountDownLatch(1);
    private PVReader<VType> pv;
    
    @Before
    public void setup() throws Exception
    {
        TestSetup.setup();
    }

    @Test
    public void testPVInfo() throws Exception
    {
        final PVReaderListener listener = new PVReaderListener()
        {
            @Override
            public void pvChanged()
            {
                System.out.println(pv.getValue());
                final ChannelHandler channel = PVManager.getDefaultDataSource().getChannels().get(pv.getName());
                if (channel == null)
                    System.err.println("No channel info for " + pv.getName());
                Map<String, Object> properties = channel.getProperties();
                for (String prop : properties.keySet())
                    System.out.println(prop + " = " + properties.get(prop));
                
                updates.countDown();
            }
        };
        pv = PVManager.read(latestValueOf(vType(TestSetup.CHANNEL_NAME))).listeners(listener ).maxRate(ofSeconds(0.5));
        
        updates.await();
        pv.close();
    }
}
