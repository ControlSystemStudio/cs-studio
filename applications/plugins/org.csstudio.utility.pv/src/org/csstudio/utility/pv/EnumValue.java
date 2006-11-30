package org.csstudio.utility.pv;

/** A value of 'enumerated' type.
 *  <p>
 *  Gives both the numeric (positive integer) value
 *  and the corresponding string.
 *  @author Kay Kasemir
 */
public class EnumValue
{
    private int value;
    private String text;
    
    static EnumValue fromData(final int value, final String strings[])
    {
        if (strings == null  ||  value < 0  ||  value >= strings.length)
            return new EnumValue(value,
                            "<" + Integer.toString(value) + ">");  //$NON-NLS-1$//$NON-NLS-2$
        return new EnumValue(value, strings[value]);
    }
    
    /** Constructor
     *  @param value Numeric value
     *  @param text String representation
     */
    public EnumValue(final int value, final String text)
    {
        this.value = value;
        this.text = text;
    }
    
    /** @return the numeric (positive integer) value */
    public int getValue()
    {   return value; }
    
    /** @return the string representation */
    public String toString()
    {   return text;  }
}
