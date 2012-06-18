/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.export;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.ILongValue;
import org.csstudio.data.values.IMinMaxDoubleValue;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Format;
import org.csstudio.trends.databrowser2.Messages;

/** Format an IValue as default, decimal, ...
 *
 *  Shows all array elements for double and long data types
 *  _if_ decimal or exponential format is selected
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ValueFormatter
{
    final private Format format;
    final private int precision;
    private boolean min_max_column = false;
    private NumberFormat number_format = null;

    /** Initialize
     *  @param format Number format to use
     *  @param precision Precision
     */
    public ValueFormatter(final Format format, final int precision)
    {
        this.format = format;
        this.precision = precision;
        if (precision > 0)
        {
        	if (format == Format.Decimal)
	        {
	        	number_format = NumberFormat.getNumberInstance();
	        	number_format.setMinimumFractionDigits(precision);
	        	number_format.setMaximumFractionDigits(precision);
	        }
        	else if (format == Format.Exponential)
        	{
        		// Is there a better way to get this silly format?
            	final StringBuffer pattern = new StringBuffer(10);
                pattern.append("0.");
                for (int i=0; i<precision; ++i)
                    pattern.append('0');
                pattern.append("E0");
                number_format = new DecimalFormat(pattern.toString());
        	}
    	}
    }

    /** @param min_max_column Display min/max info in separate column? */
    public void useMinMaxColumn(final boolean min_max_column)
    {
        this.min_max_column = min_max_column;
    }

    /** @return Text for column headers */
    public String getHeader()
    {
        if (min_max_column)
            return Messages.ValueColumn +
                   Messages.Export_Delimiter + Messages.NegErrColumn +
                   Messages.Export_Delimiter + Messages.PosErrColumn;
        else
            return Messages.ValueColumn;
    }

    /** @return Value formatted into columns */
    public String format(final IValue value)
    {
        if (value == null)
        {
            if (min_max_column)
                return Messages.Export_NoValueMarker +
                       Messages.Export_Delimiter + Messages.Export_NoValueMarker +
                       Messages.Export_Delimiter + Messages.Export_NoValueMarker;
            else
                return Messages.Export_NoValueMarker;
        }

        final StringBuilder buf = new StringBuilder();
        // Value itself
        if (number_format == null)
            buf.append(value.format(format, precision));
        else if (value instanceof IDoubleValue)
        {
        	final double v[] = ((IDoubleValue) value).getValues();
        	for (int i=0; i<v.length; ++i)
        	{
        		if (i > 0)
        			buf.append(Messages.Export_Delimiter);
        		buf.append(number_format.format(v[i]));
        	}
        }
        else if (value instanceof ILongValue)
        {
        	final long v[] = ((ILongValue) value).getValues();
        	for (int i=0; i<v.length; ++i)
        	{
        		if (i > 0)
        			buf.append(Messages.Export_Delimiter);
        		buf.append(number_format.format(v[i]));
        	}
        }
        else
        	buf.append(value.format(format, precision));
        // Optional min, max
        if (min_max_column)
        {
            buf.append(Messages.Export_Delimiter);
            if (value instanceof IMinMaxDoubleValue)
            {   // Turn min..max into negative & positive error
                final IMinMaxDoubleValue mmv = (IMinMaxDoubleValue) value;
                buf.append(mmv.getValue() - mmv.getMinimum());
                buf.append(Messages.Export_Delimiter);
                buf.append(mmv.getMaximum() - mmv.getValue());
            }
            else
            {
                buf.append(0);
                buf.append(Messages.Export_Delimiter);
                buf.append(0);
            }
        }
        return buf.toString();
    }

    @Override
    public String toString()
    {
        switch (format)
        {
        case Default:
            return Messages.Format_Default;
        case Decimal:
            return nameWithPrecision(Messages.Format_Decimal);
        case Exponential:
            return nameWithPrecision(Messages.Format_Exponential);
        default:
            return format.name();
        }
    }

    /** @return name of format with info on 'digits' */
    private String nameWithPrecision(final String name)
    {
        return name + " (" + precision + " digits)";
    }
}
