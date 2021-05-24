/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot;

import java.util.Optional;

import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.csstudio.swt.rtplot.data.PlotDataProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.RGB;

/** Trace, i.e. data to be displayed on an axis.
 *  @param <XTYPE> Data type used for the {@link PlotDataItem}
 *  @author Kay Kasemir
 */
public interface Trace<XTYPE extends Comparable<XTYPE>>
{
    /** @return <code>true</code> if trace is visible */
    public boolean isVisible();

    /** @param visible Should trace be visible? */
    public void setVisible(final boolean visible);

    /** @return Name, i.e. label of this trace */
    public String getName();

    /** @param name Name, i.e. label of this trace */
    public void setName(final String name);

    /** @return Units, may be "" */
    public String getUnits();

    /** @param units Units, may be <code>null</code> */
    public void setUnits(String units);

    /** @return Label that combines trace name with units */
    default public String getLabel()
    {
        final String units = getUnits();
        if (units.isEmpty())
            return getName();
        return NLS.bind(Messages.NameUnitsFmt, getName(), units);
    }

    /** @return {@link PlotDataProvider} */
    public PlotDataProvider<XTYPE> getData();

    /** @return Color to use for this trace */
    public RGB getColor();

    /** @param color Color to use for this trace */
    public void setColor(final RGB color);

    /** @return How to draw this trace */
    public TraceType getType();

    /** @param type How to draw this trace */
    public void setType(final TraceType type);

    /** @return Width of line or marker used to draw this trace */
    public int getWidth();

    /** @param width Width of line or marker used to draw this trace */
    public void setWidth(final int width);

    /** @return How to draw points of this trace */
    public PointType getPointType();

    /** @param type How to draw points of this trace */
    public void setPointType(final PointType type);

    /** @return Size of points */
    public int getPointSize();

    /** @param size Size of points */
    public void setPointSize(final int size);

    /** @return Y axis index */
    public int getYAxis();

    /** @return Sample that's been selected by the cursor */
    public Optional<PlotDataItem<XTYPE>> getSelectedSample();
}
