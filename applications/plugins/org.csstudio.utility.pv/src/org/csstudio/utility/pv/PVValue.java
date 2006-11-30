package org.csstudio.utility.pv;

/** Helpers for converting between PV value data types.
 *  @author Kay Kasemir
 */
public class PVValue
{
    /** @return Returns the given object as a 'double'. */
    @SuppressWarnings("nls")
    public static final double toDouble(Object value) throws Exception
    {
        if (value instanceof Double)
            return ((Double) value).doubleValue();
        if (value instanceof Integer)
            return ((Integer) value).doubleValue();
        if (value instanceof String)
            return Double.parseDouble((String) value);
        if (value instanceof EnumValue)
            return ((EnumValue)value).getValue();
        if (value instanceof Boolean)
            return ((Boolean) value).booleanValue() ? 1.0 : 0.0;
        throw new Exception("Cannot convert " + value.getClass()
                        + " to double.");
    }

    /** @return Returns the given object as an 'int'. */
    @SuppressWarnings("nls")
    public static final int toInt(Object value) throws Exception
    {
        if (value instanceof Double)
            return ((Double) value).intValue();
        if (value instanceof Integer)
            return ((Integer) value).intValue();
        if (value instanceof String)
            return Integer.parseInt((String) value);
        if (value instanceof EnumValue)
            return ((EnumValue)value).getValue();
        if (value instanceof Boolean)
            return ((Boolean) value).booleanValue() ? 1 : 0;
        throw new Exception("Cannot convert " + value.getClass()
                        + " to int.");
    }

    /** Check if the values match within the given tolerance.
     *  <p>
     *  Strings are compared without any tolerance.
     *  Rest is compared as a 'double'.
     *  
     *  @param a One value.
     *  @param b The other value
     *  @param tolerance Numeric tolerance, e.g. 0.1.
     *  @return Returns <code>true</code> if the values a, b match.
     */
    public static boolean match(Object a, Object b, double tolerance)
    {
        if (a == null  ||  b == null)
            return true;
        if (a instanceof String  ||  b instanceof String ||
            a instanceof EnumValue  ||  b instanceof EnumValue)
        {   // Compare as strings, no tolerance
            String s_a = a.toString();
            String s_b = b.toString();
            return s_a.equals(s_b);
        }
        try
        {
            double val_a = toDouble(a);
            double val_b = toDouble(b);
            return Math.abs(val_b - val_a) <= tolerance;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    /** <code>null</code>-safe conversion of anything to <code>String</code>. */
    public static final String toString(Object value) throws Exception
    {
        if (value == null)
            return ""; //$NON-NLS-1$
        // All the rest, including EnumValue, go here:
        return value.toString();
    }
}
