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
package org.csstudio.archive.common.service.mysqlimpl.sample;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.requesttypes.DesyArchiveRequestType;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;

/**
 * Dao for archive samples.
 *
 * @author bknerr
 * @since 11.11.2010
 */
public interface IArchiveSampleDao {

    /**
     * Inserts the collection of sample objects into the db.
     * @param samples the sample objects
     * @throws ArchiveSampleDaoException
     */
    <V extends Serializable, T extends ISystemVariable<V>>
    void createSamples(@Nonnull final Collection<IArchiveSample<V, T>> samples) throws ArchiveDaoException;


    /**
     * Retrieves the samples in the given time period according to the request type
     * @throws ArchiveSampleDaoException
     */
    @Nonnull
    <V extends Serializable, T extends ISystemVariable<V>>
    Collection<IArchiveSample<V, T>> retrieveSamples(@Nullable DesyArchiveRequestType type,
                                                     @Nonnull IArchiveChannel channel,
                                                     @Nonnull TimeInstant start,
                                                     @Nonnull TimeInstant end) throws ArchiveDaoException;
    /**
     * Retrieves the samples in the given time period according to the request type
     * @throws ArchiveDaoException
     */
    @Nonnull
    <V extends Serializable, T extends ISystemVariable<V>>
    Collection<IArchiveSample<V, T>> retrieveSamples(@Nullable final DesyArchiveRequestType type,
                                                     @Nonnull final ArchiveChannelId channelId,
                                                     @Nonnull final TimeInstant start,
                                                     @Nonnull final TimeInstant end) throws ArchiveDaoException;

    /**
     * Retrieves the latest sample value if existing for a channel and before a given time stamp.
     * Attention, this statement may take quite some time, when the given timestamp is not equal or
     * after the timestamp of the latest known sample. If so, it defaults to
     * {@link IArchiveSampleDao#retrieveLatestSample(IArchiveChannel)} which is much faster.
     * @param channel
     * @param time
     * @return
     * @throws ArchiveDaoException
     */
    @CheckForNull
    <V extends Serializable, T extends ISystemVariable<V>>
    IArchiveSample<V, T> retrieveLatestSampleBeforeTime(@Nonnull final IArchiveChannel channel,
                                                        @Nonnull final TimeInstant time) throws ArchiveDaoException;

    /**
     * Retrieves the latest known sample if existing for a given channel.
     * @param channel
     * @return
     * @throws ArchiveDaoException
     */
    @CheckForNull
    <V extends Serializable, T extends ISystemVariable<V>>
    IArchiveSample<V, T> retrieveLatestSample(@Nonnull final IArchiveChannel channel) throws ArchiveDaoException;

    /**
     * Checks whether at least one sample exists for the given channel.
     * Either in table sample or sample_blob.
     * @param id the channel id
     * @return true if at least one sample could be found in sample or sample_blob
     * @throws ArchiveDaoException
     */
    boolean doesSampleExistForChannelId(@Nonnull final ArchiveChannelId id) throws ArchiveDaoException;


}
