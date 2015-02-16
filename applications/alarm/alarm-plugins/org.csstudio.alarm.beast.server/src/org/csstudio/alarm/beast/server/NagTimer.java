/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import org.csstudio.alarm.beast.TimeoutTimer;

/** Timer that 'nags' when there are active alarms
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class NagTimer extends TimeoutTimer
{
    final private NagTimerHandler listener;

    /** Initialize
     *  @param period_ms Period between nags in milliseconds
     *  @param listener Who to notify
     */
    public NagTimer(final long period_ms, final NagTimerHandler listener)
    {
        super("Nag Timer", period_ms);
        this.listener = listener;
    }

    /** Check for active alarms, issue 'nag' */
    @Override
    protected void timeout()
    {
        final int active = listener.getActiveAlarmCount();
        if (active > 0)
            listener.nagAboutActiveAlarms(active);

        // Allow another time out
        reset();
    }
}
