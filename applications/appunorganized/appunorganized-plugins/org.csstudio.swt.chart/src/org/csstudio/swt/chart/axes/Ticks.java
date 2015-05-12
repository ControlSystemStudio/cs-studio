/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;

import java.text.NumberFormat;

import org.eclipse.swt.graphics.GC;

/** Helper for creating tick marks.
 *  <p>
 *  Computes tick positions, formats tick labels.
 *  Doesn't perform the actual drawing.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Ticks implements ITicks
{
    /** How many percent of the available space should be used for labels? */
    protected static final int FILL_PERCENTAGE = 90;

    /** First tick mark value. */
    protected double start;

    /** Distance between tick mark value. */
    protected double distance;

    /** Precision used for printing tick labels .*/
    protected int precision;

    /** Format helper for the number. */
    protected NumberFormat num_fmt = NumberFormat.getNumberInstance();

    public Ticks()
    {
        num_fmt.setGroupingUsed(false);
    }

    @Override
    public String toString()
    {
        return String.format("Ticks start=%g, distance=%g, precision=%d",
                             start, distance, precision);
    }

    /** Compute tick information, trying to auto-fit the screen region.
     *
     *  @param low Low limit of the axis range.
     *  @param high High limit of the axis range.
     *  @param gc The graphic context.
     *  @param screen_width Width of axis on screen.
     */
    void compute(double low, double high, GC gc, int screen_width)
    {
        if (low > high)
            throw new Error("Tick range is not ordered: " + low + " > " + high);
        if (low == high)
        {
            low = high - 1;
            high += 1;
        }
        double range = Math.abs(high-low);
        // Log gymnastics:
        // 0-10,   range 10,   log 1  -> use "5",     precision 0
        // 0-1,    range 1,    log 0  -> use "0.5",   precision 1
        // 0-0.1,  range 0.1,  log -1 -> use "0.05",  precision 2
        // 0-0.01, range 0.01, log -2 -> use "0.005", precision 3
        double log = Math.log10(range);
        // The floor(1.001..) tweak makes e.g. -10...10 work the same as 0..10.
        int rounded_log = (int)Math.floor(1.001*log);
        // Precision: 0 or more trailing digits
        if (range >= 10.0)
            precision = 0;
        else
            precision = 1 - rounded_log;
        double prec_delta = Math.pow(10.0, rounded_log-1);

        // Determine minimum label distance on the screen, using some
        // percentage of the available screen space.
        // Guess the label width, using the two extrema.
        // For testing, allow gc == null.
        String label = format(low);
        int label_width = (gc != null) ? gc.textExtent(label).x : 100;
        label = format(high);
        int label_width2 = (gc != null) ? gc.textExtent(label).x : 100;
        if (label_width < label_width2)
            label_width = label_width2;
        int num_that_fits = screen_width/label_width*FILL_PERCENTAGE/100;
        if (num_that_fits < 1)
            num_that_fits = 1;
        double min_distance = range / num_that_fits;
        // Round up to the precision used to display values
        distance = Math.ceil(min_distance / prec_delta) * prec_delta;
        // You would think that start >= low, but sometimes rounding
        // errors result in start < low.
        start = Math.ceil(low / prec_delta) * prec_delta;
        if (distance <= 0.0)
            throw new Error("Broken tickmark computation");
    }

    /** @return Returns the value of the start tick. */
    @Override
    public final double getStart()
    {
        return start;
    }

    /** @return Returns the next tick, following a given tick mark. */
    @Override
    public double getNext(double tick)
    {
        return tick + distance;
    }

    /** @return Returns the number formatted according to the tick precision. */
    @Override
    public final String format(double num)
    {
        return format(num, 0);
    }

    /** @return Returns the number formatted with some extra precision. */
    @Override
    public String format(double num, int extra_precision)
    {
        int p = precision + extra_precision;
        num_fmt.setMinimumFractionDigits(p);
        num_fmt.setMaximumFractionDigits(p);
        return num_fmt.format(num);
    }
}
