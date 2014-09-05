/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Time;
import org.epics.vtype.VEnum;
import org.epics.vtype.VNumber;
import org.epics.vtype.VString;
import org.epics.vtype.VType;
import org.epics.vtype.ValueUtil;
import org.epics.util.time.Timestamp;

/** Helper for handling {@link VType} data
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class VTypeHelper
{
    /** Decode a {@link VType}'s time stamp
     *  @param value Value to decode
     *  @return {@link Timestamp}
     */
    final public static Timestamp getTimestamp(final VType value)
    {
        if (value instanceof Time)
        {
            final Time time = (Time) value;
            if (time.isTimeValid())
                return time.getTimestamp();
        }
        return Timestamp.now();
    }

    /** @param value {@link VType} value
     *  @return {@link AlarmSeverity}
     */
    final public static AlarmSeverity getSeverity(final VType value)
    {
        final Alarm alarm = ValueUtil.alarmOf(value);
        if (alarm == null)
            return AlarmSeverity.NONE;
        return alarm.getAlarmSeverity();
    }

    /** @param value {@link VType} value
     *  @return Alarm message
     */
    final public static String getMessage(final VType value)
    {
        final Alarm alarm = ValueUtil.alarmOf(value);
        if (alarm == null)
            return "";
        return alarm.getAlarmName();
    }
    
    /** @param value {@link VType}
     *  @return Alarm text or ""
     */
    final public static String formatAlarm(final VType value)
    {
        final Alarm alarm = ValueUtil.alarmOf(value);
        if (alarm == null  ||  alarm.getAlarmSeverity() == AlarmSeverity.NONE)
            return "";
        return alarm.getAlarmSeverity().toString()
                + "/" + alarm.getAlarmName();
    }
    
    /** Format value as string
     *  @param value {@link VType}
     *  @return String representation
     */
    final public static String toString(final VType value)
    {
        if (value instanceof VNumber)
            return ((VNumber)value).getValue().toString();
        if (value instanceof VEnum)
        {
            final VEnum ev = (VEnum) value;
            try
            {
                return ev.getIndex() + " '" + ev.getValue() + "'";
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
    

    /** Get VType as double or NaN if not possible
     *  @param value {@link VType}
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

    
    /** Compare the values of to {@link VType}s
     *  @param value {@link VType}
     *  @param other {@link VType}
     *  @param tolerance Numeric tolerance. Values must be within that tolerance. 0 for 'exactly equal'.
     *  @return <code>true</code> if their value (not timestamp, not alarm state) are equal
     */
    final public static boolean equalValue(final VType value, final VType other, final double tolerance)
    {
        if (value instanceof VString)
            return toString(value).equals(toString(other));
        final double v1 = toDouble(value);
        final double v2 = toDouble(other);
        return Math.abs(v2 - v1) <= tolerance;
    }

    /** Extract basic value
     *  @param value {@link VType}
     *  @return {@link Number} or {@link String} of the {@link VType}'s value
     */
    public static Object getValue(final VType value)
    {
        if (value instanceof VNumber)
            return ((VNumber)value).getValue();
        if (value instanceof VEnum)
            return ((VEnum)value).getIndex();
        if (value instanceof VString)
            return ((VString)value).getValue();
        return null;
    }
}
