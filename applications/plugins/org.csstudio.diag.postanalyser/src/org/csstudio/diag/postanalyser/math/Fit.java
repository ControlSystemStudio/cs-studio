package org.csstudio.diag.postanalyser.math;

/** Base class for fit.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class Fit
{
    /** Helper: Check if arrays x[] and y[] have same length > 1
     *  @return Array length
     *  @throws IllegalArgumentException On error
     */
    protected int checkArguments(final double x[], final double y[])
    {
        // Check arguments
        final int N = x.length;
        if (N != y.length)
            throw new IllegalArgumentException(
                    String.format("x.length %d != y.length %d",
                            N, y.length));
        if (N <= 1)
            throw new IllegalArgumentException("Empty input arrays");
        return N;
    }

    /** @return Value of fit for given input */
    abstract public double getValue(final double x);
    
    /** @return Human-readable representation of the Fit:
     *  Formula, parameters, ...
     */
    @Override
    public String toString()
    {
        return String.format("Fit");
    }
}
