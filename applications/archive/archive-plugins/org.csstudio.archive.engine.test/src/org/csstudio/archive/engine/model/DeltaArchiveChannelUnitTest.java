/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVPool;
import org.csstudio.vtype.pv.local.LocalPVFactory;
import org.junit.Test;

/** JUnit test of the DeltaArchiveChannel
 * 
 *  <p>Depending on timing, this test does not always pass...
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DeltaArchiveChannelUnitTest
{
    private static final String PV_NAME = "loc://demo(0)";

    @Test(timeout=20000)
    public void testHandleNewValue() throws Exception
    {
        PVPool.addPVFactory(new LocalPVFactory());
    	
    	final PV pv = PVPool.getPV(PV_NAME);

        final DeltaArchiveChannel channel = new DeltaArchiveChannel(PV_NAME, Enablement.Passive, 100, null, 1.01, 0.1);
        final SampleBuffer samples = channel.getSampleBuffer();
        channel.start();
        SECONDS.sleep(2);

        pv.write(1.0);
        SECONDS.sleep(2);
        System.out.println("Initial sample(s):");
        int initial = TestHelper.dump(samples);
        assertTrue(initial == 1  ||  initial == 2);

        // Big Change
        pv.write(2.0);
        SECONDS.sleep(2);
        System.out.println("Big change:");
        assertEquals(1, TestHelper.dump(samples));

        // Small Change
        pv.write(2.05);
        SECONDS.sleep(2);
        System.out.println("Small change:");
        assertEquals(0, TestHelper.dump(samples));

        // Big Change
        pv.write(2.5);
        SECONDS.sleep(2);
        System.out.println("Big change:");
        assertEquals(1, TestHelper.dump(samples));

        channel.stop();
        PVPool.releasePV(pv);
    }
}
