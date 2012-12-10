/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VType;

/** Formatter for {@link VType} values that uses exponential formatting
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ExponentialVTypeFormat extends VTypeFormat
{
    final private int precision;
    final private NumberFormat format;
   
    /** Initialize
     *  @param precision Desired number of decimal digits
     */
    public ExponentialVTypeFormat(final int precision)
    {
        this.precision = precision;
        // Is there a better way to get this silly format?
        final StringBuffer pattern = new StringBuffer(10);
        pattern.append("0.");
        for (int i=0; i<precision; ++i)
            pattern.append('0');
        pattern.append("E0");
        format = new DecimalFormat(pattern.toString());
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
        return "Exponential (" + precision + " digits)";
    }

}
