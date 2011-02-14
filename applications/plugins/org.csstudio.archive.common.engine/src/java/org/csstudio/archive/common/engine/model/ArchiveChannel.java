/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.engine.Activator;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveWriterService;
import org.csstudio.archive.common.service.archivermgmt.ArchiverMgmtEntry;
import org.csstudio.archive.common.service.archivermgmt.ArchiverMgmtEntryId;
import org.csstudio.archive.common.service.archivermgmt.ArchiverMonitorStatus;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.alarm.IHasAlarm;
import org.csstudio.domain.desy.epics.types.EpicsIValueTypeSupport;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.ITimedCssAlarmValueType;
import org.csstudio.domain.desy.types.ITimedCssValueType;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
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
                                     T extends ITimedCssValueType<V> & IHasAlarm> {
    static final Logger PV_LOG = CentralLogger.getInstance().getLogger(PVListener.class);

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
    @GuardedBy("this")
    private volatile boolean _isRunning = false;

    /** Most recent value of the PV.
     *  <p>
     *  This is the value received from the PV,
     *  is is not necessarily written to the archive.
     *  <p>
     */
    @GuardedBy("this")
    protected T mostRecentValue;

    /**
     * The most recent value send to the archive.
     */
    @GuardedBy("this")
    protected T _lastArchivedValue;

    /** Counter for received values (monitor updates) */
    private long _receivedValueCount = 0;



    /** Construct an archive channel
     *  @param name Name of the channel (PV)
     *  @param enablement How channel affects its groups
     *  @param buffer_capacity Size of sample buffer
     *  @param last_archived_value Last value from storage, or <code>null</code>.
     *  @throws Exception On error in PV setup
     */
    public ArchiveChannel(@Nonnull final String name,
                          @Nonnull final ArchiveChannelId id) throws Exception {
        _name = name;
        _id = id;
        _buffer = new SampleBuffer<V, T, IArchiveSample<V, T>>(name);

        _pv = PVFactory.createPV(name);
        _pv.addListener(new PVListener() {

            @Override
            public void pvValueUpdate(final PV pv) {
                try {
                    // PV already suppresses updates after 'stop', but check anyway
                    if (_isRunning) {
                        final IValue value = pv.getValue();

                        final ITimedCssAlarmValueType<V> cssValue = EpicsIValueTypeSupport.toCssType(value);
                        @SuppressWarnings("unchecked")
                        final ArchiveSample<V, T> sample = new ArchiveSample<V, T>(_id, (T) cssValue);
                        handleNewSample(sample);
                    }
                } catch (final TypeSupportException e) {
                    PV_LOG.error("Handling of newly received IValue failed. Could not be converted to CssValue", e);
                    return;
                } catch (final Throwable t) {
                    PV_LOG.error("Unexpected exception in PVListener: " + t.getMessage());
                }
            }

            @Override
            public void pvDisconnected(final PV pv) {
                if (_isRunning && pv != null) {
                    handleDisconnectionInformation(pv);
                }
            }
        });
    }

    /**
     * @param pv
     */
    protected void handleDisconnectionInformation(@Nonnull final PV pv) {

        final String someMoreInfo = pv.getStateInfo();

    }

    /** @return Name of channel */
    public String getName() {
        return _name;
    }

    /** @return Short description of sample mechanism */
    @Nonnull
    public abstract String getMechanism();

    /** @return Number of Groups to which this channel belongs */
    public int getGroupCount() {
        return _groups.size();
    }

    /** @return One Group to which this channel belongs */
    @CheckForNull
    public ArchiveGroup getGroup(final int index) {
        return _groups.get(index);
    }

    /** Tell channel that it belogs to group */
    public void addGroup(@Nonnull final ArchiveGroup group) {
        _groups.add(group);
    }

    /** Tell channel that it no longer belogs to group */
    public void removeGroup(@Nonnull final ArchiveGroup group) {
        if (!_groups.remove(group)) {
            throw new Error("Channel " + getName() + " doesn't belong to group"
                            + group.getName());
        }
    }

    /** @return <code>true</code> if connected */
    public boolean isConnected() {
        return _pv.isConnected();
    }

    /** @return Human-readable info on internal state of PV */
    @CheckForNull
    public String getInternalState() {
        return _pv.getStateInfo();
    }

    /**
     * Start archiving this channel.
     * @param engineId
     * @param info human readable info about the start of this channel
     * @throws EngineModelException
     */
    public void start(@Nonnull final ArchiveEngineId engineId,
                      @Nonnull final String info) throws EngineModelException {
        try {
        if (_isRunning) {
            return;
        }
        synchronized (this) {
            _isRunning = true;
                _pv.start();
        }

            // persist the start of monitoring
            final IArchiveWriterService service = Activator.getDefault().getArchiveWriterService();
            service.writeMonitorModeInformation(new ArchiverMgmtEntry(ArchiverMgmtEntryId.NONE,
                                                                      _id,
                                                                      ArchiverMonitorStatus.ON,
                                                                      engineId,
                                                                      TimeInstantBuilder.buildFromNow(),
                                                                      info));
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException("Service unavailable on stopping archive engine channel.", e);
        } catch (final ArchiveServiceException e) {
            throw new EngineModelException("Internal service error on stopping archive engine channel.", e);
        } catch (final Exception e) {
            throw new EngineModelException("Something went wrong within Kasemir's PV stuff on channel startup", e);
        }

    }

    /**
     * Stop archiving this channel
     * @throws EngineModelException
     */
    public void stop(@Nonnull final ArchiveEngineId engineId,
                     @Nonnull final String info) throws EngineModelException {
    	if (!_isRunning) {
            return;
        }
    	synchronized (this) {
    	    _isRunning = false;
    	}
        _pv.stop();

        try {
            // persist the start of monitoring
            final IArchiveWriterService service = Activator.getDefault().getArchiveWriterService();
            service.writeMonitorModeInformation(new ArchiverMgmtEntry(ArchiverMgmtEntryId.NONE,
                                                                      _id,
                                                                      ArchiverMonitorStatus.OFF,
                                                                      engineId,
                                                                      TimeInstantBuilder.buildFromNow(),
                                                                      info));
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException("Service unavailable on stopping archive engine channel.", e);
        } catch (final ArchiveServiceException e) {
            throw new EngineModelException("Internal service error on stopping archive engine channel.", e);
        }
    }

    /** @return Most recent value of the channel's PV */
    @Nonnull
    public String getCurrentValue() {
        synchronized (this) {
            if (mostRecentValue == null) {
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
    @Nonnull
    public final String getLastArchivedValue() {
        synchronized (this) {
            if (_lastArchivedValue == null) {
                return "null"; //$NON-NLS-1$
            }
            return _lastArchivedValue.getValueData().toString();
        }
    }

    /** @return Sample buffer */
    @Nonnull
    public final SampleBuffer<V, T, IArchiveSample<V, T>> getSampleBuffer() {
        return _buffer;
    }


    /** Reset counters */
    public void reset() {
        _buffer.statsReset();
        synchronized (this) {
            _receivedValueCount = 0;
        }
    }


    protected boolean handleNewSample(@Nonnull final IArchiveSample<V, T> sample) {
        synchronized (this) {
            ++_receivedValueCount;
            mostRecentValue = sample.getData();
        }
        _buffer.add(sample);
        return true;

    }

    @Override
    @Nonnull
    public String toString() {
        return "Channel " + getName() + ", " + getMechanism();
    }

    @Nonnull
    public Iterable<ArchiveGroup> getGroups() {
        return Lists.newArrayList(_groups);
    }

    @Deprecated
    public boolean isEnabled() {
        return true;
    }
}
