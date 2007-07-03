package org.csstudio.platform.internal.data;

import org.csstudio.platform.data.IMinMaxDoubleValue;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.eclipse.osgi.util.NLS;

/** Implementation of {@link IMinMaxDoubleValue}.
 *  @author Kay Kasemir
 */
public class MinMaxDoubleValue extends DoubleValue implements IMinMaxDoubleValue
{
    /** The minimum resp. maximum. */
	final private double minimum, maximum;
	
    /** Constructor from pieces. */
	@SuppressWarnings("nls")
    public MinMaxDoubleValue(final ITimestamp time, final ISeverity severity,
                       final String status, final INumericMetaData meta_data,
                       final Quality quality,
                       final double values[],
                       final double minimum,
                       final double maximum)
	{
		super(time, severity, status, meta_data, quality, values);
        if (minimum > maximum)
            throw new IllegalArgumentException("Minimum " + minimum +
                                               " > Maximum " + maximum);
		this.minimum = minimum;
        this.maximum = maximum;
	}

    /** {@inheritDoc} */
    final public double getMinimum()
    {
        return minimum;
    }

    /** {@inheritDoc} */
    final public double getMaximum()
    {
        return maximum;
    }

    /** {@inheritDoc} */
	@Override
    final public String format(Format how, int precision)
	{
		if (getSeverity().hasValue())
            return super.format(how, precision)
                   + NLS.bind(Messages.MiniMaxiFormat, minimum, maximum);
		// else
        return Messages.NoValue;
	}
	
    /** {@inheritDoc} */
	@Override
	final public boolean equals(final Object obj)
	{
		if (! (obj instanceof MinMaxDoubleValue))
			return false;
		final MinMaxDoubleValue rhs = (MinMaxDoubleValue) obj;
        return minimum == rhs.minimum &&
               maximum == rhs.maximum &&
               super.equals(obj);
	}

    /** {@inheritDoc} */
	@Override
	final public int hashCode()
	{
        return super.hashCode() + (int) (minimum + maximum);
	}
}
