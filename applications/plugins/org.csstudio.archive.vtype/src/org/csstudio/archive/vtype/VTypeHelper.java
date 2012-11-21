/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import java.text.Format;

import org.epics.pvmanager.data.Alarm;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.Time;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VNumber;
import org.epics.pvmanager.data.VNumberArray;
import org.epics.pvmanager.data.VString;
import org.epics.pvmanager.data.VType;
import org.epics.pvmanager.data.ValueUtil;
import org.epics.util.array.ListNumber;
import org.epics.util.time.Timestamp;
import org.epics.util.time.TimestampFormat;

/** {@link VType} helper
 *  @author Kay Kasemir
 */
public class VTypeHelper
{
	/** Number of array elements to show before shortening the printout */
	final private static int MAX_ARRAY_ELEMENTS = 10;
	
	/** Time stamp format */
	final private static Format time_format = new TimestampFormat("yyyy-MM-dd HH:mm:ss.NNNNNNNNN");

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

    /** Decode a {@link VType}'s time stamp
	 *  @param value Value to decode
	 *  @return {@link Timestamp}
	 */
	final public static Timestamp getTimestamp(final VType value)
	{
	    final Time time = ValueUtil.timeOf(value);
	    if (time != null  &&  time.isTimeValid())
	        return time.getTimestamp();
	    return Timestamp.now();
	}
	
	/** @param buf Buffer where value's time stamp is added
	 *  @param value {@link VType}
	 */
	final public static void addTimestamp(final StringBuilder buf, final VType value)
	{
		final Timestamp stamp = getTimestamp(value);
		synchronized (time_format)
		{
			buf.append(time_format.format(stamp));
		}
	}
    
	/** @param buf Buffer where value's alarm info is added (unless OK)
	 *  @param value {@link VType}
	 */
	final public static void addAlarm(final StringBuilder buf, final VType value)
	{
		final Alarm alarm = ValueUtil.alarmOf(value);
		if (alarm == null  ||  alarm.getAlarmSeverity() == AlarmSeverity.NONE)
			return;
		buf.append(alarm.getAlarmSeverity().toString())
       	   .append("/")
       	   .append(alarm.getAlarmName());
	}

	/** @param buf Buffer where number is added
	 *  @param display Display information to use, may be <code>null</code>
	 *  @param number Number to format
	 */
	final private static void addNumber(final StringBuilder buf,
			final Display display, final double number)
	{
		if (display != null  &&  display.getFormat() != null)
			buf.append(display.getFormat().format(number));
		else
			buf.append(number);
	}

	/** @param buf Buffer where value's actual value is added (number, ...)
	 *  @param value {@link VType}
	 */
	final public static void addValue(final StringBuilder buf, final VType value)
	{
		// After the time this is implemented, VEnum may change into a class
		// that also implements VNumber and/or VString.
		// Handle it first to assert that VEnum is always identified as such
		// and not handled as Number.
		if (value instanceof VEnum)
		{
			final VEnum enumerated = (VEnum)value;
			try
			{
				buf.append(enumerated.getValue());
				buf.append(" (").append(enumerated.getIndex()).append(")");
			}
			catch (ArrayIndexOutOfBoundsException ex)
			{	// Error getting label for invalid index?
				buf.append("<enum ").append(enumerated.getIndex()).append(">");
			}
		}
		else if (value instanceof VNumber)
		{
			final VNumber number = (VNumber) value;
			final Display display = ValueUtil.displayOf(number);
			addNumber(buf, display, number.getValue().doubleValue());
			if (display.getUnits() != null)
				buf.append(" ").append(display.getUnits());
		}
		else if (value instanceof VNumberArray)
		{
			final VNumberArray array = (VNumberArray) value;
			final Display display = ValueUtil.displayOf(array);
			final ListNumber list = array.getData();
			final int N = list.size();
			if (N <= MAX_ARRAY_ELEMENTS)
			{
				if (N > 0)
					addNumber(buf, display, list.getDouble(0));
				for (int i=1; i<N; ++i)
				{
					buf.append(", ");
					addNumber(buf, display, list.getDouble(i));
				}
			}
			else
			{
				addNumber(buf, display, list.getDouble(0));
				for (int i=1; i<MAX_ARRAY_ELEMENTS/2; ++i)
				{
					buf.append(", ");
					addNumber(buf, display, list.getDouble(i));
				}
				buf.append(", ... (total ").append(N).append(" elements) ...");
				for (int i = N - MAX_ARRAY_ELEMENTS/2;  i<N;  ++i)
				{
					buf.append(", ");
					addNumber(buf, display, list.getDouble(i));
				}
			}
			if (display.getUnits() != null)
				buf.append(" ").append(display.getUnits());
		}
		else if (value instanceof VString)
			buf.append(((VString)value).getValue());
		else if (value == null)
			buf.append("null");
		else
			buf.append(value.toString());
	}

	/** Format value as string
     *  @param value Value
     *  @return String representation
     */
    final public static String toString(final VType value)
    {
    	final StringBuilder buf = new StringBuilder();
    	addTimestamp(buf, value);
    	buf.append("\t");
    	addValue(buf, value);
    	buf.append("\t");
    	addAlarm(buf, value);
        return buf.toString();
    }
}
