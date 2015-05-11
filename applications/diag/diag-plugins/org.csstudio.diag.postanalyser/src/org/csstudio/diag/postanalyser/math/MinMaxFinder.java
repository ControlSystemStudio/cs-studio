package org.csstudio.diag.postanalyser.math;

import org.csstudio.diag.postanalyser.Messages;

/** Locate maximum of an (x,y) series.
 *  @author Kay Kasemir
 */
public class MinMaxFinder
{
    private double max_position;
    private double min, max;
    
    /** Locate maximum of an (x,y) series.
     *  @param x Array of positions
     *  @param y Array of values
     *  @throws IllegalArgumentException if arrays differ in length or empty.
     */
    @SuppressWarnings("nls") //$NON-NLS-1$
    public MinMaxFinder(final double x[], final double y[]) throws IllegalArgumentException
    {
        final int N = x.length;
        if (N != y.length)
            throw new IllegalArgumentException(
                    "x and y vectors differ in length"); //$NON-NLS-1$
        if (N <= 0)
            throw new IllegalArgumentException("Empty vectors"); //$NON-NLS-1$
        max_position = x[0];
        min = y[0];
        max = y[0];
        for (int i=1; i<N; ++i)
        {
            if (y[i] < min)
                min = y[i];
            if (y[i] > max)
            {
                max_position = x[i];
                max = y[i];
            }
        }
    }

    /** @return Position of maximum */
    public double getMaxPosition()
    {
        return max_position;
    }

    /** @return Value of minimum */
    public double getMin()
    {
        return min;
    }

    /** @return Value of maximum */
    public double getMax()
    {
        return max;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return String.format(Messages.MinMaxFinder_Message, max_position, max);
    }
}
