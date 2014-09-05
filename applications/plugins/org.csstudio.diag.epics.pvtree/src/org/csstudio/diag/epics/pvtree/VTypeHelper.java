/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import java.text.NumberFormat;

import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VEnum;
import org.epics.vtype.VNumber;
import org.epics.vtype.VString;
import org.epics.vtype.VType;
import org.epics.vtype.ValueUtil;

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
            try
            {   // org.epics.pvmanager.jca.VStringFromDbr.getValue(VStringFromDbr.java:39) can create NullPointerException
                buf.append(text.getValue());
            }
            catch (NullPointerException ex)
            {
                buf.append("'null'");
            }
        }
        else if (value instanceof VEnum)
        {
            final VEnum item = (VEnum) value;
        	try
        	{
                buf.append(item.getValue()).append(" ");
        	}
        	catch (ArrayIndexOutOfBoundsException ex)
        	{
        		// PVManager doesn't handle enums that have no label. Ignore
        	}
            buf.append("(").append(item.getIndex()).append(")");
        }
        else
            buf.append(value.toString());
    }

    public static void appendAlarm(final StringBuilder buf, final VType value)
    {
        final Alarm alarm = ValueUtil.alarmOf(value);
        if (alarm == null)
            return;
        if (alarm.getAlarmSeverity() == AlarmSeverity.NONE)
            return;
        buf.append(" [").append(alarm.getAlarmSeverity());
        buf.append(",").append(alarm.getAlarmName()).append("]");
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
        // If there is no value, suppress the alarm.
        // TODO Check note in PVTreeItem#updateLinks():
        // If a link is empty, the record could still be in alarm.
        // So in here we must NOT return "'' [MINOR/Whatever]" for
        // an empty value ('') with alarm, because updateLinks()
        // would consider that overall a non-empty string,
        // and the tree item would appear.
        // So in here we suppress the alarm for empty values,
        // but in other cases the empty value could well be
        // a valid alarm to display...
        if (buf.length() > 0)
        	appendAlarm(buf, value);
        return buf.toString();
    }
}
