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
import javax.annotation.Nullable;

import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroup;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.controlsystem.IArchiveControlSystem;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport;
import org.csstudio.domain.desy.epics.name.EpicsChannelName;
import org.csstudio.domain.desy.epics.pvmanager.DesyJCADataSource;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static helper class for configuration logic of {@link EngineModel}.
 *
 * @author bknerr
 * @since 19.10.2011
 */
public final class EngineModelConfigurator {

    private static final Logger LOG = LoggerFactory.getLogger(EngineModelConfigurator.class);

    /**
     * Constructor.
     */
    private EngineModelConfigurator() {
        // Don't instantiate
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

        @SuppressWarnings({ "rawtypes", "unchecked" })
        final ArchiveChannelBuffer<Serializable, ISystemVariable<Serializable>> channel =
            new ArchiveChannelBuffer(channelCfg, provider, dataSource);

        ArchiveChannelBuffer<Serializable, ISystemVariable<Serializable>> presentChannel =
            channelMap.putIfAbsent(channel.getName(), channel);

        if (presentChannel == null) {
            presentChannel = channel; // channel was put into channelMap
        }
        return presentChannel;
    }

    @Nonnull
    public static IArchiveEngine findEngineConfByName(@Nonnull final String name,
                                                      @Nonnull final IServiceProvider provider)
                                                      throws EngineModelException {
        IArchiveEngine engine = null;
        try {
            engine = provider.getEngineFacade().findEngine(name);
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException("Engine could not be retrieved. OSGi service unavailable.", e);
        } catch (final ArchiveServiceException e) {
            throw new EngineModelException("Engine could not be retrieved. Internal archive service exception.", e);
        }
        if (engine == null) {
            throw new EngineModelException("Unknown engine '" + name + "'.", null);
        }
        return engine;
    }

    @Nonnull
    public static Collection<IArchiveChannelGroup> findGroupsForEngine(@Nonnull final ArchiveEngineId id,
                                                                       @Nonnull final IServiceProvider provider)
                                                                       throws ArchiveServiceException, OsgiServiceUnavailableException {
        final IArchiveEngineFacade service = provider.getEngineFacade();
        return service.getGroupsForEngine(id);
    }

    @Nonnull
    public static IArchiveChannelGroup configureNewGroup(@Nonnull final String name,
                                                         @Nonnull final ArchiveEngineId id,
                                                         @Nonnull final String desc,
                                                         @Nonnull final IServiceProvider provider) throws EngineModelException {
        final IArchiveChannelGroup archGroup =
            new ArchiveChannelGroup(ArchiveChannelGroupId.NONE, name, id, desc);
        try {
            final IArchiveChannelGroup group = provider.getEngineFacade().createGroup(archGroup);
            if (group != null) {
                throw new EngineModelException("Creation of group failed in archive service.", null);
            }
            return archGroup;
        } catch (final ArchiveServiceException e) {
            throw new EngineModelException("Creation of group failed in archive service.", e);
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException("Creation of group failed, archive service unavailable.", e);
        }
    }

    public static void removeChannel(@Nonnull final String name,
                                     @Nonnull final ConcurrentMap<String, ArchiveChannelBuffer<Serializable, ISystemVariable<Serializable>>> channelMap,
                                     @Nonnull final Collection<ArchiveGroup> groups,
                                     @Nonnull final IServiceProvider provider) throws EngineModelException {
        channelMap.remove(name);

        for (final ArchiveGroup group : groups) {
            if (group.findChannel(name) != null) {
                group.remove(name);
                break;
            }
        }

        try {
            provider.getEngineFacade().removeChannel(name);
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException("Channel deletion failed.", e);
        } catch (final ArchiveServiceException e) {
            throw new EngineModelException("Channel deletion failed.", e);
        }
    }

    // CHECKSTYLE OFF: ParameterNumber
    @Nonnull
    public static ArchiveChannelBuffer<?, ?> configureNewChannel(@Nonnull final EpicsChannelName epicsName,
                                                                 @Nullable final String type,
                                                                 @Nullable final String low,
                                                                 @Nullable final String high,
                                                                 @Nullable final ArchiveGroup group,
                                                                 @Nullable final ConcurrentMap<String, ArchiveChannelBuffer<Serializable, ISystemVariable<Serializable>>> channelMap,
                                                                 @Nullable final DesyJCADataSource dataSource,
                                                                 @Nonnull final IServiceProvider provider) throws EngineModelException {
    // CHECKSTYLE ON: ParameterNumber

        // FIXME (bknerr) : For now we use only one control system - for later this can be configured via HTTP server
        try {
            final IArchiveControlSystem cs =
                provider.getEngineFacade().getControlSystemByName(ControlSystem.EPICS_DEFAULT.getName());
            if (cs == null) {
                throw new EngineModelException("Channel creation failed. Control system unknown: " + ControlSystem.EPICS_DEFAULT.getName(), null);
            }

            // FIXME (bknerr) : check whether channel is already covered by other engine!
            // only possible after db schema refactoring
            final IArchiveChannel channel =
                ArchiveTypeConversionSupport.createArchiveChannel(ArchiveChannelId.NONE,
                                                                  epicsName.toString(),
                                                                  type,
                                                                  group.getId(),
                                                                  null,
                                                                  cs,
                                                                  true,
                                                                  low,
                                                                  high);

            final IArchiveChannel failureCfg = provider.getEngineFacade().createChannel(channel);
            if (failureCfg != null) {
                throw new EngineModelException("Channel creation failed.", null);
            }
            final IArchiveChannel cfg =
                provider.getEngineFacade().getChannelByName(epicsName.toString());
            if (cfg != null) {
                final ArchiveChannelBuffer<?, ?> channelBuffer =
                    createAndAddArchiveChannelBuffer(provider, cfg, channelMap, dataSource);
                group.add(channelBuffer);

                return channelBuffer;
            }
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException("Channel creation failed.", e);
        } catch (final ArchiveServiceException e) {
            throw new EngineModelException("Channel creation failed.", e);
        } catch (final TypeSupportException e) {
            throw new EngineModelException("Channel creation failed.", e);
        }
        throw new EngineModelException("Channel creation failed.", null);
    }
}
