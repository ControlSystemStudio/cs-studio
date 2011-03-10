/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb;

import java.util.logging.Level;

import org.csstudio.archive.rdb.internal.TimestampUtil;
import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.ILongValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueFactory;

/** Averaging sample iterator.
 *
 *  This iterator reads samples from a given 'base' iterator
 *  and returns averaged samples.
 *
 *  TODO Only handles full seconds, not fractional seconds
 *  @author Kay Kasemir
 */
public class AveragingSampleIterator implements SampleIterator
{
    /** Iterator for the underlying raw samples over which we average */
    final private SampleIterator base;

    /** Averaging period in seconds */
    final private long seconds;

    /** The most recent value from <code>base</code>, may be <code>null</code> */
    private IValue base_value = null;

    /** The average value that <code>next()</code> will return
     *  or <code>null</code> if there is none
     */
    private IValue avg_value = null;

    /** Construct averaging iterator
     *  @param base Base iterator that provides the 'raw' samples
     *  @param seconds Averaging interval in seconds
     *  @throws Exception On Error
     */
    public AveragingSampleIterator(final SampleIterator base,
                                   final double seconds)  throws Exception
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
    private IValue determineNextAverage() throws Exception
    {
        // Anything left to average?
        if (base_value == null)
            return null;
        // Determine next multiple of averaging period
        final ITimestamp average_window_start = base_value.getTime();
        final ITimestamp average_window_end =
                TimestampUtil.roundUp(average_window_start, seconds);
        Activator.getLogger().log(Level.FINE, "Average until {0}", average_window_end);
        // Determine average over values within the window
        double sum = 0.0;
        long count = 0;
        double minimum = Double.MAX_VALUE, maximum = -Double.MAX_VALUE;
        IValue last_value = null;
        ISeverity severity = base_value.getSeverity();
        String status = base_value.getStatus();
        INumericMetaData meta = null;
        while (base_value != null &&
               base_value.getTime().isLessThan(average_window_end))
        {
            final Double num = getNumericValue(base_value);
            if (num != null)
            {   // Has a numeric value
                Activator.getLogger().log(Level.FINE, "Using {0}", base_value);
                // Should have numeric meta. Remember the most recent.
                meta = (INumericMetaData) base_value.getMetaData();
                // Value average
                sum += num;
                ++count;
                // Min/max
                if (minimum > num)
                    minimum = num;
                if (maximum < num)
                    maximum = num;
                // Maximize the severity
                if (isHigherSeverity(severity, base_value.getSeverity()))
                {
                    severity = base_value.getSeverity();
                    status = base_value.getStatus();
                }
                // Load next base value
                last_value = base_value;
                base_value = base.hasNext()  ?  base.next()  :  null;
            }
            else
            {   // If some average has accumulated, return that;
                // handle non-numeric base_value on the next call.
                if (count > 0)
                    break;
                Activator.getLogger().log(Level.FINE, "Passing through: {0}", base_value);
                // We have nothing except this non-numeric sample.
                // Return as is after preparing next call.
                last_value = base_value;
                base_value = base.hasNext()  ?  base.next()  :  null;
                return last_value;
            }
        }
        // Only single value? Return as is
        if (count <= 1)
            return last_value;
        // Return the min/max/average
        final double average = sum / count;
        // Create time stamp in center of averaged samples
        final long span =
            last_value.getTime().seconds() - average_window_start.seconds();
        final ITimestamp avg_time =
            TimestampUtil.add(average_window_start, Math.round(span/2.0));
        return ValueFactory.createMinMaxDoubleValue(avg_time,
                severity, status, meta,
                IValue.Quality.Interpolated,
                new double[] { average }, minimum, maximum);
    }

    /** @return <code>true</code> if the <code>new_severity</code> is more
     *          severe than the <code>current</code> severity.
     */
    private boolean isHigherSeverity(final ISeverity current,
                                     final ISeverity new_severity)
    {
        if (current.isInvalid()) // Nothing is higher
            return false;
        if (current.isMajor())   // invalid is higher
            return new_severity.isInvalid();
        if (current.isMinor())   // major, invalid are higher
            return new_severity.isMajor() || new_severity.isInvalid();
        if (current.isOK())      // Anything other than OK is higher
            return ! new_severity.isOK();
        return false;
    }

    /** Try to get numeric value out of an IValue.
     *  @return <code>Double</code> or <code>null</code>
     */
    private static Double getNumericValue(final IValue value)
    {
        // In contrast to ValueUtil.getDouble(), we don't want to average
        // over enums.
        if (value.getSeverity().hasValue())
        {
            if (value instanceof IDoubleValue)
                return ((IDoubleValue) value).getValue();
            if (value instanceof ILongValue)
                return new Double(((ILongValue) value).getValue());
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
    public IValue next() throws Exception
    {   // Save the value we're about to return, prepare the following avg.
        final IValue ret_value = avg_value;
        avg_value = determineNextAverage();
        return ret_value;
    }
}
