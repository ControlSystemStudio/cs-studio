/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import java.util.List;

import org.csstudio.alarm.beast.client.AlarmTreeLeaf;

/** Utility for turning list of alarms into text.
 * 
 *  <p>Used to send alarm info to email, elog, clipboard
 *  @author Kay Kasemir
 */
public class AlarmTextHelper
{
	/** @param alarms List of alarms
	 *  @return Text that describes the alarms
	 */
	public static String createAlarmInfoText(final List<AlarmTreeLeaf> alarms)
    {
        final StringBuilder selected_alarms = new StringBuilder();
        for (AlarmTreeLeaf alarm : alarms)
        {
            selected_alarms.append(alarm.getVerboseDescription());
            selected_alarms.append("\n\n"); //$NON-NLS-1$
        }
	    return selected_alarms.toString();
    }
}
