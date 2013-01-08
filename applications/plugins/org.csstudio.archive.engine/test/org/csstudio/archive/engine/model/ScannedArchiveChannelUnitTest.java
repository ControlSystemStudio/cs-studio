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
import static java.util.concurrent.TimeUnit.SECONDS;

import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.loc.LocalDataSource;
import org.junit.Test;

/** JUnit test of the {@link ScannedArchiveChannel}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScannedArchiveChannelUnitTest
{
    private static final String PV_NAME = "loc://demo";

	@Test
    public void testHandleNewValue() throws Exception
    {
    	PVManager.setDefaultDataSource(new LocalDataSource());
    	
    	final PVWriter<Object> pv = PVManager.write(channel(PV_NAME)).sync();
        final ScannedArchiveChannel channel = new ScannedArchiveChannel(PV_NAME, Enablement.Passive, 5, null, 1.0, 2);
        final SampleBuffer samples = channel.getSampleBuffer();
        channel.start();

        // Simulated received value
        System.out.println("Initial value 1.0");
    	pv.write(1.0);
    	SECONDS.sleep(2);
    	
    	// Scan the channel
    	channel.run();
        assertThat(TestHelper.dump(samples), equalTo(1));

        // Intermediate value between scans
        System.out.println("Intermediate 1.5");
        pv.write(1.5);
    	SECONDS.sleep(2);
    	// Value that will be scanned
        System.out.println("Scanned 2.0");
    	pv.write(2.0);
    	SECONDS.sleep(2);
    	channel.run();
        assertThat(TestHelper.dump(samples), equalTo(1));
        
        // Repeats
        for (int repeat=0; repeat<4; ++repeat)
        {
        	System.out.println("Repeated 2.0");
	    	pv.write(2.0);
	    	SECONDS.sleep(2);
	    	channel.run();
        }
        assertThat(TestHelper.dump(samples), equalTo(2));
        
        channel.stop();
        pv.close();
    }
}
