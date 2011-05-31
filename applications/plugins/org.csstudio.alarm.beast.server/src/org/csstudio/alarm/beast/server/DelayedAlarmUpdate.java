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
import java.util.logging.Level;

/** Helper for checking alarms after a delay.
 *  It will trigger a transition to a new state only after a delay.
 *
 *  After the delay, it will invoke the listener.
 *
 *  While the timer is running, the state might be updated,
 *  for example to a higher latched state.
 *
 *  The check can also be canceled because the control system sent an 'OK'
 *  value in time.
 */
@SuppressWarnings("nls")
public class DelayedAlarmUpdate
{
    final private static Timer timer = new Timer("DelayedAlarmUpdate", true);

    /** Listener to notify when delay expires */
    final private DelayedAlarmListener listener;

    /** Alarm state to which we would update after the delay, unless it clears in time */
    private AlarmState state;

    /** Timer task used to perform the delay */
    private TimerTask scheduled_task = null;

    /** Initialize
     *  @param listener Listener to notify when delay expires
     */
    DelayedAlarmUpdate(final DelayedAlarmListener listener)
    {
        this.listener = listener;
    }

    /** Schedule a delayed state update, or adjust the update that's already
     *  scheduled with the latest state information.
     *
     *  @param new_state State to which we would go if there was no delay
     *  @param seconds Delay to use if we need to add this to the timer.
     *                 Ignored when adjusting a pending update
     */
    void schedule_update(final AlarmState new_state, final int seconds)
    {
        // TODO Remove check when done debugging
        if (new_state == null)
        {
            new NullPointerException("DelayedAlarmUpdate with null").printStackTrace();
            return;
        }
        final TimerTask new_task;
        synchronized (this)
        {
            this.state = new_state;
            // Already scheduled?
            if (scheduled_task != null)
                return;
            // Schedule in timer
            new_task = new TimerTask()
            {
                @Override
                public void run()
                {
                    final AlarmState the_state;
                    synchronized (DelayedAlarmUpdate.this)
                    {
                        // Save state for call to listener, reset everything
                        the_state = state;
                        scheduled_task = null;
                        state = null;
                    }
                    if (the_state == null)
                    {
                        // Don't run because update was cancelled
                        return;
                    }
                    //  Re-evaluate alarm logic with the delayed state,
                    //  not allowing any further delays.
                    try
                    {
                        listener.delayedStateUpdate(the_state);
                    }
                    catch (Throwable ex)
                    {
                        Activator.getLogger().log(Level.SEVERE, "Error in delayed alarm update", ex);
                    }
                }
            };
            scheduled_task = new_task;
        }
        timer.schedule(new_task, seconds * 1000L);
    }

    /** @return Alarm state to which we'll go after the delay expires */
    synchronized AlarmState getState()
    {
        return state;
    }

    /** Cancel delayed alarm check because control system PV cleared.
     *  OK to call multiple times, even when nothing was scheduled.
     */
    public void cancel()
    {
        final TimerTask task;
        synchronized (this)
        {
            state = null;
            task = scheduled_task;
            scheduled_task = null;
        }
        if (task != null)
            task.cancel();
    }
}
