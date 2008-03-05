package org.csstudio.diag.postanalyser.model;

import org.csstudio.diag.postanalyser.Messages;
import org.csstudio.swt.chart.TraceType;

/** An Algorithm that simply displays the original data.
 *  @author Kay Kasemir
 */
public class RawAlgorithm extends Algorithm
{
    public RawAlgorithm()
    {
        super(Messages.Algorithm_Original);
    }

    /** {@inheritDoc} */
    @Override
    public void process() throws Exception
    {
        if (input == null)
            throw new IllegalArgumentException(Messages.Algorithm_NoDataPoints);
        // Simply return the input points
        outputs = new AlgorithmOutput[]
        {
            new AlgorithmOutput(input.getName(), input, TraceType.Lines)
        };
        message = getName();
    }
}
