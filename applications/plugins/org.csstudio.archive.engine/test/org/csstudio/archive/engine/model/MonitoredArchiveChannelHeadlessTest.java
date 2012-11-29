/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import static org.junit.Assert.*;

import static org.epics.pvmanager.ExpressionLanguage.channel;

import org.csstudio.archive.vtype.VTypeHelper;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.data.VType;
import org.epics.pvmanager.loc.LocalDataSource;
import org.junit.Test;

/** [Headless] JUnit plug-in test of the {@link MonitoredArchiveChannel}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MonitoredArchiveChannelHeadlessTest
{
    private static final String PV_NAME = "loc://demo";

	@Test
    public void testHandleNewValue() throws Exception
    {
    	PVManager.setDefaultDataSource(new LocalDataSource());
    	
    	final PVWriter<Object> pv = PVManager.write(channel(PV_NAME)).sync();
    	
    	pv.write(1.0);

        final MonitoredArchiveChannel channel = new MonitoredArchiveChannel(PV_NAME, Enablement.Passive, 100, null, 0.1);
        final SampleBuffer samples = channel.getSampleBuffer();
        channel.start();

        // Create somewhat different time stamps by allowing a pause
        pv.write(2.0);
        Thread.sleep(100);
        pv.write(2.05);
        Thread.sleep(100);
        pv.write(2.5);
        
        Thread.sleep(4000);
        
        assertEquals(1, dump(samples));

        channel.stop();
        pv.close();
    }

    private int dump(final SampleBuffer samples)
    {
        final int count = samples.getQueueSize();
        VType sample;
        while ((sample = samples.remove()) != null)
            System.out.println(VTypeHelper.toString(sample));
        return count;
    }
}
