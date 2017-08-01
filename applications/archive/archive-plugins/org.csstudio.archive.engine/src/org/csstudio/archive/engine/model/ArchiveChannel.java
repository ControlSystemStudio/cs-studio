/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.archive.engine.Activator;
import org.csstudio.archive.engine.ThrottledLogger;
import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVListenerAdapter;
import org.csstudio.vtype.pv.PVPool;
import org.diirt.vtype.Time;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VType;

/** Base for archived channels.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class ArchiveChannel extends PVListenerAdapter
{
    /** Throttled log for NaN samples */
    private static ThrottledLogger trouble_sample_log =
                    new ThrottledLogger(Level.INFO, "log_trouble_samples"); //$NON-NLS-1$

    /** Group to which this channel belongs.
     *  <p>
     *  Using thread safe array so that HTTPD can access
     *  as well as main thread and PV
     */
    final private CopyOnWriteArrayList<ArchiveGroup> groups =
                                new CopyOnWriteArrayList<ArchiveGroup>();

    /** Channel name.
     *  This is the name by which the channel was created,
     *  not the PV name that might include decorations.
     */
    final private String name;

    /** Control system PV
     *  SYNC on access
     */
    private PV pv = null;

    /** Is this channel currently running?
     *  <p>
     *  PV sends another 'disconnected' event
     *  as the result of 'stop', but we don't
     *  want to log that, so we keep track of
     *  the 'running' state.
     */
    private boolean is_running = false;

    /** Do we need to log a 'write error' sample?
     *  <p>
     *  The sample buffer will indicate write errors.
     *  While in error, we keep adding samples, which
     *  will probably cause overrides.
     *  When we can write again, we add one info sample.
     */
    private boolean need_write_error_sample = false;

    /** Do we need to log a 'first' sample?
     *  <p>
     *  After startup, or after a network disconnect,
     *  the first sample we receive
     *  might be time-stamped days ago,
     *  while the archive has an 'off' or 'disconnected' info sample
     *  that's already newer.
     *  This flag is used to force one initial
     *  sample into the archive with current time stamp.
     */
    private boolean need_first_sample = true;

    /** How channel affects its groups */
    final private Enablement enablement;

    /** Is this channel currently enabled? */
    private boolean enabled = true;

    /** Most recent value of the PV.
     *  <p>
     *  This is the value received from the PV,
     *  is is not necessarily written to the archive.
     *  <p>
     *  SYNC:Lock on <code>this</code> for access.
     */
    protected VType most_recent_value = null;

    /** Counter for received values (monitor updates) */
    private long received_value_count = 0;

    /** Last value in the archive, i.e. the one most recently written.
     *  <p>
     *  SYNC: Lock on <code>this</code> for access.
     */
    protected VType last_archived_value = null;

    /** Buffer of received samples, periodically written */
    private final SampleBuffer buffer;

    /** Construct an archive channel
     *  @param name Name of the channel (PV)
     *  @param enablement How channel affects its groups
     *  @param buffer_capacity Size of sample buffer
     *  @param last_archived_value Last value from storage, or <code>null</code>.
     *  @throws Exception On error in PV setup
     * @deprecated Use {@link #ArchiveChannel(String,String,Enablement,int,VType)} instead
     */
    public ArchiveChannel(final String name,
                          final Enablement enablement,
                          final int buffer_capacity,
                          final VType last_archived_value) throws Exception
    {
        this(name, null, enablement, buffer_capacity, last_archived_value);
    }

    /** Construct an archive channel
     *  @param name Name of the channel (PV)
     * @param retention Sample retention policy; may be null, if default/not supported
     * @param enablement How channel affects its groups
     * @param buffer_capacity Size of sample buffer
     * @param last_archived_value Last value from storage, or <code>null</code>.
     *  @throws Exception On error in PV setup
     */
    public ArchiveChannel(final String name,
                          final String retention,
                          final Enablement enablement,
                          final int buffer_capacity, final VType last_archived_value) throws Exception
    {
        this.name = name;
        this.enablement = enablement;
        this.last_archived_value = last_archived_value;
        this.buffer = new SampleBuffer(name, retention, buffer_capacity);
        if (last_archived_value == null)
            Activator.getLogger().log(Level.INFO, "No known last value for {0}", name);
    }

    /** @return Name of channel */
    final public String getName()
    {
        return name;
    }

    /** @return How channel affects its groups */
    final public Enablement getEnablement()
    {
        return enablement;
    }

    /** @return <code>true</code> if channel is currently enabled */
    final public boolean isEnabled()
    {
        return enabled ;
    }

    /** @return Short description of sample mechanism */
    abstract public String getMechanism();

    /** @return Number of Groups to which this channel belongs */
    final public int getGroupCount()
    {
        return groups.size();
    }

    /** @return One Group to which this channel belongs */
    final public ArchiveGroup getGroup(final int index)
    {
        return groups.get(index);
    }

    /** Tell channel that it belogs to group */
    final void addGroup(final ArchiveGroup group)
    {
        groups.add(group);
    }

    /** Tell channel that it no longer belogs to group */
    final void removeGroup(final ArchiveGroup group)
    {
        if (!groups.remove(group))
            throw new Error("Channel " + getName() + " doesn't belong to group"
                            + group.getName());
    }

    /** @return <code>true</code> if connected */
    final public synchronized boolean isConnected()
    {
        return pv != null  &&  pv.read() != null;
    }

    /** @return Human-readable info on internal state of PV */
    public synchronized String getInternalState()
    {
        if (pv == null)
            return "Not initialized";
        return pv.read() != null ? "Connected" : "Disconnected";
    }

    /** Start archiving this channel. */
    final void start() throws Exception
    {
        is_running = true;
        need_first_sample = true;

        final PV pv = PVPool.getPV(name);
        pv.addListener(this);
        synchronized (this)
        {
            if (this.pv != null)
                throw new Exception(name + " started twice");
            this.pv = pv;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void valueChanged(final PV pv, final VType value)
    {
        if (enablement != Enablement.Passive)
            handleEnablement(value);
        handleNewValue(checkReceivedValue(value));
    }

    /** {@inheritDoc} */
    @Override
    public void disconnected(final PV pv)
    {
        handleDisconnected();
    }

    /** Check a received value for basic problems before
     *  passing it on to the sample mechanism
     *  @param value Value as received from network layer
     *  @return Value to be used for archive
     */
    private VType checkReceivedValue(VType value)
    {
        if (value instanceof Time)
        {
            try
            {
                final Time time = (Time) value;
                // Invoke time.getTimestamp() to detect RuntimeError in VType 2013/11/01
                if (time.isTimeValid()  &&  time.getTimestamp() != null)
                    return value;
                else
                {
                    trouble_sample_log.log("'" + getName() + "': Invalid time stamp ");
                    value = VTypeHelper.transformTimestamp(value, Instant.now());
                }
            }
            catch (RuntimeException ex)
            {
                Logger.getLogger(getClass().getName()).log(Level.WARNING,
                        "'" + getName() + "': Exception getting time stamp", ex);
                value = VTypeHelper.transformTimestamp(value, Instant.now());
            }
        }
        else
            trouble_sample_log.log("'" + getName() + "': Received no time information for " + value);
       return value;
    }

    /** Stop archiving this channel */
    final void stop()
    {
        if (!is_running)
            return;
        is_running = false;
        final PV safe_pv;
        synchronized (this)
        {
            safe_pv = pv;
            pv = null;
        }
        safe_pv.removeListener(this);
        PVPool.releasePV(safe_pv);
        addInfoToBuffer(ValueButcher.createOff());
    }

    /** @return Most recent value of the channel's PV */
    final public synchronized String getCurrentValue()
    {
        return VTypeHelper.toString(most_recent_value);
    }

    /** @return Count of received values */
    final public synchronized long getReceivedValues()
    {
        return received_value_count;
    }

    /** @return Last value written to archive */
    final public synchronized String getLastArchivedValue()
    {
        return VTypeHelper.toString(last_archived_value);
    }

    /** @return Sample buffer */
    final public SampleBuffer getSampleBuffer()
    {
        return buffer;
    }

    /** Reset counters */
    public void reset()
    {
        buffer.reset();
        synchronized (this)
        {
            received_value_count = 0;
        }
    }

    /** Enable or disable groups based on received value */
    final private void handleEnablement(final VType value)
    {
        if (enablement == Enablement.Passive)
            throw new Error("Not to be called when passive"); //$NON-NLS-1$
        // Get boolean value (true <==> >0.0)
        final double number = VTypeHelper.toDouble(value);
        final boolean yes = number > 0.0;
        // Do we enable or disable based on that value?
        final boolean enable = enablement == Enablement.Enabling ? yes : !yes;
        // Check which group needs to _change_
        for (ArchiveGroup group : groups)
        {
            if (group.isEnabled() != enable)
                group.enable(enable);
        }
    }

    /** Called for each value received from PV.
     *  <p>
     *  Base class remembers the <code>most_recent_value</code>,
     *  and asserts that one 'first' sample is archived.
     *  Derived class <b>must</b> call <code>super()</code>.
     *
     *  @param value Value received from PV
     *
     *  @return true if the value was already written because
     *               it's the first value after startup or error,
     *               so there's no need to write that sample again.
     */
    protected boolean handleNewValue(final VType value)
    {
        synchronized (this)
        {
            ++received_value_count;
            most_recent_value = value;
        }
        // NaN test
        if (value instanceof VNumber)
        {
            if (Double.isNaN(VTypeHelper.toDouble(value)))
                trouble_sample_log.log("'" + getName() + "': NaN "
                        + VTypeHelper.toString(value));
        }
        if (!enabled)
            return false;

        // Did we recover from write errors?
        if (need_write_error_sample &&
            SampleBuffer.isInErrorState() == false)
        {
            need_write_error_sample = false;
            Activator.getLogger().log(Level.FINE, "Wrote error sample for {0}", getName());
            addInfoToBuffer(ValueButcher.createWriteError());
            need_first_sample = true;
        }
        // Is this the first sample after startup or an error?
        if (!need_first_sample)
            return false;
        need_first_sample = false;
        // Try to add as-is, but time stamp will be corrected to fit in
        final VType added = addInfoToBuffer(value);
        Activator.getLogger().log(Level.FINE, "Wrote first sample for {0}: {1}", new Object[] { getName(), added });
        return true;
    }

    /** Handle a disconnect event.
     *  <p>
     *  Base class clears the <code>most_recent_value</code> and
     *  adds a 'disconnected' info sample.
     *  Subclasses may override, but must call <code>super()</code>.
     */
    protected void handleDisconnected()
    {
        synchronized (this)
        {
            most_recent_value = null;
        }
        Activator.getLogger().log(Level.FINE, "Wrote disconnect sample for {0}", getName());
        addInfoToBuffer(ValueButcher.createDisconnected());
        need_first_sample = true;
    }

    /** Add given info value to buffer, tweaking its time stamp if necessary
     *  @param value Value to archive
     *  @return Value that was actually added, which may have adjusted time stamp
     */
    final protected VType addInfoToBuffer(VType value)
    {
        synchronized (this)
        {
            if (last_archived_value != null)
            {
                final Instant last = VTypeHelper.getTimestamp(last_archived_value);
                if (last.compareTo(VTypeHelper.getTimestamp(value)) >= 0)
                {   // Patch the time stamp
                    final Instant next = last.plus(Duration.ofMillis(100));
                    value = VTypeHelper.transformTimestamp(value, next);
                }
                // else: value is OK as is
            }
        }
        addValueToBuffer(value);
        return value;
    }

    /** @param time Timestamp to check
     *  @return <code>true</code> if time is too far into the future; better ignore.
     */
    private boolean isFuturistic(final Instant time)
    {
        final long threshold = System.currentTimeMillis()/1000 + EngineModel.getIgnoredFutureSeconds();
        return time.getEpochSecond() >= threshold;
    }

    /** Add given sample to buffer, performing a back-in-time check,
     *  updating the sample buffer error state.
     *  @param value Value to archive
     *  @return <code>false</code> if value failed back-in-time or future check,
     *          <code>true</code> if value was added.
     */
    final protected boolean addValueToBuffer(final VType value)
    {
        // Suppress samples that are too far in the future
        final Instant time = VTypeHelper.getTimestamp(value);

        if (isFuturistic(time))
        {
            trouble_sample_log.log("'" + getName() + "': Futuristic " + value);
            return false;
        }

        synchronized (this)
        {
            if (last_archived_value != null &&
                VTypeHelper.getTimestamp(last_archived_value).compareTo(time) >= 0)
            {   // Cannot use this sample because of back-in-time problem.
                // Usually this is NOT an error:
                // We logged an initial sample, disconnected, disabled, ...,
                // and now we got an update from the IOC which still
                // carries the old, original time stamp of the PV,
                // and that's back in time...
                trouble_sample_log.log(getName() + " skips back-in-time:\n" +
                        "last: " + VTypeHelper.toString(last_archived_value) + "\n" +
                        "new : " + VTypeHelper.toString(value));
                return false;
            }
            // else ...
            last_archived_value = value;
        }
        buffer.add(value);
        if (SampleBuffer.isInErrorState())
            need_write_error_sample = true;
        return true;
    }

    /** Determine if the channel is enabled.
     *  <p>
     *  Checks all groups to which the channel belongs.
     *  If they're all disabled, so is the channel.
     */
    final void computeEnablement()
    {
        // 'Active' channels always stay enabled
        if (enablement != Enablement.Passive)
            return;
        for (ArchiveGroup group : groups)
        {
            if (group.isEnabled())
            {   // Found at least one enabled group
                updateEnabledState(true);
                return;
            }
        }
        // else: All groups are disabled
        updateEnabledState(false);
    }

    /** Update the enablement state in case of change */
    final private void updateEnabledState(final boolean new_enabled_state)
    {
        // Any change?
        if (new_enabled_state == enabled)
            return;
        enabled = new_enabled_state;
        // In case this arrived after shutdown, don't log it.
        if (!is_running)
            return;
        if (enabled)
        {   // If we have the 'current' value of the PV...
            VType value;
            synchronized (this)
            {
                value = most_recent_value;
            }
            if (value != null)
            {   // Add to the buffer with timestamp 'now' to show
                // the re-enablement
                value = VTypeHelper.transformTimestampToNow(value);
                addValueToBuffer(value);
            }
        }
        else
            addInfoToBuffer(ValueButcher.createDisabled());
    }

    @Override
    public String toString()
    {
        return "Channel " + getName() + ", " + getMechanism();
    }
}
