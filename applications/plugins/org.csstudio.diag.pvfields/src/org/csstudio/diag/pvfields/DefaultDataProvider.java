package org.csstudio.diag.pvfields;

import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vType;
import static org.epics.util.time.TimeDuration.ofSeconds;

import java.util.HashMap;
import java.util.Map;

import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VType;

public class DefaultDataProvider implements DataProvider
{
    private PVReader<VType> pv;

    @Override
    public void run(final String name, final PVModelListener listener)
    {
        PVReaderListener pv_listener = new PVReaderListener()
        {
            @Override
            public void pvChanged()
            {
                final ChannelHandler channel = PVManager.getDefaultDataSource().getChannels().get(pv.getName());
                if (channel == null)
                    System.err.println("No channel info for " + pv.getName());
                final Map<String, String> pv_properties = new HashMap<String, String>();
                final Map<String, Object> properties = channel.getProperties();
                for (String prop : properties.keySet())
                    pv_properties.put(prop, properties.get(prop).toString());
                
                pv.close();
                
                listener.updateProperties(pv_properties);
            }
        };
        pv = PVManager.read(latestValueOf(vType(name))).listeners(pv_listener).maxRate(ofSeconds(0.5));

        listener.updateFields(new PVField[]
        {
            new PVField(name + ".VAL", "")
        });
    }
}
