/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import java.util.logging.Level;

import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.archive.engine.Activator;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.archive.vtype.VTypeHelper;
import org.diirt.util.array.ListNumber;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VString;
import org.diirt.vtype.VType;

/** An ArchiveChannel that stores value in a periodic scan.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScannedArchiveChannel extends ArchiveChannel implements Runnable
{
    /** Scan period in seconds */
    final private double scan_period;
    final private int max_repeats;
    private int repeats = 0;

    /** @see ArchiveChannel#ArchiveChannel(String, int, IValue)
     * @deprecated Use {@link #ScannedArchiveChannel(String,String,Enablement,int,VType,double,int)} instead*/
    public ScannedArchiveChannel(final String name,
                                 final Enablement enablement,
                                 final int buffer_capacity,
                                 final VType last_archived_value,
                                 final double scan_period,
                                 final int max_repeats) throws Exception
    {
        this(name, null, enablement, buffer_capacity, last_archived_value, scan_period, max_repeats);
    }

    /** @see ArchiveChannel#ArchiveChannel(String, String, int, IValue) */
    public ScannedArchiveChannel(final String name,
                                 final String retention,
                                 final Enablement enablement,
                                 final int buffer_capacity,
                                 final VType last_archived_value,
                                 final double scan_period,
                                 final int max_repeats) throws Exception
    {
        super(name, retention, enablement, buffer_capacity, last_archived_value);
        this.scan_period = scan_period;
        this.max_repeats = max_repeats;
    }

    /** @return Scan period in seconds */
    final public double getPeriod()
    {
        return scan_period;
    }

    @Override
    public String getMechanism()
    {
        return PeriodFormat.formatSeconds(scan_period) + " scan, max. "
            + max_repeats + " repeats";
    }

    // Just for debugging...
    @Override
    protected boolean handleNewValue(final VType value)
    {
        final boolean written = super.handleNewValue(value);
        if (! written)
            Activator.getLogger().log(Level.FINE, "{0} cached {1}", new Object[] { getName(), value });
        return written;
    }

    /** Invoked by periodic scanner.
     *  Try to add the most recent value to the archive.
     *  Skip repeated values, unless we exceed the max. repeat count.
     */
    @Override
    final public void run()
    {
        if (! isEnabled())
            return;
        final VType value;
        synchronized (this)
        {   // Have anything?
            if (most_recent_value == null)
            {
                Activator.getLogger().log(Level.FINE, "scan {0}: No data", getName());
                return;
            }
            // Is it a new value?
            if (isMatchingValue(last_archived_value, most_recent_value))
            {
                ++repeats ;
                if (repeats < max_repeats)
                {
                    Activator.getLogger().log(Level.FINE, "{0} skips {1}: repeat {2}", new Object[] { getName(), VTypeHelper.toString(most_recent_value), repeats });
                    return;
                }
                // No new value, but we'd like to write a sample every once in a while
                value = VTypeHelper.transformTimestampToNow(most_recent_value);
                if (value == null)
                {
                    Activator.getLogger().log(Level.WARNING, "{0} cannot handle value type {1}",
                            new Object[] { getName(), most_recent_value.getClass().getName() });
                    return;
                }
                Activator.getLogger().log(Level.FINE, "{0} writes {1} as {2}",
                    new Object[] { getName(), VTypeHelper.toString(most_recent_value), TimestampHelper.format(VTypeHelper.getTimestamp(value)) });
            }
            else
            {   // It's a new value, so we should be able to write it
                // "as is"
                value = most_recent_value;
                Activator.getLogger().log(Level.FINE, "Wrote sample for {0}: {1}", new Object[] { getName(), value });
            }
            // New value, or exceeded repeats
            repeats = 0;
        }
        // unlocked, should have 'value'
        addValueToBuffer(value);
    }

    /** Check if values match in status, severity, and value. Time is ignored.
     *  @param val1 One value
     *  @param val2 Other value
     *  @return <code>true</code> if they match
     */
    private boolean isMatchingValue(final VType val1, final VType val2)
    {
        // Compare data type and value
        if (val1 instanceof VNumber)
        {
            if (! (val2 instanceof VNumber))
                return false;
            final double v1 = ((VNumber) val1).getValue().doubleValue();
            final double v2 = ((VNumber) val2).getValue().doubleValue();
            if (Double.doubleToLongBits(v1) != Double.doubleToLongBits(v2))
                return false;
        }
        else if (val1 instanceof VNumberArray)
        {
            if (! (val2 instanceof VNumberArray))
                return false;
            final ListNumber n1 = ((VNumberArray) val1).getData();
            final ListNumber n2 = ((VNumberArray) val2).getData();
            final int N = n1.size();
            if (n2.size() != N)
                return false;
            for (int i=0; i<N; ++i)
                if (Double.doubleToLongBits(n1.getDouble(i)) != Double.doubleToLongBits(n2.getDouble(i)))
                    return false;
        }
        else if (val1 instanceof VEnum)
        {
            if (! (val2 instanceof VEnum))
                return false;
            final int v1 = ((VEnum) val1).getIndex();
            final int v2 = ((VEnum) val2).getIndex();
            if (v1 != v2)
                return false;
        }
        else if (val1 instanceof VString)
        {
            if (! (val2 instanceof VString))
                return false;
            final String v1 = ((VString) val1).getValue();
            final String v2 = ((VString) val2).getValue();
            if (v1 == null)
            {
                if (v2 != null)
                    return false;
            }
            else if (! v1.equals(v2))
                return false;
        }
        else
            return false; // Assume that unknown type differs in value
        // Compare severity, status
        if (! ((val1 instanceof Alarm)  &&  (val2 instanceof Alarm)))
            return false;
        final Alarm a1 = (Alarm)val1;
        final Alarm a2 = (Alarm)val2;
        return a1.getAlarmSeverity() == a2.getAlarmSeverity()
            && a1.getAlarmName().equals(a2.getAlarmName());
    }
}
