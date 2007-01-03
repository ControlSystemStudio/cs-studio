package org.csstudio.archive.crawl;

import java.util.Iterator;

import org.csstudio.value.DoubleValue;
import org.csstudio.value.Value;
import org.csstudio.value.ValueUtil;

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
public class DoubleValueIterator implements Iterator<DoubleValue>
{
    private final Iterator<Value> raw_samples;
    
    public DoubleValueIterator(Iterator<Value> raw_samples)
    {
        this.raw_samples = raw_samples;
    }
    
    public boolean hasNext()
    {
        return raw_samples.hasNext();
    }

    public DoubleValue next()
    {
        Value sample = raw_samples.next();
        double value = ValueUtil.getDouble(sample);
        return new DoubleValue(sample.getTime(), sample.getSeverity(),
                        sample.getStatus(), sample.getMetaData(),
                        new double[] { value });
    }
    
    public void remove()
    {
        raw_samples.remove();
    }
}
