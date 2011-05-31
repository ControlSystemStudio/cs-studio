/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace.gui;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/** JUnit test of GUIUpdateThrottle
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GUIUpdateThrottleTest
{
    private static final long INITIAL = 100;
    private static final long SUPPRESS = 1000;
    final private AtomicInteger events = new AtomicInteger(0);
    private volatile String last_item = null;
    
    @Test
    public void testThrottle() throws Exception
    {
        final GUIUpdateThrottle<String> throttle =
            new GUIUpdateThrottle<String>(INITIAL, SUPPRESS)
        {
            @Override
            protected void update(final String item)
            {
                if (item == null)
                    System.out.println("got burst update");
                else
                    System.out.println("got update for " + item);
                events.incrementAndGet();
                last_item = item;
            }
        };
        
        // Start without events
        assertEquals(0, events.get());
        
        // One event goes through after small delay
        throttle.trigger("Initial");
        Thread.sleep(2*INITIAL);
        assertEquals(1, events.get());
        assertEquals("Initial", last_item);
        
        // Later, another one goes through
        Thread.sleep(2*SUPPRESS);
        throttle.trigger("Another single event");
        Thread.sleep(2*INITIAL);
        assertEquals(2, events.get());
        assertEquals("Another single event", last_item);

        // But the next N are delayed
        for (int i=0; i<50; ++i)
            throttle.trigger("part of burst update");
        assertEquals(2, events.get());
        
        // Later we we get one(!) other event for the N
        Thread.sleep(2*SUPPRESS);
        assertEquals(3, events.get());
        // ... but with no specific item, since it was a burst
        assertNull(last_item);

        // Later, another one goes through
        throttle.trigger("final");
        Thread.sleep(2*INITIAL);
        assertEquals(4, events.get());
        assertEquals("final", last_item);
    }
}
