/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.csstudio.swt.rtplot.Activator;
import org.csstudio.swt.rtplot.SWTMediaPool;
import org.csstudio.swt.rtplot.Trace;
import org.csstudio.swt.rtplot.YAxis;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.csstudio.swt.rtplot.internal.util.GraphicsUtils;
import org.csstudio.swt.rtplot.internal.util.IntList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/** A 'Y' or 'vertical' axis.
 *  <p>
 *  The plot maintains one or more Y axes.
 *  Each trace to plot needs to be assigned to a Y axis.
 *
 *  @param <XTYPE> Data type of the {@link PlotDataItem}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class YAxisImpl<XTYPE extends Comparable<XTYPE>> extends NumericAxis implements YAxis<XTYPE>
{
    /** How to label the axis */
    final private AxisLabelProvider<XTYPE> label_provider;

    /** Computed in getPixelWidth:
     *  Location of labels, and Y-separation between labels,
     *  used to show SEPARATOR.
     *
     *  Calls to getPixelWidth and paint will come from the same
     *  thread which updates the plot.
     *
     *  Number of label_provider entries should match the size of
     *  label_x and label_y entries after they're set in getDesiredPixelSize,
     *  but note that label_provider could change at any time:
     *  getPixelWidth ran with N labels,
     *  labels change, requesting new layout and thus call to getDesiredPixelSize,
     *  but paint() is called before that happened.
     */
    final private IntList label_x = new IntList(2), label_y = new IntList(2);
    private int label_y_separation;

    /** Auto-scale the axis range? */
    private volatile boolean autoscale = false;

    /** Show on right side? */
    private volatile boolean is_right = false;

    /** Traces on this axis.
     *
     *  <p>{@link CopyOnWriteArrayList} adds thread safety.
     *  In addition, SYNC on traces when adding a trace
     *  to avoid duplicates
     */
    final private List<TraceImpl<XTYPE>> traces = new CopyOnWriteArrayList<>();

    /** Construct a new Y axis.
     *  <p>
     *  Note that end users will typically <b>not</b> create new Y axes,
     *  but instead ask the <code>Chart</code> for a new or existing axis.
     *  <p>
     *  @param name The axis name.
     *  @param listener Listener.
     */
    public YAxisImpl(final String name, final PlotPartListener listener)
    {
        super(name, listener,
              false,      // vertical
              0.0, 10.0); // Initial range
        label_provider = new AxisLabelProvider<XTYPE>(this);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isUsingAxisName()
    {
        return label_provider.isUsingAxisName();
    }

    /** {@inheritDoc} */
    @Override
    public void useAxisName(final boolean use_axis_name)
    {
        if (label_provider.isUsingAxisName() == use_axis_name)
            return;
        label_provider.useAxisName(use_axis_name);
        requestLayout();
        requestRefresh();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isUsingTraceNames()
    {
        return label_provider.isUsingTraceNames();
    }

    /** {@inheritDoc} */
    @Override
    public void useTraceNames(final boolean use_trace_names)
    {
        if (label_provider.isUsingTraceNames() == use_trace_names)
            return;
        label_provider.useTraceNames(use_trace_names);
        requestLayout();
        requestRefresh();
    }

    /** {@inheritDoc} */
    @Override
    public void setAutoscale(boolean do_autoscale)
    {
        autoscale = do_autoscale;
        requestLayout();
        requestRefresh();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAutoscale()
    {
        return autoscale;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOnRight()
    {
        return is_right;
    }

    /** {@inheritDoc} */
    @Override
    public void setOnRight(final boolean right)
    {
        if (is_right == right)
            return;
        is_right = right;
        requestLayout();
        requestRefresh();
    }

    /** Add trace to axis
     *  @param trace {@link Trace}
     *  @throws IllegalArgumentException if trace already on axis
     */
    void addTrace(final TraceImpl<XTYPE> trace)
    {
        Objects.requireNonNull(trace);
        // CopyOnWriteArrayList is thread-safe, but race between 2 threads
        // could still end up with duplicate.
        // Could synchronized here, but then FindBugs complains.
        traces.add(trace);
        // So checking afterwards if there are more than the one expected entries
        if (traces.stream().filter((existing) -> existing == trace).count() > 1)
            throw new IllegalArgumentException("Trace " + trace.getName() + " already on Y Axis " + getName());
        requestLayout();
    }

    /** Remove a trace from the axis.
     *  <p>
     *  Not meant for end users, the trace is supposed to remove itself.
     */
    public void removeTrace(final Trace<XTYPE> trace)
    {
        Objects.requireNonNull(trace);
        if (! traces.remove(trace))
            throw new Error("Internal YAxis error. Axis " + getName() + " does not hold trace " + trace.getName());
        requestLayout();
    }

    /** 'Current' list of traces as thread-safe, read-only iterable,
     *  a snapshot of the underlying {@link CopyOnWriteArrayList}
     *
     * @return Current list of traces for an axis.
     */
    Iterable<TraceImpl<XTYPE>> getTraces()
    {
        return traces;
    }

    /** {@inheritDoc */
    @Override
    public int getDesiredPixelSize(final Rectangle region, final GC gc)
    {
        Activator.getLogger().log(Level.FINE, "YAxis({0}) layout for {1}", new Object[] { getName(),  region });

        if (! isVisible())
            return 0;

        gc.setFont(getLabelFont());
        final int x_sep = gc.getFontMetrics().getHeight();
        // Start layout of labels at x=0, 'left',
        // to determine how many fit into one line.
        // Later update these relative x positions based on 'left' or 'right' axis.
        int x = 0;
        int lines = 0;

        // Compute layout of labels
        label_provider.start();
        label_x.clear();
        label_y.clear();
        final IntList label_length = new IntList(2);
        while (label_provider.hasNext())
        {
            label_y_separation = gc.textExtent(label_provider.getSeparator()).x;
            label_length.add(gc.textExtent(label_provider.getLabel()).x);
        }
        while (label_provider.hasNext());

        // Compute location of each label
        int next = 0;
        final int N = label_length.size();
        while (next < N)
        {   // Determine how many can fit on one line
            int many = 0;
            int height = 0;
            for (int i=next; i<N; ++i)
                if (height + label_length.get(i) < region.height)
                {
                    ++many;
                    height += label_length.get(i);
                    if (i > 0)
                        height += label_y_separation;
                }
                else
                    break;
            // Can't fit any? Show one, will be clipped
            if (many == 0)
            {
                many = 1;
                height = region.height;
            }
            // Draw one line
            int y = region.y + (region.height+height)/2;
            for (int i=next; i<next+many; ++i)
            {
                y -= label_length.get(i);
                label_x.add(x);
                label_y.add(y);
                if (i < N-1)
                    y -= label_y_separation;
            }
            x += x_sep;
            next += many;
            ++lines;
        }

        final int x_correction = is_right ? region.x + region.width - lines*x_sep : region.x;
        for (int i=label_x.size()-1; i>=0; --i)
            label_x.set(i, label_x.get(i) + x_correction);

        gc.setFont(getScaleFont());
        final int scale_size = gc.getFontMetrics().getHeight();

        // Width of labels, width of axis text, tick markers.
        return lines * x_sep + scale_size + TICK_LENGTH;
    }

    /** {@inheritDoc} */
    @Override
    public void paint(final GC gc, final SWTMediaPool media, final Rectangle plot_bounds)
    {
        if (! isVisible())
            return;

        super.paint(gc, media);
        final Rectangle region = getBounds();

        final int old_width = gc.getLineWidth();
        final Color old_bg = gc.getBackground();
        final Color old_fg = gc.getForeground();
        gc.setForeground(media.get(getColor()));
        gc.setFont(getScaleFont());

        // Simple line for the axis
        final int line_x, tick_x, minor_x;
        if (is_right)
        {
            line_x = region.x;
            tick_x = region.x + TICK_LENGTH;
            minor_x = region.x + MINOR_TICK_LENGTH;
        }
        else
        {
            line_x = region.x + region.width-1;
            tick_x = region.x + region.width - TICK_LENGTH;
            minor_x = region.x + region.width - MINOR_TICK_LENGTH;
        }
        gc.drawLine(line_x, region.y, line_x, region.y + region.height-1);
        computeTicks(gc);

        final double low_value = range.getLow();
        final double high_value = range.getHigh();
        final int minor_ticks = ticks.getMinorTicks();
        double tick = ticks.getStart();
        double prev = ticks.getPrevious(tick);
        for (/**/;
             tick <= high_value  &&  Double.isFinite(tick);
             tick = ticks.getNext(tick))
        {
            // Minor ticks?
            for (int i=1; i<minor_ticks; ++i)
            {
                final double minor = prev + ((tick - prev)*i)/minor_ticks;
                if (minor < low_value)
                    continue;
                final int y = getScreenCoord(minor);
                gc.drawLine(minor_x, y, line_x, y);
            }

            // Major tick marks
            gc.setLineWidth(TICK_WIDTH);
            int y = getScreenCoord(tick);
            gc.drawLine(tick_x, y, line_x, y);
            gc.setLineWidth(old_width);

            // Grid line
            if (show_grid)
            {
                //gc.setLineStyle(SWT.LINE_DOT);
                gc.drawLine(plot_bounds.x, y, plot_bounds.x + plot_bounds.width-1, y);
                //gc.setLineStyle(SWT.LINE_SOLID);
            }

            // Tick Label
            drawTickLabel(gc, media, tick, false);

            prev = tick;
        }
        // Minor ticks after last major tick?
        if (Double.isFinite(tick))
            for (int i=1; i<minor_ticks; ++i)
            {
                final double minor = prev + ((tick - prev)*i)/minor_ticks;
                if (minor > high_value)
                    break;
                final int y = getScreenCoord(minor);
                gc.drawLine(minor_x, y, line_x, y);
            }

        gc.setForeground(old_fg);
        gc.setBackground(old_bg);

        gc.setFont(getLabelFont());
        paintLabels(gc, media);
    }

    /** {@inheritDoc} */
    @Override
    public void drawTickLabel(final GC gc, final SWTMediaPool media, final Double tick, final boolean floating)
    {
        final Rectangle region = getBounds();
        final String mark = ticks.format(tick);
        final Point mark_size = gc.textExtent(mark);

        final int x = is_right ? region.x + TICK_LENGTH : region.x + region.width - TICK_LENGTH - mark_size.y;
        final int y = getScreenCoord(tick)  - mark_size.x/2;
        if (floating)
        {
            gc.fillRectangle(x-2, y-2, mark_size.y+4, mark_size.x+4);
            gc.drawRectangle(x-2, y-2, mark_size.y+4, mark_size.x+4);
        }
        GraphicsUtils.drawVerticalText(gc, x, y, mark, SWT.UP);
    }

    protected void paintLabels(final GC gc, final SWTMediaPool media)
    {
        if (label_y == null)
            return;
        final Color old_fg = gc.getForeground();
        label_provider.start();
        int i = 0;
        while (label_provider.hasNext()  &&  i < label_x.size())
        {   // Draw labels at pre-computed locations
            if (i > 0)
                GraphicsUtils.drawVerticalText(gc, label_x.get(i-1), label_y.get(i-1) - label_y_separation,
                                               label_provider.getSeparator(), SWT.UP);
            gc.setForeground(media.get(label_provider.getColor()));
            GraphicsUtils.drawVerticalText(gc,
                label_x.get(i), label_y.get(i), label_provider.getLabel(), SWT.UP);
            gc.setForeground(old_fg);
            ++i;
        }
    }
}
