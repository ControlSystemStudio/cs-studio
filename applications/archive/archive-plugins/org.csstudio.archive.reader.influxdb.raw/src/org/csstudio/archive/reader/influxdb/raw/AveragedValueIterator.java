/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.influxdb.raw;

import java.time.Duration;
import java.time.Instant;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.csstudio.archive.vtype.StatisticsAccumulator;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.archive.vtype.VTypeHelper;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VType;

/** Averaging sample iterator.
 *
 *  This iterator reads samples from a given 'base' iterator
 *  and returns averaged samples.
 *
 *  @author Kay Kasemir
 */
public class AveragedValueIterator implements ValueIterator
{
    final private static boolean debug = false;

    /** Iterator for the underlying raw samples over which we average */
    final private ValueIterator base;

    /** Averaging period in seconds */
    final private long seconds;

    /** The most recent value from <code>base</code>, may be <code>null</code> */
    private VType base_value = null;

    /** Display meta data */
    private Display display = null;

    /** The average value that <code>next()</code> will return
     *  or <code>null</code> if there is none
     */
    private VType avg_value = null;

    /** Initialize
     *  @param base Iterator for 'raw' values
     *  @param seconds Averaging period
     *  @throws Exception on error
     */
    public AveragedValueIterator(final ValueIterator base, final double seconds) throws Exception
    {
        this.base = base;
        // Guard against negative secs or values that would map to (long)0.
        if (seconds < 1.0)
            this.seconds = 1;
        else
            this.seconds = (long) seconds;

        // Get initial 'base' sample
        if (base.hasNext())
            base_value = base.next();
        // Get initial average value so that hasNext() and next() will work.
        avg_value = determineNextAverage();
    }

    /** Determine the next average sample.
     *  <p>
     *  <code>base_value</code> has to be on the very first sample
     *  for the current average window; either
     *  <ol>
     *  <li>the very first sample of the base iterator,
     *  <li>or the last sample we got in the previous
     *      call to <code>determineNextAverage</code>,
     *      i.e. the sample that turned out to be just past
     *      the last average window.
     *  </ol>
     *  @return The next average value
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    private VType determineNextAverage() throws Exception
    {
        // Anything left to average?
        if (base_value == null)
            return null;
        // Determine next multiple of averaging period
        final Instant average_window_start = VTypeHelper.getTimestamp(base_value);
        final Instant average_window_end =
                TimestampHelper.roundUp(average_window_start, seconds);
        if (debug)
            System.out.println("Average until " + TimestampHelper.format(average_window_end));
        // Determine average over values within the window
        final StatisticsAccumulator stats = new StatisticsAccumulator();
        VType last_value = null;
        AlarmSeverity severity = VTypeHelper.getSeverity(base_value);
        String status = VTypeHelper.getMessage(base_value);
        while (base_value != null &&
                VTypeHelper.getTimestamp(base_value).compareTo(average_window_end) < 0)
        {
            final Number num = getNumericValue(base_value);
            if (num != null)
            {   // Has a numeric value
                if (debug)
                    System.out.println("Using " + base_value.toString());
                // Remember the first meta data that we can use
                if (display == null)
                {
                    if (base_value instanceof Display)
                        display = (Display) base_value;
                }
                // Value average
                stats.add(num.doubleValue());
                // Maximize the severity
                if (isHigherSeverity(severity, VTypeHelper.getSeverity(base_value)))
                {
                    severity = VTypeHelper.getSeverity(base_value);
                    status = VTypeHelper.getMessage(base_value);
                }
                // Load next base value
                last_value = base_value;
                base_value = base.hasNext()  ?  base.next()  :  null;
            }
            else
            {   // If some average has accumulated, return that;
                // handle non-numeric base_value on the next call.
                if (stats.getNSamples() > 0)
                    break;
                if (debug)
                    System.out.println("Passing through: " + base_value.toString());
                // We have nothing except this non-numeric sample.
                // Return as is after preparing next call.
                last_value = base_value;
                base_value = base.hasNext()  ?  base.next()  :  null;
                return last_value;
            }
        }
        // Only single value? Return as is
        if (stats.getNSamples() <= 1)
            return last_value;
        // Create time stamp in center of averaging window ('bin')
        final Instant bin_time = average_window_end.minus(Duration.ofSeconds(seconds/2));

        // Return the min/max/average
        final ArchiveVStatistics result = new ArchiveVStatistics(bin_time, severity, status, display, stats);
        if (debug)
            System.out.println("Result: " + result.toString());
        return result;
    }

    /** @return <code>true</code> if the <code>new_severity</code> is more
     *          severe than the <code>current</code> severity.
     */
    private boolean isHigherSeverity(final AlarmSeverity current,
            final AlarmSeverity new_severity)
    {
        return new_severity.ordinal() > current.ordinal();
    }

    /** Try to get numeric value for interpolation.
     *  <p>
     *  Does <u>not</u> return numbers for enum.
     *  @param value {@link VType}
     *  @return {@link Number} or <code>null</code>
     */
    private static Number getNumericValue(final VType value)
    {
        if (value instanceof VNumber)
        {
            final VNumber number = (VNumber) value;
            if (number.getAlarmSeverity() != AlarmSeverity.UNDEFINED)
                return number.getValue();
        }
        if (value instanceof VNumberArray)
        {
            final VNumberArray numbers = (VNumberArray) value;
            if (numbers.getAlarmSeverity() != AlarmSeverity.UNDEFINED  &&
                    numbers.getData().size() > 0)
                return numbers.getData().getDouble(0);
        }
        // String or Enum, or no Value at all
        // Cannot decode that sample type as a number.
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
    {
        return avg_value != null;
    }

    /** {@inheritDoc} */
    @Override
    public VType next() throws Exception
    {   // Save the value we're about to return, prepare the following avg.
        final VType ret_value = avg_value;
        avg_value = determineNextAverage();
        return ret_value;
    }

    @Override
    public void close()
    {
        base.close();
    }
}
