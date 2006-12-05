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
    	
        NumericMetaData meta = (NumericMetaData)getMeta();
        final int precision = meta.getPrecision();
        String val_txt;
        if (precision < 0)
        	val_txt = Double.toString(value);
        else
        {
	        NumberFormat format = NumberFormat.getNumberInstance();
			format.setMinimumFractionDigits(precision);
	        format.setMaximumFractionDigits(precision);
	        val_txt = format.format(value);
        }
        final String units = meta.getUnits();
        if (units != null  &&  units.length() > 0)
        	return val_txt + " " + units; //$NON-NLS-1$
        return val_txt;
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
