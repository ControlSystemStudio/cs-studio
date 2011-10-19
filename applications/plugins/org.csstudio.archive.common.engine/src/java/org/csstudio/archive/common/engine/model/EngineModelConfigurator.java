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
package org.csstudio.archive.common.engine.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.engine.pvmanager.DesyJCAChannelHandler;
import org.csstudio.archive.common.engine.pvmanager.DesyJCADataSource;
import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 19.10.2011
 */
public class EngineModelConfigurator {

    private static final Logger LOG = LoggerFactory.getLogger(EngineModelConfigurator.class);

    /**
     * Constructor.
     */
    private EngineModelConfigurator() {
        // TODO Auto-generated constructor stub
    }

    public static void configureGroup(@Nonnull final IServiceProvider provider,
                                      @Nonnull final ArchiveGroup group,
                                      @Nonnull final ConcurrentMap<String, ArchiveChannelBuffer<Serializable, ISystemVariable<Serializable>>> channelMap,
                                      @Nonnull final DesyJCADataSource dataSource)
                                      throws ArchiveServiceException,
                                             OsgiServiceUnavailableException {
        LOG.info("Configure group '{}'.", group.getName());
        final Collection<IArchiveChannel> channelCfgs =
            provider.getEngineFacade().getChannelsByGroupId(group.getId());
        LOG.info("with {} channels", channelCfgs.size());

        for (final IArchiveChannel channelCfg : channelCfgs) {
            final ArchiveChannelBuffer<Serializable, ISystemVariable<Serializable>> channelBuffer =
                createAndAddArchiveChannelBuffer(provider, channelCfg, channelMap, dataSource);
            group.add(channelBuffer);
        }
    }

    @Nonnull
    public static ArchiveChannelBuffer<Serializable, ISystemVariable<Serializable>>
    createAndAddArchiveChannelBuffer(@Nonnull final IServiceProvider provider,
                                     @Nonnull final IArchiveChannel channelCfg,
                                     @Nonnull final ConcurrentMap<String, ArchiveChannelBuffer<Serializable, ISystemVariable<Serializable>>> channelMap,
                                     @Nonnull final DesyJCADataSource dataSource) {

        // Actually, this handler is created by the DesyJCADataSource but as have to set some
        // specific fields for any handler (for type safety), the creation has been extracted
        final DesyJCAChannelHandler handler =
            dataSource.createHandlerFor(channelCfg.getName(), channelCfg.getDataType());

        @SuppressWarnings({ "rawtypes", "unchecked" })
        final ArchiveChannelBuffer<Serializable, ISystemVariable<Serializable>> channel =
            new ArchiveChannelBuffer(channelCfg, provider, handler);

        ArchiveChannelBuffer<Serializable, ISystemVariable<Serializable>> presentChannel =
            channelMap.putIfAbsent(channel.getName(), channel);

        if (presentChannel == null) {
            presentChannel = channel; // channel was put into channelMap
        }
        return presentChannel;
    }
}
