/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Time;
import org.diirt.vtype.VType;
import org.diirt.util.time.Timestamp;

/** Base of archive-derived {@link VType} implementations
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ArchiveVType implements Alarm, Time, VType
{
    /** Alarm status message for 'OK' */
    final public static String STATUS_OK = "NO_ALARM";

    final private Timestamp timestamp;
    final private AlarmSeverity severity;
    final private String status;

    public ArchiveVType(final Timestamp timestamp,
            final AlarmSeverity severity, final String status)
    {
        this.timestamp = timestamp;
        this.severity = severity;
        this.status = status;
    }

    @Override
    public AlarmSeverity getAlarmSeverity()
    {
        return severity;
    }

    @Override
    public String getAlarmName()
    {
        return status;
    }

    @Override
    public Timestamp getTimestamp()
    {
        return timestamp;
    }

    @Override
    public Integer getTimeUserTag()
    {
        return 0;
    }

    @Override
    public boolean isTimeValid()
    {
        return timestamp.getSec() > 0;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = severity.hashCode();
        result = prime * result + status.hashCode();
        result = prime * result + timestamp.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;

        if (! (obj instanceof VType))
            return false;

        if (! (obj instanceof Alarm))
            return false;
        final Alarm alarm = (Alarm) obj;
        if (severity != alarm.getAlarmSeverity())
            return false;
        if (! status.equals(alarm.getAlarmName()))
            return false;

        if (! (obj instanceof Time))
            return false;

        final Time time = (Time) obj;
        return timestamp.equals(time.getTimestamp())
            && getTimeUserTag() == time.getTimeUserTag();
    }
}
