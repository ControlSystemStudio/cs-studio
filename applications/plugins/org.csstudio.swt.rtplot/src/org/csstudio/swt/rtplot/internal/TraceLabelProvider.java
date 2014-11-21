/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.util.Iterator;

import org.csstudio.swt.rtplot.Trace;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.eclipse.swt.graphics.RGB;

/** Axis label provider that uses the names and colors
 *  of traces on an axis
 *  @param <XTYPE> Data type used for the {@link PlotDataItem}
 *  @author Kay Kasemir
 */
public class TraceLabelProvider<XTYPE extends Comparable<XTYPE>> implements AxisLabelProvider
{
    final private YAxisImpl<XTYPE> axis;
    private Iterator<TraceImpl<XTYPE>> traces;
    private Trace<XTYPE> trace;

    public TraceLabelProvider(final YAxisImpl<XTYPE> axis)
    {
        this.axis = axis;
    }

    /** {@inheritDoc} */
    @Override
    public void start()
    {
        traces = axis.getTraces().iterator();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
    {
        if (traces.hasNext())
        {
            trace = traces.next();
            return true;
        }
        trace = null;
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String getLabel()
    {
        return trace.getName();
    }

    /** {@inheritDoc} */
    @Override
    public RGB getColor()
    {
        return trace.getColor();
    }
}
