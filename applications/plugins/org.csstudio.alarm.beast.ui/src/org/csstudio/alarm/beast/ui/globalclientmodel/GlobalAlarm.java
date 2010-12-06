/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmUpdateInfo;
import org.csstudio.platform.data.ITimestamp;

/** A 'global' alarm
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GlobalAlarm
{
    final private String path;
    final private SeverityLevel severity;
    final private String message;
    final private ITimestamp timestamp;

    /** Initialize
     *  @param info AlarmUpdateInfo with details
     */
    public GlobalAlarm(final AlarmUpdateInfo info)
    {
        path = info.getNameOrPath();
        severity = info.getSeverity();
        message = info.getMessage();
        timestamp = info.getTimestamp();
    }

    public String getPath()
    {
        return path;
    }

    public SeverityLevel getSeverity()
    {
        return severity;
    }

    public String getMessage()
    {
        return message;
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return "GlobalAlarm: " + timestamp + " '" + path + "' (" + severity.getDisplayName() + "/" + message + ")";
    }
}
