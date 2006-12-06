package org.csstudio.value;

import org.csstudio.platform.util.ITimestamp;

/** An int-typed value.
 *  @see Value
 */
public class IntegerValue extends Value
{
	private final int values[];
	
	public IntegerValue(ITimestamp time, Severity severity,
                     String status, MetaData meta_data,
                     int values[])
	{
		super(time, severity, status, meta_data);
		this.values = values;
	}

	/** @return Returns the whole array of values. */
	public int[] getValues()
	{	return values;	}

	/** @return Returns the first array element.
	 *  <p>
	 *  Since most samples are probably scalars, this is a convenient
	 *  way to get that one and only element.
	 *  @see #getValues
	 */ 
	public int getValue()
	{	return values[0];	}
	
	public String format()
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
	
	@Override
	public boolean equals(final Object obj)
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

	// Who overrides equals() shall also provide hashCode...
	@Override
	final public int hashCode()
	{
		int h = super.hashCode();
		for (int i=0; i<values.length; ++i)
			h += values[i];
		return h;
	}
}
