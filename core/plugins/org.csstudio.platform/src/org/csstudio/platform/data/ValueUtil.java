/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.platform.data;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.csstudio.platform.internal.data.EnumeratedValue;
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
        if (value instanceof IDoubleValue)
            return ((IDoubleValue) value).getValues().length;
        else if (value instanceof ILongValue)
            return ((ILongValue) value).getValues().length;
        else if (value instanceof IEnumeratedValue)
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
            if (value instanceof IDoubleValue)
                return ((IDoubleValue) value).getValues()[index];
            else if (value instanceof ILongValue)
                return ((ILongValue) value).getValues()[index];
            else if (value instanceof IEnumeratedValue)
                return ((IEnumeratedValue) value).getValues()[index];
            // else:
            // Cannot decode that sample type as a number.
            return Double.NaN;
        }
        // else: Sample carries no value other than stat/sevr
        return Double.NEGATIVE_INFINITY;
    }

    /** Try to get a double-typed array from the Value.
     *  @param value The value to decode.
     *  @see #getSize(Value)
     *  @see #getDouble(Value)
     *  @return A double array, or an empty double array in case the value type
     *          does not decode into a number, or if the value's severity
     *          indicates that there happens to be no useful value.
     */
    public static double[] getDoubleArray(IValue value){
    	if (value.getSeverity().hasValue())
        {
            if (value instanceof IDoubleValue)
                return ((IDoubleValue) value).getValues();
            else if (value instanceof ILongValue) {
            	double[] result = new double[((ILongValue) value).getValues().length];
            	int i =0;
            	for(long l :((ILongValue) value).getValues()){
            		result[i] = l;
            		i++;
            	}
            	 return result;
            }
               
            else if (value instanceof IEnumeratedValue){
            	double[] result = new double[((IEnumeratedValue) value).getValues().length];
            	int i =0;
            	for(int l :((IEnumeratedValue) value).getValues()){
            		result[i] = l;
            		i++;
            	}
            	 return result;
            }
              
            // else:
            // Cannot decode that sample type as a number.
            return new double[0];
        }
        // else: Sample carries no value other than stat/sevr
        return new double[0];
    }
    
    /** Try to get an info string from the Value.
     *  <p>
     *  For numeric values, which is probably the vast majority of
     *  all values, this is the severity and status information.
     *  <p>
     *  For 'enum' type values, <code>getDouble()</code> will return
     *  the numeric value, and <code>getInfo()</code> returns the associated
     *  enumeration string, appended to a possible severity/status text.
     *  <p>
     *  For string type values, this is the string value and
     *  a possible severity/status text,
     *  while <code>getDouble()</code> will return <code>NaN</code>.
     *  
     *  TOFO Rethink this one. Only used in Data Browser??
     *  
     *  @param value The value to decode.
     *  @return The info string, never <code>null</code>.
     */
    public static String getInfo(final IValue value)
    {
        String info = null;
        String val_txt = null;
        final String sevr = value.getSeverity().toString();
        final String stat = value.getStatus();
        if (sevr.length() > 0  ||  stat.length() > 0)
            info = sevr + Messages.SevrStatSeparator + stat;
        if (value instanceof EnumeratedValue)
            val_txt = ((EnumeratedValue) value).format();
        else if (value instanceof StringValue)
            val_txt = ((IStringValue) value).getValue();
        if (val_txt != null) // return info appended to value 
        {
            if (info == null)
                return val_txt;
            return val_txt + Messages.SevrStatSeparator + info;
        }
        if (info == null)
            return ""; //$NON-NLS-1$
        return info;
    }
    
    /** @return Non-<code>null</code> String for the value and its
     *          severity/status. Does not include the time stamp.
     *  @see Value#format()
     *  @see #getInfo(Value)
     */
    public static String formatValueAndSeverity(final IValue value)
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
    
    /**
	 * Converts the given value into a string representation. For string values,
	 * returns the value. For numeric (double and long) values, returns a
	 * non-localized string representation. Double values use a point as the
	 * decimal seperator. For other types of values, the value's
	 * {@link IValue#format()} method is called and its result returned.
	 * 
	 * @param value
	 *            the value.
	 * @return a string representation of the value.
	 */
    public static String getString(final IValue value)
    {
		if (value instanceof IStringValue) {
			return ((IStringValue) value).getValue();
		} else if (value instanceof IDoubleValue) {
			IDoubleValue idv = (IDoubleValue) value;
			double dv = idv.getValue();
			if (Double.isNaN(dv))
			   return "NaN";
			if (Double.isInfinite(dv))
			   return "Inf";
			int precision = ((INumericMetaData) idv.getMetaData()).getPrecision();
			DecimalFormatSymbols dcf = new DecimalFormatSymbols(Locale.US);
			dcf.setDecimalSeparator('.');
			DecimalFormat format = new DecimalFormat("0.#", dcf); //$NON-NLS-1$
			format.setMinimumFractionDigits(precision);
			format.setMaximumFractionDigits(precision);
			return format.format(dv);
		} else if (value instanceof ILongValue) {
			ILongValue lv = (ILongValue) value;
			return Long.toString(lv.getValue());
		} else {
			return (value == null) ? "" : value.format();
		}
    }
}
