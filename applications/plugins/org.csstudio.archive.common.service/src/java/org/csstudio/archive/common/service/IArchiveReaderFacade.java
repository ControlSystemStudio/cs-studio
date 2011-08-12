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
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.requesttype.IArchiveRequestType;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.Limits;

import com.google.common.collect.ImmutableSet;

/**
 * Archive reader methods.
 *
 * TODO (bknerr): all database access methods should definitely return explicit immutables.
 *                Note, guavas immutable collections implement 'mutable' interfaces with
 *                throwing UOEs.
 *
 * @author bknerr
 * @since 21.12.2010
 */
public interface IArchiveReaderFacade {

    /**
     * Returns the supported request types.
     * If there isn't any choice offered by the implementation, then empty set should be returned.
     *
     * This set is immutable such that it is not possible to add or remove an element or change it's
     * definition. But it is possible to retrieve a type's internal parameter(s) and set its/their
     * value(s).
     *
     * @return the set of supported request types.
     */
    @Nonnull
    ImmutableSet<IArchiveRequestType> getRequestTypes();

    /**
     * Retrieves the samples from the archive for the given channel and time interval
     * @param channel_id
     * @param start
     * @param end
     * @return
     * @throws ArchiveServiceException
     */
    @Nonnull
    <V extends Serializable, T extends ISystemVariable<V>>
    Collection<IArchiveSample<V, T>> readSamples(@Nonnull final String channelName,
                                                 @Nonnull final TimeInstant start,
                                                 @Nonnull final TimeInstant end)
                                                 throws ArchiveServiceException;

    /**
     * Retrieves the samples from the archive for the given channel and time interval
     * @param channel_id
     * @param start
     * @param end
     * @param requestType
     * @return
     * @throws ArchiveServiceException
     */
    @Nonnull
    <V extends Serializable, T extends ISystemVariable<V>>
    Collection<IArchiveSample<V, T>> readSamples(@Nonnull final String channelName,
                                                 @Nonnull final TimeInstant start,
                                                 @Nonnull final TimeInstant end,
                                                 @Nullable final IArchiveRequestType type)
                                                 throws ArchiveServiceException;

    /**
     * Returns the latest sample before the specified time instant for the specified channel or
     * <code>null</code> if not present.
     *
     * @param channelName
     * @param time
     * @return
     * @throws ArchiveServiceException
     */
    @CheckForNull
    <V extends Serializable, T extends ISystemVariable<V>>
    IArchiveSample<V, T> readLastSampleBefore(@Nonnull final String channelName,
                                              @Nonnull final TimeInstant time)
                                              throws ArchiveServiceException;

    /**
     * Returns the channel information contained in the archive.
     * @param name name of the channel
     * @return the channel or <code>null</code>
     * @throws ArchiveServiceException
     */
    @CheckForNull
    IArchiveChannel getChannelByName(@Nonnull final String name) throws ArchiveServiceException;

    /**
     * Retrieves the display range information stored in the archive channel configuration.
     * @param channelName
     * @return
     * @throws ArchiveServiceException
     */
    @CheckForNull
    Limits<?> readDisplayLimits(@Nonnull final String channelName) throws ArchiveServiceException;

    /**
     * Returns a collection of channel names according to the passed name pattern
     * @param pattern the pattern the channel names shall match
     * @return the name collection
     */
    @Nonnull
    Collection<String> getChannelsByNamePattern(@Nonnull final Pattern pattern)
                                                throws ArchiveServiceException;
}
