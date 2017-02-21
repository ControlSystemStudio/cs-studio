/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.simplepv.vtypepv;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.csstudio.simplepv.ExceptionHandler;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVListener;
import org.csstudio.vtype.pv.PVPool;
import org.diirt.vtype.VByteArray;
import org.diirt.vtype.VType;

/** Opibuilder {@link IPV} based on vtype {@link PV}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class VTypePV implements IPV
{
    final private String name;
    final private Executor notificationThread;
    final private List<IPVListener> listeners = new CopyOnWriteArrayList<>();
    private volatile Optional<PV> pv = Optional.empty();
    /** 'Connected' means 'have received a value'.
     *  Lock on <code>connected</this> when waiting for connection.
     */
    final private AtomicBoolean connected = new AtomicBoolean(false);
    private volatile boolean is_long_string = false;

    /** vtype.PV listener, forwards events to the IPV listener
     *  on requested thread
     */
    final private PVListener listener = new PVListener()
    {
        @Override
        public void valueChanged(final PV pv, final VType value)
        {
            final boolean first_value = connected.compareAndSet(false, true);
            if (first_value)
                synchronized (connected)
                {
                    connected.notifyAll();
                }
            for (IPVListener l : listeners)
                notificationThread.execute(() ->
                {
                    if (first_value)
                    {
                        l.connectionChanged(VTypePV.this);
                        l.writePermissionChanged(VTypePV.this);
                    }
                    l.valueChanged(VTypePV.this);
                });
        }

        @Override
        public void permissionsChanged(final PV pv, final boolean readonly)
        {
            for (IPVListener l : listeners)
                notificationThread.execute(() -> l.writePermissionChanged(VTypePV.this));
        }

        @Override
        public void disconnected(final PV pv)
        {
            connected.set(false);
            for (IPVListener l : listeners)
                notificationThread.execute(() -> l.connectionChanged(VTypePV.this));
        }
    };

    /** @param name PV Name
     *  @param readOnly opibuilder always passes false, so this is ignored
     *  @param notificationThread Thread on which to call {@link IPVListener}
     *  @param exceptionHandler Not used
     *  @throws Exception
     */
    VTypePV(final String name, final boolean readOnly,
            final Executor notificationThread,
            final ExceptionHandler exceptionHandler) throws Exception
    {
        this.name = parseName(name);
        this.notificationThread = notificationThread;
    }

    /** Check name for special cases used by the PVManager
     *  @param name Original name
     *  @return Potentially adjusted name
     *  @throws Exception on error in name
     */
    private String parseName(final String name) throws Exception
    {
        // Byte array to be treated as long string?
        final int ls = name.indexOf(" {\"longString\":true}");
        if (ls > 0)
        {
            is_long_string = true;
            return name.substring(0,  ls);
        }

        // Convert constant expressions into constant local PVs
        if (name.startsWith("="))
        {
            final String constant = name.substring(1);
            // String constant?
            if (constant.startsWith("\"")  &&  constant.endsWith("\""))
                return "loc://const(" + constant + ")";
            try
            {   // Numeric constant?
                Double.parseDouble(constant);
                return "loc://const(" + constant + ")";
            }
            catch (NumberFormatException ex)
            {
                throw new Exception("Can only handle constant formulas '=123' or '=\"text\"', not '" + name + "'");
            }
        }
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public void addListener(final IPVListener listener)
    {
        listeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void removeListener(final IPVListener listener)
    {
        listeners.remove(listener);
    }

    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public void start() throws Exception
    {
        if (isStarted())
            throw new Exception("PV " + name + " already started");
        final PV the_pv = PVPool.getPV(name);
        pv = Optional.of(the_pv);
        the_pv.addListener(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void stop()
    {
        final PV safe_pv = pv.orElse(null);
        pv = Optional.empty();
        if (safe_pv != null)
        {
            safe_pv.removeListener(listener);
            PVPool.releasePV(safe_pv);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isStarted()
    {
        return pv.isPresent();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isConnected()
    {
        return connected.get();
    }

    /** @param millisecs How long to wait for connection in millisecs
     *  @return <code>true</code> if connected, <code>false</code> on timeout
     */
    private boolean isConnected(final long millisecs)
    {
        final long end = System.currentTimeMillis() + millisecs;
        while (! isConnected())
        {
            final long ms_left = end - System.currentTimeMillis();
            if (ms_left <= 0)
                return false;
            synchronized (connected)
            {
                try
                {
                    connected.wait(ms_left);
                }
                catch (Exception ex)
                {
                    return false;
                }
            }
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isWriteAllowed()
    {
        final PV safe_pv = pv.orElse(null);
        return safe_pv != null  &&  !safe_pv.isReadonly();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isBufferingValues()
    {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setPaused(boolean paused)
    {
        // Not implemented because opibuilder never calls it
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPaused()
    {
        // Not implemented because opibuilder never pauses.
        // Opibuilder will check isPaused in ShowPVInfoAction
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(final Object value) throws Exception
    {
        final PV safe_pv = pv.orElse(null);
        if (safe_pv == null)
            throw new Exception("Cannot write to " + name + ", not started");
        safe_pv.write(value);
    }

    /** {@inheritDoc} */
    @Override
    public boolean setValue(final Object value, final int timeout) throws Exception
    {
        final PV safe_pv = pv.orElse(null);
        if (safe_pv == null)
            throw new Exception("Cannot write to " + name + ", not started");

        // opibuilder will call this method for temporary PVs that
        // were just created, not waiting for connection.
        if (! isConnected(timeout))
            throw new Exception("Cannot write to " + name + ", not connected");

        safe_pv.write(value);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public List<VType> getAllBufferedValues()
    {
        return Arrays.asList(getValue());
    }

    /** {@inheritDoc} */
    @Override
    public VType getValue()
    {
        final PV safe_pv = pv.orElse(null);
        if (safe_pv == null)
            return null;
        final VType value = safe_pv.read();
        if (is_long_string  &&  value instanceof VByteArray)
            return ByteHelper.toString((VByteArray) value);
        return value;
    }
}
