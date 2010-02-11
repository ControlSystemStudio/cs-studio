package org.csstudio.trends.databrowser.export;

import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.IValue.Format;
import org.csstudio.trends.databrowser.Messages;

/** Format an IValue as default, decimal, ...
 *  @author Kay Kasemir
 */
public class ValueFormatter
{
    final private Format format;
    final private int precision;

    /** Initialize
     *  @param format Number format to use
     *  @param precision Precision
     */
    public ValueFormatter(final Format format, final int precision)
    {
        this.format = format;
        this.precision = precision;
    }

    /** @return Text for column headers */
    public String getHeader()
    {
        return Messages.ValueColumn;
    }

    /** @return Value formatted into columns */
    public String format(final IValue value)
    {
        if (value == null)
            return Messages.Export_NoValueMarker;
        return value.format(format, precision);
    }

    @Override
    public String toString()
    {
        switch (format)
        {
        case Default:
            return Messages.Format_Default;
        case Decimal:
            return nameWithPrecision(Messages.Format_Decimal);
        case Exponential:
            return nameWithPrecision(Messages.Format_Exponential);
        }
        return format.name();
    }

    /** @return name of format with info on 'digits' */
    @SuppressWarnings("nls")
    private String nameWithPrecision(final String name)
    {
        return name + " (" + precision + " digits)";
    }
}
