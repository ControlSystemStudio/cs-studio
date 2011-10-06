/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.util.TimeDuration.sec;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Monitor;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

import org.csstudio.archive.common.engine.pvmanager.DesyArchivePVManagerListener;
import org.csstudio.archive.common.engine.pvmanager.DesyJCADataSource;
import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.utility.pv.PVListener;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Base for archived channels.
 *
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 *  @param <V> the basic value type
 *  @param <T> the system variable for the basic value type
 */
@SuppressWarnings("nls")
public class ArchiveChannelBuffer<V extends Serializable, T extends ISystemVariable<V>> {

    private static final Logger LOG = LoggerFactory.getLogger(PVListener.class);

    /** Channel name.
     *  This is the name by which the channel was created,
     *  not the PV name that might include decorations.
     */
    private final String _name;

    private final ArchiveChannelId _id;

    /** Control system PV */
    private final PVReader<Object> _pv;

    private final DesyJCADataSource _dataSource;

    /** Buffer of received samples, periodically written */
    private final SampleBuffer<V, T, IArchiveSample<V, T>> _buffer;


    /** Is this channel currently running?
     *  <p>
     *  PV sends another 'disconnected' event
     *  as the result of 'stop', but we don't
     *  want to log that, so we keep track of
     *  the 'running' state.
     */
    @GuardedBy("this")
    private boolean _isStarted;

    @GuardedBy("this")
    private boolean _isEnabled;

    /** Most recent value of the PV.
     *  <p>
     *  This is the value received from the PV,
     *  is is not necessarily written to the archive.
     *  <p>
     */
    @GuardedBy("this")
    private T _mostRecentSysVar;

    /**
     * Counter for received values (monitor updates)
     */
    private long _receivedSampleCount;

    private final IServiceProvider _provider;

    private final Class<V> _typeClazz;
    private final Class<V> _collClazz;

    @SuppressWarnings("rawtypes")
    private final DesyArchivePVManagerListener _listener;

    private TimeInstant _timeOfLastSampleBeforeChannelStart;

    /**
     * Constructor
     * @throws EngineModelException on failure while creating PV
     */
    public ArchiveChannelBuffer(@Nonnull final String name,
                                @Nonnull final ArchiveChannelId id,
                                @Nullable final TimeInstant timeOfLastSample,
                                final boolean isEnabled,
                                @Nonnull final Class<V> typeClazz,
                                @Nonnull final IServiceProvider provider) throws EngineModelException {
        this(name, id, timeOfLastSample, isEnabled, null, typeClazz, provider);
    }


    /**
     * Constructor.
     * @throws EngineModelException on failure while creating PV
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ArchiveChannelBuffer(@Nonnull final String name,
                                @Nonnull final ArchiveChannelId id,
                                @Nullable final TimeInstant timeOfLastSample,
                                final boolean isEnabled,
                                @Nullable final Class<V> collClazz,
                                @Nonnull final Class<V> typeClazz,
                                @Nonnull final IServiceProvider provider) throws EngineModelException {
        _name = name;
        _id = id;
        _timeOfLastSampleBeforeChannelStart = timeOfLastSample;
        _isEnabled = isEnabled;
        _buffer = new SampleBuffer<V, T, IArchiveSample<V, T>>(name);
        _typeClazz = typeClazz;
        _collClazz = collClazz;
        _provider = provider;

        // Sets CAJ (pure java implementation) as the default data source,
        // monitoring only archive changes
        _dataSource = new DesyJCADataSource(JCALibrary.CHANNEL_ACCESS_JAVA, Monitor.LOG);
        PVManager.setDefaultDataSource(_dataSource);
        _pv = PVManager.read(channel(_name)).every(sec(3));

        _listener = new DesyArchivePVManagerListener(_pv, _provider, _name, _id, _collClazz, _typeClazz) {
            @SuppressWarnings("synthetic-access")
            @Override
            protected boolean addSampleToBuffer(@Nonnull final IArchiveSample sample) {
                synchronized (this) {
                    _receivedSampleCount++;
                    _mostRecentSysVar = (T) sample.getSystemVariable();
                }
                return _buffer.add(sample);

            }
        };
    }


    /** @return Name of channel */
    @Nonnull
    public String getName() {
        return _name;
    }

    /** @return Short description of sample mechanism */
    @Nonnull
    public String getMechanism() {
        return "MONITOR (on change)";
    }

    /** @return <code>true</code> if connected */
    public boolean isConnected() {
        // FIXME (bknerr) : is this information available?
        return _dataSource.isConnected();
    }

    /** @return <code>true</code> if connected */
    public boolean isStarted() {
        return _isStarted;
    }

    /** @return Human-readable info on internal state of PV */
    @CheckForNull
    public String getInternalState() {
        // FIXME (bknerr) : is this information available?
        return "UNKNOWN via PVManager";
    }

    @CheckForNull
    public TimeInstant getTimeOfMostRecentSample() {
        return _mostRecentSysVar != null ? _mostRecentSysVar.getTimestamp() : _timeOfLastSampleBeforeChannelStart;
    }

    /**
     * Checks whether this channel is enabled for archiving and if so it is started.
     * @return true if the channel could be started, false otherwise
     * @throws EngineModelException
     */
    public boolean start(@Nonnull final String info) throws EngineModelException {
        try {
            synchronized (this) {
                if (_isStarted) {
                    return true;
                }
                _isStarted = true;
            }
            _listener.setStartInfo(info);
            _pv.addPVReaderListener(_listener);

            enable();

        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException("Service unavailable. Enabling of channel could not be persisted.", e);
        } catch (final Exception e) {
            LOG.error("PV " + _pv.getName() + " could not be started with state info " + getInternalState(), e);
            throw new EngineModelException("Something went wrong within Gabriele's PV stuff on channel/PV startup", e);
        }
        return true;
    }

    private void enable() throws OsgiServiceUnavailableException {
        synchronized (this) {
            if (isEnabled()) {
                return;
            }
            _isEnabled = true;
        }
        _provider.getEngineFacade().setEnableChannelFlag(_name, true);
    }

    /**
     * Stop archiving this channel
     */
    public void stop(@Nonnull final String info) throws EngineModelException {
        synchronized (this) {
            if (!_isStarted) {
                return;
            }
            _isStarted = false;
        }
        _listener.setStopInfo(info);
        _pv.removePVReaderListener(_listener);

        try {
            disable();
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException("Service unavailable. Disabling of channel could not be persisted.", e);
        }
    }

    private void disable() throws OsgiServiceUnavailableException {
        synchronized (this) {
            if (!isEnabled()) {
                return;
            }
            _isEnabled = false;
        }
        _provider.getEngineFacade().setEnableChannelFlag(_name, false);
    }

    @Nonnull
    public synchronized T getMostRecentSample() {
        return _mostRecentSysVar;
    }

    /** @return Count of received values */
    public synchronized long getReceivedValues() {
        return _receivedSampleCount;
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
            _receivedSampleCount = 0;
        }
    }

    @Override
    @Nonnull
    public String toString() {
        return "Channel " + getName() + ", " + getMechanism();
    }

    public boolean isMultiScalar() {
        return _collClazz != null;
    }

    @Nonnull
    public ArchiveChannelId getId() {
        return _id;
    }

    boolean isEnabled() {
        return _isEnabled;
    }
}
