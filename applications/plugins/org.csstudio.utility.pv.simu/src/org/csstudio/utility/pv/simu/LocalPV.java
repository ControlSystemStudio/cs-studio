package org.csstudio.utility.pv.simu;

import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.data.IValue.Quality;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.runtime.PlatformObject;

/** Local PV.
 *  <p>
 *  Does not update on its own, only when set by user.
 *  Can hold numeric (double) or String value.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LocalPV extends PlatformObject implements PV, ValueListener
{
    /** PVListeners of this PV */
    private final CopyOnWriteArrayList<PVListener> listeners =
        new CopyOnWriteArrayList<PVListener>();
    
    /** Most recent value */
    final private DynamicValue value;
    
    protected boolean running = false;

    /** Initialize
     *  @param value PV name
     */
    public LocalPV(final DynamicValue value)
    {
        this.value = value;
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return value.getName();
    }

    /** {@inheritDoc} */
    public String getTypeId()
    {
        return IProcessVariable.TYPE_ID;
    }

    /** {@inheritDoc} */
    public void addListener(final PVListener listener)
    {
        listeners.add(listener);
    }

    /** {@inheritDoc} */
    public void removeListener(final PVListener listener)
    {
        listeners.remove(listener);
    }
    
    /** Inform all registered listeners about value update */
    public void changed(DynamicValue value)
    {
        for (PVListener listener : listeners)
            listener.pvValueUpdate(this);
    }

    /** {@inheritDoc} */
    public String getStateInfo()
    {
        return running ? "running" : "stopped";
    }

    /** {@inheritDoc} */
    public IValue getValue()
    {
        return value.getValue();
    }

    /** {@inheritDoc} */
    public IValue getValue(double timeoutSeconds) throws Exception
    {
        return getValue();
    }

    /** {@inheritDoc} */
    public boolean isConnected()
    {
        return running;
    }

    /** {@inheritDoc} */
    public synchronized boolean isRunning()
    {
        return running;
    }
    
    /** Meta data */
    final private INumericMetaData meta = ValueFactory.createNumericMetaData(0, 0, 0, 0, 0, 0, 3, "a.u.");

    /** {@inheritDoc} */
    public boolean isWriteAllowed()
    {
        return true;
    }

    /** {@inheritDoc} */
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
    public void start() throws Exception
    {
        synchronized (this)
        {
            running = true;
            value.addListener(this);
        }
    }

    /** {@inheritDoc} */
    public void stop()
    {
        value.removeListener(this);
        running = false;
    }
}
