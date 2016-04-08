/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import java.time.Instant;

import org.csstudio.alarm.beast.SeverityLevel;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.Time;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VString;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueUtil;

/** Helper for handling {@link VType}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
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
        {
            try
            {
                return ((VEnum)value).getValue();
            }
            catch (ArrayIndexOutOfBoundsException ex)
            {    // PVManager doesn't handle enums that have no label
                return "<enum " + ((VEnum)value).getIndex() + ">";
            }
        }
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
        if (alarm == null)
            return SeverityLevel.OK;
        switch (alarm.getAlarmSeverity())
        {
        case NONE:
            return SeverityLevel.OK;
        case MINOR:
            return SeverityLevel.MINOR;
        case MAJOR:
            return SeverityLevel.MAJOR;
        case INVALID:
            return SeverityLevel.INVALID;
        default:
            return SeverityLevel.UNDEFINED;
        }
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
     *  @return {@link Instant}
     */
    final public static Instant getTimestamp(final VType value)
    {
        final Time time = ValueUtil.timeOf(value);
        if (time != null  &&  time.isTimeValid())
            return time.getTimestamp();
        return Instant.now();
    }
}
