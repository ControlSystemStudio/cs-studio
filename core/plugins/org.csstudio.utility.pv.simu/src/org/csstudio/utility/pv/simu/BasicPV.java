/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.simu;

import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.data.values.IValue;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

/** Base class for Local and Simulated PV:
 *  Has value and listeners.
 *  Value changes are forwarded to listeners
 *
 *  @param <T> Value type
 *
 *  @author Kay Kasemir
 */
abstract public class BasicPV<T extends Value> implements PV, ValueListener
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
    @Override
    public String getName()
    {
        return prefix + PVFactory.SEPARATOR + value.getName();
    }

    /** {@inheritDoc} */
    @Override
    public void addListener(final PVListener listener)
    {
        listeners.add(listener);
    	if(running && isConnected()){
    		listener.pvValueUpdate(this);
    	}
    }

    /** {@inheritDoc} */
    @Override
    public void removeListener(final PVListener listener)
    {
        listeners.remove(listener);
    }

    /** Called by this PV's value when it changes.
     *  Informs all registered listeners about value update.
     *
     *  @see ValueListener#changed(Value)
     */
    @Override
    public void changed(Value value)
    {
        if (! running)
            return;
        for (PVListener listener : listeners)
            listener.pvValueUpdate(this);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("nls")
    public String getStateInfo()
    {
        return running ? "running" : "stopped";
    }

    /** {@inheritDoc} */
    @Override
    public IValue getValue()
    {
        return value.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public IValue getValue(double timeoutSeconds) throws Exception
    {
        return getValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isConnected()
    {
        return running && getValue() != null;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isRunning()
    {
        return running;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isWriteAllowed()
    {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(Object newValue) throws Exception
    {
        // NOP
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
