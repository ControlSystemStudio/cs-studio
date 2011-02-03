/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.concurrent.GuardedBy;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.csstudio.archive.common.engine.ThrottledLogger;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.alarm.IHasAlarm;
import org.csstudio.domain.desy.epics.types.EpicsIValueTypeSupport;
import org.csstudio.domain.desy.types.ICssAlarmValueType;
import org.csstudio.domain.desy.types.ICssValueType;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

import com.google.common.collect.Lists;

/** Base for archived channels.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public abstract class ArchiveChannel<V,
                                     T extends ICssValueType<V> & IHasAlarm>
{
    private static final Logger LOG = CentralLogger.getInstance().getLogger(ArchiveChannel.class);
    final Logger PV_LOG = CentralLogger.getInstance().getLogger(PVListener.class);

    /** Throttled log for NaN samples */
    private static ThrottledLogger trouble_sample_log =
                    new ThrottledLogger(Level.INFO, "log_trouble_samples"); //$NON-NLS-1$


    /** Channel name.
     *  This is the name by which the channel was created,
     *  not the PV name that might include decorations.
     */
    private final String _name;

    final ArchiveChannelId _id;

    /** Control system PV */
    private final PV _pv;


    /** Buffer of received samples, periodically written */
    private final SampleBuffer<V, T, IArchiveSample<V, T>> _buffer;

    /** Group to which this channel belongs.
     *  <p>
     *  Using thread safe array so that HTTPD can access
     *  as well as main thread and PV
     */
    private final CopyOnWriteArrayList<ArchiveGroup> _groups =
                                new CopyOnWriteArrayList<ArchiveGroup>();


    /** Is this channel currently running?
     *  <p>
     *  PV sends another 'disconnected' event
     *  as the result of 'stop', but we don't
     *  want to log that, so we keep track of
     *  the 'running' state.
     */
    volatile boolean _isRunning = false;

    /** Do we need to log a 'write error' sample?
     *  <p>
     *  The sample buffer will indicate write errors.
     *  While in error, we keep adding samples, which
     *  will probably cause overrides.
     *  When we can write again, we add one info sample.
     */
    private final boolean need_write_error_sample = false;

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
//    final private Enablement enablement;

    /** Is this channel currently enabled? */
    private final boolean enabled = true;

    /** Most recent value of the PV.
     *  <p>
     *  This is the value received from the PV,
     *  is is not necessarily written to the archive.
     *  <p>
     */
    @GuardedBy("this")
    protected T mostRecentValue;

    /** Counter for received values (monitor updates) */
    private long _receivedValueCount = 0;

    /**
     * The most recent value send to the archive.
     */
    @GuardedBy("this")
    protected T _lastArchivedValue;


    /** Construct an archive channel
     *  @param name Name of the channel (PV)
     *  @param enablement How channel affects its groups
     *  @param buffer_capacity Size of sample buffer
     *  @param last_archived_value Last value from storage, or <code>null</code>.
     *  @throws Exception On error in PV setup
     */
    public ArchiveChannel(final String name,
                          final ArchiveChannelId id/*,
                          final Enablement enablement,
                          final int buffer_capacity,
                          final IValue last_archived_value */) throws Exception {
        _name = name;
        _id = id;
//        this.enablement = enablement;
//        this.last_archived_value = last_archived_value;
        _buffer = new SampleBuffer<V, T, IArchiveSample<V, T>>(name);

//        if (last_archived_value == null) {
//            log.info(name + ": No known last value");
//        }
//        if (!log.isDebugEnabled()) {
//            log = null;
//        }

        _pv = PVFactory.createPV(name);
        _pv.addListener(new PVListener() {
            @Override
            public void pvValueUpdate(final PV pv) {
                try {
                    // PV already suppresses updates after 'stop', but check anyway
                    if (_isRunning) {
                        final IValue value = pv.getValue();
    //                    if (enablement != Enablement.Passive) {
    //                        handleEnablement(value);
    //                    }
                            final ICssAlarmValueType<V> cssValue = EpicsIValueTypeSupport.toCssType(value);
                            @SuppressWarnings("unchecked")
                            final ArchiveSample<V, T> sample = new ArchiveSample<V, T>(_id, (T) cssValue);
                            handleNewSample(sample);
                            //handleNewValue(cssValue);

                    }
                } catch (final TypeSupportException e) {
                    PV_LOG.error("Handling of newly received IValue failed. Could not be converted to CssValue", e);
                    return;
                } catch (final Throwable t) {
                    System.out.println("");
                }
            }

            @Override
            public void pvDisconnected(final PV pv) {
                if (_isRunning) {
                    handleDisconnected();
                }
            }
        });
    }

    /** @return Name of channel */
    final public String getName() {
        return _name;
    }

    /** @return How channel affects its groups */
//    final public Enablement getEnablement()
//    {
//        return enablement;
//    }

    /** @return <code>true</code> if channel is currently enabled */
    final public boolean isEnabled()
    {
        return enabled ;
    }

    /** @return Short description of sample mechanism */
    public abstract String getMechanism();

    /** @return Number of Groups to which this channel belongs */
    public final int getGroupCount()
    {
        return _groups.size();
    }

    /** @return One Group to which this channel belongs */
    public final ArchiveGroup getGroup(final int index)
    {
        return _groups.get(index);
    }

    /** Tell channel that it belogs to group */
    final void addGroup(final ArchiveGroup group) {
        _groups.add(group);
    }

    /** Tell channel that it no longer belogs to group */
    final void removeGroup(final ArchiveGroup group)
    {
        if (!_groups.remove(group)) {
            throw new Error("Channel " + getName() + " doesn't belong to group"
                            + group.getName());
        }
    }

    /** @return <code>true</code> if connected */
    public final boolean isConnected()
    {
        return _pv.isConnected();
    }

    /** @return Human-readable info on internal state of PV */
    public String getInternalState()
    {
        return _pv.getStateInfo();
    }

    /** Start archiving this channel. */
    final void start() throws Exception
    {
        _isRunning = true;
        need_first_sample = true;
        _pv.start();
    }

    /** Stop archiving this channel */
    final void stop()
    {
    	if (!_isRunning) {
            return;
        }
        _isRunning = false;
        _pv.stop();
        //addInfoToBuffer(ValueButcher.createOff());
    }

    /** @return Most recent value of the channel's PV */
    final public String getCurrentValue()
    {
        synchronized (this)
        {
            if (mostRecentValue == null)
             {
                return "null"; //$NON-NLS-1$
            }
            return mostRecentValue.getValueData().toString();
        }
    }

    /** @return Count of received values */
    public synchronized long getReceivedValues() {
        return _receivedValueCount;
    }

    /** @return Last value written to archive */
    public final String getLastArchivedValue() {
        synchronized (this) {
            if (_lastArchivedValue == null) {
                return "null"; //$NON-NLS-1$
            }
            return _lastArchivedValue.getValueData().toString();
        }
    }

    /** @return Sample buffer */
    public final SampleBuffer<V, T, IArchiveSample<V, T>> getSampleBuffer() {
        return _buffer;
    }


    /** Reset counters */
    public void reset() {
        _buffer.reset();
        synchronized (this) {
            _receivedValueCount = 0;
        }
    }

    /** Enable or disable groups based on received value */
//    final private void handleEnablement(final IValue value)
//    {
//        if (enablement == Enablement.Passive)
//         {
//            throw new Error("Not to be called when passive"); //$NON-NLS-1$
//        }
//        // Get boolean value (true <==> >0.0)
//        final double number = ValueUtil.getDouble(value);
//        final boolean yes = number > 0.0;
//        // Do we enable or disable based on that value?
//        final boolean enable = enablement == Enablement.Enabling ? yes : !yes;
//        // Check which group needs to _change_
//        for (final ArchiveGroup group : groups)
//        {
//            if (group.isEnabled() != enable) {
//                group.enable(enable);
//            }
//        }
//    }

    protected boolean handleNewSample(final IArchiveSample<V, T> sample) {
        synchronized (this) {
            ++_receivedValueCount;
            mostRecentValue = sample.getData();
        }
        _buffer.add(sample);
        return true;

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
//    protected boolean handleNewValue(final T value)
//    {
//        synchronized (this)
//        {
//            ++receivedValueCount;
//            mostRecentValue = value;
//        }
////        // NaN test
////        if (value instanceof IDoubleValue)
////        {
////            final IDoubleValue dbl = (IDoubleValue) value;
////            if (Double.isNaN(dbl.getValue())) {
////                trouble_sample_log.log("'" + getName() + "': NaN "
////                        + value.format());
////            }
////
////        }
//        if (!enabled) {
//            return false;
//        }
//
//        // Did we recover from write errors?
//        if (need_write_error_sample &&
//            SampleBuffer.isInErrorState() == false) {
//            need_write_error_sample = false;
//            //LOG.debug(getName() + " wrote error sample");
//            //addInfoToBuffer(ValueButcher.createWriteError());
//            need_first_sample = true;
//        }
//        // Is this the first sample after startup or an error?
//        if (!need_first_sample) {
//            return false;
//        }
//        need_first_sample = false;
//
//        // well, this one just sets the value's timestamp to now! why?
//        //final IValue updated = ValueButcher.transformTimestampToNow(value);
//        T updated = (T) new CssAlarmValueType<V>(value.getValueData(),
//                                                 value.getAlarm(),
//                                                 TimeInstantBuilder.buildFromNow());
//        LOG.debug(getName() + " wrote first sample " + updated);
//
//        addInfoToBuffer(updated);
//        return true;
//    }

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
            mostRecentValue = null;
        }
        //LOG.debug(getName() + " wrote disconnect sample");
        //addInfoToBuffer(ValueButcher.createDisconnected());
        need_first_sample = true;
    }

    /**
     * TODO (bknerr) : time stamp patching, inquire what's going on here - wrong timestamps from
     * the epics system ?
     *
     * Add given info value to buffer, tweaking its time stamp if necessary
     *  @param value Value to archive
     */
//    final protected void addInfoToBuffer(T value) {
//        synchronized (this) {
//            if (lastArchivedValue != null) {
//                final TimeInstant last = lastArchivedValue.getTimestamp();
//                if (last.isAfter(value.getTimestamp())) {   // Patch the time stamp
//                    final TimeInstant next = last.plusMillis(1000);
//                        //TimestampFactory.createTimestamp(last.seconds()+1, 0);
//                        //value = ValueButcher.transformTimestamp(value, next);
//                    value = (T) new CssAlarmValueType<V>(value.getValueData(),
//                                                         value.getAlarm(),
//                                                         next);
//                }
//                // else: value is OK as is
//            }
//        }
//        addValueToBuffer(value);
//    }

    /** @param time Timestamp to check
     *  @return <code>true</code> if time is too far into the future; better ignore.
     */
//    private boolean isFuturistic(final ITimestamp time)
//    {
//        final long threshold = System.currentTimeMillis()/1000 + EngineModel.getIgnoredFutureSeconds();
//        return time.seconds() >= threshold;
//    }

    /** Add given sample to buffer, performing a back-in-time check,
     *  updating the sample buffer error state.
     *  @param value Value to archive
     *  @return <code>false</code> if value failed back-in-time or future check,
     *          <code>true</code> if value was added.
     */
//    final protected boolean addValueToBuffer(final T value)
//    {
//        // Suppress samples that are too far in the future
//        final TimeInstant time = value.getTimestamp();

//        if (isFuturistic(time))
//        {
//            trouble_sample_log.log("'" + getName() + "': Futuristic " + value);
//            return false;
//        }
//
//        synchronized (this)
//        {
//            if (lastArchivedValue != null &&
//                last_archived_value.getTime().isGreaterOrEqual(time))
//            {   // Cannot use this sample because of back-in-time problem.
                // Usually this is NOT an error:
                // We logged an initial sample, disconnected, disabled, ...,
                // and now we got an update from the IOC which still
                // carries the old, original time stamp of the PV,
                // and that's back in time...
//                trouble_sample_log.log(getName() + " skips back-in-time:\n" +
//                        "last: " + last_archived_value.toString() + "\n" +
//                        "new : " + value.toString());
//                return false;
//            }
//            // else ...
//	        lastArchivedValue = value;
//        }
//        buffer.add(value);
//        if (SampleBuffer.isInErrorState()) {
//            need_write_error_sample = true;
//        }
//        return true;
//    }

    /** Determine if the channel is enabled.
     *  <p>
     *  Checks all groups to which the channel belongs.
     *  If they're all disabled, so is the channel.
     */
//    final void computeEnablement()
//    {
//        // 'Active' channels always stay enabled
//        if (enablement != Enablement.Passive) {
//            return;
//        }
//        for (final ArchiveGroup group : groups)
//        {
//            if (group.isEnabled())
//            {   // Found at least one enabled group
//                updateEnabledState(true);
//                return;
//            }
//        }
//        // else: All groups are disabled
//        updateEnabledState(false);
//    }

//    /** Update the enablement state in case of change */
//    final private void updateEnabledState(final boolean new_enabled_state)
//    {
//        // Any change?
//        if (new_enabled_state == enabled) {
//            return;
//        }
//        enabled = new_enabled_state;
//        // In case this arrived after shutdown, don't log it.
//        if (!_isRunning) {
//            return;
//        }
//        if (enabled)
//        {   // If we have the 'current' value of the PV...
//            IValue value;
//            synchronized (this)
//            {
//                value = most_recent_value;
//            }
//            if (value != null)
//            {   // Add to the buffer with timestamp 'now' to show
//                // the re-enablement
//                value = ValueButcher.transformTimestampToNow(value);
//                addValueToBuffer(value);
//            }
//        } else {
//            addInfoToBuffer(ValueButcher.createDisabled());
//        }
//    }

    @Override
    public String toString() {
        return "Channel " + getName() + ", " + getMechanism();
    }

    public Iterable<ArchiveGroup> getGroups() {
        return Lists.newArrayList(_groups);
    }
}
