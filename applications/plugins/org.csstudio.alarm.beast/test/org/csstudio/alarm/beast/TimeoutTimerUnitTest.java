/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/** JUnit test of the TimeoutTimer
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimeoutTimerUnitTest
{
    protected int timed_out = 0;

    @Test
    public void testTimeoutTimer() throws Exception
    {
        final TimeoutTimer timeout = new TimeoutTimer(1000)
        {
            @Override
            protected void timeout()
            {
                ++timed_out;
                System.out.println("Received timeout");
            }
        };
        timeout.start();
        timeout.reset();
        Thread.sleep(500);
        assertEquals(0, timed_out);
        
        Thread.sleep(1000);
        // At this point, ~1500 ms should have elapsed, > 1000
        assertEquals(1, timed_out);

        // No more timeouts after we've once timed out
        Thread.sleep(2000);
        assertEquals(1, timed_out);
        
        System.out.println("No more timeouts.... Resetting");

        // Restart
        timeout.reset();
        Thread.sleep(500);
        assertEquals(1, timed_out);

        Thread.sleep(1000);
        // At this point, ~1500 ms should have elapsed, > 1000
        assertEquals(2, timed_out);
        
        // Stop
        timeout.reset();
        timeout.cancel();
        Thread.sleep(2000);
        assertEquals(2, timed_out);
        timeout.join();
        assertFalse(timeout.isAlive());
    }
}
