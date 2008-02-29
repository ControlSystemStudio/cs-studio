package org.csstudio.diag.postanalyser.model;

import org.csstudio.diag.postanalyser.Messages;
import org.csstudio.diag.postanalyser.math.ExponentialFit;
import org.csstudio.diag.postanalyser.math.Fit;

/** An Algorithm that fits data to a Gauss Distribution.
 *  @author Kay Kasemir
 */
public class ExpFitAlgorithm extends AbstractFitAlgorithm
{
    public ExpFitAlgorithm()
    {
        super(Messages.Algorithm_ExpFit);
    }

    /** {@inheritDoc} */
    @Override
    protected Fit getFit(final double x[], final double y[])
    {
        return new ExponentialFit(x, y);
    }
}
