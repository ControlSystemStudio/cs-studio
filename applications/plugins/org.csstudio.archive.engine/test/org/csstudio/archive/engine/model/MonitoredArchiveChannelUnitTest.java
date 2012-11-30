/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.loc.LocalDataSource;
import org.junit.Test;

/** JUnit plug-in test of the {@link MonitoredArchiveChannel}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MonitoredArchiveChannelUnitTest
{
    private static final String PV_NAME = "loc://demo";

	@Test
    public void testHandleNewValue() throws Exception
    {
    	PVManager.setDefaultDataSource(new LocalDataSource());
    	
    	final PVWriter<Object> pv = PVManager.write(channel(PV_NAME)).sync();
        final MonitoredArchiveChannel channel = new MonitoredArchiveChannel(PV_NAME, Enablement.Passive, 100, null, 0.1);
        final SampleBuffer samples = channel.getSampleBuffer();
        channel.start();

    	pv.write(1.0);
        pv.write(2.0);
        pv.write(2.05);
        pv.write(2.5);

        // Allow monitors to arrive..
        Thread.sleep(2000);
        
        assertThat(TestHelper.dump(samples), equalTo(4));

        channel.stop();
        pv.close();
    }
}
