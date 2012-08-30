/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import java.text.NumberFormat;

import org.epics.pvmanager.data.Alarm;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VNumber;
import org.epics.pvmanager.data.VString;
import org.epics.pvmanager.data.VType;
import org.epics.pvmanager.data.ValueUtil;

/** Helper for {@link VType} gymnastics
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class VTypeHelper
{
    public static AlarmSeverity getSeverity(final VType value)
    {
        final Alarm alarm = ValueUtil.alarmOf(value);
        if (alarm == null)
            return AlarmSeverity.UNDEFINED;
        return alarm.getAlarmSeverity();
    }

    public static void appendValue(final StringBuilder buf, final VType value)
    {
        if (value == null)
            buf.append("null");
        else if (value instanceof VNumber)
        {
            final VNumber number = (VNumber) value;

            final NumberFormat format = number.getFormat();
            if (format == null)
                buf.append(number.getValue());
            else
                buf.append(format.format(number.getValue()));

            final String units = number.getUnits();
            if (units != null  &&  !units.isEmpty())
                buf.append(" ").append(units);
        }
        else if (value instanceof VString)
        {
            final VString text = (VString) value;
            buf.append(text.getValue());
        }
        else if (value instanceof VEnum)
        {
            final VEnum item = (VEnum) value;
            buf.append(item.getValue()).append(" [").append(item.getIndex()).append("]");

        }
        else
            buf.append(value.getClass().getName());
    }

    public static void appendAlarm(final StringBuilder buf, final VType value)
    {
        final Alarm alarm = ValueUtil.alarmOf(value);
        if (alarm == null)
            return;
        if (alarm.getAlarmSeverity() == AlarmSeverity.NONE)
            return;
        buf.append("\t").append(alarm.getAlarmSeverity());
        buf.append("/").append(alarm.getAlarmStatus());
    }

    public static String formatValue(final VType value)
    {
        final StringBuilder buf = new StringBuilder();
        appendValue(buf, value);
        return buf.toString();
    }

    public static String format(final VType value)
    {
        final StringBuilder buf = new StringBuilder();
        appendValue(buf, value);
        appendAlarm(buf, value);
        return buf.toString();
    }
}
