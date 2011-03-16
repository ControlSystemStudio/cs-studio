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

import org.apache.log4j.Logger;
import org.csstudio.archive.common.engine.Activator;
import org.csstudio.archive.common.engine.ArchiveEnginePreference;
import org.csstudio.archive.common.engine.types.ArchiveEngineTypeSupport;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.archivermgmt.ArchiverMgmtEntry;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.joda.time.Duration;

import com.google.common.collect.MapMaker;

/** Data model of the archive engine.
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 */
public final class EngineModel {
    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(EngineModel.class);

    /** Version code. See also webroot/version.html */
    public static String VERSION = "1.0.0";

    /** Name of this model */
    private String _name = "DESY Archive Engine";  //$NON-NLS-1$

    /** Thread that writes to the <code>archive</code> */
    private final WriteExecutor _writeExecutor;

    /**
     * All channels
     */
    private final ConcurrentMap<String, AbstractArchiveChannel<?, ?>> _channelMap;

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

    /** Write period in seconds */
    private final long _writePeriodInMS;

    private IArchiveEngine _engine;

    /**
     * Construct model that writes to archive
     */
    public EngineModel() {

        _groupMap = new MapMaker().concurrencyLevel(2).makeMap();
        _channelMap = new MapMaker().concurrencyLevel(2).makeMap();

        _writePeriodInMS = 1000*ArchiveEnginePreference.WRITE_PERIOD.getValue();

        _writeExecutor = new WriteExecutor();
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
        _groupMap.putIfAbsent(groupName, new ArchiveGroup(groupName, groupCfg.getId().longValue()));
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
    public AbstractArchiveChannel<?, ?> getChannel(@Nonnull final String name) {
        return _channelMap.get(name);
    }

    /** @return Channel by that name or <code>null</code> if not found */
    @Nonnull
    public Collection<AbstractArchiveChannel<?, ?>> getChannels() {
        return _channelMap.values();
    }

    /**
     * Start processing all channels and writing to archive.
     * @throws EngineModelException
     */
    public void start() throws EngineModelException {

        _startTime = TimeInstantBuilder.buildFromNow();
        _state = State.RUNNING;
        _writeExecutor.start(_writePeriodInMS);

        for (final ArchiveGroup group : _groupMap.values()) {
            group.start(_engine.getId(),
                        ArchiverMgmtEntry.ARCHIVER_START);
            // Check for stop request.
            if (_state == State.SHUTDOWN_REQUESTED) {
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
        return _writeExecutor.getAvgWriteDuration();
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
            for (final AbstractArchiveChannel<?, ?> channel : _channelMap.values()) {
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
            group.stop(_engine.getId(), ArchiverMgmtEntry.ARCHIVER_STOP);
        }

        LOG.info("Shutting down writer");
        _writeExecutor.shutdown();

        // Update state
        _state = State.IDLE;
        _startTime = null;
    }


    /** Read configuration of model from RDB.
     *  @param p_name Name of engine in RDB
     *  @param port Current HTTPD port
     * @throws EngineModelException
     */
    @SuppressWarnings("nls")
    public void readConfig(@Nonnull final String engineName, final int port) throws EngineModelException {
        try {
            if (_state != State.IDLE) {
                LOG.error("Read configuration while state " + _state + ". Should be " + State.IDLE);
                return;
            }
            _name = engineName;

            final IArchiveEngineFacade configService = Activator.getDefault().getArchiveEngineService();

            _engine = configService.findEngine(_name);
            if (_engine == null) {
                LOG.error("Unknown engine '" + _name + "'.");
                return;
            }
            // Is the configuration consistent?
            if (_engine.getUrl().getPort() != port) {
                LOG.error("Engine " + _name + " running on port " + port +
                          " while configuration requires " + _engine.getUrl().toString());
                return;
            }

            final Collection<IArchiveChannelGroup> groups =
                configService.getGroupsForEngine(_engine.getId());

            for (final IArchiveChannelGroup groupCfg : groups) {
                configureGroup(configService, groupCfg);
            }
        } catch (final Exception e) {
            handleExceptions(e);
        }
    }

    private void configureGroup(@Nonnull final IArchiveEngineFacade configService,
                                @Nonnull final IArchiveChannelGroup groupCfg) throws ArchiveServiceException,
                                                                                     TypeSupportException {
        final ArchiveGroup group = addGroup(groupCfg);
        // Add channels to group
        final Collection<IArchiveChannel> channelCfgs =
            configService.getChannelsByGroupId(groupCfg.getId());

        for (final IArchiveChannel channelCfg : channelCfgs) {

            final AbstractArchiveChannel<Object, IAlarmSystemVariable<Object>> channel =
                ArchiveEngineTypeSupport.toArchiveChannel(channelCfg);

            _writeExecutor.addChannel(channel);

            _channelMap.putIfAbsent(channel.getName(), channel);
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
        } catch (final Exception re) {
            throw new RuntimeException(re);
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
        for (final AbstractArchiveChannel<?, ?> channel : _channelMap.values()) {
            final StringBuilder buf = new StringBuilder();
            buf.append("'" + channel.getName() + "' (");
            //buf.append(Joiner.on(",").join(channel.getGroups()));
            buf.append("): ");
            buf.append(channel.getMechanism());

            buf.append(channel.isConnected() ? ", connected (" : ", DISCONNECTED (");
            buf.append(channel.getInternalState() + ")");
            buf.append(", value " + channel.getCurrentValueAsString());
            buf.append(", last stored " + channel.getLastArchivedValue());
            System.out.println(buf.toString());
        }
    }

}
