package org.csstudio.utility.pv.simu;

import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.data.ITimestamp.Format;
import org.csstudio.platform.data.IValue.Quality;

/** Dynamic value that holds current time as string
 *  @author Kay Kasemir
 */
public class TimeValue extends DynamicValue
{
    /** Initialize
     *  @param name
     */
    public TimeValue(final String name)
    {
        super(name);
    }

    /** {@inheritDoc} */
    @Override
    protected void update()
    {
        final ITimestamp now = TimestampFactory.now();
        final String text = now.format(Format.DateTimeSeconds);
        final ISeverity severity = ValueFactory.createOKSeverity();
        setValue(ValueFactory.createStringValue(now , severity, severity.toString(), Quality.Original, new String[] { text }));
    }
}
