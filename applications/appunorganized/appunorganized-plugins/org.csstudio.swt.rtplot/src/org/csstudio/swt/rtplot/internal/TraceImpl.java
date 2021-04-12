/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.util.Objects;
import java.util.Optional;

import org.csstudio.swt.rtplot.PointType;
import org.csstudio.swt.rtplot.RTPlot;
import org.csstudio.swt.rtplot.Trace;
import org.csstudio.swt.rtplot.TraceType;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.csstudio.swt.rtplot.data.PlotDataProvider;
import org.eclipse.swt.graphics.RGB;

/** Trace, i.e. data to be displayed on an axis.
 *  @param <XTYPE> Data type used for the {@link PlotDataItem}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TraceImpl<XTYPE extends Comparable<XTYPE>> implements Trace<XTYPE>
{
    final private PlotDataProvider<XTYPE> data;

    private volatile boolean visible = true;

    private volatile String name;

    private volatile String units;

    private volatile RGB color;

    private volatile TraceType type;

    private volatile int width;

    private volatile PointType point_type;

    private volatile int size;

    private volatile int y_axis;

    private volatile Optional<PlotDataItem<XTYPE>> selected_sample = Optional.empty();


    public TraceImpl(final String name,
            final String units,
            final PlotDataProvider<XTYPE> data,
            final RGB color,
            final TraceType type, final int width,
            final PointType point_type, final int size,
            final int y_axis)
    {
        this.name = Objects.requireNonNull(name);
        this.units = units == null ? "" : units;
        this.data = Objects.requireNonNull(data);
        this.color = Objects.requireNonNull(color);
        this.type = Objects.requireNonNull(type);
        this.width = width;
        this.point_type = Objects.requireNonNull(point_type);
        this.size = size;
        this.y_axis = y_axis;
    }

    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(final String name)
    {
        this.name = Objects.requireNonNull(name);
    }

    /** {@inheritDoc} */
    @Override
    public String getUnits()
    {
        return units;
    }

    /** {@inheritDoc} */
    @Override
    public void setUnits(final String units)
    {
        this.units = units == null ? "" : units;
    }

    /** {@inheritDoc} */
    @Override
    public PlotDataProvider<XTYPE> getData()
    {
        return data;
    }

    /** {@inheritDoc} */
    @Override
    public RGB getColor()
    {
        return color;
    }

    /** {@inheritDoc} */
    @Override
    public void setColor(final RGB color)
    {
        this.color = Objects.requireNonNull(color);
    }

    /** {@inheritDoc} */
    @Override
    public TraceType getType()
    {
        return type;
    }

    /** {@inheritDoc} */
    @Override
    public void setType(final TraceType type)
    {
        this.type = Objects.requireNonNull(type);
    }

    /** {@inheritDoc} */
    @Override
    public int getWidth()
    {
        return width;
    }

    /** {@inheritDoc} */
    @Override
    public void setWidth(final int width)
    {
        this.width = width;
    }

    /** {@inheritDoc} */
    @Override
    public PointType getPointType()
    {
        return point_type;
    }

    /** {@inheritDoc} */
    @Override
    public void setPointType(final PointType type)
    {
        this.point_type = Objects.requireNonNull(type);
    }

    /** {@inheritDoc} */
    @Override
    public int getPointSize()
    {
        return size;
    }

    /** {@inheritDoc} */
    @Override
    public void setPointSize(final int size)
    {
        this.size = size;
    }

    /** {@inheritDoc} */
    @Override
    public int getYAxis()
    {
        return y_axis;
    }

    /** Set the Y Axis index.
     *
     *  @param y_axis Y axis index
     *  @see RTPlot#moveTrace()
     */
    public void setYAxis(final int y_axis)
    {
        this.y_axis = y_axis;
    }

    /** @param sample Sample under cursor or <code>null</code> */
    public void selectSample(final PlotDataItem<XTYPE> sample)
    {
        selected_sample = Optional.ofNullable(sample);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<PlotDataItem<XTYPE>> getSelectedSample()
    {
        return selected_sample;
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return "Trace " + name;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
