package org.csstudio.trends.databrowser.export;

import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.IValue.Format;
import org.csstudio.trends.databrowser.Messages;

/** Format an IValue to show the value as well as the severity/status
 *  @author Kay Kasemir
 */
public class ValueWithInfoFormatter extends ValueFormatter
{
    /** Initialize
     *  @param format Number format to use
     *  @param precision Precision
     */
    public ValueWithInfoFormatter(final Format format, final int precision)
    {
        super(format, precision);
    }

    /** {@inheritDoc} */
    @Override
    public String getHeader()
    {
        return Messages.ValueColumn + Messages.Export_Delimiter + Messages.SeverityColumn +
            Messages.Export_Delimiter + Messages.StatusColumn;
    }

    /** {@inheritDoc} */
    @Override
    public String format(final IValue value)
    {
        if (value == null)
            return Messages.Export_NoValueMarker +
                Messages.Export_Delimiter + Messages.Export_NoValueMarker +
                Messages.Export_Delimiter + Messages.Export_NoValueMarker;
        return super.format(value) + Messages.Export_Delimiter +
            value.getSeverity() + Messages.Export_Delimiter + value.getStatus();
    }
}
