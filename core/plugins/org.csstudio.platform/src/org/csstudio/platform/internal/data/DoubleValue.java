package org.csstudio.platform.internal.data;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.ISeverity;

/** Implementation of {@link IDoubleValue}.
 *  @author Kay Kasemir
 */
public class DoubleValue extends Value implements IDoubleValue
{
    /** The values. */
	final private double values[];
	
    /** Constructor from pieces. */
	public DoubleValue(final ITimestamp time, final ISeverity severity,
                       final String status, final INumericMetaData meta_data,
                       final Quality quality,
                       final double values[])
	{
		super(time, severity, status, meta_data, quality);
		this.values = values;
	}

    /** {@inheritDoc} */
	final public double[] getValues()
	{	return values;	}

    /** {@inheritDoc} */
	final public double getValue()
	{	return values[0];	}
	
    /** {@inheritDoc} */
	@Override
    public String format(Format how, int precision)
	{
		StringBuffer buf = new StringBuffer();
		if (getSeverity().hasValue())
		{
            NumberFormat fmt;
            if (how == Format.Exponential)
            {   // Is there a better way to get this silly format?
                StringBuffer pattern = new StringBuffer(10);
                pattern.append("0."); //$NON-NLS-1$
                for (int i=0; i<precision; ++i)
                    pattern.append('0');
                pattern.append("E0"); //$NON-NLS-1$
                fmt = new DecimalFormat(pattern.toString());
            }
            else
            {
                fmt = NumberFormat.getNumberInstance();
                if (how == Format.Default)
                {
                    INumericMetaData num_meta = (INumericMetaData)getMetaData();
                    precision = num_meta.getPrecision();
                }
                fmt.setMinimumFractionDigits(precision);
                fmt.setMaximumFractionDigits(precision);
            }
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
	
    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
	@Override
	public int hashCode()
	{
		int h = super.hashCode();
		for (int i=0; i<values.length; ++i)
			h += values[i];
		return h;
	}
}
