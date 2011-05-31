/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;

import org.eclipse.swt.graphics.GC;

/** Logarithmic tick marks.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LogTicks extends Ticks
{
    /** @see Ticks#compute(double, double, GC, int) */
    @Override
    void compute(double low, double high, GC gc, int screen_width)
    {
        if (low > high)
            throw new Error("Tick range is not ordered: " + low + " > " + high);
        if (low == high)
            high = low + 1;
        
        // Idea: Ask the basic linear tick computation to find 'nice'
        // ticks for the log(low...high):
        double log_low = Log10.log10(low);
        double log_high = Log10.log10(high);
        super.compute(log_low, log_high, gc, screen_width);
        // That set 'start' to log(start_that_we_really_want), so fix that: 
        start = Log10.pow10(start);
        // distance is still in log space, handled accordingly in getNext()
    }

    /** @return Returns the next tick, following a given tick mark. */
    @Override
    public double getNext(double tick)
    {   // distance refers to the tick distance for log(value_space)!
        double next = Log10.pow10(Log10.log10(tick) + distance);
        // Rounding errors can result in a situation where
        // we don't make any progress...
        if (next <= tick)
            return tick + 1;
        return next;
    }
    
    /** @return Returns the number formated according to the tick precision. */
    @Override
    public String format(double num, int precision_change)
    {
        int p = precision + precision_change;
        num_fmt.setMaximumFractionDigits(p);
        num_fmt.setMaximumFractionDigits(p);

        // Split into mantissa and exponent
        double mantissa;
        int exponent;        
        boolean negative = num < 0.0;
        if (num == 0.0)
        {
            mantissa = 0.0;
            exponent = 0;
        }
        else
        {
            if (negative)
                num = - num;
            exponent = (int) Log10.log10(num);
            double pwr_of_10 = Log10.pow10(exponent);
            mantissa = num / pwr_of_10;
        }
        String text = num_fmt.format(mantissa) + "e" + exponent;
        if (negative)
            return "-" + text;
        return text;
        //return num_fmt.format(mantissa) + "x10^" + exponent;
    }
}
