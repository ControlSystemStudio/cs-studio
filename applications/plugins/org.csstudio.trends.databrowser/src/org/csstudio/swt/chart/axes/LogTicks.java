package org.csstudio.swt.chart.axes;

import org.eclipse.swt.graphics.GC;

/** Logarithmic tick marks.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LogTicks extends Ticks
{
    /** @see Ticks#compute(double, double, GC, int) */
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
    public double getNext(double tick)
    {   // distance refers to the tick distance for log(value_space)!
        return Log10.pow10(Log10.log10(tick) + distance);
    }
    
    /** @return Returns the number formated according to the tick precision. */
    public String format(double num, int precision_change)
    {
        int p = precision + precision_change;
        num_fmt.setMaximumFractionDigits(p);
        num_fmt.setMaximumFractionDigits(p);
        // Split into mantissa and exponent
        int exponent = (int) Log10.log10(num);
        double pwr_of_10 = Log10.pow10(exponent);
        double mantissa = num / pwr_of_10;
        
        return num_fmt.format(mantissa) + "e" + exponent;
        //return num_fmt.format(mantissa) + "x10^" + exponent;
    }
}
