package org.csstudio.utility.pv.simu;

import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.platform.data.IValue;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.runtime.PlatformObject;

/** PV with a constant value.
 *  Contructed with a fixed value,
 *  sends that value on 'start'.
 *  Read-only, sends no further updates.
 *  @author Kay Kasemir, Xihui Chen
 */
public class ConstantPV extends PlatformObject implements PV
{
    /** Name of this PV including PREFIX */
    final private String name;

    /** Constant value */
    private IValue value;
    
    /** PVListeners of this PV */
    private final CopyOnWriteArrayList<PVListener> listeners =
        new CopyOnWriteArrayList<PVListener>();

    private boolean running = false;

    
    /** Initialize PV with constant value
     *  @param name Something like "const://name(initial_value)"
     */
    @SuppressWarnings("nls")
    public ConstantPV(final String name) throws Exception
    {
        // Get full name with PREFIX
        if (name.startsWith(ConstantPVFactory.PREFIX + PVFactory.SEPARATOR ))
            this.name = name;
        else
            this.name = ConstantPVFactory.PREFIX + 
                        PVFactory.SEPARATOR + name;
        
        // Parse value, locate the "..(value)"
        final int value_start = name.indexOf('(');
        if (value_start < 0)
            throw new Exception("PV " + this.name +" contains no initial value");
        final int value_end = name.indexOf(')', value_start + 1);
        if (value_end < 0)
            throw new Exception("Value in PV " + this.name +" not terminated by ')'");
        final String value_text = name.substring(value_start+1, value_end);
        value = TextUtil.parseValueFromString(value_text, null);

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
        // When running, send the one and only initial update
        if (running && isConnected())
            listener.pvValueUpdate(this);
    }

    /** {@inheritDoc} */
    public void removeListener(final PVListener listener)
    {
        listeners.remove(listener);
    }

    /** Starting the PV means each listener receives one
     *  value update, then no more.
     */
    public void start() throws Exception
    {
        running = true;
        for (PVListener listener : listeners)
            listener.pvValueUpdate(this);
    }

    /** {@inheritDoc} */
    public void stop()
    {
        running = false;
    }

    /** {@inheritDoc} */
    public String getStateInfo()
    {
        return "constant"; //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    public boolean isConnected()
    {
        return true;
    }

    /** {@inheritDoc} */
    public boolean isRunning()
    {
        return running;
    }

    /** {@inheritDoc} */
    public IValue getValue()
    {
        return value;
    }

    /** {@inheritDoc} */
    public IValue getValue(final double timeoutSeconds) throws Exception
    {
        return value;
    }

    /** {@inheritDoc} */
    public boolean isWriteAllowed()
    {
        return false;
    }

    /** {@inheritDoc} */
    public void setValue(final Object newValue) throws Exception
    {
        throw new Exception(getName() + " is read-only"); //$NON-NLS-1$
    }
}
