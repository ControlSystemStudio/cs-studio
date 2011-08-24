/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.engine.ArchiveEnginePreference;
import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.channelstatus.IArchiveChannelStatus;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.archive.common.service.enginestatus.ArchiveEngineStatus;
import org.csstudio.archive.common.service.enginestatus.EngineMonitorStatus;
import org.csstudio.archive.common.service.enginestatus.IArchiveEngineStatus;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapMaker;

/** Data model of the archive engine.
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 */
public final class EngineModel {
    private static final Logger LOG = LoggerFactory.getLogger(EngineModel.class);

    private static final String[] ADDITIONAL_TYPE_PACKAGES =
        new String[]{
                     "org.csstudio.domain.desy.epics.types",
                     };

    /** Version code. See also webroot/version.html */
    private static String VERSION = "1.0.0";

    /** Name of this model */
    private final String _name;

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
        /** Initial model state before it has been configured */
        IDLE,
        /** Configured model state before <code>start()</code> */
        CONFIGURED,
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
    private TimeInstant _startTime;

    private final long _writePeriodInMS;
    private final long _heartBeatPeriodInMS;

    private IArchiveEngine _engine;

    private final IServiceProvider _provider;

    /**
     * Construct model that writes to archive
     * @param engineName
     * @param provider provider for services
     */
    public EngineModel(@Nonnull final String engineName,
                       @Nonnull final IServiceProvider provider) {
        _name = engineName;
        _provider = provider;

        _groupMap = new MapMaker().concurrencyLevel(2).makeMap();
        _channelMap = new MapMaker().concurrencyLevel(2).makeMap();

        _writePeriodInMS = 1000*ArchiveEnginePreference.WRITE_PERIOD_IN_S.getValue();
        _heartBeatPeriodInMS = 1000*ArchiveEnginePreference.HEARTBEAT_PERIOD_IN_S.getValue();
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
        if (_state != State.CONFIGURED) {
            throw new IllegalStateException("Engine has not been configured before start.", null);
        }
        if (_engine == null || _writeExecutor == null) {
            throw new IllegalStateException("Engine or executor are null although engine is configured.", null);
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
                                                        TimeInstantBuilder.fromNow());

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
        if (_state == State.STOPPING || _state == State.IDLE) {
            return;
        }
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

        _state = State.CONFIGURED;
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

            _engine = findEngineConfByName(_name, port, _provider);

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
        _state = State.CONFIGURED;
    }

    @Nonnull
    private IArchiveEngine findEngineConfByName(@Nonnull final String name,
                                                final int port,
                                                @Nonnull final IServiceProvider provider)
                                                throws ArchiveServiceException,
                                                       MalformedURLException,
                                                       EngineModelException,
                                                       OsgiServiceUnavailableException {
        final IArchiveEngine engine = provider.getEngineFacade().findEngine(name);
        if (engine == null) {
            throw new EngineModelException("Unknown engine '" + name + "'.", null);
        }
        // Is the configuration consistent?
        if (engine.getUrl().getPort() != port) {
            throw new EngineModelException("Engine " + name + " running on port " + port +
                                           " while configuration requires " + engine.getUrl().toString(), null);
        }
        return engine;
    }

    private void configureGroup(@Nonnull final IServiceProvider provider,
                                @Nonnull final IArchiveChannelGroup groupCfg,
                                @Nonnull final WriteExecutor writeExecutor,
                                @Nonnull final ConcurrentMap<String, ArchiveChannel<?, ?>> channelMap)
                                throws ArchiveServiceException,
                                       OsgiServiceUnavailableException,
                                       EngineModelException {
        final ArchiveGroup group = addGroup(groupCfg);

        final Collection<IArchiveChannel> channelCfgs =
            provider.getEngineFacade().getChannelsByGroupId(groupCfg.getId());

        for (final IArchiveChannel channelCfg : channelCfgs) {
            final ArchiveChannel<Serializable, ISystemVariable<Serializable>> channel =
                createArchiveChannel(channelCfg, provider);

            @SuppressWarnings("unchecked")
            final ArchiveChannel<Serializable, ISystemVariable<Serializable>> presentChannel =
                (ArchiveChannel<Serializable, ISystemVariable<Serializable>>) channelMap.putIfAbsent(channel.getName(), channel);

            if (presentChannel != null) {
                writeExecutor.addChannel(presentChannel);
                group.add(presentChannel);
            } else {
                writeExecutor.addChannel(channel);
                group.add(channel);
            }
        }
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    @Nonnull
    private ArchiveChannel<Serializable, ISystemVariable<Serializable>>
    createArchiveChannel(@Nonnull final IArchiveChannel cfg,
                         @Nonnull final IServiceProvider provider) throws EngineModelException {
        final String dataType = cfg.getDataType();
        try {
            final Class<?> typeClass =
                BaseTypeConversionSupport.createBaseTypeClassFromString(dataType,
                                                                        ADDITIONAL_TYPE_PACKAGES);

            if (!Collection.class.isAssignableFrom(typeClass)) {
                return new ArchiveChannel(cfg.getName(), cfg.getId(), typeClass, provider);
            }
            final String elemType =
                BaseTypeConversionSupport.parseForFirstNestedGenericType(dataType);
            final Class<?> elemClass = BaseTypeConversionSupport.createBaseTypeClassFromString(elemType,
                                                                                               ADDITIONAL_TYPE_PACKAGES);
            return new ArchiveChannel(cfg.getName(), cfg.getId(), typeClass, elemClass,  provider);

        } catch (final TypeSupportException e) {
            throw new EngineModelException("Datatype " + dataType + " of channel " + cfg.getName() +
                                           " could not be transformed into Class object", e);
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

    public void clearConfiguration() {
        if (_state != State.IDLE && _state != State.CONFIGURED) {
            throw new IllegalStateException("Clearing configuration only allowed in " +
                                            State.IDLE.name() + " or " + State.CONFIGURED.name() + " state.");
        }
        _engine = null;
        _groupMap.clear();
        _channelMap.clear();

        _startTime = null;

        _state = State.IDLE;
    }

    @Nonnull
    public static String getVersion() {
        return VERSION;
    }
}
