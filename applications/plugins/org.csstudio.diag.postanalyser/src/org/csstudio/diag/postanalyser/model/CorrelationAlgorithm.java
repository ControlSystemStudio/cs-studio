package org.csstudio.diag.postanalyser.model;

import org.csstudio.diag.postanalyser.Messages;
import org.csstudio.diag.postanalyser.math.Correlator;
import org.csstudio.swt.chart.TraceType;
import org.eclipse.osgi.util.NLS;

/** An Algorithm that correlates data from two channels.
 *  @author Kay Kasemir
 */
public class CorrelationAlgorithm extends Algorithm
{
    /** Channel with which to correlate */
    private Channel corr_channel;

    public CorrelationAlgorithm()
    {
        super(Messages.Algorithm_Correlation);
    }
    
    /** Set the "other" channel with which to correlate */
    public void setCorrelationChannel(final Channel other)
    {
        corr_channel = other;
        x_axis_label = other.getName();
    }
    
        @Override
    public void reset()
    {
        super.reset();
        corr_channel = null;
    }

    /** {@inheritDoc} */
    @Override
    public void process() throws Exception
    {
        if (input == null)
            throw new IllegalArgumentException(Messages.Algorithm_NoDataPoints);
        if (corr_channel == null)
            throw new IllegalArgumentException(Messages.Algorithm_NoSecondChannelError);
        
        final Correlator corr = new Correlator(
                corr_channel.getX(), corr_channel.getY(),
                input.getX(), input.getY());
        final XYChartSamples corr_samples =
            new XYChartSamples(corr.getCorrY1(), corr.getCorrY2());
        message = NLS.bind(Messages.Algorithm_CorrelationMessage,
                input.getName(), corr_channel.getName());
        outputs = new AlgorithmOutput[]
        {
            new AlgorithmOutput(message, corr_samples, TraceType.Markers)
        };
    }
    
    /** @return <code>false</code> because Correlation needs no time axis */
    @Override
    public boolean needTimeAxis()
    {
        return false;
    }
}
