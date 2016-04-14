package org.csstudio.diag.postanalyser.math;

import java.time.Instant;

import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.diag.postanalyser.Messages;
import org.teneighty.lm.CostFunction;
import org.teneighty.lm.LevenbergMarquardt;

/** Fit to Gaussian.
 *  @author Kay Kasemir
 */
public class GaussFit extends Fit
{
    private double base;
    private double amp;
    private double center;
    private double sigma;

    /** Implement gaussian.
     *  <p>
     *  <code>B + A * exp(-(x-x0)/(2*s^2))</code>
     *  Base <code>B</code> is fixed. Other parameters:
     *  <ul>
     *  <li>Parameter 0 = Amplitude <code>A</code>
     *  <li>Parameter 1 = Center <code>x0</code>
     *  <li>Parameter 2 = Sigma <code>s</code>
     *  </ul>
     */
    class Gaussian implements CostFunction
    {
        /** {@inheritDoc} */
        @Override
        public int getParameterCount()
        {
            return 3;
        }

        /** {@inheritDoc} */
        @Override
        public double evaluate(final double values[], final double params[])
        {
            final double x = values[0];
            final double amp = params[0];
            final double center = params[1];
            final double sigma = params[2];
            final double dx = x - center;
            final double exp = Math.exp(-(dx*dx)/(2*sigma*sigma));
            return base + amp * exp;
        }

        /** {@inheritDoc} */
        @Override
        public double derive(final double values[], final double params[], int ith)
        {
            final double x = values[0];
            final double amp = params[0];
            final double center = params[1];
            final double sigma = params[2];
            final double dx = x - center;
            final double exp = Math.exp(-(dx*dx)/(2*sigma*sigma));

            // dy/A ( ... )
            if (ith == 0)
                return exp;

            // dy/x0  ( ... )
            if (ith == 1)
                return amp*exp * dx/(sigma*sigma);

            // dy/sigma ( ... )
            return amp*exp * dx*dx/(sigma*sigma*sigma);
        }
    }

    /** Perform fit of data to gaussian
     *  <code>B + A * exp(-(x-x0)/(2*s^2))</code>
     *  with
     *  <ul>
     *  <li>Base <code>B</code>
     *  <li>Amplitude <code>A</code>
     *  <li>Center <code>x0</code>
     *  <li>Sigma <code>s</code>
     *  </ul>

     *  @param x Array of 'x' values
     *  @param y Array of 'y' values
     *  @throws IllegalArgumentException when input arrays differ in length
     *                                   or are empty
     */
    public GaussFit(final double x[], final double y[])
    {
        final int N = checkArguments(x, y);

        // Perform the fit
        final CostFunction func = new Gaussian();
        final LevenbergMarquardt lm = new LevenbergMarquardt(N, 1);
        lm.setPoints(x, 0);
        lm.setCostFunction(func);
        lm.setValues(y);

        // LevenbergMarquardt needs some reasonable estimates for the
        // amplitude and center, which we get from the maximum of
        // the function
        final MinMaxFinder max = new MinMaxFinder(x, y);
        base = max.getMin();
        amp = max.getMax();
        center = max.getMaxPosition();
        // Assume the Gaussian uses about half of the x range
        sigma = (x[x.length-1] - x[0]) / 2.0;

        lm.setGuess(new double[] { amp, center, sigma });

        // solve it.
        lm.solve();

        // get params.
        amp = lm.getParameter(0);
        // For our data, the center position is a time, i.e. > 0
        center = Math.abs(lm.getParameter(1));
        // Fit might return sigma or -sigma, since that results
        // in the same gaussian function
        sigma = Math.abs(lm.getParameter(2));
    }

    /** @return the base */
    public double getBase()
    {
        return base;
    }

    /** @return the amp */
    public double getAmp()
    {
        return amp;
    }

    /** @return the center */
    public double getCenter()
    {
        return center;
    }

    /** @return the sigma */
    public double getSigma()
    {
        return sigma;
    }

    /** {@inheritDoc} */
    @Override
    public double getValue(final double x)
    {
        final double dx = x - center;
        return base + amp * Math.exp(-(dx*dx)/(2*sigma*sigma));
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        final Instant time = TimestampHelper.fromMillisecs((long)center);
        return String.format(Messages.GaussFit_Message,
            base, amp, time.toString(), sigma);
    }
}
