/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import static org.junit.Assert.assertEquals;

import org.csstudio.apputil.time.BenchmarkTimer;
import org.junit.Test;

/** JUnit test of the NagTimer
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class NagTimerUnitTest implements NagTimerHandler
{
    private volatile int active = 2;
    private int nags = 0;

    @Override
    public int getActiveAlarmCount()
    {
        return active;
    }

    @Override
    public void nagAboutActiveAlarms(final int active)
    {
        System.out.println("There are " + active + " active alarms");
        synchronized (this)
        {
            ++nags;
            notifyAll();
        }
    }

    @Test(timeout=8000)
    public void testNagTimer() throws Exception
    {
        final NagTimer nag_timer = new NagTimer(1000, this);
        nag_timer.start();
        BenchmarkTimer t = new BenchmarkTimer();

        synchronized (this)
        {
            // Initially, nothing
            assertEquals(0, nags);

            // Expect nag after one second
            wait(1100);
            assertEquals(1, nags);
        }
        t.stop();
        System.out.println("Time: " + t);
        assertEquals(1.0, t.getSeconds(), 0.1);

        // No more if we keep resetting
        Thread.sleep(500);
        nag_timer.reset();
        Thread.sleep(500);
        nag_timer.reset();
        Thread.sleep(500);
        nag_timer.reset();

        // But then time out again
        t.start();
        synchronized (this)
        {
            // Initially, nothing new
            assertEquals(1, nags);

            // Expect nag after one second
            wait(1100);
            assertEquals(2, nags);
        }
        t.stop();
        System.out.println("Time: " + t);
        assertEquals(1.0, t.getSeconds(), 0.1);

        // .. and again
        t.start();
        synchronized (this)
        {
            wait(1100);
            assertEquals(3, nags);
        }
        t.stop();
        System.out.println("Time: " + t);
        assertEquals(1.0, t.getSeconds(), 0.1);

        // Clear all alarms
        active = 0;
        Thread.sleep(1100);
        synchronized (this)
        {   // Should not see another
            assertEquals(3, nags);
        }

        // and then again have alarms
        active = 1;
        Thread.sleep(1100);
        synchronized (this)
        {   // Should have received another
            assertEquals(4, nags);
        }

        nag_timer.cancel();
    }
}
