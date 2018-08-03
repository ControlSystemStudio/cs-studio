/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import org.csstudio.swt.rtplot.Activator;
import org.csstudio.swt.rtplot.Axis;
import org.csstudio.swt.rtplot.AxisRange;
import org.csstudio.swt.rtplot.RTPlot;
import org.csstudio.swt.rtplot.SWTMediaPool;
import org.csstudio.swt.rtplot.internal.util.ScreenTransform;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.swt.widgets.Display;

/** Base class for X and Y axes.
 *  <p>
 *  Handles the basic screen-to-value transformation.
 *
 *  @param <T> Data type of this axis
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public abstract class AxisPart<T extends Comparable<T>> extends PlotPart implements Axis<T>
{
    protected static final int TICK_LENGTH = 10;

    protected static final int TICK_WIDTH = 3;

    protected static final int MINOR_TICK_LENGTH = 5;

    protected volatile boolean show_grid = false;

    private AtomicBoolean visible = new AtomicBoolean(true);

    private AtomicBoolean axisNameVisible = new AtomicBoolean(true);

    private Font labelFont;

    private Font scaleFont;

    /** Is this a horizontal axis? Otherwise: Vertical. */
    final private boolean horizontal;

    /** Transformation from value into screen coordinates. */
    protected volatile ScreenTransform<T> transform;

    /** Helper for computing the tick marks. */
    protected volatile Ticks<T> ticks;

    /** Do we need to re-compute the ticks? */
    protected volatile boolean dirty_ticks = true;

    /** Low end of value range. */
    protected AxisRange<T> range;

    /** Low end of screen range. */
    private volatile int low_screen = 0;

    /** High end of screen range. */
    private volatile int high_screen = 1;

    final private SWTMediaPool media;

    /** @param name Axis name
     *  @param listener {@link PlotPartListener}
     *  @param horizontal <code>true</code> if axis is horizontal
     *  @param low_value Initial low end of value range
     *  @param high_value Initial high end of value range
     *  @param transform {@link ScreenTransform}
     *  @param ticks {@link Ticks}
     */
    protected AxisPart(final String name, final PlotPartListener listener, final boolean horizontal,
         final T low_value, final T high_value,
         final ScreenTransform<T> transform,
         final Ticks<T> ticks)
    {
        super(name, listener);
        this.horizontal = horizontal;
        this.transform = Objects.requireNonNull(transform);
        this.range = new AxisRange<T>(low_value, high_value);
        this.ticks = Objects.requireNonNull(ticks);

        //TODO there might be a better way to create this resource
        media = new SWTMediaPool(Display.getCurrent());
    }

    /** {@inheritDoc} */
    @Override
    public void setLabelFont(FontData font) {
        this.labelFont = media.get(font);
        requestLayout();
    };

    /** {@inheritDoc} */
    @Override
    public Font getLabelFont() {
        return labelFont;
    };

    /** {@inheritDoc} */
    @Override
    public void setScaleFont(FontData font) {
        this.scaleFont = media.get(font);
        requestLayout();
    };

    /** {@inheritDoc} */
    @Override
    public Font getScaleFont() {
        return scaleFont;
    };


    /** {@inheritDoc} */
    @Override
    public boolean isGridVisible()
    {
        return show_grid;
    }

    /** {@inheritDoc} */
    @Override
    public void setGridVisible(final boolean grid)
    {
        if (show_grid == grid)
            return;
        show_grid = grid;
        requestLayout();
        requestRefresh();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isIndicatorLineVisible()
    {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setIndicatorLineVisible(final boolean state)
    {
    	return;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isAxisNameVisible()
    {
        return axisNameVisible.get();
    }

    /** {@inheritDoc} */
    @Override
    public void setAxisNameVisible(final boolean visible)
    {
        if (this.axisNameVisible.getAndSet(visible) != visible)
        {
            requestLayout();
            requestRefresh();
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isVisible()
    {
        return visible.get();
    }

    /** {@inheritDoc} */
    @Override
    public void setVisible(final boolean visible)
    {
        if (this.visible.getAndSet(visible) != visible)
        {
            requestLayout();
            requestRefresh();
        }
    }

    /** Called by {@link RTPlot} to determine layout.
     *  @param region Tentative on-screen region.
     *                X/Y location is actual location.
     *                For horizontal axis, x, y and width are set, and height is tentative.
     *                For vertical axis, x, y and height are set, and width is tentative.
     *  @param gc {@link GC} that can be used to determine font measurements
     *  @return For horizontal axis, desired height in pixels.
     *          For vertical axis, desired width in pixels.
     */
    abstract public int getDesiredPixelSize(Rectangle region, GC gc);

    /** {@inheritDoc} */
    @Override
    final public int getScreenCoord(final T value)
    {
        return (int)Math.round(transform.transform(value));
    }

    /** Update the axis scaling
     *  @param transform Transformation between values and pixels
     *  @param ticks Tick marks
     */
    final void updateScaling(final ScreenTransform<T> transform,
                              final Ticks<T> ticks)
    {
        synchronized (this)
        {
            this.transform = Objects.requireNonNull(transform);
            this.ticks = Objects.requireNonNull(ticks);
            transform.config(range.getLow(), range.getHigh(), low_screen, high_screen);
        }
        dirty_ticks = true;
        requestLayout();
    }

    /** @return Transformation between values and pixels */
    final public ScreenTransform<T> getScreenTransform()
    {
        return transform.copy();
    }

    /** {@inheritDoc} */
    @Override
    final public T getValue(final int coord)
    {
        return transform.inverse(coord);
    }

    /** {@inheritDoc} */
    @Override
    final synchronized public AxisRange<T> getValueRange()
    {
        return range;
    }

    /** {@inheritDoc} */
    @Override
    public boolean setValueRange(final T low, final T high)
    {
        synchronized (this)
        {   // Any change at all?
            if (low.equals(range.getLow())  &&  high.equals(range.getHigh()))
                return false;
            // Can axis handle this range?
            if (! ticks.isSupportedRange(low, high))
            {
                Activator.getLogger().log(Level.WARNING,
                        "Axis {0}: Bad value range {1} ... {2}",
                        new Object[] { getName(), low, high });
                return false;
            }
            range = new AxisRange<T>(low, high);
            transform.config(low, high, low_screen, high_screen);
        }
        dirty_ticks = true;
        requestLayout();
        requestRefresh();
        return true;
    }

    /** Zoom axis in or out around a central point that remains fixed
     *  @param center Center of the zoom in pixels
     *  @param factor Zoom factor. Larger 1 to zoom 'out', smaller than 1 to zoom 'in'
     */
    abstract public void zoom(final int center, final double factor);

    /** Pan axis
     *  @param original_range Original range of axis when starting to pan
     *  @param start Axis value where pan was started
     *  @param end Axis value to which to pan
     */
    abstract public void pan(final AxisRange<T> original_range, final T start, final T end);

    /** {@inheritDoc} */
    @Override
    public void setBounds(final int x, final int y,
                          final int width, final int height)
    {
        super.setBounds(x, y, width, height);
        if (horizontal)
            setScreenRange(x, x + width-1);
        else
            setScreenRange(y + height-1, y);
    }

    /** Update the screen coordinate range of the axis. */
    protected void setScreenRange(final int low, final int high)
    {
        synchronized (this)
        {
            if (low == low_screen  &&  high == high_screen)
                return;
            low_screen = low;
            high_screen = high;
        }
        dirty_ticks = true;
        final AxisRange<T> safe_range = range;
        transform.config(safe_range.getLow(), safe_range.getHigh(), low, high);
    }

    /** @return Pixel range on screen */
    public AxisRange<Integer> getScreenRange()
    {
        return new AxisRange<Integer>(low_screen, high_screen);
    }

    /** @return Tick mark information. */
    public Ticks<T> getTicks()
    {
        return ticks;
    }

    protected void computeTicks(final GC gc)
    {
        if (! dirty_ticks)
            return;
        final AxisRange<T> safe_range = range;
        if (horizontal)
            ticks.compute(safe_range.getLow(), safe_range.getHigh(), gc, getBounds().width);
        else
            ticks.compute(safe_range.getLow(), safe_range.getHigh(), gc, getBounds().height);
        dirty_ticks = false;
    }

    /** Invoked to paint the part.
     *
     *  <p>Is invoked on background thread.
     *  <p>Derived part can override, should invoke super.
     *
     *  @param gc {@link GC} for painting in background thread
     *  @param media {@link SWTMediaPool}
     *  @param plot_bounds Bounds of the plot, the area used for grid lines and traces
     */
    abstract public void paint(final GC gc, final SWTMediaPool media, final Rectangle plot_bounds);

    /** Draw a tick label, used both to paint the normal axis labels
     *  and for special, cursor-related tick locations.
     *
     *  @param gc GC to use
     *  @param media
     *  @param tick Location of the tick
     *  @param floating <code>true</code> to 'float' on top of usual tick labels
     */
    abstract public void drawTickLabel(final GC gc, final SWTMediaPool media, final T tick, final boolean floating);

    @Override
    public String toString()
    {
        return "Axis '" + getName() + "', range " + range.getLow() + " .. " + range.getHigh();
    }
}
