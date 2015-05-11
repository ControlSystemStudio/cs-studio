package org.csstudio.diag.postanalyser.math;

import static org.junit.Assert.*;

import org.junit.Test;

/** Example of fit to exponential.
 *  @author Kay Kasemir
 */
public class ExponentialFitTest
{
    @Test
    public void testDecayFit()
    {
        final double amp = 10.0;
        final double time = -2.0;
        
        fit(amp, time);
    }

    @Test
    public void testRiseFit()
    {
        final double amp = 10.0;
        final double time = 2.0;
        
        fit(amp, time);
    }

    @SuppressWarnings("nls")
    private void fit(final double amp, final double time)
    {
        final double x0 = 0.0;
        final double x1 = 10.0;
        final double dx = 0.1;
        final int N = (int) ((x1-x0)/dx) + 1;
        final double x[] = new double[N];
        final double data[] = new double[N];
        for (int i=0; i<N; ++i)
        {
            x[i] = x0 + i*dx;
            data[i] = amp * Math.exp(x[i]/time);
            System.out.println(x[i] + "\t" + data[i]);
        }
        // Perform the fit
        final ExponentialFit fit = new ExponentialFit(x, data);
        System.out.println("\n\n# Fit:" + fit);
        
        for (int i=0; i<N; ++i)
        {
            System.out.println(x[i] + "\t" + fit.getValue(x[i]));
        }
        System.out.println("# Paste into file, then use gnuplot:");
        System.out.println("# plot 'x' index 0, 'x' index 1 with lines");

        // Should be somewhat similar to "real" parameters
        assertEquals(amp, fit.getAmp(), 0.3);
        assertEquals(time, fit.getTimeConstant(), 0.3);
    }
}
