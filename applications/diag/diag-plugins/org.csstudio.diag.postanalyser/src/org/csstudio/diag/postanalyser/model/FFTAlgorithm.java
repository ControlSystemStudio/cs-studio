package org.csstudio.diag.postanalyser.model;

import org.csstudio.diag.postanalyser.Messages;
import org.csstudio.diag.postanalyser.math.Filter;
import org.csstudio.diag.postanalyser.math.Fourier;
import org.csstudio.diag.postanalyser.math.Filter.Type;
import org.csstudio.swt.chart.TraceType;
import org.eclipse.osgi.util.NLS;

/** An Algorithm that performs FFT.
 *  <p>
 *  The input signal isn't sampled at a fixed, known period.
 *  While a strict FFT isn't possible, we assume an average period
 *  of <code>(end - start)/sample_count</code> seconds.
 *  @author Kay Kasemir
 */
public class FFTAlgorithm extends Algorithm
{
    private Type type;

    public FFTAlgorithm()
    {
        super(Messages.Algorithm_FFT);
    }

    /** Define the FFT window filter */
    public void setFilterType(Type type)
    {
        this.type = type;
    }
    
    /** @return <code>false</code> because FFT needs no time axis */
    @Override
    public boolean needTimeAxis()
    {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void process() throws Exception
    {
        x_axis_label = Messages.FFT_XAxisLabel;
        if (input == null)
            throw new IllegalArgumentException(Messages.Algorithm_NoDataPoints);
        
        // Guess the signal period
        final int N = input.size();
        if (N <= 0)
        {
            error(Messages.Algorithm_NoDataPoints);
            return;
        }
        final double period = (input.get(N-1).getX() - input.get(0).getX()) / N;
        final double signal[] = Filter.window(input.getY(), type);
        final Fourier fft = new Fourier(period, signal);
        message = NLS.bind(Messages.FFT_Message, input.getName());
        outputs = new AlgorithmOutput[]
        {
            new AlgorithmOutput(message,
                    new XYChartSamples(fft.getFrequencies(),
                                       fft.getAmplitudes()), TraceType.Bars)
        };
    }

    private void error(final String error)
    {
        message = error;
        outputs = new AlgorithmOutput[]
        {
            new AlgorithmOutput(input.getName(), input, TraceType.Markers)
        };
    }
}
