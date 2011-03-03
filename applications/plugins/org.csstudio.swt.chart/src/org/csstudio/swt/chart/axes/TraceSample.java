/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;

import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.Trace;

/** Information about a sample on a trace.
 *  @author Kay Kasemir
 */
public class TraceSample
{
    private Trace trace;
    private ChartSample sample;
    
    /** Constructor, private to package. */
    TraceSample(Trace trace, ChartSample sample)
    {
        super();
        this.trace = trace;
        this.sample = sample;
    }

    /** @return the sample */
    public ChartSample getSample()
    {
        return sample;
    }

    /** @return the trace */
    public Trace getTrace()
    {
        return trace;
    }

    @Override
    public String toString()
    {
        return String.format("TraceSample %s @ %g/%g", //$NON-NLS-1$
                sample.getType().name(), sample.getX(), sample.getY());
    }
}
