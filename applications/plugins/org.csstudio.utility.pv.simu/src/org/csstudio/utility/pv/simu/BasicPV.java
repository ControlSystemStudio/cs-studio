package org.csstudio.utility.pv.simu;

import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.platform.data.IValue;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.runtime.PlatformObject;

/** Base class for Local and Simulated PV:
 *  Has value and listeners.
 *  Value changes are forwarded to listeners
 *  
 *  @param <T> Value type
 *  
 *  @author Kay Kasemir
 */
abstract public class BasicPV<T extends Value> extends PlatformObject implements PV, ValueListener
{
    /** PV type prefix */
    final String prefix;
    
    /** Most recent value */
    final protected T value;
    
    /** PVListeners of this PV */
    private final CopyOnWriteArrayList<PVListener> listeners =
        new CopyOnWriteArrayList<PVListener>();

    /** Started ? */
    protected boolean running = false;

    /** Initialize
     *  @param name PV name
     */
    public BasicPV(final String prefix, final T value)
    {
        this.prefix = prefix;
        this.value = value;
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return prefix + "://" + value.getName(); //$NON-NLS-1$
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
    
    /** Inform all registered listeners about value update
     *  @see ValueListener#changed(Value)
     */
    public void changed(Value value)
    {
        if (! running)
            return;
        for (PVListener listener : listeners)
            listener.pvValueUpdate(this);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
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

    /** {@inheritDoc} */
    public boolean isWriteAllowed()
    {
        return false;
    }

    /** {@inheritDoc} */
    public void setValue(Object newValue) throws Exception
    {
        // NOP
    }
}
