package org.csstudio.utility.pv;

/** A Value of type 'int'.
 *  @author Kay Kasemir
 */
public class IntegerValue extends NumericValue
{
    private final int value;
    
    public IntegerValue(NumericMetaData meta, int value)
    {
        super(meta);
        this.value = value;
    }
    
    public double toDouble()
    {   return value;    }

    public int toInt()
    {   return value;    }

    public String toString()
    {
        return Integer.toString(value) + " " +      //$NON-NLS-1$
            ((NumericMetaData)getMeta()).getUnits(); 
    }

    public boolean match(Value other, double tolerance)
    {
        if (other != null  &&  other instanceof IntegerValue)
            return value == ((IntegerValue)other).value;
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
