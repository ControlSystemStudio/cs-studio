/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import org.csstudio.alarm.beast.AlarmTreeItem;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.platform.data.ITimestamp;

/** A 'global' alarm
 *
 *  @author Kay Kasemir
 */
public class GlobalAlarm extends AlarmTreeItem
{
    // Similar to the AlarmTreePV, but doesn't track 'current' state,
    // only 'alarm' state
    GlobalAlarm(final AlarmTreeItem parent, final String name, final int id,
            final SeverityLevel severity, final String message, final ITimestamp timestamp)
    {
        super(parent, name, id);

        setAlarmState(severity, severity, message);
        if (parent != null)
            parent.maximizeSeverity(null);
    }
}
