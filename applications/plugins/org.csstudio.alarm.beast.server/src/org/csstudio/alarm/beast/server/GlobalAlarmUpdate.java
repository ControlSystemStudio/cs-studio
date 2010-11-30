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

/** Helper for sending global alarm updates after a delay.
 *
 *  After the delay, it will invoke the listener.
 *
 *  The check can also be canceled because the alarm was ack'ed or cleared in time.
 */
public class GlobalAlarmUpdate
{
    final private static Timer timer = new Timer("GlobalAlarmUpdate", true); //$NON-NLS-1$

    /** Listener to notify when delay expires */
    final private GlobalAlarmListener listener;

    /** Timer task used to perform the delay */
    private TimerTask scheduled_task = null;

    /** Initialize
     *  @param listener Listener to notify when delay expires
     */
    GlobalAlarmUpdate(final GlobalAlarmListener listener)
    {
        this.listener = listener;
    }

    /** Schedule a state update.
     *  Has no effect if an update was already started,
     *  has not expired nor canceled.
     *
     *  @param seconds Delay to use
     */
    void schedule_update(final int seconds)
    {
        final TimerTask new_task;
        synchronized (this)
        {
            // Already scheduled?
            if (scheduled_task != null)
                return;
            // Schedule in timer
            new_task = new TimerTask()
            {
                @Override
                public void run()
                {
                    synchronized (GlobalAlarmUpdate.this)
                    {
                        scheduled_task = null;
                    }
                    //  Re-evaluate alarm logic with the delayed state,
                    //  not allowing any further delays.
                    listener.updateGlobalState();
                }
            };
            scheduled_task = new_task;
        }
        timer.schedule(new_task, seconds * 1000L);
    }

    /** Cancel delayed alarm check because control system PV cleared.
     *  OK to call multiple times, even when nothing was scheduled.
     */
    public void cancel()
    {
        final TimerTask task;
        synchronized (this)
        {
            task = scheduled_task;
            scheduled_task = null;
        }
        if (task != null)
            task.cancel();
    }
}
