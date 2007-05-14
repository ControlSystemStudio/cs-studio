package org.csstudio.platform.data;

import org.csstudio.platform.internal.data.DoubleValue;
import org.csstudio.platform.internal.data.EnumeratedValue;
import org.csstudio.platform.internal.data.IntegerValue;
import org.csstudio.platform.internal.data.Messages;
import org.csstudio.platform.internal.data.StringValue;
import org.csstudio.platform.internal.data.Value;

/** Helper for decoding the data in a {@link IValue},
 *  mostly for display purposes.
 *  @author Kay Kasemir
 */
public class ValueUtil
{
    /** @return Array length of the value. <code>1</code> for scalars. */
    public static int getSize(IValue value)
    {
        if (value instanceof DoubleValue)
            return ((IDoubleValue) value).getValues().length;
        else if (value instanceof IntegerValue)
            return ((IIntegerValue) value).getValues().length;
        else if (value instanceof EnumeratedValue)
            return ((IEnumeratedValue) value).getValues().length;
        return 1;
    }
    
    /** Try to get a double number from the Value.
     *  <p>
     *  Some applications only deal with numeric data,
     *  so they want to interprete integer, enum and double values
     *  all the same.
     *  @param value The value to decode.
     *  @return A double, or <code>Double.NaN</code> in case the value type
     *          does not decode into a number, or
     *          <code>Double.NEGATIVE_INFINITY</code> if the value's severity
     *          indicates that there happens to be no useful value.
     */
    public static double getDouble(IValue value)
    {
        return getDouble(value, 0);
    }

    /** Try to get a double-typed array element from the Value.
     *  @param value The value to decode.
     *  @param index The array index, 0 ... getSize()-1.
     *  @see #getSize(Value)
     *  @see #getDouble(Value)
     *  @return A double, or <code>Double.NaN</code> in case the value type
     *          does not decode into a number, or
     *          <code>Double.NEGATIVE_INFINITY</code> if the value's severity
     *          indicates that there happens to be no useful value.
     */
    public static double getDouble(IValue value, int index)
    {
        if (value.getSeverity().hasValue())
        {
            if (value instanceof DoubleValue)
                return ((IDoubleValue) value).getValues()[index];
            else if (value instanceof IntegerValue)
                return ((IIntegerValue) value).getValues()[index];
            else if (value instanceof EnumeratedValue)
                return ((IEnumeratedValue) value).getValues()[index];
            // else:
            // Cannot decode that sample type as a number.
            return Double.NaN;
        }
        // else: Sample carries no value other than stat/sevr
        return Double.NEGATIVE_INFINITY;
    }

    /** Try to get an info string from the Value.
     *  <p>
     *  For numeric values, which is probably the vast majority of
     *  all values, this is the severity and status information.
     *  If they are 'OK', it's an empty string, otherwise its the 
     *  severity/status text.
     *  <p>
     *  For 'enum' type values, <code>getDouble()</code> will return
     *  the numeric value, and <code>getInfo()</code> returns the associated
     *  enumeration string, appended to a possible severity/status text.
     *  <p>
     *  For string type values, this is the string value,
     *  appended to a possible severity/status text,
     *  while <code>getDouble()</code> will return <code>NaN</code>.
     *  
     *  @param value The value to decode.
     *  @return The info string. May be <code>null</code>!
     */
    public static String getInfo(IValue value)
    {
        String info = null;
        String val_txt = null;
        String sevr = value.getSeverity().toString();
        String stat = value.getStatus();
        if (sevr.length() > 0  ||  stat.length() > 0)
            info = sevr + Messages.SevrStatSeparator + stat;
        if (value instanceof EnumeratedValue)
            val_txt = ((EnumeratedValue) value).format();
        else if (value instanceof StringValue)
            val_txt = ((IStringValue) value).getValue();
        if (val_txt != null) // return value appended to info
            if (info == null)
                return val_txt;
            else
                return info + Messages.SevrStatSeparator + val_txt;
        return info;
    }
    
    /** @return Non-<code>null</code> String for the value and its
     *          severity/status. Does not include the time stamp.
     *  @see Value#format()
     *  @see #getInfo(Value)
     */
    public static String formatValueAndSeverity(IValue value)
    {
        if (value == null)
            return ""; //$NON-NLS-1$
        String v = value.format();
        if (value.getSeverity().isOK())
            return v;
        return v + Messages.ValueSevrStatSeparator
               + value.getSeverity().toString()
               + Messages.SevrStatSeparator
               + value.getStatus()
               + Messages.SevrStatEnd;
    }
}
