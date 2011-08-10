/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.export;

import org.csstudio.common.trendplotter.Messages;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Format;

/** Format an IValue as default, decimal, ...
 *  @author Kay Kasemir
 */
public class ValueFormatter
{
    final private Format format;
    final private int precision;

    /** Initialize
     *  @param format Number format to use
     *  @param precision Precision
     */
    public ValueFormatter(final Format format, final int precision)
    {
        this.format = format;
        this.precision = precision;
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
        // TODO Find a way to show full arrays, not just "1, 2, 3, ...."
        return value.format(format, precision);
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
