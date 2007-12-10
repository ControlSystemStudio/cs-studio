package org.csstudio.platform.internal.data;

import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.IEnumeratedValue;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.eclipse.osgi.util.NLS;

/** Implementation of {@link IEnumeratedValue}.
 *  @see IEnumeratedValue
 *  @author Kay Kasemir
 */
public class EnumeratedValue extends Value implements IEnumeratedValue
{
    final private int values[];
	
    /** Constructor from pieces. */
	public EnumeratedValue(ITimestamp time, ISeverity severity, String status,
                    IEnumeratedMetaData meta_data, Quality quality,
                    int values[])
	{
		super(time, severity, status, meta_data, quality);
		this.values = values;
	}

    /** {@inheritDoc} */
	final public int[] getValues()
	{	return values;	}

    /** {@inheritDoc} */
	final public int getValue()
	{	return values[0];  }
	
    /** {@inheritDoc} */
	@Override
	final public String format(final Format how, final int precision)
	{
	    final IEnumeratedMetaData enum_meta = (IEnumeratedMetaData)getMetaData();
	    final StringBuffer buf = new StringBuffer();
		if (getSeverity().hasValue())
		{
			buf.append(NLS.bind(Messages.EnumStateNumberFormat,
			        enum_meta.getState(values[0]), values[0]));
			for (int i = 1; i < values.length; i++)
			{
				buf.append(Messages.ArrayElementSeparator);
	            buf.append(NLS.bind(Messages.EnumStateNumberFormat,
	                    enum_meta.getState(values[i]), values[i]));
			}
		}
		else
			buf.append(Messages.NoValue);
		return buf.toString();
	}
	
    /** {@inheritDoc} */
	@Override
	final public boolean equals(final Object obj)
	{
		if (! (obj instanceof EnumeratedValue))
			return false;
		final EnumeratedValue rhs = (EnumeratedValue) obj;
		if (rhs.values.length != values.length)
			return false;
		for (int i=0; i<values.length; ++i)
			if (rhs.values[i] != values[i])
				return false;
		return super.equals(obj);
	}

    /** {@inheritDoc} */
	@Override
	final public int hashCode()
	{
		int h = super.hashCode();
		for (int i=0; i<values.length; ++i)
			h += values[i];
		return h;
	}
}
