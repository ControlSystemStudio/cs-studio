/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

/** Listener to {@link ReadInfoJob}
 *  @author Kay Kasemir
 */
public interface ReadInfoJobListener
{
    /** Invoked when new GUI information has been received
     *  @param alarm Global alarm that has new info
     */
    public void receivedAlarmInfo(GlobalAlarm alarm);
}
