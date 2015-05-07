/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot;

import org.csstudio.swt.rtplot.data.PlotDataItem;

/**
 * Listener to changes in the plot
 * 
 * @param <XTYPE>
 *            Data type used for the {@link PlotDataItem}
 * @author Kay Kasemir
 * 
 * @TODO (shroffk) Remove this Adapter by providing default implementations in
 *       the {@link PlotListener}
 */
public class PlotListenerAdapter<XTYPE extends Comparable<XTYPE>> implements PlotListener<XTYPE>
{
    /** {@inheritDoc} */
    @Override
    public void changedXAxis(Axis<XTYPE> x_axis)
    {
        // NOP
    }

    /** {@inheritDoc} */
    @Override
    public void changedYAxis(YAxis<XTYPE> y_axis)
    {
        // NOP
    }

    /** {@inheritDoc} */
    @Override
    public void changedAnnotations()
    {
        // NOP
    }

    /** {@inheritDoc} */
    @Override
    public void changedCursors()
    {
        // NOP
    }
    
    /** {@inheritDoc} */
    @Override
    public void changedToolbar(boolean visible)
    {
        // NOP
    }

    /** {@inheritDoc} */
    @Override
    public void changedLegend(boolean visible)
    {
        // NOP
    }
}
