/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;

/** Timer API tests: Use single timer for multiple time-out type checks.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimerQueueUnitTest
{
    static class Check extends TimerTask
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
            System.out.println(System.currentTimeMillis() + ": Check " + delay + " done.");
        }

        @Override
        public String toString()
        {
            return "Check after " + delay + " secs";
        }
    }
    
    @Test
    public void timerTest() throws Exception
    {
        final Timer timer = new Timer("Check Timer", true);
        System.out.println(System.currentTimeMillis() + ": Start!");
        new Check(timer, 1.0);
        new Check(timer, 2.0);
        new Check(timer, 3.0);
        
        Thread.sleep(5000);
        timer.cancel();
        timer.purge();
        
        System.out.println("Finished.");
    }
}
