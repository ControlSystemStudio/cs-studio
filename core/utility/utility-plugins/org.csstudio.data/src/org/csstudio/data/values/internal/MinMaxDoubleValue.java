/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values.internal;

import org.csstudio.data.values.IMinMaxDoubleValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.Messages;
import org.eclipse.osgi.util.NLS;

/** Implementation of {@link IMinMaxDoubleValue}.
 *  @author Kay Kasemir
 */
public class MinMaxDoubleValue extends DoubleValue implements IMinMaxDoubleValue
{
    private static final long serialVersionUID = 1L;

    /** The minimum resp. maximum. */
	final private double minimum, maximum;

    /** Constructor from pieces. */
    public MinMaxDoubleValue(final ITimestamp time, final ISeverity severity,
                       final String status, final INumericMetaData meta_data,
                       final Quality quality,
                       final double values[],
                       final double minimum,
                       final double maximum)
	{
		super(time, severity, status, meta_data, quality, values);
        if (minimum <= maximum)
        {
    		this.minimum = minimum;
            this.maximum = maximum;
        }
        else
        {
            this.minimum = maximum;
            this.maximum = minimum;
        }
	}

    /** {@inheritDoc} */
    @Override
    final public double getMinimum()
    {
        return minimum;
    }

    /** {@inheritDoc} */
    @Override
    final public double getMaximum()
    {
        return maximum;
    }

    /** {@inheritDoc} */
	@Override
    final public String format(final Format how, final int precision)
	{
		if (getSeverity().hasValue())
            return super.format(how, precision)
                   + NLS.bind(Messages.MiniMaxiFormat,
                              new Double(minimum), new Double(maximum));
		// else
        return Messages.NoValue;
	}

    /** {@inheritDoc} */
	@Override
	final public boolean equals(final Object obj)
	{
		if (! (obj instanceof MinMaxDoubleValue))
			return false;
		final MinMaxDoubleValue rhs = (MinMaxDoubleValue) obj;
        return Double.doubleToLongBits(minimum) ==
                   Double.doubleToLongBits(rhs.minimum) &&
               Double.doubleToLongBits(maximum) ==
                   Double.doubleToLongBits(rhs.maximum) &&
               super.equals(obj);
	}

    /** {@inheritDoc} */
	@Override
	final public int hashCode()
	{
        return super.hashCode() + (int) (minimum + maximum);
	}
}
