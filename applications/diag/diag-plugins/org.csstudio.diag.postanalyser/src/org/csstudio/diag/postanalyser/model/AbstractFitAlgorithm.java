package org.csstudio.diag.postanalyser.model;

import org.csstudio.diag.postanalyser.Messages;
import org.csstudio.diag.postanalyser.math.Fit;
import org.csstudio.swt.chart.TraceType;

/** base for Algorithms that fits data to a line etc.
 *  @author Kay Kasemir
 */
abstract public class AbstractFitAlgorithm extends Algorithm
{
    public AbstractFitAlgorithm(String name)
    {
        super(name);
    }

    /** @return Fit that fits the given x/y data. */
    abstract protected Fit getFit(final double x[], final double y[]);

    /** {@inheritDoc} */
    @Override
    public void process() throws Exception
    {
        if (input == null)
            throw new IllegalArgumentException(Messages.Algorithm_NoDataPoints);
        
        try
        {
            // Perform the fit
            final double[] x = input.getX();
            final Fit fit = getFit(x, input.getY());
    
            // Describe the output
            message = fit.toString();
            
            // Generate line
            final double fit_line[] = new double[x.length];
            for (int i=0; i<x.length; ++i)
                fit_line[i] = fit.getValue(x[i]);
            // Return original data and the line
            outputs = new AlgorithmOutput[]
            {
                new AlgorithmOutput(input.getName(), input, TraceType.Markers),
                new AlgorithmOutput(message, new XYChartSamples(x, fit_line),
                        TraceType.Lines)
            };
        }
        catch (Throwable ex)
        {
            final String msg = ex.getMessage();
            if (msg != null)
                message = msg;
            else
                message = Messages.Algorithm_FitError;
            outputs = new AlgorithmOutput[]
            {
                new AlgorithmOutput(input.getName(), input, TraceType.Markers),
            };
        }
    }
}
