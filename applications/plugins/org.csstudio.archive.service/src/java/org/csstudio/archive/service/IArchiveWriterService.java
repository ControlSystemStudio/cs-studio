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
package org.csstudio.archive.service;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.service.adapter.IValueWithChannelId;
import org.csstudio.archive.service.channel.IArchiveChannel;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 12.11.2010
 */
public interface IArchiveWriterService extends IArchiveConnectionService {

    /**
     * Writes the samples to the archive.
     *
     * FIXME (bknerr, kasemir) : the signature with separated channel id and list of IValues is
     * not cleanly defined, better having a collection of composite objects,
     * e.g. Collection<IArchiveSample<T>>. I've introduced such a composite as workaround.
     *
     * @param samples the samples to be archived with their channel id
     * @return true, if the samples have been persisted
     * @throws ArchiveServiceException
     */
    boolean writeSamples(final List<IValueWithChannelId> samples) throws ArchiveServiceException;

    /**
     * Retrieves the channel configuration from the archive with the given name.
     *
     * @param name the name of the channel
     * @return the archive channel
     * @throws ArchiveServiceException
     */
    IArchiveChannel getChannel(@Nonnull final String name) throws ArchiveServiceException;

    /**
     * Retrieves the channel id for a given channel name.
     * @param name the name of the channel
     * @return the id
     * @throws ArchiveServiceException
     */
    int getChannelId(@Nonnull final String name) throws ArchiveServiceException;

    /**
     * Writes metadata out of a sample for a channel.
     *
     * @param channelName the name of the channel
     * @param sample the current sample
     * @throws ArchiveServiceException if the writing of meta data failed
     */
    void writeMetaData(@Nonnull final String channelName, @Nonnull final IValue sample) throws ArchiveServiceException;

    /**
     * Retrieves the time stamp of the latest sample for the given channel.
     *
     * @param name the identifying channel name.
     * @return the time stamp of the latest sample
     * @throws ArchiveServiceException if the retrieval of the latest time stamp failed
     */
    @CheckForNull
    ITimestamp getLatestTimestampByChannel(@Nonnull final String name) throws ArchiveServiceException;
}
