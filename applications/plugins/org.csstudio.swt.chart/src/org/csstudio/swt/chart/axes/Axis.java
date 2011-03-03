/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;

import java.util.logging.Level;

import org.csstudio.swt.chart.Activator;
import org.csstudio.swt.chart.Chart;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/** Base class for X and Y axes.
 *  <p>
 *  Handles the basic screen-to-value transformation.
 *  <p>
 *  @author Kay Kasemir
 */
public class Axis
{
    protected static final int TICK_LENGTH = 10;

    /** Is this a horizontal axis? Otherwise: Vertical. */
    private boolean horizontal;

    /** The screen region of this axis. */
    protected Rectangle region = new Rectangle(0, 0, 1, 1);

    /** Transformation from value into screen coordinates. */
    protected ITransform transform;

    /** Low end of value range. */
    protected double low_value;

    /** High end of value range. */
    protected double high_value;

    /** Helper for computing the tick marks. */
    protected Ticks ticks;

    /** Do we need to re-compute the ticks? */
    private boolean dirty_ticks;

    /** Low end of screen range. */
    private int low_screen;

    /** High end of screen range. */
    private int high_screen;

    /** Axis label. */
    protected String label;

    /** Constructor. */
    Axis(final boolean horizontal, final String label, final Ticks ticks)
    {
        this(horizontal, label, ticks, new LinearTransform());
    }

    /** Constructor. */
    Axis(final boolean horizontal, final String label, final Ticks ticks,
         final ITransform transform)
    {
        this.horizontal = horizontal;
        this.transform = transform;
        this.ticks = ticks;
        dirty_ticks = true;
        low_screen = 0;
        high_screen = 10;
        low_value = 0;
        high_value = 10;
        this.label = label;
    }

    /** @return Returns the label. */
    final public String getLabel()
    {
        return label;
    }

    /** Get the screen coordinates of the given value.
     *  <p>
     *  Values are mapped from value to screen coordinates via
     *  'transform', except for infinite values, which get mapped
     *  to the edge of the screen range.
     *
     *  @return Returns the value transformed in screen coordinates.
     */
    final public int getScreenCoord(final double value)
    {
        // Map undefined values to bottom of range
        if (Double.isInfinite(value))
            return low_screen;
        // Catch values beyond screen range, because the SWT clipping
        // doesn't work when we are way beyond the screen range.
        // In principle, we should use the (x,y) coordinates of the line ends
        // inside and outside the screen range and determine the intersection
        // with the screen.
        // As long as we only perform staircase plots, this simple
        // mapping of a single x or y coordinate onto the screen border works.
        if (value <= low_value)
            return low_screen;
        if (value >= high_value)
            return high_screen;
        return (int)(transform.transform(value)+0.5);
    }

    /** @return Returns screen coordinate transformed into a value. */
    final public double getValue(final int coord)
    {
        return transform.inverse(coord);
    }

    /** @return Returns low end of axis value range. */
    final public double getLowValue()
    {
        return low_value;
    }

    /** @return Returns high end of axis value range. */
    final public double getHighValue()
    {
        return high_value;
    }

    /** Set the new value range.
     *  @return <code>true</code> if this actually did something.
     */
    @SuppressWarnings("nls")
    public boolean setValueRange(final double low, final double high)
    {
        // Any change at all?
        if (low == low_value  &&  high == high_value)
            return false;
        if (low >= high)
        {
            Activator.getLogger().log(Level.WARNING,
                "Axis {0}: Cannot set value range to {1} ... {2}",
                  new Object[] { getLabel(), low, high });
            return false;
        }
        dirty_ticks = true;
        low_value = low;
        high_value = high;
        transform.config(low_value, high_value, low_screen, high_screen);
        return true;
    }

    /** Tell the axis where it is on the screen.
     *  @param region The on-screen region to set.
     *  @see #setScreenRange(int, int)
     */
    public void setRegion(final int x, final int y,
                          final int width, final int height)
    {
        region.x = x;
        region.y = y;
        region.width = width;
        region.height = height;

        if (horizontal)
            setScreenRange(x, x + width-1);
        else
            setScreenRange(y + height-1, y);
    }

    /** Update the screen coordinate range of the axis. */
    protected void setScreenRange(final int low, final int high)
    {
        dirty_ticks = true;
        low_screen = low;
        high_screen = high;
        transform.config(low_value, high_value, low_screen, high_screen);
    }

    /** @return Returns the region used by this axis on the screen. */
    final public Rectangle getRegion()
    {
        return region;
    }

    /** @return Tick mark information. */
    public ITicks getTicks()
    {
        return ticks;
    }

    @SuppressWarnings("nls")
    protected void computeTicks(GC gc)
    {
        if (dirty_ticks)
        {
            if (Chart.debug)
                System.out.println("Compute ticks for '" + label + "'");
            if (horizontal)
                ticks.compute(low_value, high_value, gc, region.width);
            else
                ticks.compute(low_value, high_value, gc, region.height);
            dirty_ticks = false;
        }
    }

    @Override
    public String toString()
    {
        return String.format("Axis '%s', value %g...%g", //$NON-NLS-1$
                getLabel(), getLowValue(), getHighValue());
    }
}
