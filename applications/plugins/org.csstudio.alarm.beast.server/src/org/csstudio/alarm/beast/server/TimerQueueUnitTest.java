/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

/** Timer API tests: Use single timer for multiple time-out type checks.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimerQueueUnitTest
{
    final private StringBuilder buf = new StringBuilder();

    class Check extends TimerTask
    {
        final double delay;

        public Check(final Timer timer, final double delay)
        {
            this.delay = delay;
            timer.schedule(this, (long) (delay*1000));
        }

        @Override
        public void run()
        {
            System.out.println(new Date() + ": Check " + delay + " done.");
            synchronized (buf)
            {
                buf.append(delay + " done\n");
            }
        }

        @Override
        public String toString()
        {
            return "Check after " + delay + " secs";
        }
    }

    /** Basic timer test/demo that includes cancellation */
    // @Ignore
    @Test
    public void timerTest() throws Exception
    {
        final Timer timer = new Timer("Check Timer", true);
        System.out.println(new Date() + ": Start!");

        // Start 3 times
        new Check(timer, 1.0);
        new Check(timer, 2.0);
        final TimerTask last = new Check(timer, 3.0);

        // Stop 3rd timer before it expires
        Thread.sleep(2000);
        last.cancel();

        // First 2 timers should have run, but not the last
        Thread.sleep(1000);
        final String result;
        synchronized (buf)
        {
            result = buf.toString();
        }
        assertEquals("1.0 done\n2.0 done\n", result);

        // Cleanup
        timer.cancel();
        timer.purge();

        System.out.println("Finished.");
    }

    /** Comparison of scheduling delays on 'Timer' vs. 'ScheduledExecutorService'.
     *
     *  ScheduledExecutorService is the new API, maybe meant to replace the Timer.
     *
     *  In JProfiler, however, Timer.schedule() uses less than half the CPU of Executor.schedule().
     */
    @Ignore
    @Test
    public void runBothTimerTypes() throws Exception
    {
        final Timer timer = new Timer();
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

        // Schedule stuff on timer in one thread
        final Thread timer_thread = new Thread("TimerTest")
        {
            @Override
            public void run()
            {
                int i = 0;
                while (true)
                {
                    ++i;
                    final int num = i;
                    timer.schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            System.out.println("Timer " + num);
                        }
                    }, 100);
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        // Ignore
                    }
                }
            }
        };

        // Schedule stuff on scheduled executer in other thread
        final Thread executor_thread = new Thread("ExecutorTest")
        {
            @Override
            public void run()
            {
                int i = 0;
                while (true)
                {
                    ++i;
                    final int num = i;
                    executor.schedule(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            System.out.println("Sched. " + num);
                        }
                    }, 100, TimeUnit.MILLISECONDS);
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        // Ignore
                    }
                }
            }
        };

        timer_thread.start();
        executor_thread.start();

        // Run forever
        timer_thread.join();
        executor_thread.join();
    }
}
