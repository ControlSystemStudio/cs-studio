/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.undo;

import java.util.List;

import org.csstudio.swt.rtplot.Axis;
import org.csstudio.swt.rtplot.AxisRange;
import org.csstudio.swt.rtplot.internal.Plot;
import org.csstudio.swt.rtplot.internal.YAxisImpl;

/** Un-doable action to modify value range of axes
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ChangeAxisRanges<XTYPE extends Comparable<XTYPE>> extends UndoableAction
{
    final private Plot<XTYPE> plot;
    final private Axis<XTYPE> x_axis;
    final private AxisRange<XTYPE> original_x_range, new_x_range;
    final private List<YAxisImpl<XTYPE>> yaxes;
    final private List<AxisRange<Double>> original_yranges;
    final private List<AxisRange<Double>> new_yranges;
    final private List<Boolean> original_autoscale;
    final private List<Boolean> new_autoscale;

    /** @param plot Plot
     *  @param name Name of the action
     *  @param x_axis X Axis or <code>null</code>
     *  @param original_x_range Original ..
     *  @param new_x_range .. and new X range, or <code>null</code>
     *  @param y_axes Y Axes or <code>null</code>
     *  @param original_y_ranges Original
     *  @param new_y_ranges .. and new value ranges, or <code>null</code>
     *  @param original_autoscale Original auto-scale values, or <code>null</code>
     *  @param new_autoscale New auto-scale values, or <code>null</null>
     */
    public ChangeAxisRanges(final Plot<XTYPE> plot,
            final String name,
            final Axis<XTYPE> x_axis,
            final AxisRange<XTYPE> original_x_range,
            final AxisRange<XTYPE> new_x_range,
            final List<YAxisImpl<XTYPE>> y_axes,
            final List<AxisRange<Double>> original_y_ranges,
            final List<AxisRange<Double>> new_y_ranges,
            final List<Boolean> original_autoscale,
            final List<Boolean> new_autoscale)
    {
        super(name);
        this.plot = plot;
        this.x_axis = x_axis;
        this.original_x_range = original_x_range;
        this.new_x_range = new_x_range;
        this.yaxes = y_axes;
        this.original_yranges = original_y_ranges;
        this.new_yranges = new_y_ranges;
        this.original_autoscale = original_autoscale;
        this.new_autoscale = new_autoscale;

        if (yaxes != null)
        {
            if (y_axes.size() != original_y_ranges.size())
                throw new IllegalArgumentException(y_axes.size() + " Y axes, but " + original_y_ranges.size() + " orig. ranges");
            if (y_axes.size() != new_y_ranges.size())
                throw new IllegalArgumentException(y_axes.size() + " Y axes, but " + new_y_ranges.size() + " new ranges");
        }
    }

    /** @param plot Plot
     *  @param name Name of the action
     *  @param x_axis X Axis or <code>null</code>
     *  @param original_x_range Original ..
     *  @param new_x_range .. and new X range, or <code>null</code>
     *  @param y_axes Y Axes or <code>null</code>
     *  @param original_y_ranges Original
     *  @param new_y_ranges .. and new value ranges, or <code>null</code>
     */
    public ChangeAxisRanges(final Plot<XTYPE> plot,
            final String name,
            final Axis<XTYPE> x_axis,
            final AxisRange<XTYPE> original_x_range,
            final AxisRange<XTYPE> new_x_range,
            final List<YAxisImpl<XTYPE>> y_axes,
            final List<AxisRange<Double>> original_y_ranges,
            final List<AxisRange<Double>> new_y_ranges)
    {
        this(plot, name, x_axis, original_x_range, new_x_range, y_axes, original_y_ranges, new_y_ranges, null, null);
    }

    /** @param plot Plot
     *  @param name Name of the action
     *  @param x_axis X Axis or <code>null</code>
     *  @param original_x_range Original ..
     *  @param new_x_range .. and new X range, or <code>null</code>
     */
    public ChangeAxisRanges(final Plot<XTYPE> plot, final String name,
            final Axis<XTYPE> x_axis,
            final AxisRange<XTYPE> original_x_range,
            final AxisRange<XTYPE> new_x_range)
    {
        this(plot, name, x_axis, original_x_range, new_x_range, null, null, null, null, null);
    }

    /** @param plot Plot
     *  @param name Name of the action
     *  @param y_axes Y Axes or <code>null</code>
     *  @param original_y_ranges Original
     *  @param new_y_ranges .. and new value ranges, or <code>null</code>
     */
    public ChangeAxisRanges(final Plot<XTYPE> plot, final String name,
            final List<YAxisImpl<XTYPE>> y_axes,
            final List<AxisRange<Double>> original_y_ranges,
            final List<AxisRange<Double>> new_y_ranges)
    {
        this(plot, name, null, null, null, y_axes, original_y_ranges, new_y_ranges, null, null);
    }

    /** @param plot Plot
     *  @param name Name of the action
     *  @param y_axes Y Axes or <code>null</code>
     *  @param original_y_ranges Original
     *  @param new_y_ranges .. and new value ranges, or <code>null</code>
     *  @param original_autoscale Original auto-scale values, or <code>null</code>
     *  @param new_autoscale New auto-scale values, or <code>null</null>
     */
    public ChangeAxisRanges(final Plot<XTYPE> plot, final String name,
            final List<YAxisImpl<XTYPE>> y_axes,
            final List<AxisRange<Double>> original_y_ranges,
            final List<AxisRange<Double>> new_y_ranges,
            final List<Boolean> original_autoscale,
            final List<Boolean> new_autoscale)
    {
        this(plot, name, null, null, null, y_axes, original_y_ranges, new_y_ranges, original_autoscale, new_autoscale);
    }

    @Override
    public void run()
    {
        if (x_axis != null)
        {
            if (x_axis.setValueRange(new_x_range.getLow(), new_x_range.getHigh()))
                plot.fireXAxisChange();
        }
        if (yaxes != null)
        {
            setAutoscale(new_autoscale);
            setRange(new_yranges);
        }
    }

    @Override
    public void undo()
    {
        if (x_axis != null)
        {
            if (x_axis.setValueRange(original_x_range.getLow(), original_x_range.getHigh()))
                plot.fireXAxisChange();
        }
        if (yaxes != null)
        {
            setAutoscale(original_autoscale);
            setRange(original_yranges);
        }
    }

    private void setRange(final List<AxisRange<Double>> ranges)
    {
        for (int i=0; i<yaxes.size(); ++i)
        {
            final AxisRange<Double> range = ranges.get(i);
            final YAxisImpl<XTYPE> axis = yaxes.get(i);
            if (axis.setValueRange(range.getLow(), range.getHigh()))
                plot.fireYAxisChange(axis);
        }
    }

    private void setAutoscale(List<Boolean> autoscales)
    {
        if(autoscales != null)
            for (int i=0; i<yaxes.size(); ++i)
            {
                final Boolean autoscale = autoscales.get(i);
                final YAxisImpl<XTYPE> axis = yaxes.get(i);
                axis.setAutoscale(autoscale);
                plot.fireAutoScaleChange(axis);
            }
    }
}
