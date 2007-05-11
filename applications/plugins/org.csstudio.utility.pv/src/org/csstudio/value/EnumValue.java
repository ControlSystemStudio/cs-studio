package org.csstudio.value;

import org.csstudio.platform.data.ITimestamp;

/** An enum-typed value.
 *  @see Value
 *  @author Kay Kasemir
 */
public class EnumValue extends Value
{
	private final int values[];
	
	public EnumValue(ITimestamp time, Severity severity, String status,
                      MetaData meta_data, int values[])
	{
		super(time, severity, status, meta_data);
        assert meta_data instanceof EnumeratedMetaData;
		this.values = values;
	}

	/** @return Returns the whole array of values. */
	public final int[] getValues()
	{	return values;	}

	/** @return Returns the first array element.
	 *  <p>
	 *  Since most values are probably scalars, this is a convenient
	 *  way to get that one and only element.
	 *  @see #getValues
	 */ 
	public int getValue()
	{	return values[0];  }
	
	public String format(Format how, int precision)
	{
		EnumeratedMetaData enum_meta = (EnumeratedMetaData)getMetaData();
		StringBuffer buf = new StringBuffer();
		if (getSeverity().hasValue())
		{
			buf.append(enum_meta.getState(values[0]));
			for (int i = 1; i < values.length; i++)
			{
				buf.append(Messages.ArrayElementSeparator);
				buf.append(enum_meta.getState(values[i]));
			}
		}
		else
			buf.append(Messages.NoValue);
		return buf.toString();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (! (obj instanceof EnumValue))
			return false;
		EnumValue rhs = (EnumValue) obj;
		if (rhs.values.length != values.length)
			return false;
		for (int i=0; i<values.length; ++i)
			if (rhs.values[i] != values[i])
				return false;
		return super.equals(obj);
	}

	// Who overrides equals() shall also provide hashCode...
	@Override
	public int hashCode()
	{
		int h = super.hashCode();
		for (int i=0; i<values.length; ++i)
			h += values[i];
		return h;
	}
}
