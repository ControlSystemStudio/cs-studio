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
import org.csstudio.archive.common.service.channelstatus.ArchiveChannelStatus;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.enginestatus.ArchiveEngineStatus;
import org.csstudio.archive.common.service.enginestatus.EngineMonitorStatus;
import org.csstudio.archive.common.service.mysqlimpl.channel.IArchiveChannelDao;
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
    private final IArchiveChannelStatusDao _channelStatusDao;

    /**
     * Constructor.
     */
    @Inject
    public MysqlArchiveCreationServiceSupport(@Nonnull final IArchiveEngineStatusDao mgmtDao,
                                              @Nonnull final IArchiveSampleDao sampleDao,
                                              @Nonnull final IArchiveChannelDao channelDao,
                                              @Nonnull final IArchiveChannelStatusDao channelStatusDao) {
        _mgmtDao = mgmtDao;
        _sampleDao = sampleDao;
        _channelDao = channelDao;
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

    @CheckForNull
    public IArchiveChannel createChannel(@Nonnull final IArchiveChannel channel) throws ArchiveServiceException {
        final Collection<IArchiveChannel> coll = createChannels(Collections.singleton(channel));
        return coll.isEmpty() ? channel : null;
    }


    @Nonnull
    public Collection<IArchiveChannel> createChannels(@Nonnull final Collection<IArchiveChannel> channels) throws ArchiveServiceException {
        try {
            return _channelDao.createChannels(channels);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Creation of channels failed.", e);
        }
    }

}
