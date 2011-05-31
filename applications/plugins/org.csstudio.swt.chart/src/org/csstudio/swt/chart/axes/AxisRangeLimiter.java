/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;

import org.csstudio.swt.chart.ChartSampleSearch;
import org.csstudio.swt.chart.ChartSampleSequence;

/** Helper for searching a SampleSequence for samples that reside within
 *  the visible region of an axis. 
 *  @author Kay Kasemir
 */
public class AxisRangeLimiter
{
    private Axis axis;
    
    /** Construct a limiter for the given axis' low and high value range. */
    public AxisRangeLimiter(Axis axis)
    {
        this.axis = axis;
    }
    
    /** Determine the low index into the sample sequence, the one that's
     *  just below or at the axis' lower range limit.
     */
    public int getLowIndex(ChartSampleSequence samples)
    {
        int i = ChartSampleSearch.findSampleLessOrEqual(samples, axis.getLowValue());
        if (i >= 0)
            return i;
        return 0;
    }

    /** Determine the high index into the sample sequence, the one that's
     *  just above or at the axis' upper range limit.
     */
    public int getHighIndex(ChartSampleSequence samples)
    {
        synchronized (samples)
        {
            int i = ChartSampleSearch.findSampleGreaterOrEqual(samples,
                                                          axis.getHighValue());
            if (i >= 0)
                return i;
            return samples.size() - 1;
        }
    }
}
