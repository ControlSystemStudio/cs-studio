package org.csstudio.diag.postanalyser.math;

import org.csstudio.diag.postanalyser.Messages;
import org.eclipse.osgi.util.NLS;

/** Base class for fit.
 *  @author Kay Kasemir
 */
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
                    NLS.bind(Messages.Algorithm_XYArraysDiffer,
                            N, y.length));
        if (N <= 1)
            throw new IllegalArgumentException(Messages.Algorithm_NoDataPoints);
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
        return String.format("Fit"); //$NON-NLS-1$
    }
}
