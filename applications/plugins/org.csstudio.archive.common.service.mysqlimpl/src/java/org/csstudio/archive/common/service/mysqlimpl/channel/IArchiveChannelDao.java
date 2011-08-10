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
package org.csstudio.archive.common.service.mysqlimpl.channel;

import java.io.Serializable;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.domain.desy.types.Limits;

/**
 * Dao for archive channel configurations.
 *
 * @author bknerr
 * @since 09.11.2010
 */
public interface IArchiveChannelDao {

    /**
     * @param name the name of the channel
     * @return the cached or freshly retrieved channel
     * @throws ArchiveChannelDaoException when the retrieval fails
     */
    @CheckForNull
    IArchiveChannel retrieveChannelBy(@Nonnull final String name) throws ArchiveDaoException;

    /**
     * @param pattern the regular expression the channel names have to match
     * @return the channels matching the reg exp
     * @throws ArchiveChannelDaoException when the retrieval fails
     */
    @CheckForNull
    Collection<IArchiveChannel> retrieveChannelsByNamePattern(@Nonnull final Pattern pattern) throws ArchiveDaoException;

    /**
     * @param groupId
     * @return
     * @throws ArchiveChannelDaoException when the retrieval fails
     */
    @Nonnull
    Collection<IArchiveChannel> retrieveChannelsByGroupId(@Nonnull final ArchiveChannelGroupId groupId) throws ArchiveDaoException;

    /**
     * @param id the channel id
     * @return the cached or freshly retrieved channel
     */
    @CheckForNull
    IArchiveChannel retrieveChannelById(@Nonnull final ArchiveChannelId id) throws ArchiveDaoException;


    <V extends Comparable<? super V> & Serializable>
    void updateDisplayRanges(@Nonnull final ArchiveChannelId id,
                             @Nonnull final V displayLow,
                             @Nonnull final V displayHigh) throws ArchiveDaoException;

    @CheckForNull
    <V extends Comparable<? super V>>
    Limits<V> retrieveDisplayRanges(@Nonnull final String channelName) throws ArchiveDaoException;

}
