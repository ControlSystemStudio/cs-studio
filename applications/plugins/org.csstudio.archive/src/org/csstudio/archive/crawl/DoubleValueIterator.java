package org.csstudio.archive.crawl;

import java.util.Iterator;

import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.data.values.ValueUtil;

/** Turns an Iterator over raw <code>Value</code>s into one over
 *  <code>DoubleValue</code>s.
 *  <p>
 *  Since many applications probably only need scalar double data,
 *  this iterator might be more convenient than the underlying
 *  raw sample iterator.
 *  <p>
 *  Samples with no numeric value, for example because of a
 *  status/severity that indicates a disconnected state,
 *  are returned with Double.NaN to be easier to spot.
 *
 *  @see org.csstudio.archive.crawl.RawValueIterator
 *  @author Kay Kasemir
 */
public class DoubleValueIterator implements Iterator<IDoubleValue>
{
    final private Iterator<IValue> raw_samples;
    static private INumericMetaData numeric_meta = null;

    public DoubleValueIterator(Iterator<IValue> raw_samples)
    {
        this.raw_samples = raw_samples;
    }

    public boolean hasNext()
    {
        return raw_samples.hasNext();
    }

    public IDoubleValue next()
    {
        final IValue sample = raw_samples.next();
        final double value = ValueUtil.getDouble(sample);
        // Check if meta data is INumericMetaData. Else, create one.
        IMetaData meta = sample.getMetaData();
        if (! (meta instanceof INumericMetaData))
        {
            if (numeric_meta == null)
                numeric_meta = ValueFactory.createNumericMetaData(0.0, 0.0,
                                0.0, 0.0, 0.0, 0.0, 0, ""); //$NON-NLS-1$
            meta = numeric_meta;
        }
        return ValueFactory.createDoubleValue(sample.getTime(),
                        sample.getSeverity(),
                        sample.getStatus(),
                        (INumericMetaData)meta,
                        IValue.Quality.Interpolated,
                        new double[] { value });
    }

    public void remove()
    {
        raw_samples.remove();
    }
}
