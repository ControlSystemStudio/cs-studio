package org.csstudio.platform.internal.data;

import org.csstudio.platform.data.ILongValue;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.ISeverity;

/** Implementation of {@link ILongValue}.
 *  @see ILongValue
 *  @author Kay Kasemir
 */
public class LongValue extends Value implements ILongValue
{
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
	public final long[] getValues()
	{	return values;	}

    /** {@inheritDoc} */
    public final long getValue()
	{	return values[0];	}
	
    /** {@inheritDoc} */
    @Override
    public final String format(final Format how, int precision)
	{
	    StringBuffer buf = new StringBuffer();
		if (getSeverity().hasValue())
		{
			buf.append(values[0]);
			for (int i = 1; i < values.length; i++)
			{
				buf.append(Messages.ArrayElementSeparator);
				buf.append(values[i]);
			}
		}
		else
			buf.append(Messages.NoValue);
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
