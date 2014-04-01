/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVPool;
import org.csstudio.vtype.pv.local.LocalPVFactory;
import org.junit.Test;

/** JUnit test of the {@link ScannedArchiveChannel}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScannedArchiveChannelUnitTest
{
    private static final String PV_NAME = "loc://demo(42)";

	@Test
    public void testHandleNewValue() throws Exception
    {
	    PVPool.addPVFactory(new LocalPVFactory());
    	
    	final PV pv = PVPool.getPV(PV_NAME);
        final ScannedArchiveChannel channel = new ScannedArchiveChannel(PV_NAME, Enablement.Passive, 5, null, 1.0, 2);
        final SampleBuffer samples = channel.getSampleBuffer();
        channel.start();
        
        // Might get initial sample from PV, dump it
        SECONDS.sleep(2);
        TestHelper.dump(samples);

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
        PVPool.releasePV(pv);
    }
}
