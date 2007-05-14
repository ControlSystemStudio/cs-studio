package org.csstudio.archive.crawl;

import java.util.Iterator;

import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.IEnumeratedValue;
import org.csstudio.platform.data.IIntegerValue;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;

/** Turns an Iterator over raw <code>Value</code>s into one over
 *  <code>IntegetValue</code>s.
 *  <p>
 *  Since many applications probably only need scalar data,
 *  this iterator might be more convenient than the underlying
 *  raw sample iterator.
 *  <p>
 *  Samples with no numeric value, for example because of a
 *  status/severity that indicates a disconnected state,
 *  are returned as IntegerSampleIterator.INVALID, in the hope that this
 *  avoids accidental use of these samples.
 *  
 *  @see org.csstudio.archive.crawl.RawValueIterator
 *  @see org.csstudio.archive.crawl.DoubleValueIterator
 *  @author Kay Kasemir
 */
public class IntegerValueIterator implements Iterator<IIntegerValue>
{
    /** Special value to indicate samples without numeric value.
     *  <p>
     *  Unfortunately this cuts into the usable value range,
     *  but it is certainly a better choice than '0' or another
     *  common value.
     *  Clients who need to retrieve MIN_VALUE samples can to so via
     *  DoubleValueInfo or RawValueInfo.
     */
    public static final int INVALID = Integer.MIN_VALUE;
    
    static ISeverity invalid_type = ValueFactory.createInvalidSeverity();
    private final Iterator<IValue> raw_values;
    
    public IntegerValueIterator(ValueIterator raw_values)
    {
        this.raw_values = raw_values;
    }
    
    public boolean hasNext()
    {
        return raw_values.hasNext();
    }

    public IIntegerValue next()
    {
        IValue sample = raw_values.next();
        // TODO check if meta data is INumericMetaData. Else, create one.
        int num;
        if (sample.getSeverity().hasValue())
        {
            if (sample instanceof IDoubleValue)
                num = (int)((IDoubleValue) sample).getValue();
            else if (sample instanceof IIntegerValue)
                num = ((IIntegerValue) sample).getValue();
            else if (sample instanceof IEnumeratedValue)
                num = ((IEnumeratedValue) sample).getValue();
            else
            {   // Cannot decode that sample type as a number
                return ValueFactory.createIntegerValue(sample.getTime(),
                                    invalid_type,
                                    "<" + sample.getClass().getName() + ">",  //$NON-NLS-1$//$NON-NLS-2$
                                    (INumericMetaData)sample.getMetaData(),
                                    IValue.Quality.Interpolated,
                                    new int[] { INVALID }
                                );
            }
        }
        else // Value carries no value other than stat/sevr
            num = INVALID;
        return ValueFactory.createIntegerValue(sample.getTime(),
                             sample.getSeverity(),
                             sample.getStatus(),
                             (INumericMetaData)sample.getMetaData(),
                             IValue.Quality.Interpolated,
                             new int[] { num });
    }
    
    public void remove()
    {
        raw_values.remove();
    }
}
