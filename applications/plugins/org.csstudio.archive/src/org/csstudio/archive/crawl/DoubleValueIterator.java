package org.csstudio.archive.crawl;

import java.util.Iterator;

import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.data.ValueUtil;

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
    private final Iterator<IValue> raw_samples;
    
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
        IValue sample = raw_samples.next();
        // TODO check if meta data is INumericMetaData. Else, create one.
        double value = ValueUtil.getDouble(sample);
        return ValueFactory.createDoubleValue(sample.getTime(), sample.getSeverity(),
                        sample.getStatus(),
                        (INumericMetaData)sample.getMetaData(),
                        IValue.Quality.Interpolated,
                        new double[] { value });
    }
    
    public void remove()
    {
        raw_samples.remove();
    }
}
