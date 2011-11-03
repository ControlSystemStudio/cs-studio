/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.channelstatus.ArchiveChannelStatus;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.enginestatus.ArchiveEngineStatus;
import org.csstudio.archive.common.service.enginestatus.EngineMonitorStatus;
import org.csstudio.archive.common.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.common.service.mysqlimpl.channelgroup.IArchiveChannelGroupDao;
import org.csstudio.archive.common.service.mysqlimpl.channelstatus.IArchiveChannelStatusDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.enginestatus.IArchiveEngineStatusDao;
import org.csstudio.archive.common.service.mysqlimpl.sample.IArchiveSampleDao;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;

import com.google.inject.Inject;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 28.09.2011
 */
public class MysqlArchiveCreationServiceSupport {

    /**
     * Injected by GUICE construction.
     */
    private final IArchiveEngineStatusDao _mgmtDao;
    private final IArchiveSampleDao _sampleDao;
    private final IArchiveChannelDao _channelDao;
    private final IArchiveChannelGroupDao _channelGroupDao;
    private final IArchiveChannelStatusDao _channelStatusDao;

    /**
     * Constructor.
     */
    @Inject
    public MysqlArchiveCreationServiceSupport(@Nonnull final IArchiveEngineStatusDao mgmtDao,
                                              @Nonnull final IArchiveSampleDao sampleDao,
                                              @Nonnull final IArchiveChannelDao channelDao,
                                              @Nonnull final IArchiveChannelGroupDao channelGroupDao,
                                              @Nonnull final IArchiveChannelStatusDao channelStatusDao) {
        _mgmtDao = mgmtDao;
        _sampleDao = sampleDao;
        _channelDao = channelDao;
        _channelGroupDao = channelGroupDao;
        _channelStatusDao = channelStatusDao;
    }

    <V extends Serializable, T extends ISystemVariable<V>>
    boolean createSamples(@Nonnull final Collection<IArchiveSample<V, T>> samples) throws ArchiveServiceException {
        try {
            _sampleDao.createSamples(samples);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Creation of samples failed.", e);
        }
        return true;
    }

    public void createChannelStatusInfo(@Nonnull final ArchiveChannelId id,
                                       @Nonnull final boolean connected,
                                       @Nonnull final String info,
                                       @Nonnull final TimeInstant timestamp) throws ArchiveServiceException {
        try {
            _channelStatusDao.createChannelStatus(new ArchiveChannelStatus(id, connected, info, timestamp));
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Creation of channel status entry failed.", e);
        }
    }

    public void createEngineStatusInformation(@Nonnull final ArchiveEngineId engineId,
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
     * Tries to create the channel specified in the parameter, returns the channel if it could
     * <em>not</em> be created.
     * @param channel the channel to be created
     * @return null on success, otherwise the channel that could not be created
     * @throws ArchiveDaoException
     */
    @CheckForNull
    public IArchiveChannel createChannel(@Nonnull final IArchiveChannel channel) throws ArchiveServiceException {
        final Collection<IArchiveChannel> coll = createChannels(Collections.singleton(channel));
        return coll.isEmpty() ? null : channel;
    }

    /**
     * Tries to create all the channels specified in the parameter collection, returns a collection
     * of those channels that could <em>not</em> be created.
     * @param channels the channels to be created
     * @return empty list on success, otherwise those channels that could not be created
     * @throws ArchiveDaoException
     */
    @Nonnull
    public Collection<IArchiveChannel> createChannels(@Nonnull final Collection<IArchiveChannel> channels) throws ArchiveServiceException {
        try {
            return _channelDao.createChannels(channels);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Creation of channel(s) failed.", e);
        }
    }

    /**
     * Tries to create the given group. Returns <code>null</code> on success, otherwise the
     * group that has been failed to be added.
     * @param group
     * @return <code>null</code> on success, and the group on failure
     * @throws ArchiveServiceException
     */
    @CheckForNull
    public IArchiveChannelGroup createGroup(@Nonnull final IArchiveChannelGroup group) throws ArchiveServiceException {
        final Collection<IArchiveChannelGroup> coll = createGroups(Collections.singleton(group));
        return coll.isEmpty() ? group : null;
    }

    /**
     * Tries to create all the groups specified in the parameter collection, returns a collection
     * of those groups that could <em>not</em> be created.
     * @param groups the groups to be created
     * @return empty list on success, otherwise those groups that could not be created
     * @throws ArchiveServiceException
     * @throws ArchiveDaoException
     */
    @Nonnull
    public Collection<IArchiveChannelGroup> createGroups(@Nonnull final Collection<IArchiveChannelGroup> groups) throws ArchiveServiceException {
        try {
            return _channelGroupDao.createGroups(groups);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Creation of group(s) failed.", e);
        }
    }

}
