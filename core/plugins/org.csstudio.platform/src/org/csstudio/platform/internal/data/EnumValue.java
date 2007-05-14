package org.csstudio.platform.internal.data;

import org.csstudio.platform.data.IEnumValue;
import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.ISeverity;

/** Implementation of {@link IEnumValue}.
 *  @see IEnumValue
 *  @author Kay Kasemir
 */
public class EnumValue extends Value implements IEnumValue
{
	private final int values[];
	
    /** Constructor from pieces. */
	public EnumValue(ITimestamp time, ISeverity severity, String status,
                    IEnumeratedMetaData meta_data, Quality quality,
                    int values[])
	{
		super(time, severity, status, meta_data, quality);
		this.values = values;
	}

    /** {@inheritDoc} */
	public final int[] getValues()
	{	return values;	}

    /** {@inheritDoc} */
	public final int getValue()
	{	return values[0];  }
	
    /** {@inheritDoc} */
	public final String format(Format how, int precision)
	{
		IEnumeratedMetaData enum_meta = (IEnumeratedMetaData)getMetaData();
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
	
    /** {@inheritDoc} */
	@Override
	public final boolean equals(Object obj)
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
