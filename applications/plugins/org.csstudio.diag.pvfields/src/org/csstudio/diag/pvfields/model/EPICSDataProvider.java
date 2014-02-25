/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.model;

import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.vtype.ExpressionLanguage.vType;
import static org.epics.util.time.TimeDuration.ofSeconds;
import static org.epics.util.time.TimeDuration.ofMillis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.diag.pvfields.Activator;
import org.csstudio.diag.pvfields.DataProvider;
import org.csstudio.diag.pvfields.PVField;
import org.csstudio.diag.pvfields.PVInfo;
import org.csstudio.diag.pvfields.Preferences;
import org.csstudio.utility.pvmanager.ConfigurationHelper;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.vtype.VType;

/** Data provider based on PVManager and assumptions about EPICS channels
 * 
 *  <p>Fetches basic channel information from PVManager
 *  and picks a default set of fields.
 *  
 *  @author Kay Kasemir
 */
public class EPICSDataProvider implements DataProvider
{
    final private CountDownLatch done = new CountDownLatch(1);
    final private Map<String, String> properties = new HashMap<String, String>();
    private PVReader<VType> pv;

    @Override
    public PVInfo lookup(final String name) throws Exception
    {
        final PVReaderListener<VType> pv_listener = new PVReaderListener<VType>()
        {
            @Override
            public void pvChanged(final PVReaderEvent<VType> event)
            {
                final PVReader<VType> pv = event.getPvReader();
            	final Exception error = pv.lastException();
            	if (error != null)
            	{
                	Activator.getLogger().log(Level.WARNING, "Error for " + pv.getName(), error);
                	// Done (with no data)
                    done.countDown();
            	}
            
            	// No error:
            	final String full_name;
            	
            	if (name.indexOf("://") > 0)
            		full_name = name;
            	else
					full_name = ConfigurationHelper.defaultDataSourceName() + "://" + name;
                final Map<String, ChannelHandler> channels = PVManager.getDefaultDataSource().getChannels();
				final ChannelHandler channel = channels.get(full_name);
                if (channel == null)
                {
                	Activator.getLogger().log(Level.WARNING, "No channel info for {0}", full_name);
                }
                else
                {
                    final Map<String, Object> properties = channel.getProperties();
                    for (String prop : properties.keySet())
                    	EPICSDataProvider.this.properties.put("PV: " + prop, properties.get(prop).toString());
                }
                done.countDown();
            }
        };
        
        pv = PVManager.read(latestValueOf(vType(name))).timeout(ofMillis(Preferences.getTimeout())).readListener(pv_listener).maxRate(ofSeconds(0.5));
        // Wait for value from reader
        done.await();
        pv.close();

        // TODO Determine better set of fields based on record type?
        final List<PVField> fields = Arrays.asList(
            new PVField(name + ".DESC"),
            new PVField(name + ".SCAN"),
            new PVField(name + ".VAL")
        );
        
        final PVInfo info = new PVInfo(properties, fields);
        Logger.getLogger(getClass().getName()).log(Level.FINE, "EPICS Info for {0}: {1}", new Object[] { name, info });
        return info;
    }
}
