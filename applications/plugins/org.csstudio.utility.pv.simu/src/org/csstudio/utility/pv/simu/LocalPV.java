package org.csstudio.utility.pv.simu;

import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.data.IValue.Quality;

/** Local PV.
 *  <p>
 *  Provides updates based on changes of the underlying Value,
 *  which can be set by writing to this PV.
 *  Can hold numeric (double) or String value.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LocalPV extends BasicPV<Value>
{
    /** Initialize
     *  @param value PV name
     */
    public LocalPV(final Value value)
    {
        super(value);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isWriteAllowed()
    {
        return true;
    }

    /** Meta data */
    final private INumericMetaData meta = ValueFactory.createNumericMetaData(0, 0, 0, 0, 0, 0, 3, "a.u.");

    /** {@inheritDoc} */
    @Override
    public void setValue(Object newValue) throws Exception
    {
        final ITimestamp time = TimestampFactory.now();
        final ISeverity severity = ValueFactory.createOKSeverity();
        double number;
        try
        {
            number = Double.parseDouble(newValue.toString());
        }
        catch (Throwable ex)
        {
            value.setValue(ValueFactory.createStringValue(time, severity, severity.toString(),  Quality.Original,
                    new String[] { newValue.toString() }));
            return;
        }
        // else: Value parses into number, so set numeric value
        value.setValue(ValueFactory.createDoubleValue(time, severity, severity.toString(), meta, Quality.Original, new double[] { number }));
    }

    /** {@inheritDoc} */
    public synchronized void start() throws Exception
    {
        running = true;
        value.addListener(this);
    }

    /** {@inheritDoc} */
    public void stop()
    {
        value.removeListener(this);
        running = false;
    }
}
