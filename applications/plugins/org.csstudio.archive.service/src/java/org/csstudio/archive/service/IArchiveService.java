/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
import java.util.Map;

import javax.annotation.Nonnull;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.platform.data.IValue;

/**
 * Interface that covers all domain specific archive accesses.
 *
 * Implementors should consider the use of DAOs and DAOImpls/DAOHibernates.
 *
 * @author bknerr
 * @since 01.11.2010
 */
public interface IArchiveService {

    /**
     * Reconnects the service with archive API.
     * @throws ArchiveConnectionException when the connection could not be established
     */
    void reconnect() throws ArchiveConnectionException;

    /**
     * Connects the service with the archive API.
     * If a connection is present, it is closed.
     *
     * @param connectionPrefs the connection preferences
     * @throws ArchiveConnectionException when the connection could not be established
     */
    void connect(@Nonnull final Map<String, Object> connectionPrefs) throws ArchiveConnectionException;

    /**
     * Configures the service to the user's needs.
     * For instance the ornl implementation needs knowledge about batch sizes etc.
     *
     * @param cfgPrefs the configuration preferences
     */
    void configure(@Nonnull final Map<String, Object> cfgPrefs);

    /**
     * Writes the sample to the archive.
     *
     * @param channelId the id of the channel
     * @param sample the sample to be archived
     * @return true, if the sample has been persisted
     * @throws ArchiveServiceException
     */
    boolean writeSample(final int channelId, final IValue sample) throws ArchiveServiceException;

    /**
     * Writes the samples to the archive.
     *
     * @param channelId the id of the channel
     * @param samples the samples to be archived
     * @return true, if the samples has been persisted
     * @throws ArchiveServiceException
     */
    boolean writeSamples(final int channelId, final List<IValue> samples) throws ArchiveServiceException;

    /**
     * Retrieves the channel configuration from the archive with the given name.
     *
     * @param name the name of the channel
     * @throws ArchiveServiceException
     */
    ChannelConfig getChannel(@Nonnull final String name) throws ArchiveServiceException;


}
