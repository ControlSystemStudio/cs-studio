package org.csstudio.diag.postanalyser.math;

import static org.junit.Assert.*;

import org.junit.Test;

/** Example of fit to polynominal that even I can understand.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LineFitTest
{
    @Test
    public void testLineFit()
    {
        // y = a x + b
        final double a = 2.0;
        final double b = 3.0;
        
        final double x0 = -1.0;
        final double x1 = +8.0;
        final double dx = 0.1;
        final int N = (int) ((x1-x0)/dx) + 1;
        
        final double x[] = new double[N];
        final double data[] = new double[N];
        for (int i=0; i<N; ++i)
        {
            x[i] = x0 + i*dx;
            data[i] = a * x[i] + b;
            data[i] += 2.0*(Math.random()-0.5);
            System.out.println(x[i] + "\t" + data[i]);
        }
        
        // Perform the fit
        final LineFit fit = new LineFit(x, data);

        // get params.
        System.out.println("\n\n# Fit: " + fit);
        for (int i=0; i<N; ++i)
        {
            System.out.println(x[i] + "\t" + fit.getValue(x[i]));
        }
        System.out.println("# Paste into file, then use gnuplot:");
        System.out.println("# plot 'x' index 0, 'x' index 1 with lines");

        // Should be somewhat similar to "real" parameters
        assertEquals(a, fit.getSlope(), 0.3);
        assertEquals(b, fit.getIntersect(), 0.3);
    }
}
