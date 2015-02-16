/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

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
public class LogTicks extends LinearTicks
{
    private int minor = 2;

    // 'start' is the first tick mark as in LinearTicks
    // 'distance' is used as in LinearTicks when positive.
    // A negative 'distance' is used when it refers to the exponent

    public LogTicks()
    {
        num_fmt = createExponentialFormat(2);
    }

    /** {@inheritDoc} */
    @Override
    public void compute(final Double low, final Double high, final GC gc, final int screen_width)
    {
        Activator.getLogger().log(Level.FINE, "Compute log ticks, width {0}, for {1} - {2}",
                new Object[] { screen_width, low, high });

        // Determine range of values on axis
        if (! isSupportedRange(low, high))
            throw new Error("Unsupported range " + low + " .. " + high);

        final double low_exp = (int) Math.floor(Log10.log10(low));
        final double high_exp = (int) Math.floor(Log10.log10(high));
        final double low_power = Log10.pow10(low_exp);
        final double low_mantissa = low / low_power;

        // Test format
        num_fmt = createExponentialFormat(2);

        // Determine minimum label distance on the screen, using some
        // percentage of the available screen space.
        // Guess the label width, using the two extremes.
        final String low_label = format(low);
        final String high_label = format(high);
        final int label_width = Math.max(gc.textExtent(low_label).x, gc.textExtent(high_label).x);
        final int num_that_fits = Math.max(1,  screen_width/label_width*FILL_PERCENTAGE/100);

        if (high_exp <= low_exp)
        {   // Are numbers are within the same order of magnitude,
            // i.e. same exponent xeN = 2e4, 4e4, 6e4

            // Determine distance in terms of mantissa, relative to lower end of range
            final double high_mantissa = high / low_power;
            double dist = (high_mantissa - low_mantissa) / num_that_fits;
            dist = selectNiceStep(dist);
            if (dist <= 0.0)
                throw new Error("Broken tickmark computation");

            precision = determinePrecision(low_mantissa) + 1;
            minor = 2;
            num_fmt = createExponentialFormat(precision);

            // Start at 'low' adjusted to a multiple of the tick distance
            start = Math.ceil(low_mantissa / dist) * dist;
            start = start * low_power;

            distance = dist * low_power;
        }
        else
        {   // Range covers different orders of magnitude,
            // example 1eN = 1e-5, 1e0, 1e10
            double dist = (high_exp - low_exp) / num_that_fits;

            // Round up to the precision used to display values
            dist = selectNiceStep(dist);
            if (dist <= 0.0)
                throw new Error("Broken tickmark computation");

            if (dist >= 1)
            {
                precision = 0;
                minor = 10;
            }
            else
            {
                precision = determinePrecision(low_mantissa);
                minor = 2;
            }
            num_fmt = createExponentialFormat(precision);

            start = Log10.pow10(Math.ceil(Log10.log10(low) / dist) * dist);
            distance = -dist; // negative to indicate that distance refers to exponent
        }
    }

    /** {@inheritDoc} */
    @Override
    public Double getPrevious(final Double tick)
    {
        final double prev = distance < 0
            ? tick / Log10.pow10(-distance)  // distance refers to exponent of value
            : tick - distance;               // distance refers to plain value

        // Rounding errors can result in a situation where
        // we don't make any progress...
        if (prev >= tick)
            return tick - 1;
        return prev;
    }

    /** {@inheritDoc} */
    @Override
    public Double getNext(final Double tick)
    {
        // distance refers to exponent
        final double next = distance < 0
            ? tick * Log10.pow10(-distance)  // distance refers to exponent of value
            : tick + distance;               // distance refers to plain value

        // Rounding errors can result in a situation where
        // we don't make any progress...
        if (next <= tick)
            return tick + 1;
        return next;
    }

    /** {@inheritDoc} */
    @Override
    public int getMinorTicks()
    {
        return minor;
    }
}
