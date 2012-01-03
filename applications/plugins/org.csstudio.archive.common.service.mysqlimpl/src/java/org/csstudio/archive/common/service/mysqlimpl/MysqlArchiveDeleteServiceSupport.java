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

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.common.service.mysqlimpl.channelstatus.IArchiveChannelStatusDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.domain.common.service.DeleteResult;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 30.09.2011
 */
public class MysqlArchiveDeleteServiceSupport {

    /**
     * Injected by GUICE construction.
     */
    private final IArchiveChannelDao _channelDao;
    private final IArchiveChannelStatusDao _channelStatusDao;

    /**
     * Constructor.
     */
    @Inject
    public MysqlArchiveDeleteServiceSupport(@Nonnull final IArchiveChannelDao channelDao,
                                            @Nonnull final IArchiveChannelStatusDao channelStatusDao) {
        _channelDao = channelDao;
        _channelStatusDao = channelStatusDao;
    }

    @Nonnull
    public DeleteResult deleteChannel(@Nonnull final String name) throws ArchiveServiceException {
        Collection<IArchiveChannel> channels;
        try {
            channels = _channelDao.retrieveChannelsByNames(Sets.newHashSet(name));
            if (channels.isEmpty()) {
                return DeleteResult.failed("Channel '" + name + "' does not exist.");
            }
            final IArchiveChannel channel = channels.iterator().next();
            if (channel.getLatestTimestamp() != null) {
                return DeleteResult.failed("Removal of channel '" + name + "' not possible. It has already archived samples.");
            }

            final DeleteResult statusDeletion = _channelStatusDao.deleteStatusForChannelId(channel.getId());
            if (statusDeletion.failed()) {
                return DeleteResult.failed("Removal of channel '" + name + "' failed:\n" + statusDeletion.getMessage());
            }

            return _channelDao.deleteChannel(name);

        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Deletion of channel failed.", e);
        }
    }
}
