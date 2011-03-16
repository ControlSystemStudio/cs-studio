/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart;

import java.util.ArrayList;

/** Simple implementation of the sample sequence interface.
 *  @see ChartSampleSequence
 *  @author Kay Kasemir
 */
public class ChartSampleSequenceContainer implements ChartSampleSequence
{
    private ArrayList<ChartSampleContainer> samples
        = new ArrayList<ChartSampleContainer>();

    /** Add a new sample with x/y coords. */
    public void add(double x, double y)
    {
        add(ChartSample.Type.Normal, x, y);
    }

    /** Add a new sample with type and x/y coords. */
    public void add(ChartSample.Type type, double x, double y)
    {
        add(type, x, y, null);
    }

    /** Add a new sample with type and x/y coords. */
    public void add(ChartSample.Type type, double x, double y, String info)
    {
        samples.add(new ChartSampleContainer(type, x, y, y, y, info));
    }

    /** Add a new sample with type and x/y coords and error info. */
    public void add(ChartSample.Type type, double x, double y,
                    double y_min, double y_max, String info)
    {
        samples.add(new ChartSampleContainer(type, x, y, y_min, y_max, info));
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
