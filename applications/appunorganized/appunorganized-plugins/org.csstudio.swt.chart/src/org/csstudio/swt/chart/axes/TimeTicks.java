/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;

import java.util.Calendar;

import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.eclipse.swt.graphics.GC;

/** Helper for creating tick marks.
 *  <p>
 *  Computes tick positions, formats tick labels.
 *  Doesn't perform the actual drawing.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimeTicks extends Ticks
{
    /** Seconds in a day. */
    private static final int DAY_SECS = 24*60*60;

    /** Compute tick information.
     *
     *  @param low Low limit of the axis range.
     *  @param high High limit of the axis range.
     *  @param char_width Aproximate width of one character.
     *  @param screen_width Width of axis on screen.
     */
    @Override
    void compute(double low, double high, GC gc, int screen_width)
    {
        if (low > high)
            throw new Error("Tick range is not ordered: " + low + " > " + high);
        double range = high-low;

        // Which of the available formats suits the visible time range?
        if (range < 1.0) // one second
        {   // Show nanoseconds, keep 'high', show whatever fits
            precision = ITimestamp.Format.Full.ordinal();
            // Time axis should show the 'high' value,
            // then see how much more we can show going back from there.
            String label = format(high);
            int label_width = gc.textExtent(label).x;
            // Fit available space with labels
            int num_that_fits = screen_width/label_width*FILL_PERCENTAGE/100;
            distance = range / num_that_fits;
            if (distance <= 0.0)
                throw new Error("Broken tickmark computation");
            start = high - num_that_fits * distance;
            return;
        }

        // The rest involves some rounding of the local time
        Calendar local_high = TimestampFactory.fromDouble(high).toCalendar();
        int round_seconds;   // how to round
        if (range < 5*60) // five minutes
        {   // Show seconds
            precision = ITimestamp.Format.DateTimeSeconds.ordinal();
            round_seconds = 1;
            // Round down to full seconds.
            local_high.set(Calendar.MILLISECOND, 0);
        }
        else if (range < 2*DAY_SECS) // two days
        {   // Show time down to minutes
            precision = ITimestamp.Format.DateTime.ordinal();
            round_seconds = 60;
            // Round down to minutes
            local_high.set(Calendar.MILLISECOND, 0);
            local_high.set(Calendar.SECOND, 0);
        }
        else
        {   // Show time down to days?
            // This is tricky with dayllight saving time,
            // where there are 23 or 25 hours in a day.
            // So still display the hours and minutes,
            // even when they'll mostly be "00:00".
            precision = ITimestamp.Format.DateTime.ordinal();
            round_seconds = DAY_SECS;
            // Round down to days
            // The rounding up to full days actually breaks around
            // daylight saving changes, but that's OK, since we'll display
            // the hours and minutes anyway.
            local_high.set(Calendar.MILLISECOND, 0);
            local_high.set(Calendar.SECOND, 0);
            local_high.set(Calendar.MINUTE, 0);
            local_high.set(Calendar.HOUR_OF_DAY, 0);
        }

        // Get the timestamp for the rounded calendar, then back into double.
        double last = TimestampFactory.fromCalendar(local_high).toDouble();
        // Determine minimum label distance on the screen.
        // Fit about 80% of the available space with labels
        String label = format(last);
        int label_width = gc.textExtent(label).x;
        int num_that_fits = screen_width/label_width*FILL_PERCENTAGE/100;
        double min_distance = range / num_that_fits;

        // Round up to full seconds or minutes or ...
        distance = (1.0 + (int)(min_distance / round_seconds)) * round_seconds;
        // ... and move start some N*distance before 'last'
        start = last - ( (int)((last-low)/distance) ) * distance;
    }

    /** @return Returns the number formated according to the tick precision. */
    @Override
    public String format(double num, int precision_change)
    {
        if (precision_change > 0)
            precision_change = 1;
        ITimestamp stamp = TimestampFactory.fromDouble(num);
        return stamp.format(ITimestamp.Format.fromOrdinal(precision + precision_change));
    }

    /** @return Returns the time as a two-line string. */
    public String getMultiline(double num)
    {
        ITimestamp stamp = TimestampFactory.fromDouble(num);
        String s = stamp.format(ITimestamp.Format.fromOrdinal(precision));
        if (precision > ITimestamp.Format.Date.ordinal())
        {   // Split into date and time
            return s = s.substring(0, 10) + "\n" + s.substring(11); //$NON-NLS-1$
        }
        return s;
    }
}
