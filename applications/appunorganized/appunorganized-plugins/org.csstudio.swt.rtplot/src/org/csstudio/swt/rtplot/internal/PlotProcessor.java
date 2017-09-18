/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.csstudio.swt.rtplot.Activator;
import org.csstudio.swt.rtplot.Axis;
import org.csstudio.swt.rtplot.AxisRange;
import org.csstudio.swt.rtplot.Messages;
import org.csstudio.swt.rtplot.Trace;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.csstudio.swt.rtplot.data.PlotDataProvider;
import org.csstudio.swt.rtplot.data.PlotDataSearch;
import org.csstudio.swt.rtplot.data.ValueRange;
import org.csstudio.swt.rtplot.internal.util.Log10;
import org.csstudio.swt.rtplot.undo.AddAnnotationAction;
import org.csstudio.swt.rtplot.undo.ChangeAxisRanges;
import org.eclipse.swt.graphics.Point;

/** Helper for processing traces of a plot
 *  in a thread pool to avoid blocking UI thread.
 *  @param <XTYPE> Data type of horizontal {@link Axis}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PlotProcessor<XTYPE extends Comparable<XTYPE>>
{
    final private static ExecutorService thread_pool = Executors.newWorkStealingPool();

    final private Plot<XTYPE> plot;

    /** @param plot Plot on which this processor operates */
    public PlotProcessor(final Plot<XTYPE> plot)
    {
        this.plot = plot;
    }

    /** Submit background job to determine value range for y axis for values within the
     * specified x axis.
     *  @param data {@link PlotDataProvider} with values
     *  @param x_range {@link AxisRange} covering visible part of plot
     *  @return {@link Future} to {@link ValueRange}
     */
    public Future<ValueRange> determineValueRange(final PlotDataProvider<XTYPE> data, final AxisRange<XTYPE> x_range)
    {
        return thread_pool.submit(new Callable<ValueRange>()
        {
            @Override
            public ValueRange call() throws Exception
            {
                double low = Double.MAX_VALUE;
                double high = -Double.MAX_VALUE;
                final PlotDataSearch<XTYPE> search = new PlotDataSearch<>();
                data.getLock().lock();
                try
                {
                    if (data.size() > 0)
                    {   // Consider first sample at-or-before start
                        int start = search.findSampleLessOrEqual(data, x_range.getLow());
                        if (start < 0)
                            start = 0;
                        // Last sample is the one just inside end of range.
                        int stop = search.findSampleLessOrEqual(data, x_range.getHigh());
                        if (stop < 0)
                            stop = 0;
                        // If data is completely outside the x_range,
                        // we end up using just data[0]
                        // Check [start .. stop], including stop
                        for (int i=start; i<=stop; ++i)
                        {
                            final PlotDataItem<XTYPE> item = data.get(i);
                            final double value = item.getValue();
                            if (! Double.isFinite(value))
                                continue;
                            if (value < low)
                                low = value;
                            if (value > high)
                                high = value;
                        }
                    }
                }
                finally
                {
                    data.getLock().unlock();
                }
                return new ValueRange(low, high);
            }
        });
    }

    /** Submit background job to determine value range
     *  @param axis {@link YAxisImpl} for which to determine range
     *  @param x_axis {@link AxisRange} over which range is to be determined
     *  @return {@link Future} to {@link ValueRange}
     */
    public Future<ValueRange> determineValueRange(final YAxisImpl<XTYPE> axis, final AxisPart<XTYPE> x_axis)
    {
        return thread_pool.submit(new Callable<ValueRange>()
        {
            @Override
            public ValueRange call() throws Exception
            {
                // In parallel, determine range of all traces in this axis
                final List<Future<ValueRange>> ranges = new ArrayList<Future<ValueRange>>();
                for (Trace<XTYPE> trace : axis.getTraces())
                    ranges.add(determineValueRange(trace.getData(), x_axis.getValueRange()));

                // Merge the trace ranges into overall axis range
                double low = Double.MAX_VALUE;
                double high = -Double.MAX_VALUE;
                for (Future<ValueRange> result : ranges)
                {
                    final ValueRange range = result.get();
                    if (range.getLow() < low)
                        low = range.getLow();
                    if (range.getHigh() > high)
                        high = range.getHigh();
                }
                return new ValueRange(low, high);
            }
        });
    }

    /** Round value range up/down to add a little room above & below the exact range.
     *  This results in "locking" to a nice looking range for a while
     *  until a new sample outside of the rounded range is added.
     *
     *  @param low Low and
     *  @param high high end of value range
     *  @return Adjusted range
     */
    public ValueRange roundValueRange(final double low, final double high)
    {
        final double size = Math.abs(high-low);
        if (size > 0)
        {   // Add 2 digits to the 'tight' order of magnitude
            final double order_of_magnitude = Math.floor(Log10.log10(size))-2;
            final double round = Math.pow(10, order_of_magnitude);
            return new ValueRange(Math.floor(low / round) * round,
                                         Math.ceil(high / round) * round);
        }
        else
            return new ValueRange(low, high);
    }

    /** Stagger the range of axes */
    public void stagger()
    {
        thread_pool.execute(() ->
        {
            final double GAP = 0.1;
            // Arrange all axes so they don't overlap by assigning 1/Nth of
            // the vertical range to each one
            // Determine range of each axes' traces in parallel
            final List<YAxisImpl<XTYPE>> y_axes = new ArrayList<>();
            final List<AxisRange<Double>> original_ranges = new ArrayList<>();
            final List<AxisRange<Double>> new_ranges = new ArrayList<>();
            final List<Future<ValueRange>> ranges = new ArrayList<Future<ValueRange>>();
            for (YAxisImpl<XTYPE> axis : plot.getYAxes())
            {
                y_axes.add(axis);
                // As fallback, assume that new range matches old range
                new_ranges.add(axis.getValueRange());
                original_ranges.add(axis.getValueRange());
                ranges.add(determineValueRange(axis, plot.getXAxis()));
            }
            final int N = y_axes.size();
            for (int i=0; i<N; ++i)
            {
                final YAxisImpl<XTYPE> axis = y_axes.get(i);
                // Does axis handle itself in another way?
                if (axis.isAutoscale())
                   continue;

               // Fetch range of values on this axis
               final ValueRange axis_range;
               try
               {
                   axis_range = ranges.get(i).get();
               }
               catch (Exception ex)
               {
                   Activator.getLogger().log(Level.WARNING, "Axis stagger error", ex);
                   continue;
               }

               // Skip axis which for some reason cannot determine its range
               double low = axis_range.getLow();
               double high = axis_range.getHigh();
               if (low > high)
                   continue;
               if (low == high)
               {   // Center trace with constant value (empty range)
                   final double half = Math.abs(low/2);
                   low -= half;
                   high += half;
               }
               if (axis.isLogarithmic())
               {   // Transition into log space
                   low = Log10.log10(low);
                   high = Log10.log10(high);
               }
               double span = high - low;
               // Make some extra space
               low -= GAP*span;
               high += GAP*span;
               span = high-low;

               // With N axes, assign 1/Nth of the vertical plot space to this axis
               // by shifting the span down according to the axis index,
               // using a total of N*range.
               low -= (N-i-1)*span;
               high += i*span;

               final ValueRange rounded = roundValueRange(low, high);
               low = rounded.getLow();
               high = rounded.getHigh();

               if (axis.isLogarithmic())
               {   // Revert from log space
                   low = Log10.pow10(low);
                   high = Log10.pow10(high);
               }

               // Sanity check for empty traces
               if (low < high  &&
                   !Double.isInfinite(low) && !Double.isInfinite(high))
                   new_ranges.set(i, new AxisRange<Double>(low, high));
            }

            // 'Stagger' tends to be on-demand,
            // or executed infrequently as archived data arrives after a zoom operation
            // -> Use undo, which also notifies listeners
            plot.getUndoableActionManager().execute(new ChangeAxisRanges<>(plot, Messages.Zoom_Stagger, y_axes, original_ranges, new_ranges));
        });
    }

    /** Compute cursor values for the various traces
     *
     *  <p>Updates the 'selected' sample for each trace,
     *  and sends valid {@link CursorMarker}s to the {@link Plot}
     *
     *  @param cursor_x Pixel location of cursor
     *  @param location Corresponding position on X axis
     *  @param callback Will be called with markers for the cursor location
     */
    public void updateCursorMarkers(final int cursor_x, final XTYPE location, final Consumer<List<CursorMarker>> callback)
    {
        // Run in thread
        thread_pool.execute(() ->
        {
            final List<CursorMarker> markers = new ArrayList<>();
            final PlotDataSearch<XTYPE> search = new PlotDataSearch<>();
            for (YAxisImpl<XTYPE> axis : plot.getYAxes())
                for (TraceImpl<XTYPE> trace : axis.getTraces())
                {
                    final PlotDataProvider<XTYPE> data = trace.getData();
                    final PlotDataItem<XTYPE> sample;
                    data.getLock().lock();
                    try
                    {
                        final int index = search.findSampleLessOrEqual(data, location);
                        sample = index >= 0 ? data.get(index) : null;
                    }
                    finally
                    {
                        data.getLock().unlock();
                    }
                    trace.selectSample(sample);
                    if (sample == null)
                        continue;
                    final double value = sample.getValue();
                    if (Double.isFinite(value)  &&  axis.getValueRange().contains(value))
                    {
                        String label = axis.getTicks().formatDetailed(value);
                        final String units = trace.getUnits();
                        if (! units.isEmpty())
                            label += " " + units;
                        markers.add(new CursorMarker(cursor_x, axis.getScreenCoord(value), trace.getColor(), label));
                    }
                }
            Collections.sort(markers);
            callback.accept(markers);
        });
    }

    /** @param plot Plot where annotation is added
     *  @param trace Trace to which a annotation should be added
     *  @param text Text for the annotation
     */
    public void createAnnotation(final Trace<XTYPE> trace, final String text)
    {
        final AxisPart<XTYPE> x_axis = plot.getXAxis();
        // Run in thread
        thread_pool.execute(() ->
        {
            final AxisRange<Integer> range = x_axis.getScreenRange();
            XTYPE location = x_axis.getValue((range.getLow() + range.getHigh())/2);
            final PlotDataSearch<XTYPE> search = new PlotDataSearch<>();
            final PlotDataProvider<XTYPE> data = trace.getData();
            double value= Double.NaN;
            data.getLock().lock();
            try
            {
                final int index = search.findSampleGreaterOrEqual(data, location);
                if (index >= 0)
                {
                    location = data.get(index).getPosition();
                    value = data.get(index).getValue();
                }
                else
                    location = null;
            }
            finally
            {
                data.getLock().unlock();
            }
            if (location != null)
                plot.getUndoableActionManager().execute(
                    new AddAnnotationAction<XTYPE>(plot, new AnnotationImpl<XTYPE>(false, trace, location, value, new Point(20, -20), text)));
        });
    }

    public void updateAnnotation(final AnnotationImpl<XTYPE> annotation, final XTYPE location)
    {
        // Run in thread
        thread_pool.execute(() ->
        {
            final PlotDataSearch<XTYPE> search = new PlotDataSearch<>();
            final PlotDataProvider<XTYPE> data = annotation.getTrace().getData();
            XTYPE position;
            double value;
            data.getLock().lock();
            try
            {
                final int index = search.findSampleLessOrEqual(data, location);
                if (index < 0)
                    return;
                position = data.get(index).getPosition();
                value = data.get(index).getValue();
            }
            finally
            {
                data.getLock().unlock();
            }
            plot.updateAnnotation(annotation, position, value, annotation.getOffset());
        });
    }

    /**
     * Perform autoscale for all axes that are marked as such.
     * Autoscale only for the data that is visible on the plot.
     */
    public void autoscale()
    {
        // Determine range of each axes' traces in parallel
        final List<YAxisImpl<XTYPE>> y_axes = new ArrayList<>();
        final List<Future<ValueRange>> ranges = new ArrayList<Future<ValueRange>>();
        for (YAxisImpl<XTYPE> axis : plot.getYAxes())
            if (axis.isAutoscale())
            {
                y_axes.add(axis);
                ranges.add(determineValueRange(axis, plot.getXAxis()));
            }
        final int N = y_axes.size();
        for (int i=0; i<N; ++i)
        {
            final YAxisImpl<XTYPE> axis = y_axes.get(i);
            try
            {
                final ValueRange new_range = ranges.get(i).get();
                double low = new_range.getLow(), high = new_range.getHigh();
                if (low > high)
                    continue;
                if (low == high)
                {   // Center trace with constant value (empty range)
                    final double half = Math.abs(low/2);
                    low -= half;
                    high += half;
                }
                if (axis.isLogarithmic())
                {   // Perform adjustment in log space.
                    // But first, refuse to deal with <= 0
                    if (low <= 0.0)
                        low = 1;
                    if (high <= low)
                        high = 100;
                    low = Log10.log10(low);
                    high = Log10.log10(high);
                }
                final ValueRange rounded = roundValueRange(low, high);
                low = rounded.getLow();
                high = rounded.getHigh();
                if (axis.isLogarithmic())
                {
                    low = Log10.pow10(low);
                    high = Log10.pow10(high);
                }
                else
                {
                    // Stretch range a little bit
                    // (but not for log scale, where low just above 0
                    //  could be stretched to <= 0)
                    final double headroom = (high - low) * 0.05;
                    low -= headroom;
                    high += headroom;
                }
                // Autoscale happens 'all the time'.
                // Do not use undo, but notify listeners.
                if (low < high)
                    if (axis.setValueRange(low, high))
                        plot.fireYAxisChange(axis);
            }
            catch (Exception ex)
            {
                Activator.getLogger().log(Level.WARNING, "Axis autorange error for " + axis, ex);
            }
        }
    }
}
