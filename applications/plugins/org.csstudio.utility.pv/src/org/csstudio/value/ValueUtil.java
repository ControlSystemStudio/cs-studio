package org.csstudio.value;

/** Helper for decoding the data in a Value for e.g. display purposes.
 *  @author Kay Kasemir
 */
public class ValueUtil
{
    /** Try to get a double number from the Sample.
     *  @param value The sample to decode.
     *  @return A double, or <code>Double.NaN</code> in case the Sample type
     *          does not decode into a number, or
     *          <code>Double.NEGATIVE_INFINITY</code> if the sample's severity
     *          indicates that there happens to be no useful value.
     */
    public static double getDouble(Value value)
    {
        if (value.getSeverity().hasValue())
        {
            if (value instanceof DoubleValue)
                return ((DoubleValue) value).getValue();
            else if (value instanceof IntegerValue)
                return ((IntegerValue) value).getValue();
            else if (value instanceof EnumValue)
                return ((EnumValue) value).getValue();
            // else:
            // Cannot decode that sample type as a number.
            return Double.NaN;
        }
        // else: Sample carries no value other than stat/sevr
        return Double.NEGATIVE_INFINITY;
    }

    /** Try to get an info string from the Sample.
     *  <p>
     *  For numeric samples, which is probably the vast majority of
     *  samples, this is the severity and status information.
     *  If they are 'OK', it's an empty string, otherwise its the 
     *  severity/status text.
     *  <p>
     *  For 'enum' type samples, <code>getDouble()</code> will return
     *  the numeric value, and <code>getInfo()</code> returns the associated
     *  enumeration string, appended to a possible severity/status text.
     *  <p>
     *  For string type samples, this is the string value,
     *  appended to a possible severity/status text,
     *  while <code>getDouble()</code> will return <code>NaN</code>.
     *  
     *  @param value The sample to decode.
     *  @return The info string. May be <code>null</code>!
     */
    public static String getInfo(Value value)
    {
        String info = null;
        String val_txt = null;
        String sevr = value.getSeverity().toString();
        String stat = value.getStatus();
        if (sevr.length() > 0  ||  stat.length() > 0)
            info = sevr + Messages.SevrStatSeparator + stat;
        if (value instanceof EnumValue)
            val_txt = ((EnumValue) value).format();
        else if (value instanceof StringValue)
            val_txt = ((StringValue) value).getValue();
        if (val_txt != null) // return value appended to info
            if (info == null)
                return val_txt;
            else
                return info + Messages.SevrStatSeparator + val_txt;
        return info;
    }
    
    /** @return Non-<code>null</code> String for the value and its
     *          severity/status info.
     *  @see Value#format()
     *  @see #getInfo(Value)
     */
    public static String formatValueAndInfo(Value value)
    {
        if (value == null)
            return ""; //$NON-NLS-1$
        String v = value.format();
        String i = getInfo(value);
        if (i != null && i.length() > 0)
            return v + Messages.SevrStatSeparator + i;
        return v;
    }
}
