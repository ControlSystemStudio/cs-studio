package org.csstudio.diag.postanalyser.model;

import org.csstudio.diag.postanalyser.Messages;
import org.csstudio.diag.postanalyser.math.Fit;
import org.csstudio.diag.postanalyser.math.LineFit;

/** An Algorithm that fits data to a line.
 *  @author Kay Kasemir
 */
public class LineFitAlgorithm extends AbstractFitAlgorithm
{
    public LineFitAlgorithm()
    {
        super(Messages.Algorithm_LineFit);
    }

    /** {@inheritDoc} */
    @Override
    protected Fit getFit(final double x[], final double y[])
    {
        return new LineFit(x, y);
    }
}
