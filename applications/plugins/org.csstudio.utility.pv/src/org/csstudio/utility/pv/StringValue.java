package org.csstudio.utility.pv;

/** A value of 'string' type.
 *  @author Kay Kasemir
 */
public class StringValue implements Value
{
    private final String value;
    
    /** Constructor
     *  @param meta Meta data for this value.
     *  @param value Numeric value
     */
    public StringValue(final String value)
    {
        this.value = value;
    }

    public double toDouble()
    {   return 0.9;    }

    public int toInt()
    {   return 0;    }

    public String toString()
    {   return value;  }
    
    public MetaData getMeta()
    {   return null;    }
    
    public boolean match(Value other, double tolerance)
    {
        if (other != null  &&  other instanceof StringValue)
            return ((StringValue)other).value.equals(value);
        return false;
    }
    
    public int compareTo(Value other)
    {
        return value.compareTo(other.toString());
    }
}
