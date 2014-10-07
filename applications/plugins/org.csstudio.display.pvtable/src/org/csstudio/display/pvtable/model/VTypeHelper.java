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
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VEnum;
import org.epics.vtype.VFloatArray;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VString;
import org.epics.vtype.VType;
import org.epics.vtype.ValueUtil;
import org.epics.util.array.IteratorNumber;
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
    
    /** Format value as string for display
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
        if (value instanceof VDoubleArray  ||  value instanceof VFloatArray)
        {   // Show double arrays as floating point
            final StringBuilder buf = new StringBuilder();
            final IteratorNumber numbers =  ((VNumberArray)value).getData().iterator();
            if (numbers.hasNext())
                buf.append(numbers.nextDouble());
            while (numbers.hasNext())
                buf.append(", ").append(numbers.nextDouble());
            return buf.toString();
        }
        if (value instanceof VNumberArray)
        {   // Show other number arrays as integer
            final StringBuilder buf = new StringBuilder();
            final IteratorNumber numbers =  ((VNumberArray)value).getData().iterator();
            if (numbers.hasNext())
                buf.append(numbers.nextLong());
            while (numbers.hasNext())
                buf.append(", ").append(numbers.nextLong());
            return buf.toString();
        }
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
