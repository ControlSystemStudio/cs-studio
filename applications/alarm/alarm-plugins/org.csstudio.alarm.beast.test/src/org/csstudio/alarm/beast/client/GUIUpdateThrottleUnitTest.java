/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.client;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/** JUnit test of GUIUpdateThrottle
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GUIUpdateThrottleUnitTest
{
    private static final long INITIAL = 100;
    private static final long SUPPRESS = 1000;
    final AtomicInteger events = new AtomicInteger(0);

    @Test
    public void testThrottle() throws Exception
    {
        final GUIUpdateThrottle throttle =
            new GUIUpdateThrottle(INITIAL, SUPPRESS)
        {
            @Override
            protected void fire()
            {
                System.out.println("got event");
                events.incrementAndGet();
            }
        };
        throttle.start();

        // Start without events
        assertEquals(0, events.get());

        // One event goes through after small delay
        throttle.trigger();
        Thread.sleep(2*INITIAL);
        assertEquals(1, events.get());

        // Later, another one goes through
        Thread.sleep(2*SUPPRESS);
        throttle.trigger();
        Thread.sleep(2*INITIAL);
        assertEquals(2, events.get());

        // But the next N are delayed
        for (int i=0; i<50; ++i)
            throttle.trigger();
        assertEquals(2, events.get());

        // Later we we get one(!) other event for the N
        Thread.sleep(2*SUPPRESS);
        assertEquals(3, events.get());

        // Later, another one goes through
        throttle.trigger();
        Thread.sleep(2*INITIAL);
        assertEquals(4, events.get());

        // Cleanup
        System.out.println("Done?");
        throttle.dispose();
        // Wait for exit
        throttle.join(5000);
        assertEquals(false, throttle.isAlive());
        System.out.println("Done.");
    }
}
