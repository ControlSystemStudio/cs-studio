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
package org.csstudio.archive.common.service;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.channelstatus.IArchiveChannelStatus;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.archive.common.service.enginestatus.EngineMonitorStatus;
import org.csstudio.archive.common.service.enginestatus.IArchiveEngineStatus;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;

/**
 * Archive engine writer methods.
 *
 * TODO (bknerr): all database access methods should definitely return explicit immutables.
 *                Note guavas immutable collections implement 'mutable' interfaces with
 *                throwing UOEs. mmh.
 *
 * @author bknerr
 * @since 12.11.2010
 */
public interface IArchiveEngineFacade {

    /**
     * Retrieves the engine by id.
     *
     *  @param name name of engine to locate
     *  @return SampleEngineInfo or <code>null</code> when not found
     *  @throws ArchiveServiceException
     */
    @CheckForNull
    IArchiveEngine findEngine(@Nonnull final String name) throws ArchiveServiceException;

    /**
     * @param engineId
     * @return
     *  @throws ArchiveServiceException
     */
    @Nonnull
    Collection<IArchiveChannelGroup> getGroupsForEngine(@Nonnull final ArchiveEngineId id)
                                                        throws ArchiveServiceException;

    /**
     * @param group_config
     * @return the list of channels in this group
     * @throws ArchiveServiceException
     */
    @Nonnull
    Collection<IArchiveChannel> getChannelsByGroupId(@Nonnull final ArchiveChannelGroupId groupId)
                                                     throws ArchiveServiceException;

    /**
     * Writes the samples to the archive.
     *
     * @param samples the samples to be archived with their channel id
     * @return true, if the samples have been persisted
     * @throws ArchiveServiceException
     */
    <V extends Serializable, T extends ISystemVariable<V>>
    boolean writeSamples(@Nonnull final Collection<IArchiveSample<V, T>> samples)
                         throws ArchiveServiceException;


    /**
     * Writes the monitoring information of an engine for a channel.
     *
     * @throws ArchiveServiceException
     */
    void writeEngineStatusInformation(@Nonnull final ArchiveEngineId engineId,
                                      @Nonnull final EngineMonitorStatus st,
                                      @Nonnull final TimeInstant time,
                                      @Nonnull final String info) throws ArchiveServiceException;

    /**
     * Writes the channel connection information.
     *
     * @param id the id of the channel
     * @param connected whether it is connected or not
     * @param info specific information, mostly dedicated to the control system
     * @param timestamp the timestamp of the event, whether it originates
     *        from the control system or the engine is up to the invoker.
     */
    void writeChannelStatusInfo(@Nonnull final ArchiveChannelId id,
                                final boolean connected,
                                @Nonnull final String info,
                                @Nonnull final TimeInstant timestamp)
                                throws ArchiveServiceException;

    /**
     * Writes the channel display range info
     * @param id
     * @param displayLow
     * @param displayHigh
     */
    <V extends Comparable<? super V> & Serializable>
    void writeChannelDisplayRangeInfo(@Nonnull final ArchiveChannelId id,
                                      @Nonnull final V displayLow,
                                      @Nonnull final V displayHigh) throws ArchiveServiceException;

    /**
     * Updates the time information for the given archive engine.
     * @param engineId
     * @param lastTimeAlive
     */
    void updateEngineIsAlive(@Nonnull final ArchiveEngineId engineId,
                             @Nonnull final TimeInstant lastTimeAlive) throws ArchiveServiceException;

    /**
     * @param id
     * @return
     * @throws ArchiveServiceException
     */
    @Nonnull
    IArchiveEngineStatus getLatestEngineStatusInformation(@Nonnull final ArchiveEngineId id,
                                                          @Nonnull final TimeInstant latestAliveTime)
                                                          throws ArchiveServiceException;

    /**
     * @param name
     * @return
     */
    @CheckForNull
    IArchiveChannelStatus getChannelStatusByChannelName(@Nonnull final String name)
                                                        throws ArchiveServiceException;

}
