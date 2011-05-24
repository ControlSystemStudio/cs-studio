/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values.internal;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.csstudio.data.values.ILongValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.Messages;

/** Implementation of {@link ILongValue}.
 *  @see ILongValue
 *  @author Kay Kasemir, Xihui Chen
 */
public class LongValue extends Value implements ILongValue
{
    private static final long serialVersionUID = 1L;

    private final long values[];

    /** Constructor from pieces. */
	public LongValue(final ITimestamp time, final ISeverity severity,
                     final String status, final INumericMetaData meta_data,
                     final Quality quality, final long values[])
	{
		super(time, severity, status, meta_data, quality);
		this.values = values;
	}

    /** {@inheritDoc} */
	@Override
    public final long[] getValues()
	{	return values;	}

    /** {@inheritDoc} */
    @Override
    public final long getValue()
	{	return values[0];	}

    /** {@inheritDoc} */
	@Override
	public final String format(final Format how, int precision)
	{
		// Any value at all?
		if (!getSeverity().hasValue())
			return Messages.NoValue;

		final StringBuffer buf = new StringBuffer();
		if (how == Format.Exponential)
		{
			// Is there a better way to get this silly format?
			NumberFormat fmt;
			StringBuffer pattern = new StringBuffer(10);
			pattern.append("0."); //$NON-NLS-1$
			for (int i = 0; i < precision; ++i)
				pattern.append('0');
			pattern.append("E0"); //$NON-NLS-1$
			fmt = new DecimalFormat(pattern.toString());
			buf.append(fmt.format(values[0]));
			for (int i = 1; i < values.length; i++)
			{
				buf.append(Messages.ArrayElementSeparator);
				buf.append(fmt.format(values[i]));
			}
		}
		else if (how == Format.String)
		{   // Format array elements as characters
			for (int i = 0; i < values.length; i++)
			{
				final char c = getDisplayChar((char) values[i]);
				if (c == 0)
					break;
				buf.append(c);
			}
		}
		else
		{
			buf.append(values[0]);
			for (int i = 1; i < values.length; i++)
			{
				buf.append(Messages.ArrayElementSeparator);
				buf.append(values[i]);
				if (i >= MAX_FORMAT_VALUE_COUNT)
				{
					buf.append(Messages.ArrayElementSeparator);
					buf.append("..."); //$NON-NLS-1$
					break;
				}
			}
		}
		return buf.toString();
	}

    /** {@inheritDoc} */
	@Override
	public final boolean equals(final Object obj)
	{
		if (! (obj instanceof LongValue))
			return false;
		final LongValue rhs = (LongValue) obj;
		if (rhs.values.length != values.length)
			return false;
		for (int i=0; i<values.length; ++i)
			if (rhs.values[i] != values[i])
				return false;
		return super.equals(obj);
	}

    /** {@inheritDoc} */
	@Override
	public final int hashCode()
	{
		int h = super.hashCode();
		for (int i=0; i<values.length; ++i)
			h += values[i];
		return h;
	}
}
