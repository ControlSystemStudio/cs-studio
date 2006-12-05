package org.csstudio.utility.pv;

/** Base for numric values.
 *  @author Kay Kasemir
 */
public abstract class NumericValue implements Value
{
    private final MetaData meta;
    
    /** Constructor
     *  @param value Numeric value
     *  @param text String representation
     */
    public NumericValue(final MetaData meta)
    {
        this.meta = meta;
    }
    
    public MetaData getMeta()
    {   return meta;  }
}
