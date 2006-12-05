package org.csstudio.utility.pv;

/** A value of 'enumerated' type.
 *  <p>
 *  Gives both the numeric (positive integer) value
 *  and the corresponding string.
 *  @author Kay Kasemir
 */
public class EnumValue implements Value
{
    private final EnumeratedMetaData meta;
    private final int value;
    
    /** Constructor
     *  @param meta Meta data for this value.
     *  @param value Numeric value
     */
    public EnumValue(final EnumeratedMetaData meta, final int value)
    {
        this.meta = meta;
        this.value = value;
    }

    public double toDouble()
    {   return value;    }

    public int toInt()
    {   return value;    }

    public String toString()
    {   return meta.getState(value);  }
    
    public MetaData getMeta()
    {   return meta;    }
    
    public boolean match(Value other, double tolerance)
    {
        if (other != null  &&  other instanceof EnumValue)
            return ((EnumValue)other).value == value;
        return false;
    }
    
    public int compareTo(Value other)
    {
        int diff = value - other.toInt();
        if (diff < 0)
            return -1;
        if (diff > 0)
            return 1;
        return 0;
    }
}
