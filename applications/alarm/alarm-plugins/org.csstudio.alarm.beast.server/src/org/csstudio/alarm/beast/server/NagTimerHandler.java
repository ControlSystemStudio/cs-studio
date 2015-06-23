/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

/** Interface used by {@link NagTimer}
 *  to query active alarms and send notification
 *
 *  @author Kay Kasemir
 */
public interface NagTimerHandler
{
    /** @return Number of active alarms */
    public int getActiveAlarmCount();

    /** Invoked by {@link NagTimer} when there are active alarms
     *  @param active Number of active alarms
     */
    public void nagAboutActiveAlarms(int active);
}
