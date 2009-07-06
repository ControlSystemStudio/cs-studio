package org.csstudio.utility.pv.simu;

import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.platform.data.IValue;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.runtime.PlatformObject;

/** Base class for local as well as simulated PVs.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public abstract class BasicPV extends PlatformObject implements PV
{
    /** PV Name */
    protected final String name;

    /** PVListeners of this PV */
    private final CopyOnWriteArrayList<PVListener> listeners = new CopyOnWriteArrayList<PVListener>();
    
    /** Most recent value */
    protected IValue value = null;
    
    protected boolean running = false;

    /** Initialize
     *  @param name PV name
     */
    public BasicPV(final String name)
    {
        this.name = name;
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return name;
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
    protected void fireValueUpdate()
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
        return value;
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
}