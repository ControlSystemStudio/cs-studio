package org.csstudio.diag.pvfields;

import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vType;
import static org.epics.util.time.TimeDuration.ofSeconds;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VType;

/** Data provider based on PVManager and assumptions about EPICS channels
 * 
 *  <p>Fetches basic channel information from PVManager
 *  and picks a default set of fields.
 *  
 *  @author Kay Kasemir
 */
public class EPICSDataProvider implements DataProvider
{
    final public static int TIMEOUT_SECS = 10;
    final private CountDownLatch done = new CountDownLatch(1);
    final private Map<String, String> properties = new HashMap<String, String>();
    private PVReader<VType> pv;

    @Override
    public PVInfo lookup(final String name) throws Exception
    {
        final PVReaderListener pv_listener = new PVReaderListener()
        {
            @Override
            public void pvChanged()
            {
                final ChannelHandler channel = PVManager.getDefaultDataSource().getChannels().get(pv.getName());
                if (channel == null)
                    System.err.println("No channel info for " + pv.getName());
                else
                {
                    final Map<String, Object> properties = channel.getProperties();
                    for (String prop : properties.keySet())
                        properties.put(prop, properties.get(prop).toString());
                }
                done.countDown();
            }
        };
        pv = PVManager.read(latestValueOf(vType(name))).timeout(ofSeconds(TIMEOUT_SECS)).listeners(pv_listener).maxRate(ofSeconds(0.5));
        // Wait for value from reader
        done.await();
        pv.close();

        // TODO Determine better set of fields based on record type
        final List<PVField> fields = Arrays.asList(
            new PVField(name + ".DESC", ""),
            new PVField(name + ".SCAN", ""),
            new PVField(name + ".VAL", "")
        );
        
        final PVInfo info = new PVInfo(properties, fields);
        Logger.getLogger(getClass().getName()).log(Level.FINE, "EPICS Info for {0}: {1}", new Object[] { name, info });
        return info;
    }
}
