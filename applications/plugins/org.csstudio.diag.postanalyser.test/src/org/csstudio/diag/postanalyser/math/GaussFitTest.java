package org.csstudio.diag.postanalyser.math;

import static org.junit.Assert.*;

import org.junit.Test;

/** Example of fit to gaussian.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GaussFitTest
{
    @Test
    public void testGaussianFit()
    {
        final double base = 1.0;
        final double amp = 2.0;
        final double center = 4.0;
        final double sigma = 0.5;
        
        final double x0 = 0.0;
        final double x1 = 8.0;
        final double dx = 0.1;
        final int N = (int) ((x1-x0)/dx) + 1;
        final double x[] = new double[N];
        final double data[] = new double[N];
        for (int i=0; i<N; ++i)
        {
            x[i] = x0 + i*dx;
            final double d_x = x[i] - center;
            data[i] = base + amp * Math.exp(-(d_x*d_x)/(2*sigma*sigma));
            data[i] += 0.2*(Math.random()-0.5);
            System.out.println(x[i] + "\t" + data[i]);
        }
        // Perform the fit
        final GaussFit fit = new GaussFit(x, data);
        System.out.println("\n\n# Fit:" + fit);
        
        for (int i=0; i<N; ++i)
        {
            System.out.println(x[i] + "\t" + fit.getValue(x[i]));
        }
        System.out.println("# Paste into file, then use gnuplot:");
        System.out.println("# plot 'x' index 0, 'x' index 1 with lines");

        // Should be somewhat similar to "real" parameters
        assertEquals(base, fit.getBase(), 0.3);
        assertEquals(amp, fit.getAmp(), 0.3);
        assertEquals(center, fit.getCenter(), 0.3);
        assertEquals(sigma, fit.getSigma(), 0.3);
    }
}
