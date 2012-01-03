/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.archive.common.service.mysqlimpl;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.requesttype.IArchiveRequestType;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.channelstatus.IArchiveChannelStatus;
import org.csstudio.archive.common.service.controlsystem.IArchiveControlSystem;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.archive.common.service.enginestatus.EngineMonitorStatus;
import org.csstudio.archive.common.service.enginestatus.IArchiveEngineStatus;
import org.csstudio.archive.common.service.mysqlimpl.requesttypes.DesyArchiveRequestType;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.common.service.DeleteResult;
import org.csstudio.domain.common.service.UpdateResult;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.Limits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;


/**
 * Archive service implementation to separate the processing and logic layer from
 * the data access layer.
 *
 * Uses DAO design pattern with Guice Injection.
 * TODO (bknerr) : CRUD command pattern
 *
 * @author bknerr
 * @since 01.11.2010
 */
public class MySQLArchiveServiceImpl implements IArchiveEngineFacade, IArchiveReaderFacade {

    static final Logger LOG = LoggerFactory.getLogger(MySQLArchiveServiceImpl.class);

    /**
     * Injected by GUICE construction.
     */
    private final MysqlArchiveCreationServiceSupport _createSupport;
    private final MysqlArchiveRetrievalServiceSupport _retrievalSupport;
    private final MysqlArchiveUpdateServiceSupport _updateSupport;
    private final MysqlArchiveDeleteServiceSupport _deleteSupport;


    /**
     * Constructor.
     */
    @Inject
    public MySQLArchiveServiceImpl(@Nonnull final MysqlArchiveCreationServiceSupport createSupport,
                                   @Nonnull final MysqlArchiveRetrievalServiceSupport retrieveSupport,
                                   @Nonnull final MysqlArchiveUpdateServiceSupport updateSupport,
                                   @Nonnull final MysqlArchiveDeleteServiceSupport deleteSupport) {
        _createSupport = createSupport;
        _retrievalSupport = retrieveSupport;
        _updateSupport = updateSupport;
        _deleteSupport = deleteSupport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ImmutableSet<IArchiveRequestType> getRequestTypes() {
        return ImmutableSet.<IArchiveRequestType>builder().addAll(EnumSet.allOf(DesyArchiveRequestType.class)).build();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <V extends Serializable, T extends ISystemVariable<V>>
    boolean writeSamples(@Nonnull final Collection<IArchiveSample<V, T>> samples) throws ArchiveServiceException {
        return _createSupport.createSamples(samples);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeChannelStatusInfo(@Nonnull final ArchiveChannelId id,
                                       @Nonnull final boolean connected,
                                       @Nonnull final String info,
                                       @Nonnull final TimeInstant timestamp) throws ArchiveServiceException {
        _createSupport.createChannelStatusInfo(id, connected, info, timestamp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeEngineStatusInformation(@Nonnull final ArchiveEngineId engineId,
                                             @Nonnull final EngineMonitorStatus status,
                                             @Nonnull final TimeInstant time,
                                             @Nonnull final String info) throws ArchiveServiceException {
        _createSupport.createEngineStatusInformation(engineId, status, time, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveEngine findEngine(@Nonnull final String name) throws ArchiveServiceException {
        return _retrievalSupport.retrieveEngine(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEngineIsAlive(@Nonnull final ArchiveEngineId id,
                                    @Nonnull final TimeInstant lastTimeAlive)
                                    throws ArchiveServiceException {
        _updateSupport.updateEngineIsAlive(id, lastTimeAlive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannelGroup> getGroupsForEngine(@Nonnull final ArchiveEngineId id)
                                                               throws ArchiveServiceException {
        return _retrievalSupport.retrieveGroupsForEngine(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannel> getChannelsByGroupId(@Nonnull final ArchiveChannelGroupId groupId)
                                                            throws ArchiveServiceException {
        return _retrievalSupport.retrieveChannelsByGroupId(groupId);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <V extends Comparable<? super V> & Serializable>
    void writeChannelDisplayRangeInfo(@Nonnull final ArchiveChannelId id,
                                      @Nonnull final V displayLow,
                                      @Nonnull final V displayHigh) throws ArchiveServiceException {
        _updateSupport.updateChannelDisplayRangeInfo(id, displayLow, displayHigh);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeChannelDataTypeInfo(@Nonnull final ArchiveChannelId id,
                                         @Nonnull final String datatype) throws ArchiveServiceException {
        _updateSupport.updateChannelDataType(id, datatype);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveEngineStatus getLatestEngineStatusInformation(@Nonnull final ArchiveEngineId id,
                                                                 @Nonnull final TimeInstant latestAliveTime)
                                                                 throws ArchiveServiceException {
        return _retrievalSupport.retrieveLatestEngineStatusInformation(id, latestAliveTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannelStatus> getLatestChannelsStatusBy(@Nonnull final Collection<ArchiveChannelId> channels,
                                                                       @Nonnull final TimeInstant start,
                                                                       @Nonnull final TimeInstant end) throws ArchiveServiceException {
        return _retrievalSupport.retrieveLatestChannelsStatusForChannels(channels, start, end);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveChannel createChannel(@Nonnull final IArchiveChannel channel) throws ArchiveServiceException {
        return _createSupport.createChannel(channel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannel> createChannels(@Nonnull final Collection<IArchiveChannel> channels) throws ArchiveServiceException {
        return _createSupport.createChannels(channels);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveControlSystem getControlSystemByName(@Nonnull final String name) throws ArchiveServiceException {
        return _retrievalSupport.retrieveControlSystemByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveChannel getChannelByName(@Nonnull final String name) throws ArchiveServiceException {
        return _retrievalSupport.retrieveChannelByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public <V extends Serializable, T extends ISystemVariable<V>>
    Collection<IArchiveSample<V, T>> readSamples(@Nonnull final String channelName,
                                                 @Nonnull final TimeInstant start,
                                                 @Nonnull final TimeInstant end) throws ArchiveServiceException {
        return readSamples(channelName, start, end, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public <V extends Serializable, T extends ISystemVariable<V>>
    Collection<IArchiveSample<V, T>> readSamples(@Nonnull final String channelName,
                                                 @Nonnull final TimeInstant start,
                                                 @Nonnull final TimeInstant end,
                                                 @Nullable final IArchiveRequestType type) throws ArchiveServiceException {
        return _retrievalSupport.retrieveSamples(channelName, start, end, type);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public <V extends Serializable, T extends ISystemVariable<V>>
    IArchiveSample<V, T> readLastSampleBefore(@Nonnull final String channelName,
                                              @Nonnull final TimeInstant time) throws ArchiveServiceException {
        return _retrievalSupport.retrieveLastSampleBefore(channelName, time);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public Collection<String> getChannelsByNamePattern(@Nonnull final Pattern pattern)
    throws ArchiveServiceException {
        return _retrievalSupport.retrieveChannelsByNamePattern(pattern);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public Limits<?> readDisplayLimits(@Nonnull final String channelName) throws ArchiveServiceException {
        return _retrievalSupport.retrieveDisplayLimits(channelName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public DeleteResult removeChannel(@Nonnull final String name) throws ArchiveServiceException {
        return _deleteSupport.deleteChannel(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveChannelGroup createGroup(@Nonnull final IArchiveChannelGroup group) throws ArchiveServiceException {
        return _createSupport.createGroup(group);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannelGroup> createGroups(@Nonnull final Collection<IArchiveChannelGroup> groups) throws ArchiveServiceException {
        return _createSupport.createGroups(groups);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public UpdateResult setEnableChannelFlag(@Nonnull final String name, final boolean isEnabled) {
        return _updateSupport.updateChannelIsEnabledFlag(name, isEnabled);
    }
}
