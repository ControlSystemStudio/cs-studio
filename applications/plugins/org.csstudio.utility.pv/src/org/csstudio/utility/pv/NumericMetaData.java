package org.csstudio.utility.pv;

/** Meta Data common to all numeric Value types.
 *  @author Kay Kasemir
 */
public class NumericMetaData implements MetaData
{
    private final int precision;
    private final String units;
    
    /** Constructor. */
    public NumericMetaData(final int precision, final String units)
    {
        this.precision = precision;
        this.units = units;
    }

    /** @return Suggested display precision. */
    public int getPrecision()
    {   return precision;    }

    /** @return Suggested engineering unit string. */
    public String getUnits()
    {   return units;    }
}
