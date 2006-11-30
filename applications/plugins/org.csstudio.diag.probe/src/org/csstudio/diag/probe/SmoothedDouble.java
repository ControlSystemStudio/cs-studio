package org.csstudio.diag.probe;

/** Running average type smoothing.
 *  @author Kay Kasemir
 */
public class SmoothedDouble
{
    private static final double WEIGHT = 0.9;
    private double value;
    
    /** Constructor. */
    public SmoothedDouble()
    {
        reset();
    }

    /** Reset value to 0 */
    public void reset()
    {
        value = 0.0;
    }

    /** Add a sample. */
    public void add(double sample)
    {
        value = WEIGHT * value + (1.0 - WEIGHT) * sample;
    }
    
    /** @return The current smoothed value. */
    public double get()
    {
        return value;
    }
}
