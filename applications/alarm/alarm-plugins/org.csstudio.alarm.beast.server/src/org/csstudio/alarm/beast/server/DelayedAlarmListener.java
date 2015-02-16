/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

/** Listener for the {@link DelayedAlarmUpdate}
 *  @author Kay Kasemir
 */
public interface DelayedAlarmListener
{
    /** Invoked when the DelayedAlarmUpdate expires
     *   @param delayed_state State that was delayed
     */
    public void delayedStateUpdate(AlarmState delayed_state);
}
