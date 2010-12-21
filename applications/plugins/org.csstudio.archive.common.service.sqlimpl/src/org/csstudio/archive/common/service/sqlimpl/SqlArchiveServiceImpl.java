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
package org.csstudio.archive.common.service.sqlimpl;

import java.util.Collection;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineConfigService;
import org.csstudio.archive.common.service.IArchiveWriterService;
import org.csstudio.archive.common.service.adapter.IValueWithChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.archive.common.service.samplemode.ArchiveSampleModeId;
import org.csstudio.archive.common.service.samplemode.IArchiveSampleMode;
import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.SampleMode;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupHelper;
import org.csstudio.archive.rdb.engineconfig.SampleEngineHelper;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

/**
 * Example archive service implementation to separate the processing and logic layer from
 * the data access layer.
 *
 * IMPORTANT - PLEASE READ CAREFULLY - this service implementation is a stub to demonstrate the
 * decoupling of the engine from the data (archive) access layer!
 *
 * This stub <b>tries always</b> to redirect to o.c.archive.rdb if possible, so that the SNS
 * developers can navigate for consideration to familiar points in their own code.
 * If this is not possible, as the archive.rdb methods are not accessible (not public and/or
 * exported or at least not without much ado), then the rdb access code is duplicated with a comment
 * explaining where to look and why a redirection wasn't possible.
 *
 * (An example implementation of such a service with dedicated DAO layers can be found in
 * o.c.archive.service.mysqlimpl).

 * @author bknerr
 * @since 01.11.2010
 */
public enum SqlArchiveServiceImpl implements IArchiveEngineConfigService, IArchiveWriterService {

    INSTANCE;

    /**
     * Converter function.
     *
     * @author bknerr
     * @since 20.12.2010
     */
    private final class ChannelCfg2ArchiveChannelFunction implements
            Function<ChannelConfig, IArchiveChannel> {
        /**
         * Constructor.
         */
        public ChannelCfg2ArchiveChannelFunction() {
            // empty
        }

        @Override
        @CheckForNull
        public IArchiveChannel apply(@Nonnull final ChannelConfig from) {
            try {
                ITimestamp lastTimestamp;
                try {
                    // ATTENTION: obscured database access via
                    // lastTimestamp = channel.getLastTimestamp();
                    // Looks like a getter, but is a db call, perform via service:
                    lastTimestamp =
                        SqlArchiveServiceImpl.INSTANCE.getLatestTimestampForChannel(from.getName());

                } catch (final Exception e) {
                    throw new ArchiveServiceException("Last timestamp for channel " + from.getName() +
                                                      " could not be retrieved.", e);
                }
                return ADAPT_MGR.adapt(from, lastTimestamp);

            } catch (final ArchiveServiceException e) {
                // FIXME (bknerr) : How to propagate an exception from here???
                return null;
            }
        }
    }

    @SuppressWarnings("unused")
    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(SqlArchiveServiceImpl.class);

    static final ArchiveEngineAdapter ADAPT_MGR = ArchiveEngineAdapter.INSTANCE;

    public static final String ARCHIVE_PREF_KEY = "archive";
    public static final String PREFIX_PREF_KEY = "prefix";
    public static final String SAMPLE_TB_PREF_KEY = "sampleTable";
    public static final String ARRAYVAL_TB_PREF_KEY = "arrayValTable";

    /**
     * In case there'll be several WriteThreads later on.
     */
    private final ThreadLocal<RDBArchive> _archive = new ThreadLocal<RDBArchive>();


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean writeSamples(@Nonnull final Collection<IValueWithChannelId> samples) throws ArchiveServiceException {
        throw new ArchiveServiceException("Not implemented", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getChannelId(@Nonnull final String channelName) throws ArchiveServiceException {
        try {
            final RDBArchive rdbArchive = _archive.get();
            final ChannelConfig channel = rdbArchive != null ? rdbArchive.getChannel(channelName) :
                                                               null;
            if (channel == null) {
                throw new NullPointerException("Channel reference from DB is null");
            }
            return channel.getId();
        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveServiceException("Retrieval of channel id for channel " + channelName + " failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveEngine findEngine(@Nonnull final String name) throws ArchiveServiceException {
        // FIXME (bknerr) : data access object is created anew on every invocation?!
        final SampleEngineHelper engines = new SampleEngineHelper(_archive.get());
        try {
            return ADAPT_MGR.adapt(engines.find(name));
        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveServiceException("Retrieval of engine for " + name + " failed.", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public List<IArchiveChannelGroup> getGroupsForEngine(@Nonnull final ArchiveEngineId id) throws ArchiveServiceException {
        // FIXME (bknerr) : data access object is created anew on every invocation?!
        final ChannelGroupHelper groupHelper = new ChannelGroupHelper(_archive.get());
        try {
            final ChannelGroupConfig[] groups = groupHelper.get(id.intValue());
            return ADAPT_MGR.adapt(groups);
        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveServiceException("Retrieval of channel group configurations for engine " + id .intValue() + " failed.", e);
        }
    }

    @Override
    @Nonnull
    public Collection<IArchiveChannel> getChannelsByGroupId(@Nonnull final ArchiveChannelGroupId groupId) throws ArchiveServiceException {
        try {
            final ChannelGroupConfig cfg = _archive.get().findGroup(groupId.intValue()); // cache in archive ???

            // ATTENTION : in this adapt another database access is obscured (channel.getLastTimeStamp)
            final Collection<ChannelConfig> channels = cfg.getChannels();

            // Due to the internal service call, the transformation can't be hidden in the ArchiveEngineAdapter, (or
            // alternatively, the archive.service plugin would need an activator and register for its own service)
            final Function<ChannelConfig, IArchiveChannel> channelCfg2ArchChannel = new ChannelCfg2ArchiveChannelFunction();
            final Collection<IArchiveChannel> moreChannels = Collections2.filter(Collections2.transform(channels,
                                                                                                        channelCfg2ArchChannel),
                                                                                 Predicates.<IArchiveChannel>notNull());
            if (moreChannels.size() != channels.size()) {
                throw new Exception("Conversion from ChannelGroupConfig to IArchiveChannel failed. ");
            }
            return moreChannels;

        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveServiceException("Retrieval of channels for " + groupId.intValue() + " failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveSampleMode getSampleModeById(@Nonnull final ArchiveSampleModeId sampleModeId) throws ArchiveServiceException {

        SampleMode sampleMode;
        try {
            sampleMode = _archive.get().getSampleMode(sampleModeId.intValue());
        } catch (final Exception e) {
            throw new ArchiveServiceException("Retrieval of sample mode for " + sampleModeId.intValue() + " failed.", e);
        }
        return ADAPT_MGR.adapt(sampleMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeMetaData(@Nonnull final String channelName, @Nonnull final IValue sample) throws ArchiveServiceException {
        try {
            _archive.get().writeMetaData(getChannelConfig(channelName), sample);
        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveServiceException("Committing of meta data failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public ITimestamp getLatestTimestampForChannel(@Nonnull final String name) throws ArchiveServiceException {

        final ChannelConfig cfg = getChannelConfig(name);
        if (cfg == null) {
            return null;
        }
        try {
            return cfg.getLastTimestamp();
        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveServiceException("Retrieval of last sample's time stamp for channel " + name + " failed.", e);
        }
    }

    @Nonnull
    private ChannelConfig getChannelConfig(@Nonnull final String channelName) throws ArchiveServiceException {
        try {
            return _archive.get().getChannel(channelName);
        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveServiceException("Retrieval of channel failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commitSample(final int channelId, final IValue value) throws ArchiveServiceException {
        try {
            _archive.get().batchSample(channelId, value);
        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveServiceException("Batching of samples failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean flush() throws ArchiveServiceException {
        try {
            _archive.get().commitBatch();
        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveServiceException("Committing of batch failed.", e);
        }
        return true;
    }

}
