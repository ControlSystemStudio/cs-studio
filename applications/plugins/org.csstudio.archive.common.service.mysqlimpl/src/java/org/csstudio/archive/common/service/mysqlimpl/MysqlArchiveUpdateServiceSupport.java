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

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.engine.IArchiveEngineDao;
import org.csstudio.domain.common.service.UpdateResult;
import org.csstudio.domain.desy.time.TimeInstant;

import com.google.inject.Inject;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 28.09.2011
 */
public class MysqlArchiveUpdateServiceSupport {

    /**
     * Injected by GUICE construction.
     */
    private final IArchiveEngineDao _engineDao;
    private final IArchiveChannelDao _channelDao;

    /**
     * Constructor.
     */
    @Inject
    public MysqlArchiveUpdateServiceSupport(@Nonnull final IArchiveEngineDao engineDao,
                                            @Nonnull final IArchiveChannelDao channelDao) {
        _engineDao = engineDao;
        _channelDao = channelDao;
    }

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

    public <V extends Comparable<? super V> & Serializable>
    void updateChannelDisplayRangeInfo(@Nonnull final ArchiveChannelId id,
                                      @Nonnull final V displayLow,
                                      @Nonnull final V displayHigh) throws ArchiveServiceException {
        try {
            _channelDao.updateDisplayRanges(id, displayLow, displayHigh);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Channel info for " + id +
                                              " could not be updated.", e);
        }
    }

    @Nonnull
    public UpdateResult updateChannelIsEnabledFlag(@Nonnull final String name, final boolean isEnabled) {
        return _channelDao.updateChannelEnabledFlag(name, isEnabled);
    }

    @Nonnull
    public UpdateResult updateChannelDataType(@Nonnull final ArchiveChannelId id,
                                      @Nonnull final String datatype) {
        return _channelDao.updateChannelDatatype(id, datatype);
    }
}
