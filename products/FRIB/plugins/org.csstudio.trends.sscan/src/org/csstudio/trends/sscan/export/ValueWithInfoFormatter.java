/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.export;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Format;
import org.csstudio.trends.sscan.Messages;

/** Format an IValue to show the value as well as the severity/status
 *  @author Kay Kasemir
 */
public class ValueWithInfoFormatter extends ValueFormatter
{
    /** Initialize
     *  @param format Number format to use
     *  @param precision Precision
     */
    public ValueWithInfoFormatter(final Format format, final int precision)
    {
        super(format, precision);
    }

    /** {@inheritDoc} */
    @Override
    public String getHeader()
    {
        return Messages.ValueColumn + Messages.Export_Delimiter + Messages.SeverityColumn +
            Messages.Export_Delimiter + Messages.StatusColumn;
    }

    /** {@inheritDoc} */
    @Override
    public String format(final IValue value)
    {
        if (value == null)
            return Messages.Export_NoValueMarker +
                Messages.Export_Delimiter + Messages.Export_NoValueMarker +
                Messages.Export_Delimiter + Messages.Export_NoValueMarker;
        return super.format(value) + Messages.Export_Delimiter +
            value.getSeverity() + Messages.Export_Delimiter + value.getStatus();
    }
}
