package org.csstudio.diag.postanalyser.math;

import static org.junit.Assert.*;

import org.junit.Test;
import org.teneighty.lm.CostFunction;
import org.teneighty.lm.LevenbergMarquardt;

/** Example of fit to polynominal that even I can understand.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PolyFitExample
{
    /** Implement polynominal <code>y = p[0]*x^2 + 2*p[1] + p[2]</code> */
    class SimplePoly implements CostFunction
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
            return params[0]*x*x + params[1]*x + params[2];
        }

        /** {@inheritDoc} */
        @Override
        public double derive(final double values[], final double params[], int ith)
        {
            // dy/dp0 ( p0*x^2 + p1*x + p2 ) = x^2
            if (ith == 0)
                return values[0]*values[0];

            // dy/dp1 ( p0*x^2 + p1*x + p2 ) = x
            if (ith == 1)
                return values[0];

            // dy/dp2 ( p0*x^2 + p1*x + p2 ) = 1
            return 1;
        }
    }

    @Test
    public void testPolyFit()
    {
        // y = (x-2)^2 = x^2 -4x + 4
        final double params[] = new double[] { 1.0, -4.0, 4.0 };

        final CostFunction func = new SimplePoly();

        final double x0 = -1.0;
        final double x1 = +8.0;
        final double dx = 0.1;
        final int N = (int) ((x1-x0)/dx) + 1;

        System.out.format("# Function: y = %g * x^2 + %g *x + %g + Noise\n",
                params[0], params[1], params[2]);
        final double x[] = new double[N];
        final double data[] = new double[N];
        for (int i=0; i<N; ++i)
        {
            x[i] = x0 + i*dx;
            data[i] = func.evaluate(new double[] { x[i] }, params);
            data[i] += 2.0*(Math.random()-0.5);
            System.out.println(x[i] + "\t" + data[i]);
        }

        // Perform the fit
        final LevenbergMarquardt lm = new LevenbergMarquardt(N, 1);
        lm.setPoints(x, 0);
        lm.setCostFunction(func);
        lm.setValues(data);
        lm.setGuess(new double[] { 1.0, 1.0, 1.0 });

        // solve it.
        lm.solve();

        // get params.
        final double[] fit_params = lm.getParameters();
        System.out.format("\n\n# Fit: y = %g * x^2 + %g *x + %g + Noise\n",
                fit_params[0], fit_params[1], fit_params[2]);

        final double fit[] = new double[N];
        for (int i=0; i<N; ++i)
        {
            fit[i] = func.evaluate(new double[] { x[i] }, fit_params);
            System.out.println(x[i] + "\t" + fit[i]);
        }
        System.out.println("# Paste into file, then use gnuplot:");
        System.out.println("# plot 'x' index 0, 'x' index 1 with lines");

        // Should be somewhat similar to "real" parameters
        for (int i=0; i<params.length; ++i)
            assertEquals(params[i], fit_params[i], 0.3);
    }
}
