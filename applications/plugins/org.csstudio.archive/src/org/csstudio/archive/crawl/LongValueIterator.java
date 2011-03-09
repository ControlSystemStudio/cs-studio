package org.csstudio.archive.crawl;

import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.IEnumeratedValue;
import org.csstudio.data.values.ILongValue;
import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueFactory;

/** Turns an Iterator over raw <code>Value</code>s into one over
 *  <code>ILongValue</code>s.
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
public class LongValueIterator implements ValueIterator
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

    private final ValueIterator raw_values;

    static private INumericMetaData numeric_meta = null;

    public LongValueIterator(ValueIterator raw_values)
    {
        this.raw_values = raw_values;
    }

    public boolean hasNext()
    {
        return raw_values.hasNext();
    }

    public ILongValue next() throws Exception
    {
        final IValue sample = raw_values.next();
        // Check if meta data is INumericMetaData. Else, create one.
        IMetaData meta = sample.getMetaData();
        if (! (meta instanceof INumericMetaData))
        {
            if (numeric_meta == null)
                numeric_meta = ValueFactory.createNumericMetaData(0.0, 0.0,
                                0.0, 0.0, 0.0, 0.0, 0, ""); //$NON-NLS-1$
            meta = numeric_meta;
        }
        long num;
        if (sample.getSeverity().hasValue())
        {
            if (sample instanceof IDoubleValue)
                num = (long)((IDoubleValue) sample).getValue();
            else if (sample instanceof ILongValue)
                num = ((ILongValue) sample).getValue();
            else if (sample instanceof IEnumeratedValue)
                num = ((IEnumeratedValue) sample).getValue();
            else
            {   // Cannot decode that sample type as a number
                return ValueFactory.createLongValue(sample.getTime(),
                                    invalid_type,
                                    "<" + sample.getClass().getName() + ">",  //$NON-NLS-1$//$NON-NLS-2$
                                    (INumericMetaData)meta,
                                    IValue.Quality.Interpolated,
                                    new long[] { INVALID }
                                );
            }
        }
        else // Value carries no value other than stat/sevr
            num = INVALID;
        return ValueFactory.createLongValue(sample.getTime(),
                             sample.getSeverity(),
                             sample.getStatus(),
                             (INumericMetaData)meta,
                             IValue.Quality.Interpolated,
                             new long[] { num });
    }
}
