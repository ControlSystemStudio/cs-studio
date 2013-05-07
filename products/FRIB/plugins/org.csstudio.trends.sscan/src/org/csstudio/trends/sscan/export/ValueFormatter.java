/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.export;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.ILongValue;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Format;
import org.csstudio.trends.sscan.Messages;

/** Format an IValue as default, decimal, ...
 * 
 *  TODO Now shows all array elements for double and long data types
 *  _if_ decimal or exponential format is selected
 *  Could be better overall.
 *  
 *  @author Kay Kasemir
 */
public class ValueFormatter
{
    final private Format format;
    final private int precision;
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
                pattern.append("0."); //$NON-NLS-1$
                for (int i=0; i<precision; ++i)
                    pattern.append('0');
                pattern.append("E0"); //$NON-NLS-1$
                number_format = new DecimalFormat(pattern.toString());
        	}
    	}
    }

    /** @return Text for column headers */
    public String getHeader()
    {
        return Messages.ValueColumn;
    }

    /** @return Value formatted into columns */
    public String format(final IValue value)
    {
        if (value == null)
            return Messages.Export_NoValueMarker;
        if (number_format == null)
        	return value.format(format, precision);
        
    	final StringBuilder buf = new StringBuilder();
        if (value instanceof IDoubleValue)
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
    @SuppressWarnings("nls")
    private String nameWithPrecision(final String name)
    {
        return name + " (" + precision + " digits)";
    }
}
