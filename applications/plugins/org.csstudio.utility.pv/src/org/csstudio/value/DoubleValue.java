package org.csstudio.value;

import java.text.NumberFormat;

import org.csstudio.platform.util.ITimestamp;

/** A double-typed value.
 *  @see Value
 *  @author Kay Kasemir
 */
public class DoubleValue extends Value
{
	final private double values[];
	
	public DoubleValue(final ITimestamp time, final Severity severity,
                    final String status, final MetaData meta_data,
                    final double values[])
	{
		super(time, severity, status, meta_data);
        assert meta_data instanceof NumericMetaData;
		this.values = values;
	}

	/** @return Returns the whole array of values. */
	public double[] getValues()
	{	return values;	}

	/** @return Returns the first array element.
	 *  <p>
	 *  Since most values are probably scalars, this is a convenient
	 *  way to get that one and only element.
	 *  @see #getValues
	 */ 
	public double getValue()
	{	return values[0];	}
	
	public String format()
	{
	    NumericMetaData num_meta = (NumericMetaData)getMetaData();
		StringBuffer buf = new StringBuffer();
		if (getSeverity().hasValue())
		{
            NumberFormat fmt = NumberFormat.getNumberInstance();
            fmt.setMinimumFractionDigits(num_meta.getPrecision());
            fmt.setMaximumFractionDigits(num_meta.getPrecision());
			buf.append(fmt.format(values[0]));
			for (int i = 1; i < values.length; i++)
			{
				buf.append(Messages.ArrayElementSeparator);
				buf.append(fmt.format(values[i]));
			}
		}
		else
			buf.append(Messages.NoValue);
		return buf.toString();
	}
	
	@Override
	public boolean equals(final Object obj)
	{
		if (! (obj instanceof DoubleValue))
			return false;
		final DoubleValue rhs = (DoubleValue) obj;
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
