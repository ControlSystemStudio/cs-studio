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

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.archive.rdb.engineconfig.SampleEngineConfig;

/**
 * Archive engine configuration methods.
 *
 * @author bknerr
 * @since 12.11.2010
 */
public interface IArchiveEngineConfigService extends IArchiveConnectionService {

    /**
     * Retrieves the engine by id.
     *
     *  @param engine_id ID of engine to locate
     *  @return SampleEngineInfo or <code>null</code> when not found
     *  @throws ArchiveServiceException
     */
    @CheckForNull
    SampleEngineConfig findEngine(final int id) throws ArchiveServiceException;

    /**
     * Retrieves the engine by id.
     *
     *  @param name name of engine to locate
     *  @return SampleEngineInfo or <code>null</code> when not found
     *  @throws ArchiveServiceException
     */
    @CheckForNull
    SampleEngineConfig findEngine(@Nonnull final String name) throws ArchiveServiceException;

    /**
     * @param engineId
     * @return
     *  @throws ArchiveServiceException
     */
    @Nonnull
    List<ChannelGroupConfig> getGroups(int engineId) throws ArchiveServiceException;

    /**
     * @param group_config
     * @return the list of channels in this group
     * @throws ArchiveServiceException
     */
    @Nonnull
    ChannelConfig[] getChannels(@Nonnull final ChannelGroupConfig group_config) throws ArchiveServiceException;
}
