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
import java.util.Collections;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.channelstatus.ArchiveChannelStatus;
import org.csstudio.archive.common.service.channelstatus.IArchiveChannelStatus;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.archive.common.service.enginestatus.ArchiveEngineStatus;
import org.csstudio.archive.common.service.enginestatus.EngineMonitorStatus;
import org.csstudio.archive.common.service.enginestatus.IArchiveEngineStatus;
import org.csstudio.archive.common.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.common.service.mysqlimpl.channelgroup.IArchiveChannelGroupDao;
import org.csstudio.archive.common.service.mysqlimpl.channelstatus.IArchiveChannelStatusDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.engine.IArchiveEngineDao;
import org.csstudio.archive.common.service.mysqlimpl.enginestatus.IArchiveEngineStatusDao;
import org.csstudio.archive.common.service.mysqlimpl.sample.IArchiveSampleDao;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class MySQLArchiveEngineServiceImpl implements IArchiveEngineFacade {

    static final Logger LOG = LoggerFactory.getLogger(MySQLArchiveEngineServiceImpl.class);

    /**
     * Injected by GUICE construction.
     */
    private final IArchiveEngineStatusDao _mgmtDao;
    private final IArchiveEngineDao _engineDao;
    private final IArchiveSampleDao _sampleDao;
    private final IArchiveChannelDao _channelDao;
    private final IArchiveChannelGroupDao _channelGroupDao;
    private final IArchiveChannelStatusDao _channelStatusDao;


    /**
     * Constructor.
     */
    @Inject
    public MySQLArchiveEngineServiceImpl(@Nonnull final IArchiveEngineStatusDao mgmtDao,
                                         @Nonnull final IArchiveEngineDao engineDao,
                                         @Nonnull final IArchiveSampleDao sampleDao,
                                         @Nonnull final IArchiveChannelDao channelDao,
                                         @Nonnull final IArchiveChannelGroupDao channelGroupDao,
                                         @Nonnull final IArchiveChannelStatusDao channelStatusDao) {
        _mgmtDao = mgmtDao;
        _engineDao = engineDao;
        _sampleDao = sampleDao;
        _channelDao = channelDao;
        _channelGroupDao = channelGroupDao;
        _channelStatusDao = channelStatusDao;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <V extends Serializable, T extends ISystemVariable<V>>
    boolean writeSamples(@Nonnull final Collection<IArchiveSample<V, T>> samples) throws ArchiveServiceException {
        try {
            _sampleDao.createSamples(samples);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Creation of samples failed.", e);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeChannelStatusInfo(@Nonnull final ArchiveChannelId id,
                                           @Nonnull final boolean connected,
                                           @Nonnull final String info,
                                           @Nonnull final TimeInstant timestamp) throws ArchiveServiceException {
        try {
            _channelStatusDao.createChannelStatus(new ArchiveChannelStatus(id, connected, info, timestamp));
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Creation of channel status entry failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeEngineStatusInformation(@Nonnull final ArchiveEngineId engineId,
                                             @Nonnull final EngineMonitorStatus status,
                                             @Nonnull final TimeInstant time,
                                             @Nonnull final String info) throws ArchiveServiceException {
        try {
            _mgmtDao.createMgmtEntry(new ArchiveEngineStatus(engineId, status, time, info));
      } catch (final ArchiveDaoException e) {
          throw new ArchiveServiceException("Creation of archiver management entry failed.", e);
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveEngine findEngine(@Nonnull final String name) throws ArchiveServiceException {
        try {
            return _engineDao.retrieveEngineByName(name);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Engine information for " + name +
                                              " could not be retrieved.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEngineIsAlive(@Nonnull final ArchiveEngineId id,
                                    @Nonnull final TimeInstant lastTimeAlive)
                                    throws ArchiveServiceException {
        try {
            _engineDao.updateEngineAlive(id, lastTimeAlive);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Engine alive time could not be updated for id " + id +
                                              ".", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannelGroup> getGroupsForEngine(@Nonnull final ArchiveEngineId id)
                                                               throws ArchiveServiceException {
        try {
            return _channelGroupDao.retrieveGroupsByEngineId(id);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Groups for engine " + id.asString() +
                                              " could not be retrieved.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannel> getChannelsByGroupId(@Nonnull final ArchiveChannelGroupId groupId)
                                                            throws ArchiveServiceException {
        try {
            return _channelDao.retrieveChannelsByGroupId(groupId);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Channels for group " + groupId.asString() +
                                              " could not be retrieved.", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <V extends Comparable<? super V> & Serializable>
    void writeChannelDisplayRangeInfo(@Nonnull final ArchiveChannelId id,
                                      @Nonnull final V displayLow,
                                      @Nonnull final V displayHigh) throws ArchiveServiceException {
        try {
            _channelDao.updateDisplayRanges(id, displayLow, displayHigh);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Channel info for " + id +
                                              " could not be updated.", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveEngineStatus getLatestEngineStatusInformation(@Nonnull final ArchiveEngineId id,
                                                                 @Nonnull final TimeInstant latestAliveTime)
                                                                 throws ArchiveServiceException {
        try {
            return _mgmtDao.retrieveLastEngineStatus(id, latestAliveTime);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Engine status info for " + id +
                                              " could not be retrieved.", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveChannelStatus getLatestChannelStatusByChannelName(@Nonnull final String name) throws ArchiveServiceException {
        try {
            final IArchiveChannel channel = _channelDao.retrieveChannelBy(name);
            if (channel != null) {
                final Collection<IArchiveChannelStatus> result = getLatestChannelsStatusBy(Collections.singleton(channel.getId()));
                if (!result.isEmpty()) {
                    return result.iterator().next();
                }
            }
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Channel status info for " + name +
                                              " could not be retrieved.", e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannelStatus> getLatestChannelsStatusBy(@Nonnull final Collection<ArchiveChannelId> channels) throws ArchiveServiceException {
        try {
            return _channelStatusDao.retrieveLatestStatusByChannelIds(channels);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Multiple channel status could not be retrieved.", e);
        }
    }
}
