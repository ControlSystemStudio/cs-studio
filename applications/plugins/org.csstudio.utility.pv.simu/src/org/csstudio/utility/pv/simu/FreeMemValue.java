package org.csstudio.utility.pv.simu;

import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.data.IValue.Quality;

/** Dynamic value that holds free memory (MB)
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FreeMemValue extends DynamicValue
{
    private static final double MB = 1024.0 * 1024.0;
    private final INumericMetaData meta;

    /** Initialize
     *  @param name
     */
    public FreeMemValue(final String name)
    {
        super(name);
        final double max = Runtime.getRuntime().maxMemory() / MB;
        meta = ValueFactory.createNumericMetaData(0, max, 0, max, 0, max, 3, "MB");
    }

    /** {@inheritDoc} */
    @Override
    protected void update()
    {
        final double free = Runtime.getRuntime().freeMemory() / MB;
        final ISeverity severity = ValueFactory.createOKSeverity();
        setValue(ValueFactory.createDoubleValue(TimestampFactory.now(), severity, severity.toString(), meta, Quality.Original,
                new double[] { free }));
    }
}
