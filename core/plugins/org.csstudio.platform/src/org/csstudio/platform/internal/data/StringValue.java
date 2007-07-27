package org.csstudio.platform.internal.data;

import org.csstudio.platform.data.IStringValue;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.ISeverity;

/** Implementation of {@link IStringValue}.
 *  @see IStringValue
 *  @author Kay Kasemir
 */
public class StringValue extends Value implements IStringValue
{
    // Slight inconsistency, because that's the way EPICS works right now:
    // There is no array of Strings as there would be arrays of
    // the other types, so we only handle a scalar string as well....
	private final String values[];
	
	public StringValue(ITimestamp time, ISeverity severity, String status,
                       Quality quality,
                       String values[])
    {   // String has no meta data!
		super(time, severity, status, null, quality);
		this.values = values;
		if (values == null  ||  values.length < 1)
		    throw new java.lang.IllegalArgumentException("Values"); //$NON-NLS-1$
	}

    /** {@inheritDoc} */
    public final String[] getValues()
    {
        return values;
    }

    /** {@inheritDoc} */
	public final String getValue()
	{
		return values[0];
	}
	
    /** {@inheritDoc} */
	@Override
    public final String format(Format how, int precision)
	{
		if (getSeverity().hasValue() == false)
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
	public final boolean equals(Object obj)
	{
		if (! (obj instanceof StringValue))
			return false;
		StringValue rhs = (StringValue) obj;
		return rhs.values.equals(values) && super.equals(obj);
	}

    /** {@inheritDoc} */
	@Override
	public final int hashCode()
	{
		return values.hashCode() + super.hashCode();
	}
}
