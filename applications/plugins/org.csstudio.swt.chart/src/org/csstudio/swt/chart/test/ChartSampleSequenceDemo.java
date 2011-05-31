/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.test;

import java.util.ArrayList;

import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.ChartSampleContainer;
import org.csstudio.swt.chart.ChartSampleSequence;
import org.csstudio.swt.chart.Range;
import org.csstudio.swt.chart.ChartSample.Type;

/** Demo data sample sequence.
 *  @see ChartSampleSequence
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ChartSampleSequenceDemo implements ChartSampleSequence
{
    final private ArrayList<ChartSampleContainer> samples
        = new ArrayList<ChartSampleContainer>();
    final private double x0, period;
    private double x;

    /** Initialize a sine-wave
     *  @param phase Initial phase [degrees]
     *  @param period Period [number of samples for full sine wave]
     */
    public ChartSampleSequenceDemo(final double x0, final double phase, final double period)
    {
        this.x0 = x0;
        this.period = period;
        x = Math.toRadians(phase);
    }

    /** Add a new sample */
    public void add()
    {
        // 5% annotation, rest actual samples
        if (Math.random() > 0.95)
            samples.add(new ChartSampleContainer(Type.Point, x0+x, Double.NEGATIVE_INFINITY, "Comment"));
        else
        {
            final double y = 0.1*Math.random()  +  Math.sin(x);
            // Some raw samples, rest min/max/average
            if (Math.random() > 0.5)
                samples.add(new ChartSampleContainer(Type.Normal, x0+x, y, y, y, "Sample"));
            else
            {
                final double noise = 0.2;
                final double y_min = y - y*noise*Math.random();
                final double y_max = y + y*noise*Math.random();
                samples.add(new ChartSampleContainer(Type.Normal, x0+x, y, y_min, y_max, "Average"));
            }
        }
        // Prepare next sample
        x += 2.0*Math.PI/period;
    }

    /** {@inheritDoc} */
    @Override
    public int size()
    {   return samples.size();    }

    /** {@inheritDoc} */
    @Override
    public ChartSample get(int i)
    {   return samples.get(i);    }

    /** {@inheritDoc} */
    @Override
    public Range getDefaultRange()
    {   return null;  }
}
