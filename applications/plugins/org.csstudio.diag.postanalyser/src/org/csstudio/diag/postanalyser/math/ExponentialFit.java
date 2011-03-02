package org.csstudio.diag.postanalyser.math;

import org.csstudio.diag.postanalyser.Activator;
import org.csstudio.diag.postanalyser.Messages;
import org.teneighty.lm.CostFunction;
import org.teneighty.lm.LevenbergMarquardt;

/** Fit to Exponential.
 *  @author Kay Kasemir
 */
public class ExponentialFit extends Fit
{
    private double x0;
    private double amp;
    private double time;

    /** Implement exponential.
     *  <p>
     *  <code>A * exp((x-x0)/T)</code>
     *  <ul>
     *  <li>Parameter 0 = Amplitude <code>A</code>
     *  <li>Parameter 1 = Decay/rise time constant <code>T</code>
     *  </ul>
     */
    class Exponential implements CostFunction
    {
        /** {@inheritDoc} */
        @Override
        public int getParameterCount()
        {
            return 2;
        }

        /** {@inheritDoc} */
        @Override
        public double evaluate(final double values[], final double params[])
        {
            final double x = values[0] - x0;
            final double amp = params[0];
            final double time = params[1];
            return amp * Math.exp(x/time);
        }

        /** {@inheritDoc} */
        @Override
        public double derive(final double values[], final double params[], int ith)
        {
            final double x = values[0] - x0;
            final double amp = params[0];
            final double time = params[1];

            // dy/A ( ... )
            if (ith == 0)
                return Math.exp(x/time);

            // dy/T ( ... )
            return -amp * x / (time*time) * Math.exp(x/time);
        }
    }

    /** Perform fit of data to exponential <code>A * exp(x/T)</code>
     *  with Amplitude <code>A</code>
     *  and time constant <code>T</code>.

     *  @param x Array of 'x' values
     *  @param y Array of 'y' values
     *  @throws IllegalArgumentException when input arrays differ in length
     *                                   or are empty
     */
    public ExponentialFit(final double x[], final double y[])
    {
        final int N = checkArguments(x, y);
        x0 = x[0];

        // Attempt the faster line fit
        if (lineFit(x, y, N))
            return;

        Activator.getLogger().info("ExponentialFit uses LevenbergMarquardt"); //$NON-NLS-1$
        // Failed, probably because of noise that generated y <= 0.0
        // Perform the trial-and-error fit
        final CostFunction func = new Exponential();
        final LevenbergMarquardt lm = new LevenbergMarquardt(N, 1);
        lm.setPoints(x, 0);
        lm.setCostFunction(func);
        lm.setValues(y);

        // LevenbergMarquardt needs some reasonable estimates,
        // which we get from the maximum of the function
        final MinMaxFinder max = new MinMaxFinder(x, y);
        amp = max.getMax();
        // Assume the rise uses about half of the x range
        time = (x[N-1] - x0) / 2.0;
        // Is it actually a decay?
        if (y[N-1] < y[0])
            time = -time;
        lm.setGuess(new double[] { amp, time });

        // solve it.
        lm.solve();

        // get params.
        amp = lm.getParameter(0);
        time = lm.getParameter(1);
    }

    /** Try to use line fit to <code>log(y)</code>
     *  @return <code>true</code> if parameters were set OK,
     *          <code>false</code> on error
     */
    private boolean lineFit(final double[] x, final double[] y, final int N)
    {
        //          y  = amp * exp(x/time)
        // ==>  log(y) = log(amp) + x/time
        final double shift_x[] = new double[N];
        final double log_y[] = new double[N];
        for (int i=0; i<N; ++i)
        {
            shift_x[i] = x[i] - x0;
            if (y[i] <= 0.0)
                return false;
            log_y[i] = Math.log(y[i]);
        }
        // Fit line to log(y)
        final LineFit line = new LineFit(shift_x, log_y);
        final double log_amp = line.getIntersect();
        final double inv_time = line.getSlope();
        if (inv_time == 0.0)
            return false;
        amp = Math.exp(log_amp);
        time = 1.0 / inv_time;
        return true;
    }

    /** @return amplitude */
    public double getAmp()
    {
        return amp;
    }

    /** @return Decay or rise time constant */
    public double getTimeConstant()
    {
        return time;
    }

    /** {@inheritDoc} */
    @Override
    public double getValue(final double x)
    {
        return amp * Math.exp((x-x0)/time);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        if (time < 0)
            return String.format(Messages.ExponentialFit_DecayMessage, amp, -time);
        else
            return String.format(Messages.ExponentialFit_RiseMessage, amp, time);
    }
}
