/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;

import org.csstudio.swt.rtplot.Activator;
import org.csstudio.swt.rtplot.internal.util.Log10;
import org.eclipse.swt.graphics.GC;

/** Helper for creating tick marks.
 *  <p>
 *  Computes tick positions, formats tick labels.
 *  Doesn't perform the actual drawing.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LinearTicks implements Ticks<Double>
{
    /** First tick mark value. */
    protected double start = 0.0;

    /** Distance between tick marks. */
    protected double distance = 1.0;

    /** Precision used for printing tick labels .*/
    protected int precision = 1;

    /** Format helper for the number. */
    protected NumberFormat num_fmt = createDecimalFormat(1);

    /** Threshold for order-of-magnitude to use exponential notation */
    protected long exponential_threshold = 4;

    /** @param order_of_magnitude If value range exceeds this threshold, use exponential notation */
    public void setExponentialThreshold(long order_of_magnitude)
    {
        exponential_threshold = order_of_magnitude;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSupportedRange(final Double low, final Double high)
    {
        if (! (Double.isFinite(low)  &&  Double.isFinite(high)))
            return false;
        final double span = high - low;
        // For now we require low < high.
        if (span <= 0.0)
            return false;
        // Avoid degraded axes like
        // 1000.00000000000001 .. 1000.00000000000002
        // where low + (high - low) == low,
        // i.e. tick computations will fail because
        // they reach the granularity of the Double type.
        return span > Math.ulp(low);
    }

    /** {@inheritDoc} */
    @Override
    public void compute(Double low, Double high, final GC gc, final int screen_width)
    {
        Activator.getLogger().log(Level.FINE, "Compute linear ticks, width {0}, for {1} - {2}",
                new Object[] { screen_width, low, high });

        // Determine range of values on axis
        if (! isSupportedRange(low, high))
            throw new Error("Unsupported range " + low + " .. " + high);
        if (low.equals(high))
        {
            low = high - 1;
            high += 1;
        }
        final double range = Math.abs(high-low);

        final long order_of_magnitude = Math.round(Log10.log10(range));

        // Determine precision for displaying numbers in this range.
        // Precision must be set to format test entries, which
        // are then used to compute ticks.
        if (Math.abs(order_of_magnitude) > exponential_threshold)
        {
            precision = 2;
            num_fmt = createExponentialFormat(precision);
        }
        else
        {
            precision = determinePrecision(range/2);
            num_fmt = createDecimalFormat(precision);
        }

        // Determine minimum label distance on the screen, using some
        // percentage of the available screen space.
        // Guess the label width, using the two extremes.
        final String low_label = format(low);
        final String high_label = format(high);
        final int label_width = Math.max(gc.textExtent(low_label).x, gc.textExtent(high_label).x);
        final int num_that_fits = Math.max(1,  screen_width/label_width*FILL_PERCENTAGE/100);
        final double min_distance = range / num_that_fits;

        // Round up to the precision used to display values
        distance = selectNiceStep(min_distance);
        if (distance <= 0.0)
            throw new Error("Broken tickmark computation");

        // Start at 'low' adjusted to a multiple of the tick distance
        start = Math.ceil(low / distance) * distance;
    }

    /** @param number A number
     *  @return Suggested precision, i.e. floating point digits to display
     */
    public static int determinePrecision(final double number)
    {
        // Log gymnastics:
        // Number  Ceil(Log10)     Show as     Precision
        // 10.0        1             "5.0"        1
        //  5.0        1             "5.0"        1
        //  0.5        0             "0.50"       2
        //  0.05      -1             "0.05"       3
        final double log = Math.log10(number);
        final int rounded_log = (int)Math.ceil(log);
        // Precision: 0 or more trailing digits
        if (number > 10.0)
            return 0;
        return 2 - rounded_log;
    }

    /** Nice looking steps for the distance between tick,
     *  and the threshold for using them.
     *  In general, the computed steps "fill" the axis.
     *  The nice looking steps should be wider apart,
     *  because tighter steps would result in overlapping label.
     *  The thresholds thus favor the larger steps:
     *  A computed distance of 6.1 turns into 10.0, not 5.0.
     *  @see #selectNiceStep(double)
     */
    final private static double[] NICE_STEPS = { 10.0, 5.0, 2.0, 1.0 },
                             NICE_THRESHOLDS = {  6.0, 3.0, 1.2, 0.0 };

    /** To a human viewer, tick distances of 5.0 are easier to see
     *  than for example 7.
     *
     *  <p>This method tries to adjust a computed tick distance
     *  to one that is hopefully 'nicer'
     *
     *  @param distance Original step distance
     *  @return
     */
    public static double selectNiceStep(final double distance)
    {
        final double log = Math.log10(distance);
        final double order_of_magnitude = Math.pow(10, Math.floor(log));
        final double step = distance / order_of_magnitude;
        for (int i=0; i<NICE_STEPS.length; ++i)
            if (step >= NICE_THRESHOLDS[i])
                return NICE_STEPS[i] * order_of_magnitude;
        return step * order_of_magnitude;
    }

    /** Create decimal format
     *  @param precision
     *  @return NumberFormat
     */
    protected static NumberFormat createDecimalFormat(final int precision)
    {
        final NumberFormat fmt = NumberFormat.getNumberInstance();
        fmt.setGroupingUsed(false);
        fmt.setMinimumFractionDigits(precision);
        fmt.setMaximumFractionDigits(precision);
        return fmt;
    }

    /** Create exponential format
     *  @param mantissa_precision
     *  @return NumberFormat
     */
    protected static NumberFormat createExponentialFormat(final int mantissa_precision)
    {
        // DecimalFormat needs pattern for exponential notation,
        // there are no factory or configuration methods
        final StringBuilder pattern = new StringBuilder("0");
        if (mantissa_precision > 0)
            pattern.append('.');
        for (int i=0; i<mantissa_precision; ++i)
            pattern.append('0');
        pattern.append("E0");
        return new DecimalFormat(pattern.toString());
    }

    /** {@inheritDoc} */
    @Override
    public Double getStart()
    {
        return start;
    }

    /** {@inheritDoc} */
    @Override
    public Double getPrevious(final Double tick)
    {
        if (distance <= 0.0)
            throw new Error("Broken tickmark computation");
        return tick - distance;
    }

    /** {@inheritDoc} */
    @Override
    public Double getNext(final Double tick)
    {
        if (distance <= 0.0)
            throw new Error("Broken tickmark computation");
        return tick + distance;
    }

    /** {@inheritDoc} */
    @Override
    public int getMinorTicks()
    {
        return 5;
    }

    /** {@inheritDoc} */
    @Override
    public String format(final Double num)
    {
        // Patch numbers that are "very close to zero"
        // to avoid "-0.00" or "0.0e-22"
        if (Math.abs(num) < distance/100000)
            return num_fmt.format(0.0);
        return formatDetailed(num);
    }

    /** {@inheritDoc} */
    @Override
    public String formatDetailed(final Double num)
    {
        // Do NOT patch numbers "very close to zero"
        // in detailed format because that could
        // hide what user wants to inspect
        if (num.isNaN())
            return "NaN";
        if (num.isInfinite())
            return "Inf";
        return num_fmt.format(num);
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return String.format("Ticks start=%g, distance=%g, precision=%d",
                             start, distance, precision);
    }
}
