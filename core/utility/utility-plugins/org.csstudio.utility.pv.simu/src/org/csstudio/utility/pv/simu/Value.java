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

/** A value that might change and have multiple listeners
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Value
{
    final private String name;
    private IValue value = null;
    private final CopyOnWriteArrayList<ValueListener> listeners = new CopyOnWriteArrayList<ValueListener>();

    /** Initialize
     * @param name
     */
    public Value(final String name)
    {
        this.name = name;
    }

    /** @return Name */
    public String getName()
    {
        return name;
    }

    /** Add listener */
    public void addListener(final ValueListener listener)
    {
        listeners.add(listener);
        // If we already have a value, inform new listener right away
        if (value != null)
            listener.changed(this);
    }

    /** Remove listener */
    public void removeListener(final ValueListener listener)
    {
        listeners.remove(listener);
    }

    /** Inform all registered listeners about value update */
    protected void fireValueUpdate()
    {
        for (ValueListener listener : listeners)
            listener.changed(this);
    }

    /** @return Most recent value */
    public synchronized IValue getValue()
    {
        return value;
    }

    /** Set a new value.
     *  Will trigger updates to all registered listeners
     *  @param value New value
     */
    public void setValue(final IValue value)
    {
        synchronized (this)
        {
            this.value = value;
        }
        fireValueUpdate();
    }

    @Override
    public String toString()
    {
        return name + " = " + value;
    }
}
