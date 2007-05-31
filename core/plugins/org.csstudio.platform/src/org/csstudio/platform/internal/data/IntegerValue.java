package org.csstudio.platform.internal.data;

import org.csstudio.platform.data.IIntegerValue;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.ISeverity;

/** Implementation of {@link IIntegerValue}.
 *  @see IIntegerValue
 *  @author Kay Kasemir
 */
public class IntegerValue extends Value implements IIntegerValue
{
	private final int values[];
	
    /** Constructor from pieces. */
	public IntegerValue(final ITimestamp time, final ISeverity severity,
                    final String status, final INumericMetaData meta_data,
                    final Quality quality, final int values[])
	{
		super(time, severity, status, meta_data, quality);
		this.values = values;
	}

    /** {@inheritDoc} */
	public final int[] getValues()
	{	return values;	}

    /** {@inheritDoc} */
    public final int getValue()
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
		if (! (obj instanceof IntegerValue))
			return false;
		final IntegerValue rhs = (IntegerValue) obj;
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
