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

import java.util.Collection;
import java.util.EnumSet;
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.requesttype.IArchiveRequestType;
import org.csstudio.archive.common.requesttype.RequestTypeParameterException;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.requesttypes.DesyArchiveRequestType;
import org.csstudio.archive.common.service.mysqlimpl.sample.IArchiveSampleDao;
import org.csstudio.archive.common.service.mysqlimpl.types.ArchiveTypeConversionSupport;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.epics.typesupport.EpicsSystemVariableSupport;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.Limits;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
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
 * @since Feb 25, 2011
 */
public class MySQLArchiveReaderServiceImpl implements IArchiveReaderFacade {

    /**
     * Injected by GUICE construction.
     */
    private final IArchiveSampleDao _sampleDao;
    private final IArchiveChannelDao _channelDao;


    /**
     * Constructor.
     */
    @Inject
    public MySQLArchiveReaderServiceImpl(@Nonnull final IArchiveChannelDao channelDao,
                                         @Nonnull final IArchiveSampleDao sampleDao) {

        _channelDao = channelDao;
        _sampleDao = sampleDao;

        ArchiveTypeConversionSupport.install();
        EpicsSystemVariableSupport.install();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ImmutableSet<IArchiveRequestType> getRequestTypes() {
        return ImmutableSet.<IArchiveRequestType>builder().addAll(EnumSet.allOf(DesyArchiveRequestType.class)).build();
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


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public <V, T extends IAlarmSystemVariable<V>>
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
    public <V, T extends ISystemVariable<V>>
    Collection<IArchiveSample<V, T>> readSamples(@Nonnull final String channelName,
                                                 @Nonnull final TimeInstant start,
                                                 @Nonnull final TimeInstant end,
                                                 @Nullable final IArchiveRequestType type) throws ArchiveServiceException {
        try {
            final DesyArchiveRequestType desyType = validateRequestType(type);

            final IArchiveChannel channel = _channelDao.retrieveChannelBy(channelName);
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

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public <V, T extends ISystemVariable<V>>
    IArchiveSample<V, T> readLastSampleBefore(@Nonnull final String channelName,
                                              @Nonnull final TimeInstant time) throws ArchiveServiceException {
        try {
            final IArchiveChannel channel = _channelDao.retrieveChannelBy(channelName);
            return _sampleDao.retrieveLatestSampleBeforeTime(channel, time);
        } catch (final ArchiveDaoException ade) {
            throw new ArchiveServiceException("Latest sample retrieval failed for channel " + channelName, ade);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveChannel getChannelByName(@Nonnull final String name)
                                            throws ArchiveServiceException {
        try {
            return _channelDao.retrieveChannelBy(name);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Channel information for " + name +
                                              " could not be retrieved.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public Collection<String> getChannelsByNamePattern(@Nonnull final Pattern pattern)
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

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public Limits<?> readDisplayLimits(@Nonnull final String channelName) throws ArchiveServiceException {
        try {
            return _channelDao.retrieveDisplayRanges(channelName);
        } catch (final ArchiveDaoException ade) {
            throw new ArchiveServiceException("Channel retrieval failed.", ade);
        }
    }
}
