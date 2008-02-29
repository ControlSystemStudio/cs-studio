package org.csstudio.diag.postanalyser.model;

import org.csstudio.diag.postanalyser.Messages;
import org.csstudio.diag.postanalyser.math.Fit;
import org.csstudio.diag.postanalyser.math.GaussFit;

/** An Algorithm that fits data to a Gauss Distribution.
 *  @author Kay Kasemir
 */
public class GaussFitAlgorithm extends AbstractFitAlgorithm
{
    public GaussFitAlgorithm()
    {
        super(Messages.Algorithm_GaussFit);
    }

    /** {@inheritDoc} */
    @Override
    protected Fit getFit(final double x[], final double y[])
    {
        return new GaussFit(x, y);
    }
}
