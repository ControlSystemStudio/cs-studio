package org.csstudio.value;

import org.csstudio.platform.util.ITimestamp;

/** A string-typed value.
 *  @see Value
 *  @author Kay Kasemir
 */
public class StringValue extends Value
{
    // Slight inconsistency, because that's the way EPICS works right now:
    // There is no array of Strings as there would be arrays of
    // the other types, so we only handle a scalar string as well....
	final private String value;
	
	public StringValue(ITimestamp time, Severity severity, String status,
                        String value)
    {   // String has no meta data!
		super(time, severity, status, null);
		this.value = value;
	}

	/** @return Returns the String. */ 
	public String getValue()
	{
		return value;
	}
	
	public String format()
	{
		if (getSeverity().hasValue())
            return Messages.StringDelimiter + value + Messages.StringDelimiter;
		else
			return Messages.NoValue;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (! (obj instanceof StringValue))
			return false;
		StringValue rhs = (StringValue) obj;
		return rhs.value.equals(value) && super.equals(obj);
	}

	// Who overrides equals() shall also provide hashCode...
	@Override
	public int hashCode()
	{
		return value.hashCode() + super.hashCode();
	}
}
