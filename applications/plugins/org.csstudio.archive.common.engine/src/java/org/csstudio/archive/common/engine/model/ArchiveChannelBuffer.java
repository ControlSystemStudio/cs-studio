/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.newValuesOf;
import static org.epics.pvmanager.util.TimeDuration.ms;

import java.io.Serializable;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;

import org.csstudio.archive.common.engine.pvmanager.DesyArchivePVManagerListener;
import org.csstudio.archive.common.engine.pvmanager.DesyJCAChannelHandler;
import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.util.TimeDuration;
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

    private static final Logger LOG = LoggerFactory.getLogger(ArchiveChannelBuffer.class);

    private static final TimeDuration RATE = ms(2000);

    /** Channel name.
     *  This is the name by which the channel was created,
     *  not the PV name that might include decorations.
     */
    private final String _name;

    private final ArchiveChannelId _id;

    private final DesyJCAChannelHandler _channelHandler;

    /** Control system PV */
    private PVReader<List<Object>> _pv;

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

    @SuppressWarnings("rawtypes")
    private DesyArchivePVManagerListener _listener;

    private final TimeInstant _timeOfLastSampleBeforeChannelStart;

    /**
     * Constructor.
     */
    public ArchiveChannelBuffer(@Nonnull final IArchiveChannel cfg,
                                @Nonnull final IServiceProvider provider,
                                @Nonnull final DesyJCAChannelHandler handler) {

        _name = cfg.getName();
        _id = cfg.getId();
        _timeOfLastSampleBeforeChannelStart = cfg.getLatestTimestamp();
        _isEnabled = cfg.isEnabled();
        _buffer = new SampleBuffer<V, T, IArchiveSample<V, T>>(_name);
        _provider = provider;
        _channelHandler = handler;
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
        return _channelHandler != null && _channelHandler.isConnected();
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
                initPvAndListener();
                _isStarted = true;
            }

            _listener.setStartInfo(info);
            _pv.addPVReaderListener(_listener);

            enable();

        } catch (final Exception e) {
            LOG.error("PV " + _pv.getName() + " could not be started with state info " + getInternalState(), e);
            throw new EngineModelException("Something went wrong within Gabriele's PV stuff on channel/PV startup", e);
        }
        return true;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void initPvAndListener() {
        _pv = PVManager.read(newValuesOf(channel(_name))).every(RATE);

        _listener = new DesyArchivePVManagerListener(_pv, _provider, _name, _id) {
            @SuppressWarnings("synthetic-access")
            @Override
            protected boolean addSampleToBuffer(@Nonnull final IArchiveSample sample) {
                synchronized (this) {
                    _receivedSampleCount++;
                    _mostRecentSysVar = (T) sample.getSystemVariable();
                }
                return _buffer.add(sample);

            }
            @Override
            public boolean isConnected() {
                return ArchiveChannelBuffer.this.isConnected();
            }
        };
    }

    public void enable() throws EngineModelException {
        synchronized (this) {
            if (isEnabled()) {
                return;
            }
            _isEnabled = true;
        }
        try {
            _provider.getEngineFacade().setEnableChannelFlag(_name, true);
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException("Service unavailable. Disabling of channel could not be persisted.", e);
        }
    }

    /**
     * Stop archiving this channel
     */
    public void stop(@Nonnull final String info) {
        synchronized (this) {
            if (!_isStarted) {
                return;
            }
            _isStarted = false;
        }
        _listener.setStopInfo(info);
        _pv.removePVReaderListener(_listener);
    }

    public void disable() throws EngineModelException {

        stop("PERMAMENT DISABLE");

        synchronized (this) {
            if (!isEnabled()) {
                return;
            }
            _isEnabled = false;
        }
        try {
            _provider.getEngineFacade().setEnableChannelFlag(_name, false);
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException("Service unavailable. Disabling of channel could not be persisted.", e);
        }
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

    @Nonnull
    public ArchiveChannelId getId() {
        return _id;
    }

    public boolean isEnabled() {
        return _isEnabled;
    }
}
