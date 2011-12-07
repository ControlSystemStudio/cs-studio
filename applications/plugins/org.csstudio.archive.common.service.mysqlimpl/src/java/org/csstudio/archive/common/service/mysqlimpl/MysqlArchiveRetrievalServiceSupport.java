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
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.requesttype.IArchiveRequestType;
import org.csstudio.archive.common.requesttype.RequestTypeParameterException;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.channelstatus.IArchiveChannelStatus;
import org.csstudio.archive.common.service.controlsystem.IArchiveControlSystem;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.archive.common.service.enginestatus.IArchiveEngineStatus;
import org.csstudio.archive.common.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.common.service.mysqlimpl.channelgroup.IArchiveChannelGroupDao;
import org.csstudio.archive.common.service.mysqlimpl.channelstatus.IArchiveChannelStatusDao;
import org.csstudio.archive.common.service.mysqlimpl.controlsystem.IArchiveControlSystemDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.engine.IArchiveEngineDao;
import org.csstudio.archive.common.service.mysqlimpl.enginestatus.IArchiveEngineStatusDao;
import org.csstudio.archive.common.service.mysqlimpl.requesttypes.DesyArchiveRequestType;
import org.csstudio.archive.common.service.mysqlimpl.sample.IArchiveSampleDao;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.Limits;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 28.09.2011
 */
public class MysqlArchiveRetrievalServiceSupport {

    /**
     * Injected by GUICE construction.
     */
    private final IArchiveEngineStatusDao _mgmtDao;
    private final IArchiveEngineDao _engineDao;
    private final IArchiveSampleDao _sampleDao;
    private final IArchiveChannelDao _channelDao;
    private final IArchiveChannelGroupDao _channelGroupDao;
    private final IArchiveChannelStatusDao _channelStatusDao;
    private final IArchiveControlSystemDao _controlSystemDao;


    /**
     * Constructor.
     */
    @Inject
    public MysqlArchiveRetrievalServiceSupport(@Nonnull final IArchiveEngineStatusDao mgmtDao,
                                               @Nonnull final IArchiveEngineDao engineDao,
                                               @Nonnull final IArchiveSampleDao sampleDao,
                                               @Nonnull final IArchiveChannelDao channelDao,
                                               @Nonnull final IArchiveChannelGroupDao channelGroupDao,
                                               @Nonnull final IArchiveChannelStatusDao channelStatusDao,
                                               @Nonnull final IArchiveControlSystemDao controlSystemDao) {
        _mgmtDao = mgmtDao;
        _engineDao = engineDao;
        _sampleDao = sampleDao;
        _channelDao = channelDao;
        _channelGroupDao = channelGroupDao;
        _channelStatusDao = channelStatusDao;
        _controlSystemDao = controlSystemDao;
    }

    @CheckForNull
    public IArchiveEngine retrieveEngine(@Nonnull final String name) throws ArchiveServiceException {
        try {
            return _engineDao.retrieveEngineByName(name);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Engine information for " + name +
                                              " could not be retrieved.", e);
        }
    }

    @Nonnull
    public Collection<IArchiveChannelGroup> retrieveGroupsForEngine(@Nonnull final ArchiveEngineId id)
                                                               throws ArchiveServiceException {
        try {
            return _channelGroupDao.retrieveGroupsByEngineId(id);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Groups for engine " + id.asString() +
                                              " could not be retrieved.", e);
        }
    }

    @Nonnull
    public Collection<IArchiveChannel> retrieveChannelsByGroupId(@Nonnull final ArchiveChannelGroupId groupId)
                                                            throws ArchiveServiceException {
        try {
            return _channelDao.retrieveChannelsByGroupId(groupId);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Channels for group " + groupId.asString() +
                                              " could not be retrieved.", e);
        }
    }

    @CheckForNull
    public IArchiveEngineStatus retrieveLatestEngineStatusInformation(@Nonnull final ArchiveEngineId id,
                                                                 @Nonnull final TimeInstant latestAliveTime)
                                                                 throws ArchiveServiceException {
        try {
            return _mgmtDao.retrieveLastEngineStatus(id, latestAliveTime);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Engine status info for " + id +
                                              " could not be retrieved.", e);
        }
    }

    @Nonnull
    public Collection<IArchiveChannelStatus> retrieveLatestChannelsStatusForChannels(@Nonnull final Collection<ArchiveChannelId> channels,
                                                                                     @Nonnull final TimeInstant start,
                                                                                     @Nonnull final TimeInstant end) throws ArchiveServiceException {
        try {
            return _channelStatusDao.retrieveLatestStatusByChannelIds(channels, start, end);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Multiple latest channel status could not be retrieved.", e);
        }
    }

    @CheckForNull
    public IArchiveControlSystem retrieveControlSystemByName(@Nonnull final String name) throws ArchiveServiceException {
        try {
            return _controlSystemDao.retrieveControlSystemByName(name);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Control system retrieval failed for: " + name, e);
        }
    }

    @CheckForNull
    public IArchiveChannel retrieveChannelByName(@Nonnull final String name) throws ArchiveServiceException {
        try {
            final Collection<IArchiveChannel> channels =
                _channelDao.retrieveChannelsByNames(Sets.newHashSet(name));
            if (channels.isEmpty()) {
                return null;
            }
            return channels.iterator().next();
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Channel retrieval failed for " + name, e);
        }
    }

    @Nonnull
    public <V extends Serializable, T extends ISystemVariable<V>>
    Collection<IArchiveSample<V, T>> retrieveSamples(@Nonnull final String channelName,
                                                     @Nonnull final TimeInstant start,
                                                     @Nonnull final TimeInstant end,
                                                     @Nullable final IArchiveRequestType type) throws ArchiveServiceException {
        try {
            final DesyArchiveRequestType desyType = validateRequestType(type);

            final IArchiveChannel channel = retrieveChannelByName(channelName);
            if (channel == null) {
                throw new ArchiveDaoException("Information for channel " + channelName + " could not be retrieved.", null);
            }

            final Collection<IArchiveSample<V, T>> samples =
                _sampleDao.retrieveSamples(desyType, channel, start, end);

            return samples;

        } catch (final ArchiveDaoException ade) {
            throw new ArchiveServiceException("Sample retrieval failed.", ade);
        } catch (final RequestTypeParameterException re) {
            throw new ArchiveServiceException("Sample retrieval failed.", re);
        }
    }

    @CheckForNull
    private DesyArchiveRequestType validateRequestType(@CheckForNull final IArchiveRequestType type) throws RequestTypeParameterException {
        try {
            return DesyArchiveRequestType.class.cast(type);
        } catch(final ClassCastException cce) {
            throw new RequestTypeParameterException("Request type is not the correct type instance!" +
                                                    " Use one the type instances returned by the service interface or null", cce);
        }
    }

    @CheckForNull
    public <V extends Serializable, T extends ISystemVariable<V>>
    IArchiveSample<V, T> retrieveLastSampleBefore(@Nonnull final String channelName,
                                                  @Nonnull final TimeInstant time) throws ArchiveServiceException {
        try {
            final IArchiveChannel channel = retrieveChannelByName(channelName);
            if (channel != null) {
                return _sampleDao.retrieveLatestSampleBeforeTime(channel, time);
            }
        } catch (final ArchiveDaoException ade) {
            throw new ArchiveServiceException("Latest sample retrieval failed for channel " + channelName, ade);
        }
        return null;
    }

    @CheckForNull
    public Collection<String> retrieveChannelsByNamePattern(@Nonnull final Pattern pattern)
    throws ArchiveServiceException {
        try {
            final Collection<IArchiveChannel> channels =
                _channelDao.retrieveChannelsByNamePattern(pattern);
            final Collection<String> names =
                Collections2.transform(channels,
                                       new Function<IArchiveChannel, String>() {
                                           @Override
                                           @Nonnull
                                           public String apply(@Nonnull final IArchiveChannel channel) {
                                               return channel.getName();
                                           }
                                       });
            return names;
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Channel information for pattern " + pattern.toString() +
                                              " could not be retrieved.", e);
        }
    }

    @CheckForNull
    public Limits<?> retrieveDisplayLimits(@Nonnull final String channelName) throws ArchiveServiceException {
        try {
            return _channelDao.retrieveDisplayRanges(channelName);
        } catch (final ArchiveDaoException ade) {
            throw new ArchiveServiceException("Channel retrieval failed.", ade);
        }
    }

}
