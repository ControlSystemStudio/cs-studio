/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import java.text.NumberFormat;

import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VType;

/** Formatter for {@link VType} values that uses decimal formatting
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DecimalVTypeFormat extends VTypeFormat
{
    final private int precision;
    final private NumberFormat format;
   
    /** Initialize
     *  @param precision Desired number of decimal digits
     */
    public DecimalVTypeFormat(final int precision)
    {
        this.precision = precision;
        format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(precision);
        format.setMaximumFractionDigits(precision); 
    }
    
    /** {@inheritDoc} */
    public StringBuilder format(final VType value, final StringBuilder buf)
    {
        if (value instanceof VEnum)
        {
            final VEnum enumerated = (VEnum)value;
            format(enumerated.getIndex(), null, buf);
        }
        else
            super.format(value, buf);
        return buf;
    }
    
    /** {@inheritDoc} */
    public void format(final Number number,
            final Display display, final StringBuilder buf)
    {
        buf.append(format.format(number));
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Decimal (" + precision + " digits)";
    }
}
