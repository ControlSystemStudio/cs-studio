/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;

import java.util.ArrayList;

import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.ChartSampleSearch;
import org.csstudio.swt.chart.ChartSampleSequence;
import org.csstudio.swt.chart.Range;
import org.csstudio.swt.chart.Trace;
import org.csstudio.swt.util.GraphicsUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/** A 'Y' or 'vertical' axis.
 *  <p>
 *  The chart maintains one or more Y axes.
 *  Each trace to plot needs to be assigned to a Y axis.
 *  
 *  @see Trace
 *  @see Chart
 *  @author Kay Kasemir
 */
public class YAxis extends Axis
{
    /** Is the axis itself visible?
     *  Doesn't affect he data on the axis
     */
    private boolean visible = true;

    /** The traces to plot. */
    private ArrayList<Trace> traces = new ArrayList<Trace>();
    
    /** Markers.
     *  <p>
     *  One could argue if markers belong to the Graph, YAxis or Trace.
     *  If on a trace, they should probably be on a single sample.
     *  Usually, that's what we actually want.
     *  But maybe we'll later also want to mark a location that's
     *  between samples or between traces?
     *  So then it must be on an axis or global to the graph.
     *  For the latter, it's unclear where to exactly paint it,
     *  so it's for now on the y-axis.
     */
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    
    /** The listener. For now exactly one, must not be <code>null</code>. */ 
    private YAxisListener listener;
    
    /** <code>true</code> if this axis is selected. 
     *  The chart actually only deals with one selected axis at a time,
     *  but maybe later one can select multiple axes? */
    private boolean selected;
    
    /** Set <code>true</code> to get minor tick marks. */
    private boolean show_minor_ticks = true;
    
    /** <code>true</code> if this axis auto-zooms. */
    private boolean auto_scale = false;

    /** Construct a new Y axis.
     *  <p>
     *  Note that end users will typically <b>not</b> create new Y axes,
     *  but instead ask the <code>Chart</code> for a new or existing axis.
     *  <p>
     *  @param label The (initial) axis label.
     *  @param listener Listener.
     */
    public YAxis(String label, YAxisListener listener)
    {
        super(false, label, new Ticks(), new LinearTransform());
        this.listener = listener;
    }
    
    /** @return Something useful for debugging
     *  @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "YAxis '" + label + "'";
    }

    /** Hide or display an axis?
     *  Does not affect the traces of the axis.
     *  They will be displayed even if the axis itself is not drawn.
     */
    public void setVisible(final boolean visible)
    {
        this.visible  = visible;
    }

    /** @return <code>true</code> if axis is visible
     *  @see #setVisible(boolean)
     */
    public boolean isVisible()
    {
        return visible;
    }
    
    /** Set a new label. */
    public final void setLabel(String new_label)
    {
        label = new_label;
        fireEvent(YAxisListener.Aspect.LABEL);
    }
    
    /** Configure the Y-Axis to use a log. scale or not.
     *  <p>
     *  Initial default is <code>false</code>, i.e. linear scale.
     */ 
    public final void setLogarithmic(boolean use_log)
    { 
        ticks = (use_log ? new LogTicks() : new Ticks());
        transform = (use_log ? new LogTransform() : new LinearTransform());
        // re-initialize the transform; set 'dirty' ticks
        setValueRange(low_value, high_value);
    }
    
    /** @return <code>true</code> if the axis is logarithmic. */
    public boolean isLogarithmic()
    {
        return ticks instanceof LogTicks;
    }

    /** Fire given event. */
    protected final void fireEvent(YAxisListener.Aspect what)
    {   // Silly, but maybe there'll once be a list of listeners?
        listener.changedYAxis(what, this);
    }
    
    /** Add a trace to the axis.
     *  <p>
     *  Not meant for end users, the trace is supposed to add itself.
     */
    public void addTrace(Trace trace)
    {
        if (traces.contains(trace))
            throw new Error(
               "Internal YAxis error, Trace is added multiple times"); //$NON-NLS-1$
        traces.add(trace);
    }

    /** Remove a trace from the axis.
     *  <p>
     *  Not meant for end users, the trace is supposed to remove itself.
     */
    public void removeTrace(Trace trace)
    {
        if (!traces.contains(trace))
            throw new Error("Internal YAxis error, unknown trace"); //$NON-NLS-1$
        traces.remove(trace);
    }
    
    /** @return Returns the number of traces. */
    public final int getNumTraces()
    {   return traces.size();  }
    
    /** @return Returns the trace of given index. */
    public final Trace getTrace(int index)
    {   return traces.get(index); }

    /** Add a marker.
     *  @param position The 'x' position.
     *  @param value The 'y' location or value.
     *  @param text The text to display
     */
    public final void addMarker(final Marker marker)
    {
        markers.add(marker);
        fireEvent(YAxisListener.Aspect.MARKER);
    }
    
    /** @return Array of current markers. */
    public final Marker[] getMarkers()
    {
        final Marker result[] = new Marker[markers.size()];
        return markers.toArray(result);
    }

    /** @return <code>true</code> if any markers are currently selected */
    public final boolean haveSelectedMarkers()
    {
        for (Marker marker : markers)
            if (marker.isSelected())
                return true;
        return false;
    }
    
    /** Toggle the selection of markers under given screen coordinates.
     *  @return Removed <code>true</code> if any marker was touched
     */
    public final boolean selectMarkers(int x, int y)
    {
        boolean anything = false;
        for (Marker marker : markers)
        {
            final Rectangle area = marker.getScreenCoords();
            if (area.contains(x, y))
            {
                marker.select(! marker.isSelected());
                anything = true;
            }
        }
        if (anything)
            fireEvent(YAxisListener.Aspect.SELECTION);
        return anything;
    }
    
    /** Remove all markers from this axis. */
    public final void removeMarkers()
    {
        markers.clear();
        fireEvent(YAxisListener.Aspect.MARKER);
    }

    /** Remove all markers that are currently selected from this axis. */
    public final void removeSelectedMarkers()
    {
        int i = 0;
        while (i < markers.size())
        {
            Marker marker = markers.get(i);
            if (marker.isSelected())
                markers.remove(i);
            else
                ++i;
        }        
        fireEvent(YAxisListener.Aspect.MARKER);
    }
    
    /** Set the 'selected' state of this axis.
     *  <p>
     *  If this actually changes the selection, the axis will notify the listener
     *  so that it can redraw the Y axes.
     *  @param selected The new state.
     */
    public final void setSelected(boolean selected)
    {
        if (this.selected == selected)
            return;
        this.selected = selected;
        fireEvent(YAxisListener.Aspect.SELECTION);
    }

    /** @return Returns <code>true</code> if this axis is selected. */
    public final boolean isSelected()
    {   return selected; }

    /** @return <code>true</code> if auto-scale is enabled. */
    public final boolean getAutoScale()
    {   return auto_scale; }

    /** @param auto_zoom <code>true</code> enables auto-scale.
     *  @see #autozoom(XAxis)
     */
    public final void setAutoScale(boolean auto_scale)
    {   this.auto_scale = auto_scale; }

    @Override
    public boolean setValueRange(double low, double high)
    {
        // Does this change anything?
        if (this.low_value == low  &&  this.high_value == high)
            return false; // no change
        if (! super.setValueRange(low, high))
            return false; // no change
        // Tell the world.
        fireEvent(YAxisListener.Aspect.RANGE);
        return true;
    }
    
    /** Auto-Zoom the value range of this Y axis to include all traces.
     *  <p>
     *  This call forces an auto-zoom.
     *  In addition, one can also set the auto-scale flag of the axis,
     *  which will cause the axis to auto-zoom automatically
     *  whenever it's painted
     *  (actually, the chart will auto-zoom via this very method
     *   just before painting the axis).
     *  @param xaxis The xaxis is required to obtain the visible sample range.
     *  @see #setAutoScale(boolean)
     */
    @SuppressWarnings("nls")
    public final void autozoom(XAxis xaxis)
    {
        if (Chart.debug)
            System.out.println("Autozoom " + this + " ...");
        final AxisRangeLimiter limiter = new AxisRangeLimiter(xaxis);
        double low = Double.MAX_VALUE;
        double high = -Double.MAX_VALUE;
        for (Trace trace : traces)
        {
            final ChartSampleSequence samples = trace.getSampleSequence();
            // Lock samples so they don't change on us.
            synchronized (samples)
            {   // Any data at all?
                if (samples.size() < 1)
                    continue;
                // Autozoom only the visible part, i.e. xaxis low .. high
                final int i0 = limiter.getLowIndex(samples);
                final int i1 = limiter.getHighIndex(samples);
                for (int i=i0; i<=i1; ++i)
                {
                    final ChartSample sample = samples.get(i);
                    if (sample.haveMinMax())
                    {
                        final double min = sample.getMinY();
                        // Ignore infinite numbers
                        if (Double.isInfinite(min)  || Double.isNaN(min))
                            continue;
                        // Otherwise update low
                        if (min < low)
                            low = min;
                        // Same for max
                        final double max = sample.getMaxY();
                        if (Double.isInfinite(max)  || Double.isNaN(max))
                            continue;
                        if (max > high)
                            high = max;
                    }
                    else
                    {
                        final double y = sample.getY();
                        // Ignore infinite numbers
                        if (Double.isInfinite(y)  || Double.isNaN(y))
                            continue;
                        // Otherwise update low, high.
                        if (y < low)
                            low = y;
                        if (y > high)
                            high = y;
                    }
                }
            }
        }
        // Determined low..high.
        if (low == high)
        {	// If low equals high we'll move the trace to the middle.
            low -= 1.0;
            high += 1.0;
        }
        if (low >= high)
            return; // give up
        if (isLogarithmic())
        {   // Perform adjustment in log space.
            // But first, refuse to deal with <= 0
            if (low <= 0.0)
                low = 1;
            if (high <= low)
                high = 100;
            low = Log10.log10(low);
            high = Log10.log10(high);
        }
        // Add a little room above & below the exact value range
        final double extra = (high - low) * 0.01;
        low = low - extra;
        high = high + extra;
        if (isLogarithmic())
        {
            low = Log10.pow10(low);
            high = Log10.pow10(high);
        }
        if (!setValueRange(low, high))
            return; // was a NOP for some reason
        if (Chart.debug)
            System.out.println("Autozoom " + this + " to "
                               + low + " ... " + high);
    }
    
    /** Zoom axis to the default range of its traces.
     *  If a traces have no default display range, auto-zoom.
     */ 
    public final void setDefaultZoom(XAxis xaxis)
    {
        double low = Double.MAX_VALUE;
        double high = -Double.MAX_VALUE;
        for (Trace trace : traces)
        {
            ChartSampleSequence samples = trace.getSampleSequence();
            Range range = samples.getDefaultRange();
            if (range != null)
            {   // Expand low..high to include trace's default range.
                if (low > range.getLow())
                    low = range.getLow();
                if (high < range.getHigh())
                    high = range.getHigh();
            }
        }
        if (low < high)
            setValueRange(low, high);
        else
            autozoom(xaxis);
    }

    /** The axis (label) color.
     *  <p>
     *  If there is only a single trace on this axis, that trace's color
     *  is used.
     *  Otherwise, a default color is used.
     *
     *  @return Returns the color or null. Do NOT dispose.
     */
    public final Color getColor()
    {
        if (traces.size() == 1)
            return traces.get(0).getColor();
        return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
    }
    
    /** Seach all traces on this axis, return the sample that's closest
     *  to the given coordinates.
     *  @param xaxis The XAxis over which the YAxis is drawn
     *  @param xval X value in value space
     *  @param yval Y value in value space
     */
    public final TraceSample getClosestSample(Axis xaxis, double xval, double yval)
    {
        // N determines the pixel range that we'll search
        final int N = 10;
        ChartSample best_sample = null;
        Trace best_trace = null;
        long closest = Long.MAX_VALUE;
        // Convert the position/value into screen coordinates,
        // since we have to seach in screen space.
        int x0 = xaxis.getScreenCoord(xval);
        int y0 = this.getScreenCoord(yval);
        for (Trace trace : traces)
        {
            ChartSampleSequence samples = trace.getSampleSequence();
            synchronized (samples)
            {
                // Check vicinity of the given x (position, time stamp)
                int i0 = ChartSampleSearch.findClosestSample(samples, xval);
                // Go 'left' for N pixels
                for (int i=i0; i>=0; --i)
                {
                    ChartSample sample = samples.get(i);
                    int x = xaxis.getScreenCoord(sample.getX());
                    if (x < x0 - N)
                        break;
                    int y = getScreenCoord(sample.getY());
                    int dx = x0-x;
                    int dy = y0-y;
                    long dist = dx*dx + dy*dy;
                    if (dist < closest)
                    {
                        closest = dist;
                        best_trace = trace;
                        best_sample = sample;
                    }
                }
                // Go 'right' for N pixels
                for (int i=i0+1; i<samples.size(); ++i)
                {
                    ChartSample sample = samples.get(i);
                    int x = xaxis.getScreenCoord(sample.getX());
                    if (x > x0 + N)
                        break;
                    int y = getScreenCoord(sample.getY());
                    int dx = x0-x;
                    int dy = y0-y;
                    long dist = dx*dx + dy*dy;
                    if (dist < closest)
                    {
                        closest = dist;
                        best_trace = trace;
                        best_sample = sample;
                    }
                }
            }
        }
        if (best_trace == null)
            return null;
        return new TraceSample(best_trace, best_sample);
    }
    
    /** @return Width (approximated) of this axis in pixels */
    public int getPixelWidth(final GC gc)
    {
        if (!visible)
            return 0;
        final Point char_size = gc.textExtent("X"); //$NON-NLS-1$
        // Room for label (vertical) + value text + tick markers.
        return 2*char_size.y + TICK_LENGTH;
    }
    
    /** Paint the axis.
     *  <p>
     *  Does not paint any series data, only the axis (labels, ticks, ...)
     *  @param event Clipping information from the paint event is used for optimization)
     */
    @SuppressWarnings("nls")
    public void paint(final Color grid_color, final PaintEvent event)
    {
        if (! (visible &&
               region.intersects(event.x, event.y, event.width, event.height)))
            return;
        if (Chart.debug)
            System.out.println("paint axis '" + getLabel() + "', "
                          + region.height + " pixel heigh");
        final GC gc = event.gc;
        final Point char_size = gc.textExtent("X"); //$NON-NLS-1$
        
        final Color old_bg = gc.getBackground();
        
        // Axis and Tick marks
/*
          if (selected)
        {	// Fill yaxis background with grid color.
        	gc.setBackground(grid_color);
        	gc.fillRectangle(region.x + 1, region.y, region.width - 2, region.height);
        	// "Thick" line
            gc.drawRectangle(
                region.x + region.width-2,
                region.y, 
                1,
                region.height-1);
        }
        else
 */
         // Simple line for the axis
            gc.drawLine(
                region.x + region.width-1,
                region.y, 
                region.x + region.width-1,
                region.y + region.height-1);
        computeTicks(gc);
        double last_tick = Double.NaN;
        for (double tick = ticks.getStart();
            tick <= high_value;
            tick = ticks.getNext(tick))
        {
            // Major tick marks
            int y = getScreenCoord(tick);
            gc.drawLine(region.x + region.width - TICK_LENGTH, y, 
                        region.x + region.width - 1, y);
            // Tick Label
            String mark = ticks.format(tick);
            Point mark_size = gc.textExtent(mark);
            GraphicsUtils.drawVerticalText(mark,
                    region.x + region.width - TICK_LENGTH - char_size.y,
                    y  - mark_size.x/2,
                    gc, SWT.UP);
            // Minor ticks
            if (show_minor_ticks)
            {
                if (last_tick != Double.NaN)
                {
                    final int MINOR_TICKS = 5;
                    double dist = (tick - last_tick) / MINOR_TICKS;
                    if (dist > 0)
                        for (int i=1; i<MINOR_TICKS; ++i)
                        {
                            double minor = last_tick + dist * i;
                            int my = getScreenCoord(minor);
                            gc.drawLine(region.x + region.width - (2*TICK_LENGTH)/3, my, 
                                        region.x + region.width - 1, my);
                        }
                }
                last_tick = tick;
            }
        }
        
        paintLabel(gc);
        if (Chart.debug)
            gc.drawRectangle(region.x, region.y, region.width-1, region.height-1);
        
        gc.setBackground(old_bg);
    }

    protected void paintLabel(GC gc)
    {
        // Label: At left edge of region, vertically apx. centered
        Color fg = gc.getForeground();
        gc.setForeground(getColor());
        Point label_size = gc.textExtent(getLabel());
    
        GraphicsUtils.drawVerticalText(getLabel(),
                region.x + 1,
                region.y + (region.height-label_size.x)/2,
                gc, SWT.UP);
    
        gc.setForeground(fg);
    }
    
    /** Draw all the markers of this axis */
    public void paintMarkers(GC gc, Axis xaxis)
    {
        for (Marker marker : markers)
            marker.paint(gc, xaxis, this);
    }
}
