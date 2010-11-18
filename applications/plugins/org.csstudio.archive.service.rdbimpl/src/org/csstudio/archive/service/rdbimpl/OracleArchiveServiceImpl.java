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
package org.csstudio.archive.service.rdbimpl;

import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.RDBArchivePreferences;
import org.csstudio.archive.rdb.SampleMode;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupHelper;
import org.csstudio.archive.rdb.engineconfig.SampleEngineHelper;
import org.csstudio.archive.service.ArchiveConnectionException;
import org.csstudio.archive.service.ArchiveServiceException;
import org.csstudio.archive.service.IArchiveEngineConfigService;
import org.csstudio.archive.service.IArchiveWriterService;
import org.csstudio.archive.service.adapter.IValueWithChannelId;
import org.csstudio.archive.service.channel.IArchiveChannel;
import org.csstudio.archive.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.service.engine.ArchiveEngineId;
import org.csstudio.archive.service.engine.IArchiveEngine;
import org.csstudio.archive.service.rdbimpl.adapter.ArchiveEngineAdapter;
import org.csstudio.archive.service.samplemode.ArchiveSampleModeId;
import org.csstudio.archive.service.samplemode.IArchiveSampleMode;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;

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
public enum OracleArchiveServiceImpl implements IArchiveEngineConfigService, IArchiveWriterService {

    INSTANCE;

    @SuppressWarnings("unused")
    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(OracleArchiveServiceImpl.class);

    public static final String ARCHIVE_PREF_KEY = "archive";
    public static final String PREFIX_PREF_KEY = "prefix";
    public static final String SAMPLE_TB_PREF_KEY = "sampleTable";
    public static final String ARRAYVAL_TB_PREF_KEY = "arrayValTable";

    /**
     * In case there'll be several WriteThreads later on.
     */
    private final ThreadLocal<RDBArchive> _archive = new ThreadLocal<RDBArchive>();

    private final ThreadLocal<String> _archivePrefix = new ThreadLocal<String>();
    private final ThreadLocal<String> _sampleTable = new ThreadLocal<String>();
    private final ThreadLocal<String> _arrayValTable = new ThreadLocal<String>();


    /**
     * {@inheritDoc}
     */
    public void connect(@Nonnull final Map<String, Object> prefs) throws ArchiveConnectionException {

        // TODO (bknerr) : here the RDBArchivePreferences are used - there are still prefs in engine2 as well
        // get that straight, prefs' keys belong here as well.
        final String url = (String) prefs.get(RDBArchivePreferences.URL);
        final String user = (String) prefs.get(RDBArchivePreferences.USER);
        final String password = (String) prefs.get(RDBArchivePreferences.PASSWORD);

        RDBArchive rdbArchive = null;
        try {
            rdbArchive = RDBArchive.connect(url, user, password); // creates a new archive connection instance everytime
        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveConnectionException("Archive connection failed.", e);
        }

        _archive.set(rdbArchive);
    }

    /**
     * {@inheritDoc}
     */
    public void reconnect() throws ArchiveConnectionException {
        try {
            _archive.get().reconnect();
        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveConnectionException("Archive reconnection failed.", e);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void disconnect() {
        _archive.get().close();
    }

    /**
     * {@inheritDoc}
     */
    public void configure(@Nonnull final Map<String, Object> cfgPrefs) {
        _archivePrefix.set((String) cfgPrefs.get(PREFIX_PREF_KEY));
        _sampleTable.set((String) cfgPrefs.get(SAMPLE_TB_PREF_KEY));
        _arrayValTable.set((String) cfgPrefs.get(ARRAYVAL_TB_PREF_KEY));
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *  For performance reasons, this call actually only adds
     *  the sample to a 'batch'.
     *  Need to follow up with <code>RDBArchive.commitBatch()</code> when done.
     *
     *  No transaction handling?
     */
    public boolean writeSamples(@Nonnull final List<IValueWithChannelId> samples)
        throws ArchiveServiceException {

        try {
            for (final IValueWithChannelId sample : samples) {
                _archive.get().batchSample(sample.getChannelId(), sample.getValue());
                // certainly, batching *could* be done in the processing layer, leaving the commitBatch for here,
                // but that would break encapsulation...
            }
            _archive.get().commitBatch();
        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveServiceException("Committing of batch of samples failed.", e);
        }

        return true;
    }


    /**
     * {@inheritDoc}
     */
    @CheckForNull
    public int getChannelId(@Nonnull final String channelName) throws ArchiveServiceException {
        try {
            final ChannelConfig channel = _archive.get().getChannel(channelName);
            return channel != null ? channel.getId() : null;
        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveServiceException("Retrieval of channel id for channel " + channelName + " failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @CheckForNull
    public IArchiveEngine findEngine(@Nonnull final String name) throws ArchiveServiceException {
        // FIXME (bknerr) : data access object is created anew on every invocation?!
        final SampleEngineHelper engines = new SampleEngineHelper(_archive.get());
        try {
            return ArchiveEngineAdapter.INSTANCE.adapt(engines.find(name));
        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveServiceException("Retrieval of engine for " + name + " failed.", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Nonnull
    public List<IArchiveChannelGroup> getGroupsByEngineId(@Nonnull final ArchiveEngineId id) throws ArchiveServiceException {
        // FIXME (bknerr) : data access object is created anew on every invocation?!
        final ChannelGroupHelper groupHelper = new ChannelGroupHelper(_archive.get());
        try {
            final ChannelGroupConfig[] groups = groupHelper.get(id.intValue());
            return ArchiveEngineAdapter.INSTANCE.adapt(groups);
        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveServiceException("Retrieval of channel group configurations for engine " + id .intValue() + " failed.", e);
        }
    }

    @Nonnull
    public List<IArchiveChannel> getChannelsByGroupId(@Nonnull final ArchiveChannelGroupId groupId) throws ArchiveServiceException {
        try {
            final ChannelGroupConfig cfg = _archive.get().findGroup(groupId.intValue()); // cache in archive ???

            // ATTENTION : in this adapt another database access is obscured (channel.getLastTimeStamp)
            return ArchiveEngineAdapter.INSTANCE.adapt(cfg.getChannels());

        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveServiceException("Retrieval of channels for " + groupId.intValue() + " failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @CheckForNull
    public IArchiveSampleMode getSampleModeById(@Nonnull final ArchiveSampleModeId sampleModeId) throws ArchiveServiceException {

        SampleMode sampleMode;
        try {
            sampleMode = _archive.get().getSampleMode(sampleModeId.intValue());
        } catch (final Exception e) {
            throw new ArchiveServiceException("Retrieval of sample mode for " + sampleModeId.intValue() + " failed.", e);
        }
        return ArchiveEngineAdapter.INSTANCE.adapt(sampleMode);
    }

    /**
     * {@inheritDoc}
     */
    public void writeMetaData(@Nonnull final String channelName, @Nonnull final IValue sample) throws ArchiveServiceException {
        try {
            _archive.get().writeMetaData(getChannelConfig(channelName), sample);
        } catch (final Exception e) {
            // FIXME (bknerr) : untyped exception swallows anything, use dedicated exception
            throw new ArchiveServiceException("Writing of meta data failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @CheckForNull
    public ITimestamp getLatestTimestampByChannel(@Nonnull final String name) throws ArchiveServiceException {

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

}
