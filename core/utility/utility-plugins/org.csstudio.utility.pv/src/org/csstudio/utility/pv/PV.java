/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv;

import org.csstudio.data.values.IValue;

/** A control system PV.
 *  <p>
 *  When 'start'ed, the PV will attempt to connect or
 *  do whatever is needed to obtain the meta information like
 *  units, precision, ... Then it will subscribe to updates
 *  of the current value.
 *  <p>
 *  While the {@link PVListener} might receive events on a
 *  non-UI thread, all the calls to the PV should come from
 *  the UI thread to prevent possible deadlocks.
 *  (The JNI CA client has deadlocked when both UI and non-UI
 *   threads called into it at the 'same' time).
 *
 *  @author Kay Kasemir
 */
public interface PV
{
    /** @return Returns the name. */
    public String getName();

    /** Synchronous 'get'.
     *  <p>
     *  Attempts to get a value within the given timeout.
     *  Either returns a value within the timeout,
     *  or throws an exception at the timeout.
     *  <p>
     *  When called for a new channel, i.e. on a channel where neither
     *  <code>get</code> nor <code>start</code> have been called,
     *  it will also perform the connection attempt, and leave the channel
     *  connected after getting a value.
     *  To force a disconnect, call <code>stop</code>.
     *
     *  @param timeout_seconds Timeout in seconds.
     *  @return Value
     *  @throws Exception on error
     */
    public IValue getValue(double timeout_seconds) throws Exception;

    /** Add a new listener.
     *  @see PVListener
     */
    public void addListener(PVListener listener);

    /** Remove a listener. */
    public void removeListener(PVListener listener);

    /** Start the PV: connect, get meta data, subscribe to updates,
     *  invoke {@link PVListener} for incoming values, ...
     *  @see #addListener(PVListener)
     *  @see #stop()
     */
    public void start() throws Exception;

    /** @return Returns <code>true</code> between <code>start()</code> and <code>stop()</code>. */
    public boolean isRunning();

    /** @return Returns <code>true</code> when connected.
     *  While <code>isRunning</code>, we are subscribed for value updates,
     *  but we might still be disconnected, at least temporarily.
     */
    public boolean isConnected();

    /** @return <code>true</code> if we have write access to the PV */
    public boolean isWriteAllowed();

    /** Internal state information on the PV.
     *  <p>
     *  Especially when <code>isConnected()</code> is <code>false</code>,
     *  this information might help to diagnose the problem:
     *  Did the PV never connect?
     *  Was it once connected, but some error occurred?
     *  @return Some human readable state info */
    public String getStateInfo();

    /** Stop the PV: disconnect, ...
     *  When the PV is no longer needed, one should 'stop' it
     *  to release resources.
     */
    public void stop();

    /** Get the value.
     *  <p>
     *  This is the most recent value.
     *  Check isConnected() to see if this is valid,
     *  or use inside a PV listener's value update.
     *
     *  @see PVListener
     *  @see #isConnected()
     *  @return Returns the most recent value,
     *          or <code>null</code> if there is none.
     */
    public IValue getValue();

    /** Set PV to given value.
     *  Should accept <code>Double</code>, <code>Double[]</code>,
     *  <code>Integer</code>,
     *  <code>String</code>, maybe more.
     *  @param new_value Value to write to PV
     *  @throws Exception on error
     */
    public void setValue(Object new_value) throws Exception;
}
