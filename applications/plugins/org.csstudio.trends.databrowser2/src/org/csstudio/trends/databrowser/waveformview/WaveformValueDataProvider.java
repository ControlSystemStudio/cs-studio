package org.csstudio.trends.databrowser.waveformview;

import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.dataprovider.IDataProviderListener;
import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.csstudio.swt.xygraph.dataprovider.Sample;
import org.csstudio.swt.xygraph.linearscale.Range;

/** Data provider for the XYGraph that shows waveform elements of an IValue
 *  @author Kay Kasemir
 */
public class WaveformValueDataProvider implements IDataProvider
{
    final private IValue value;

    public WaveformValueDataProvider(final IValue value)
    {
        this.value = value;
    }

    /** {@inheritDoc} */
    public int getSize()
    {
        return ValueUtil.getSize(value);
    }

    /** {@inheritDoc} */
    public ISample getSample(final int index)
    {
        return new Sample(index, ValueUtil.getDouble(value, index));
    }

    /** {@inheritDoc} */
    public Range getXDataMinMax()
    {
        return new Range(0, getSize()-1);
    }

    /** {@inheritDoc} */
    public Range getYDataMinMax()
    {
        double min, max;
        min = max = ValueUtil.getDouble(value);
        for (int i=getSize()-1; i>=1; --i)
        {
            final double num = ValueUtil.getDouble(value);
            if (num < min)
                min = num;
            if (num > max)
                max = num;
        }
        return new Range(min, max);
    }

    /** {@inheritDoc} */
    public boolean isChronological()
    {   // x range is [0..waveform size]
        return true;
    }

    /** {@inheritDoc} */
    public void addDataProviderListener(final IDataProviderListener listener)
    {
        // Data doesn't change, so we ignore listeners
    }

    /** {@inheritDoc} */
    public boolean removeDataProviderListener(final IDataProviderListener listener)
    {
        // Data doesn't change, so we ignore listeners
        return true;
    }
}
