/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader;

import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.csstudio.archive.vtype.StatisticsAccumulator;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.archive.vtype.VTypeHelper;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.VType;

/** {@link ValueIterator} that performs linear interpolation
 * 
 *  <p>Reads from an underlying value iterator and returns
 *  linearly interpolated values.
 *  @author Kay Kasemir
 */
public class LinearValueIterator implements ValueIterator
{
    /** Base iterator */
    final private ValueIterator base;

    /** Interpolation interval */
    final private TimeDuration interval;
    
    /** Last value read from the base iterator */
    private VType base_value;
    
    /** The next value to return */
    private VType next;

    /** Initialize
     *  @param base Base iterator
     *  @param interval Interpolation interval
     *  @throws Exception on error
     */
    public LinearValueIterator(final ValueIterator base, final TimeDuration interval) throws Exception
    {
        this.base = base;
        this.interval = interval;
        base_value = base.hasNext() ? base.next() : null;
        next = determineNextValue();
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
    {
        return next != null;
    }

    /** {@inheritDoc} */
    @Override
    public VType next() throws Exception
    {
        final VType result = next;
        next = determineNextValue();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        base.close();
    }
    
    /** Continue to read base iterator to determine next
     *  interpolated value
     *  @return next value
     *  @throws Exception on error
     */
    private VType determineNextValue() throws Exception
    {
        if (base_value == null)
            return null;
        
        // TODO: Check invalid samples
        // NaN from VTypeHelper.toDouble? AlarmSeverity?
        
        // Have one, initial value
        final StatisticsAccumulator accumulator = new StatisticsAccumulator();
        Timestamp t0, t1 = VTypeHelper.getTimestamp(base_value);
        double v0, v1 = VTypeHelper.toDouble(base_value);
        accumulator.add(v1);
        
        // Look for values until end of current interpolation bin
        final Timestamp end_of_bin = TimestampHelper.roundUp(t1, interval);
        do
        {
            // Track previous value
            t0 = t1;
            v0 = v1;
            
            // Reached end of input data?
            if (!base.hasNext())
            {
                final VType last_value = base_value;
                base_value = null;
                return last_value;
            }
            
            // Get next value
            base_value = base.next();
            t1 = VTypeHelper.getTimestamp(base_value);
            v1 = VTypeHelper.toDouble(base_value);
            accumulator.add(v1);
        }
        while (t1.compareTo(end_of_bin) < 0);
        
        if (accumulator.getNSamples() >= 2)
        {   // Found at least one value in this bin
            // t0, v0 are before, t1, v1 at-or-after end_of_bin
            // Linear interpolation between t0,v0 and t1,v1 onto end_of_bin time
            final double dT = t1.durationFrom(t0).toSeconds();
            final double interpol;
            if (dT > 0)
                interpol = v0 + (v1 - v0) * (end_of_bin.durationFrom(t0).toSeconds() / dT);
            else
                interpol = (v0 + v1)/2; // Use average?
            
            final AlarmSeverity severity = VTypeHelper.getSeverity(base_value);
            final String status = VTypeHelper.getMessage(base_value);
            return new ArchiveVStatistics(end_of_bin, severity, status, (Display) base_value,
                interpol, accumulator.getMin(), accumulator.getMax(), accumulator.getStdDev(), accumulator.getNSamples());
        }
        else
        {   // Have nothing in this bin
            throw new Exception("Not handled");
        }
    }
}
