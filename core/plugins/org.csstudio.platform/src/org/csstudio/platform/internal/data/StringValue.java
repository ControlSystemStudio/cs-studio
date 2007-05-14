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
	private final String value;
	
	public StringValue(ITimestamp time, ISeverity severity, String status,
                       Quality quality,
                        String value)
    {   // String has no meta data!
		super(time, severity, status, null, quality);
		this.value = value;
	}

    /** {@inheritDoc} */
	public final String getValue()
	{
		return value;
	}
	
    /** {@inheritDoc} */
	public final String format(Format how, int precision)
	{
		if (getSeverity().hasValue())
            return value;
		else
			return Messages.NoValue;
	}
	
    /** {@inheritDoc} */
	@Override
	public final boolean equals(Object obj)
	{
		if (! (obj instanceof StringValue))
			return false;
		StringValue rhs = (StringValue) obj;
		return rhs.value.equals(value) && super.equals(obj);
	}

    /** {@inheritDoc} */
	@Override
	public final int hashCode()
	{
		return value.hashCode() + super.hashCode();
	}
}
