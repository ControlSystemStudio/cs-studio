package org.csstudio.utility.pv;

import java.text.NumberFormat;

/** A Value of type 'double'.
 *  @author Kay Kasemir
 */
public class DoubleValue extends NumericValue
{
    private final double value;
    
    public DoubleValue(NumericMetaData meta, double value)
    {
        super(meta);
        this.value = value;
    }
    
    public double toDouble()
    {   return value;    }

    public int toInt()
    {   return (int)value;    }

    public String toString()
    {
        NumberFormat format = NumberFormat.getNumberInstance();
        NumericMetaData meta = (NumericMetaData)getMeta();
        format.setMinimumFractionDigits(meta.getPrecision());
        format.setMaximumFractionDigits(meta.getPrecision());
        return format.format(value) + " " + meta.getUnits(); //$NON-NLS-1$
    }

    public boolean match(Value other, double tolerance)
    {
        if (other != null  &&  other instanceof DoubleValue)
            return Math.abs(value - ((DoubleValue)other).value) <= tolerance;
        return false;
    }
    
    public int compareTo(Value other)
    {
        double diff = value - other.toDouble();
        if (diff < 0)
            return -1;
        if (diff > 0)
            return 1;
        return 0;
    }
}
