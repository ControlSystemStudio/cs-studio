/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values.internal;

import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.Messages;

/** Implementation of the {@link IValue} interface.
 *  @author Kay Kasemir
 */
abstract public class Value implements IValue
{
    private static final long serialVersionUID = 1L;

    /** Time stamp of this value. */
	private final ITimestamp time;

    /** Severity code of this value. */
    private final ISeverity severity;

    /** Status text for this value's severity. */
	private final String status;

    /** Meta data (may be null). */
    private final IMetaData meta_data;

    /** The data quality. */
    private final Quality quality;

	/**
	 * The max count of values to be formatted into string.
	 * The value beyond this count will be omitted.
	 */
	public final static int MAX_FORMAT_VALUE_COUNT = 20;

    /** Construct a new value from pieces. */
    public Value(final ITimestamp time, final ISeverity severity,
                 final String status, final IMetaData meta_data,
                 final Quality quality)
    {
        this.time = time;
        this.severity = severity;
        this.status = status;
        this.meta_data = meta_data;
        this.quality = quality;
    }

    /** {@inheritDoc} */
    @Override
    final public ITimestamp getTime()
    {   return time;   }

    /** {@inheritDoc} */
    @Override
    final public ISeverity getSeverity()
	{	return severity;	}

    /** {@inheritDoc} */
    @Override
    final public String getStatus()
	{	return status; 	 }

    /** {@inheritDoc} */
    @Override
    final public Quality getQuality()
    {   return quality; }

    /** {@inheritDoc} */
    @Override
    public IMetaData getMetaData()
    {   return meta_data;    }

    /** {@inheritDoc} */
    @Override
    abstract public String format(Format how, int precision);

    /** {@inheritDoc} */
    @Override
    final public String format()
    {   return format(Format.Default, -1); }

    /** {@inheritDoc} */
    @Override
    final public String toString()
    {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(getTime().toString());
        buffer.append(Messages.ColumnSeperator);
        buffer.append(format(Format.Default, -1));
        final String sevr = getSeverity().toString();
        final String stat = getStatus();
        if (sevr.length() > 0 || stat.length() > 0)
        {
            buffer.append(Messages.ColumnSeperator);
            buffer.append(sevr);
            buffer.append(Messages.SevrStatSeparator);
            buffer.append(stat);
        }
        return buffer.toString();
    }

	/** Convert char into printable character for Format.String
	 *  @param c Char, 0 for end-of-string
	 *  @return Printable version
	 */
	protected char getDisplayChar(final char c)
	{
		if (c == 0) {
            return 0;
        }
		if (Character.getType(c) != Character.CONTROL) {
            return c;
        }
		return '?';
	}

    /** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj)
	{
		if (! (obj instanceof Value)) {
            return false;
        }
		final Value rhs = (Value) obj;
		if (! (rhs.time.equals(time) &&
		       rhs.quality == quality &&
			   rhs.status.equals(status) &&
			   rhs.severity.toString().equals(severity.toString()))) {
            return false;
        }
        // Meta_data might be null
        final IMetaData rhs_meta = rhs.getMetaData();
		if (meta_data == null)
		{   // OK if both are null
		    return rhs_meta == null;
		}
		return rhs.meta_data.equals(meta_data);
	}

    /** {@inheritDoc} */
	@Override
	public int hashCode()
	{
		return time.hashCode() + status.hashCode() + severity.hashCode();
	}
}
