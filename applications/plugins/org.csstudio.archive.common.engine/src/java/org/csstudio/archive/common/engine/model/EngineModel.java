/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.csstudio.archive.common.engine.ArchiveEnginePreference;
import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.engine.types.ArchiveEngineTypeSupport;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.channelstatus.IArchiveChannelStatus;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.archive.common.service.enginestatus.ArchiveEngineStatus;
import org.csstudio.archive.common.service.enginestatus.EngineMonitorStatus;
import org.csstudio.archive.common.service.enginestatus.IArchiveEngineStatus;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.slf4j.LoggerFactory;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.joda.time.Duration;

import com.google.common.collect.MapMaker;

/** Data model of the archive engine.
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 */
public final class EngineModel {
    private static final Logger LOG =
        LoggerFactory.getLogger(EngineModel.class);

    /** Version code. See also webroot/version.html */
    private static String VERSION = "1.0.0";

    /** Name of this model */
    private String _name = "DESY Archive Engine";  //$NON-NLS-1$

    /** Thread that writes to the <code>archive</code> */
    private WriteExecutor _writeExecutor;

    /**
     * All channels
     */
    private final ConcurrentMap<String, ArchiveChannel<?, ?>> _channelMap;

    /** Groups of archived channels
     *  <p>
     *  @see channels about thread safety
     */
    private final ConcurrentMap<String, ArchiveGroup> _groupMap;

    /** Engine states */
    public enum State {
        /** Initial model state before <code>start()</code> */
        IDLE,
        /** Running model, state after <code>start()</code> */
        RUNNING,
        /** State after <code>requestStop()</code>; still running. */
        SHUTDOWN_REQUESTED,
        /** State after <code>requestRestart()</code>; still running. */
        RESTART_REQUESTED,
        /** State while in <code>stop()</code>; will then be IDLE again. */
        STOPPING
    }

    /** Engine state */
    private volatile State _state = State.IDLE;

    /** Start time of the model */
    private TimeInstant _startTime = null;

    private final long _writePeriodInMS;
    private final long _heartBeatPeriodInMS;

    private IArchiveEngine _engine;

    private final IServiceProvider _provider;

    /**
     * Construct model that writes to archive
     * @param engineName
     * @param provider
     */
    public EngineModel(@Nonnull final String engineName,
                       @Nonnull final IServiceProvider provider) {
        _name = engineName;
        _provider = provider;

        _groupMap = new MapMaker().concurrencyLevel(2).makeMap();
        _channelMap = new MapMaker().concurrencyLevel(2).makeMap();

        _writePeriodInMS = 1000*ArchiveEnginePreference.WRITE_PERIOD.getValue();
        _heartBeatPeriodInMS = 1000*ArchiveEnginePreference.HEARTBEAT_PERIOD.getValue();
    }

    /** @return Name (description) */
    @Nonnull
    public String getName() {
        return _name;
    }

    /** @return Write period in milliseconds */
    public long getWritePeriodInMS() {
        return _writePeriodInMS;
    }

    /** @return Current model state */
    @Nonnull
    public State getState() {
        return _state;
    }

    /** @return Start time of the engine or <code>null</code> if not running */
    @CheckForNull
    public TimeInstant getStartTime() {
        return _startTime;
    }

    /**
     *  Add new group if not already exists.
     *
     *  @param _name Name of the group to find or add.
     *  @return ArchiveGroup the already existing or, if not, newly added group
     */
    @Nonnull
    private ArchiveGroup addGroup(@Nonnull final IArchiveChannelGroup groupCfg) {
        final String groupName = groupCfg.getName();
        _groupMap.putIfAbsent(groupName, new ArchiveGroup(groupName));
        return _groupMap.get(groupName);
    }

    /** @return Group by that name or <code>null</code> if not found */
    @CheckForNull
    public ArchiveGroup getGroup(@Nonnull final String name) {
        return _groupMap.get(name);
    }

    @Nonnull
    public Collection<ArchiveGroup> getGroups() {
        return _groupMap.values();
    }

    /** @return Channel by that name or <code>null</code> if not found */
    @CheckForNull
    public ArchiveChannel<?, ?> getChannel(@Nonnull final String name) {
        return _channelMap.get(name);
    }

    /** @return Channel by that name or <code>null</code> if not found */
    @Nonnull
    public Collection<ArchiveChannel<?, ?>> getChannels() {
        return _channelMap.values();
    }

    /**
     * Start processing all channels and writing to archive.
     * @throws EngineModelException
     */
    public void start() throws EngineModelException {

        if (_engine == null || _writeExecutor == null) {
            throw new EngineModelException("Engine or writeExecutor is null. Did you read the engine configuration successfully?", null);
        }

        checkAndUpdateLastShutdownStatus(_provider, _engine, _channelMap.values());


        _startTime = TimeInstantBuilder.fromNow();

        _state = State.RUNNING;

        _writeExecutor.start(_heartBeatPeriodInMS, _writePeriodInMS);

        startChannelGroups(_groupMap.values());
    }


    /**
     * Retrieves the last archiver status from the archive.
     *
     * If it was a graceful shutdown, anything's fine.
     *
     * Otherwise:
     * 1) update the engine_status table by an 'engine OFF' info with the timestamp of the
     * last engine.alive value.
     * 2) update the channel_status table for all channels of this engine that have status
     * 'connected' with a new row disconnected and the timestamp of the last engine.alive value.
     * @param collection
     *
     * @throws EngineModelException
     */
    private void checkAndUpdateLastShutdownStatus(@Nonnull final IServiceProvider provider,
                                                  @Nonnull final IArchiveEngine engine,
                                                  @Nonnull final Collection<ArchiveChannel<?, ?>> channels)
                                                  throws EngineModelException {
        try {
            final IArchiveEngineFacade facade = provider.getEngineFacade();

            final IArchiveEngineStatus engineStatus =
                facade.getLatestEngineStatusInformation(engine.getId(),
                                                        engine.getLastAliveTime());

            if (isNotFirstStart(engineStatus) && wasNotGracefullyShutdown(engineStatus)) {
                facade.writeEngineStatusInformation(engine.getId(),
                                                    EngineMonitorStatus.OFF,
                                                    engine.getLastAliveTime(),
                                                    "Ungraceful shutdown");

                checkAndUpdateChannelsStatus(facade, engine, channels);
            }

            facade.writeEngineStatusInformation(engine.getId(),
                                                EngineMonitorStatus.ON,
                                                TimeInstantBuilder.fromNow(),
                                                "Engine Startup");

        } catch (@Nonnull final Exception e) {
            handleExceptions(e);
        }
    }


    private boolean wasNotGracefullyShutdown(@Nonnull final IArchiveEngineStatus engineStatus) {
        return !EngineMonitorStatus.OFF.equals(engineStatus.getStatus());
    }


    private boolean isNotFirstStart(@CheckForNull final IArchiveEngineStatus engineStatus) {
        return engineStatus != null;
    }


    private void checkAndUpdateChannelsStatus(@Nonnull final IArchiveEngineFacade facade,
                                              @Nonnull final IArchiveEngine engine,
                                              @Nonnull final Collection<ArchiveChannel<?, ?>> channels)
                                              throws ArchiveServiceException {
        for (final ArchiveChannel<?, ?> channel : channels) {
            final IArchiveChannelStatus status =
                facade.getChannelStatusByChannelName(channel.getName());

            if (status!= null && status.isConnected()) { // still connected?
                facade.writeChannelStatusInfo(status.getChannelId(),
                                              false,
                                              "Ungraceful engine shutdown",
                                              engine.getLastAliveTime());
            }
        }
    }


    private void startChannelGroups(@Nonnull final Collection<ArchiveGroup> groups) throws EngineModelException {
        for (final ArchiveGroup group : groups) {
            group.start(ArchiveEngineStatus.ENGINE_START);
            if (getState() == State.SHUTDOWN_REQUESTED) {
                break;
            }
        }
    }


    /** @return Timestamp of end of last write run */
    @CheckForNull
    public TimeInstant getLastWriteTime() {
        return _writeExecutor.getLastWriteTime();
    }

    /** @return Average number of values per write run */
    @CheckForNull
    public Double getAvgWriteCount() {
        return _writeExecutor.getAvgWriteCount();
    }

    /** @return  Average duration of write run in milliseconds */
    @CheckForNull
    public Duration getAvgWriteDuration() {
        return _writeExecutor.getAvgWriteDurationInMS();
    }

    /** Ask the model to stop.
     *  Merely updates the model state.
     *  @see #getState()
     */
    public void requestStop() {
        _state = State.SHUTDOWN_REQUESTED;
    }

    /** Ask the model to restart.
     *  Merely updates the model state.
     *  @see #getState()
     */
    public void requestRestart() {
        _state = State.RESTART_REQUESTED;
    }

    /** Reset engine statistics */
    public void resetStats() {
        _writeExecutor.reset();
        synchronized (this) {
            for (final ArchiveChannel<?, ?> channel : _channelMap.values()) {
                channel.reset();
            }
        }
    }

    /**
     * Stop monitoring the channels, flush the write buffers.
     * @throws EngineModelException
     */
    @SuppressWarnings("nls")
    public void stop() throws EngineModelException {
        _state = State.STOPPING;
        // Disconnect from network
        LOG.info("Stopping archive groups");
        for (final ArchiveGroup group : _groupMap.values()) {
            group.stop(ArchiveEngineStatus.ENGINE_STOP);
        }

        try {
            _provider.getEngineFacade().writeEngineStatusInformation(_engine.getId(),
                                                                     EngineMonitorStatus.OFF,
                                                                     TimeInstantBuilder.fromNow(),
                                                                     "Graceful Shutdown");
        } catch (final Exception e) {
            handleExceptions(e);
        }

        LOG.info("Shutting down writer");
        _writeExecutor.shutdown();

        // Update state
        _state = State.IDLE;
        _startTime = null;
    }


    /** Read configuration of model from RDB.
     *  @param port Current HTTPD port
     * @throws EngineModelException
     */
    @SuppressWarnings("nls")
    public void readConfig(final int port) throws EngineModelException {
        try {
            if (_state != State.IDLE) {
                LOG.error("Read configuration while state " + _state + ". Should be " + State.IDLE);
                return;
            }

            _engine = findEngineConfByName(port, _provider);

            _writeExecutor = new WriteExecutor(_provider, _engine.getId());

            final IArchiveEngineFacade service = _provider.getEngineFacade();

            final Collection<IArchiveChannelGroup> groups =
                service.getGroupsForEngine(_engine.getId());

            for (final IArchiveChannelGroup groupCfg : groups) {
                configureGroup(_provider, groupCfg, _writeExecutor, _channelMap);
            }
        } catch (final Exception e) {
            handleExceptions(e);
        }
    }

    @Nonnull
    private IArchiveEngine findEngineConfByName(final int port,
                                                @Nonnull final IServiceProvider provider)
                                                throws ArchiveServiceException,
                                                       MalformedURLException,
                                                       EngineModelException,
                                                       OsgiServiceUnavailableException {
        final IArchiveEngine engine = provider.getEngineFacade().findEngine(_name);
        if (engine == null) {
            throw new EngineModelException("Unknown engine '" + _name + "'.", null);
        }
        // Is the configuration consistent?
        if (engine.getUrl().getPort() != port) {
            throw new EngineModelException("Engine " + _name + " running on port " + port +
                                           " while configuration requires " + _engine.getUrl().toString(), null);
        }
        return engine;
    }

    private void configureGroup(@Nonnull final IServiceProvider provider,
                                @Nonnull final IArchiveChannelGroup groupCfg,
                                @Nonnull final WriteExecutor writeExecutor,
                                @Nonnull final ConcurrentMap<String, ArchiveChannel<?, ?>> channelMap)
                                throws ArchiveServiceException,
                                       TypeSupportException,
                                       OsgiServiceUnavailableException {
        final ArchiveGroup group = addGroup(groupCfg);

        final Collection<IArchiveChannel> channelCfgs =
            provider.getEngineFacade().getChannelsByGroupId(groupCfg.getId());

        for (final IArchiveChannel channelCfg : channelCfgs) {

            final ArchiveChannel<Object, ISystemVariable<Object>> channel =
                ArchiveEngineTypeSupport.toArchiveChannel(channelCfg);
            channel.setServiceProvider(provider);

            writeExecutor.addChannel(channel);

            channelMap.putIfAbsent(channel.getName(), channel);

            group.add(channel);
        }
    }

    private void handleExceptions(@Nonnull final Exception inE) throws EngineModelException {
        final String msg = "Failure during archive engine configuration retrieval: ";
        try {
            throw inE;
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException(msg + "Service unavailable.", e);
        } catch (final ArchiveServiceException e) {
            throw new EngineModelException(msg + "Internal service exception.", e);
        } catch (final MalformedURLException e) {
            throw new EngineModelException(msg + "Engine url malformed.", e);
        } catch (final TypeSupportException e) {
            throw new EngineModelException(msg + "Channel type not supported.", e);
        } catch (final EngineModelException eme) {
            throw eme;
        } catch (final Exception re) {
            throw new EngineModelException("Unknown exception: ", re);
        }
    }

    /** Remove all channels and groups. */
    @SuppressWarnings("nls")
    public void clearConfig() {
        if (_state != State.IDLE) {
            throw new IllegalStateException("Only allowed in IDLE state");
        }
        _name = null;
        _engine = null;
        _groupMap.clear();
        _channelMap.clear();
    }

    /** Write debug info to stdout */
    @SuppressWarnings("nls")
    public void dumpDebugInfo() {
        System.out.println(TimestampFactory.now().toString() + ": Debug info");
        for (final ArchiveChannel<?, ?> channel : _channelMap.values()) {
            final StringBuilder buf = new StringBuilder();
            buf.append("'" + channel.getName() + "' (");
            //buf.append(Joiner.on(",").join(channel.getGroups()));
            buf.append("): ");
            buf.append(channel.getMechanism());

            buf.append(channel.isConnected() ? ", connected (" : ", DISCONNECTED (");
            buf.append(channel.getInternalState() + ")");
            final Object mostRecentValue = channel.getMostRecentSample();
            buf.append(", value " + mostRecentValue == null ? "null" : mostRecentValue);
            final Object lastArchivedValue = channel.getLastArchivedSample();
            buf.append(", last stored " + lastArchivedValue == null ? "null" : lastArchivedValue);
            System.out.println(buf.toString());
        }
    }

    @Nonnull
    public static String getVersion() {
        return VERSION;
    }

}
