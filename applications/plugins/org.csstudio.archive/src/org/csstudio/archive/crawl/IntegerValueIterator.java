package org.csstudio.archive.crawl;

import java.util.Iterator;

import org.csstudio.archive.crawl.ValueIterator;
import org.csstudio.value.DoubleValue;
import org.csstudio.value.EnumValue;
import org.csstudio.value.IntegerValue;
import org.csstudio.value.Severity;
import org.csstudio.value.Value;

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
public class IntegerValueIterator implements Iterator<IntegerValue>
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
    
    static Severity invalid_type = null;
    private final Iterator<Value> raw_values;
    
    public IntegerValueIterator(ValueIterator raw_values)
    {
        this.raw_values = raw_values;
    }
    
    public boolean hasNext()
    {
        return raw_values.hasNext();
    }

    public IntegerValue next()
    {
        Value sample = raw_values.next();
        int num;
        if (sample.getSeverity().hasValue())
        {
            if (sample instanceof DoubleValue)
                num = (int)((DoubleValue) sample).getValue();
            else if (sample instanceof IntegerValue)
                num = ((IntegerValue) sample).getValue();
            else if (sample instanceof EnumValue)
                num = ((EnumValue) sample).getValue();
            else
            {   // Cannot decode that sample type as a number
                if (invalid_type == null)
                {   // Lazy init of invalid_type Severity
                    invalid_type = new Severity()
                    {
                        public String toString()
                        {   return "Invalid Type";  } //$NON-NLS-1$
    
                        public boolean hasValue()
                        {   return false; }
    
                        public boolean isInvalid()
                        {   return false; }
    
                        public boolean isMajor()
                        {   return false; }
    
                        public boolean isMinor()
                        {   return false; }
    
                        public boolean isOK()
                        {   return false; }
                    };
                }
                return new IntegerValue(sample.getTime(),
                                    invalid_type,
                                    "<" + sample.getClass().getName() + ">",  //$NON-NLS-1$//$NON-NLS-2$
                                    sample.getMetaData(),
                                    new int[] { INVALID }
                                );
            }
        }
        else // Value carries no value other than stat/sevr
            num = INVALID;
        return new IntegerValue(sample.getTime(),
                             sample.getSeverity(),
                             sample.getStatus(),
                             sample.getMetaData(),
                             new int[] { num });
    }
    
    public void remove()
    {
        raw_values.remove();
    }
}
