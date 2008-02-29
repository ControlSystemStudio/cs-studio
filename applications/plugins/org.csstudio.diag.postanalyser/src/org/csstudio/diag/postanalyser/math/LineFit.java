package org.csstudio.diag.postanalyser.math;

import org.csstudio.diag.postanalyser.Messages;

/** Fit data to a line.
 *  @see #LineFit(double[], double[])
 *  @author Kay Kasemir
 */
public class LineFit extends Fit
{
    private double slope;
    private double intersect;
    
    /** Perform fit of data to line <code>y = a*x + b</code>
     *  with slope <code>a</code> and y-axis intersection <code>b</code>.
     *  @param x Array of 'x' values
     *  @param y Array of 'y' values
     *  @throws IllegalArgumentException when input arrays differ in length
     *                                   or are empty
     */
    public LineFit(final double x[], final double y[])
    {
        final int N = checkArguments(x, y);

        // For time stamps x[], the sums get too large,
        // resulting in numerical errors.
        // So we map them relative to x0
        final double x0 = x[0];
        
        // Variable names close to
        // http://en.wikipedia.org/wiki/Linear_least_squares
        double Sx = 0.0;
        double Sx2 = 0.0;
        double Sy = 0.0;
        double Sxy = 0.0;
        for (int i=0; i<N; ++i)
        {
            final double xm = x[i] - x0;
            Sx  += xm;
            Sx2 += xm * xm;
            Sy  += y[i];
            Sxy += xm * y[i];
        }
        final double D = N*Sx2 - Sx*Sx;
        if (D == 0.0)
            throw new Error(Messages.LineFit_Error);

        // Put the x0 back in by using the corrected intersect:
        //   slope*(x-x0) + intersect
        // = slope*x + (slope*x0 - intersect)
        slope = (N*Sxy - Sx*Sy) / D;
        intersect = (Sx2*Sy - Sx*Sxy) / D  - slope * x0;
    }

    /** @return Slope <code>a</code> of line <code>y = a*x + b</code> */
    public double getSlope()
    {
        return slope;
    }

    /** @return Intersect <code>b</code> of line <code>y = a*x + b</code> */
    public double getIntersect()
    {
        return intersect;
    }

    /** {@inheritDoc} */
    @Override
    public double getValue(final double x)
    {
        return slope*x + intersect;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return String.format(Messages.LineFit_Message, slope, intersect);
    }
}
