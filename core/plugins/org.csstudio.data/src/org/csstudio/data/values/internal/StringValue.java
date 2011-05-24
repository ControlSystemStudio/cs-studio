/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values.internal;

import java.util.Arrays;

import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IStringValue;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.Messages;

/** Implementation of {@link IStringValue}.
 *  @see IStringValue
 *  @author Kay Kasemir
 */
public class StringValue extends Value implements IStringValue
{
    private static final long serialVersionUID = 1L;

    // Slight inconsistency, because that's the way EPICS works right now:
    // There is no array of Strings as there would be arrays of
    // the other types, so we only handle a scalar string as well....
	private final String values[];

	public StringValue(final ITimestamp time, final ISeverity severity, final String status,
	        final Quality quality, final String values[])
    {   // String has no meta data!
		super(time, severity, status, null, quality);
		this.values = values;
		if (values == null  ||  values.length < 1)
		    throw new java.lang.IllegalArgumentException("Values"); //$NON-NLS-1$
	}

    /** {@inheritDoc} */
    @Override
    public final String[] getValues()
    {
        return values;
    }

    /** {@inheritDoc} */
	@Override
    public final String getValue()
	{
		return values[0];
	}

    /** {@inheritDoc} */
	@Override
    public final String format(final Format how, final int precision)
	{
		if (getSeverity() != null && getSeverity().hasValue() == false)
	        return Messages.NoValue;
		if (values.length == 1)
            return values[0];
		final StringBuffer result = new StringBuffer();
        result.append(values[0]);
        for (int i = 1; i < values.length; i++)
        {
            result.append(Messages.ArrayElementSeparator);
            result.append(values[i]);
        }
        return result.toString();
	}

    /** {@inheritDoc} */
	@Override
	public final boolean equals(final Object obj)
	{
		if (! (obj instanceof StringValue))
			return false;
		final StringValue rhs = (StringValue) obj;
		// compare strings
		return Arrays.equals(values, rhs.getValues());
	}

    @Override
    public int hashCode()
    {
    	return Arrays.hashCode(values);
    }
}
