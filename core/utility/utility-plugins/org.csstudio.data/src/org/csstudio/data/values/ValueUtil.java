/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Helper for decoding the data in a {@link IValue},
 *  mostly for display purposes.
 *  @author Kay Kasemir
 */
public class ValueUtil
{
    /** @return Array length of the value. <code>1</code> for scalars. */
    public static int getSize(final IValue value)
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
    public static double getDouble(final IValue value)
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
    public static double getDouble(final IValue value, final int index)
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
    public static double[] getDoubleArray(final IValue value)
    {
        if (! value.getSeverity().hasValue())
            // Sample carries no value other than stat/sevr
            return new double[0];

        if (value instanceof IDoubleValue)
            return ((IDoubleValue) value).getValues();
        else if (value instanceof ILongValue)
        {
            final long ilv[] = ((ILongValue) value).getValues();
            final double result[] = new double[ilv.length];
            for (int i = ilv.length-1; i>=0; --i)
                result[i] = ilv[i];
            return result;
        }
        else if (value instanceof IEnumeratedValue)
        {
            final int iev[] = ((IEnumeratedValue) value).getValues();
            final double result[] = new double[iev.length];
            for (int i = iev.length-1; i>=0; --i)
                result[i] = iev[i];
            return result;
        }
        // else:
        // Cannot decode that sample type as a number.
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
     *  @param value The value to decode.
     *  @return The info string, never <code>null</code>.
     *
     *  @deprecated Remove. Only used in old Data Browser??
     */
    @Deprecated
    public static String getInfo(final IValue value)
    {
        String info = null;
        String val_txt = null;
        final String sevr = value.getSeverity().toString();
        final String stat = value.getStatus();
        if (sevr.length() > 0  ||  stat.length() > 0)
            info = sevr + Messages.SevrStatSeparator + stat;
        if (value instanceof IEnumeratedValue)
            val_txt = ((IEnumeratedValue) value).format();
        else if (value instanceof IStringValue)
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
     * decimal separator. For other types of values, the value's
     * {@link IValue#format()} method is called and its result returned.
     *
     * @param value
     *            the value.
     * @return a string representation of the value.
     */
    @SuppressWarnings("nls")
    public static String getString(final IValue value)
    {
        if (value instanceof IStringValue)
            return ((IStringValue) value).getValue();
        else if (value instanceof IDoubleValue)
        {
            final IDoubleValue idv = (IDoubleValue) value;
            final double dv = idv.getValue();
            if (Double.isNaN(dv))
               return "NaN";
            if (Double.isInfinite(dv))
               return "Inf";
            final int precision = ((INumericMetaData) idv.getMetaData()).getPrecision();
            final DecimalFormatSymbols dcf = new DecimalFormatSymbols(Locale.US);
            dcf.setDecimalSeparator('.');
            final DecimalFormat format = new DecimalFormat("0.#", dcf);
            format.setMinimumFractionDigits(precision);
            format.setMaximumFractionDigits(precision);
            return format.format(dv);
        }
        else if (value instanceof ILongValue)
        {
            final ILongValue lv = (ILongValue) value;
            return Long.toString(lv.getValue());
        }
        else
            return (value == null) ? "" : value.format();
    }
}
