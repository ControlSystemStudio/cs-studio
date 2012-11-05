/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import org.csstudio.alarm.beast.SeverityLevel;
import org.epics.pvmanager.data.Alarm;
import org.epics.pvmanager.data.Time;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VNumber;
import org.epics.pvmanager.data.VString;
import org.epics.pvmanager.data.VType;
import org.epics.pvmanager.data.ValueUtil;
import org.epics.util.time.Timestamp;

/** Helper for handling {@link VType}
 *  @author Kay Kasemir
 */
public class VTypeHelper
{
    /** Read number from a {@link VType}
     *  @param value Value
     *  @return double or NaN
     */
    final public static double toDouble(final VType value)
    {
        if (value instanceof VNumber)
            return ((VNumber)value).getValue().doubleValue();
        if (value instanceof VEnum)
            return ((VEnum)value).getIndex();
        return Double.NaN;
    }

    /** Format value as string
     *  @param value Value
     *  @return String representation
     */
    final public static String toString(final VType value)
    {
        if (value instanceof VNumber)
            return ((VNumber)value).getValue().toString();
        if (value instanceof VEnum)
            return ((VEnum)value).getValue();
        if (value instanceof VString)
            return ((VString)value).getValue();
        if (value == null)
            return "null";
        return value.toString();
    }
    
    /** Decode a {@link VType}'s severity
     *  @param value Value to decode
     *  @return {@link SeverityLevel}
     */
    final public static SeverityLevel decodeSeverity(final VType value)
    {
        final Alarm alarm = ValueUtil.alarmOf(value);
        if (alarm != null)
        {
            switch (alarm.getAlarmSeverity())
            {
            case MINOR:
                return SeverityLevel.MINOR;
            case MAJOR:
                return SeverityLevel.MAJOR;
            case INVALID:
                return SeverityLevel.INVALID;
            default:
                break;
            }
        }
        return SeverityLevel.OK;
    }

    /** Decode a {@link VType}'s severity
     *  @param value Value to decode
     *  @return Status message
     */
    final public static String getStatusMessage(final VType value)
    {
        final Alarm alarm = ValueUtil.alarmOf(value);
        if (alarm != null)
            return alarm.getAlarmName();
        return SeverityLevel.OK.getDisplayName();
    }

    /** Decode a {@link VType}'s time stamp
     *  @param value Value to decode
     *  @return {@link Timestamp}
     */
    final public static Timestamp getTimestamp(final VType value)
    {
        final Time time = ValueUtil.timeOf(value);
        if (time != null)
            return time.getTimestamp();
        return Timestamp.now();
    }
}
