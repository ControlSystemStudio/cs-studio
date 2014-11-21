/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import org.csstudio.swt.rtplot.YAxis;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.eclipse.swt.graphics.RGB;

/** {@link AxisLabelProvider} that uses the name of the axis
 *  @param <XTYPE> Data type used for the {@link PlotDataItem}
 *  @author Kay Kasemir
 */
public class AxisNameLabelProvider<XTYPE extends Comparable<XTYPE>> implements AxisLabelProvider
{
    final private YAxis<XTYPE> axis;
    private boolean in_use = false;

    public AxisNameLabelProvider(final YAxis<XTYPE> axis)
    {
        this.axis = axis;
    }

    /** {@inheritDoc} */
    @Override
    public void start()
    {
        in_use = true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
    {
        if (in_use)
        {
            in_use = false;
            return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String getLabel()
    {
        return axis.getName();
    }

    /** {@inheritDoc} */
    @Override
    public RGB getColor()
    {
        return axis.getColor();
    }
}
