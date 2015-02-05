/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

/** JUnit test of the {@link GlobalAlarmUpdate}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GlobalAlarmUpdateTest implements GlobalAlarmListener
{
    private int update_count = 0;
    private long last_update_millis;

    @Override
    public synchronized void updateGlobalState()
    {
        last_update_millis = System.currentTimeMillis();
        System.out.println(new Date() + ": Received update!");
        ++update_count;
        notifyAll();
    }

    @Test
    public void testGlobalAlarmUpdate() throws Exception
    {
        final GlobalAlarmUpdate timer = new GlobalAlarmUpdate(this);
        System.out.println(new Date() + ": Scheduling global alarm update ...");
        final long start_millis = System.currentTimeMillis();

        // Schedule update in 1 second
        timer.schedule_update(1);
        // Cancel, schedule again..
        timer.cancel();
        timer.schedule_update(1);
        timer.cancel();

        // Schedule the 'real' update in 2 secs
        timer.schedule_update(2);

        // Changing it to 1 or 0 secs has no effect once it's running
        // (not canceled nor expired)
        timer.schedule_update(1);
        timer.schedule_update(0);

        // Wait for update to happen
        synchronized (this)
        {
            for (int secs=0; update_count <= 0  &&  secs<4; ++secs)
                wait(1000);
            assertEquals(1, update_count);
            // Should be close to 2 seconds
            assertEquals(2.0, Math.abs((last_update_millis - start_millis)/1000.0), 0.5);
        }

        // There should be no more updates
        Thread.sleep(3000);
        synchronized (this)
        {
            assertEquals(1, update_count);
        }
    }
}
