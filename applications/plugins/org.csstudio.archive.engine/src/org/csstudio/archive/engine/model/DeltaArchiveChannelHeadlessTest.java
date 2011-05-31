/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import static org.junit.Assert.*;

import org.csstudio.data.values.IValue;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.junit.Test;

/** [Headless] JUnit plug-in test of the DeltaArchiveChannel
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DeltaArchiveChannelHeadlessTest
{
    @Test
    public void testHandleNewValue() throws Exception
    {
        final PV pv = PVFactory.createPV("loc://demo");
        pv.start();
        pv.setValue(1.0);

        final DeltaArchiveChannel channel = new DeltaArchiveChannel("loc://demo", Enablement.Passive, 100, null, 1.01, 0.1);
        final SampleBuffer samples = channel.getSampleBuffer();
        channel.start();

        System.out.println("Initial sample:");
        assertEquals(1, dump(samples));

        // Need small delays to assert a new time stamp
        Thread.sleep(10);

        // Big Change
        pv.setValue(2.0);
        System.out.println("Big change:");
        assertEquals(1, dump(samples));

        Thread.sleep(10);

        // Small Change
        pv.setValue(2.05);
        System.out.println("Small change:");
        assertEquals(0, dump(samples));

        Thread.sleep(10);

        // Big Change
        pv.setValue(2.5);
        System.out.println("Big change:");
        assertEquals(1, dump(samples));

        channel.stop();
        pv.stop();
    }

    private int dump(final SampleBuffer samples)
    {
        final int count = samples.getQueueSize();
        IValue sample;
        while ((sample = samples.remove()) != null)
            System.out.println(sample);
        return count;
    }
}
