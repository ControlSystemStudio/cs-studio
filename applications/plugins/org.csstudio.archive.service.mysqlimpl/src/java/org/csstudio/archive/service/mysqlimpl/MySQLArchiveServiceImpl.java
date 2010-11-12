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
package org.csstudio.archive.service.mysqlimpl;

import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.archive.rdb.engineconfig.SampleEngineConfig;
import org.csstudio.archive.service.ArchiveConnectionException;
import org.csstudio.archive.service.ArchiveServiceException;
import org.csstudio.archive.service.IArchiveEngineConfigService;
import org.csstudio.archive.service.IArchiveWriterService;
import org.csstudio.archive.service.adapter.IValueWithChannelId;
import org.csstudio.archive.service.channel.IArchiveChannel;
import org.csstudio.archive.service.mysqlimpl.adapter.ArchiveEngineAdapter;
import org.csstudio.archive.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.service.mysqlimpl.dao.ArchiveDaoManager;
import org.csstudio.archive.service.samplemode.IArchiveSampleMode;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.joda.time.DateTime;



/**
 * Example archive service implementation to separate the processing and logic layer from
 * the data access layer.
 *
 * Uses DAO design pattern with DaoManager to handle several connections in a pool (later) and
 * facilite CRUD command infrastructure for proper multiple command transactions.
 *
 * @author bknerr
 * @since 01.11.2010
 */
public enum MySQLArchiveServiceImpl implements IArchiveEngineConfigService, IArchiveWriterService {

    INSTANCE;

    /**
     * Constructor.
     *
     * Establishes connection to archive.
     */
    private MySQLArchiveServiceImpl() {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(@Nonnull final Map<String, Object> connectionPrefs) throws ArchiveConnectionException {
        ArchiveDaoManager.INSTANCE.connect(connectionPrefs);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reconnect() throws ArchiveConnectionException {
        ArchiveDaoManager.INSTANCE.reconnect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() throws ArchiveConnectionException {
        ArchiveDaoManager.INSTANCE.disconnect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(@Nonnull final Map<String, Object> cfgPrefs) {
        // nothing to configure for now
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *  For performance reasons, this call actually only adds
     *  the sample to a 'batch'.
     *  Need to follow up with <code>RDBArchive.commitBatch()</code> when done.
     */
    @Override
    public boolean writeSamples(@Nonnull final List<IValueWithChannelId> samples) throws ArchiveServiceException { // TODO : Untyped exception? A catch would swallow ALL exceptions!

        //        for (final IValue sample : samples) {
        //            _archive.get().batchSample(channelId, sample);
        //            // certainly, batching *could* be done in the processing layer, leaving the commitBatch for here,
        //            // but that would break encapsulation...
        //        }
        //        _archive.get().commitBatch();

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelConfig getChannel(@Nonnull final String name) throws ArchiveServiceException {

        IArchiveChannel channel = null;
        IArchiveSampleMode sampleMode = null;
        try {
            channel = ArchiveDaoManager.INSTANCE.getChannelDao().getChannel(name);
            sampleMode = ArchiveDaoManager.INSTANCE.getSampleModeDao().getSampleModeById(channel.getSampleModeId());
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Data retrieval failure for channel.", e);
        }

        return ArchiveEngineAdapter.INSTANCE.adapt(name, channel, sampleMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getChannelId(final String name) throws ArchiveServiceException {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public void writeMetaData(@Nonnull final String channelName, final IValue sample) {
        // FIXME (bknerr) : complete "meta data" concept seems broken.
        // Metadata are partly themselves record fields (for numerics), which might be again registered channels,
        // hence having channel configurations that then have again meta data and so on.
        //
        // Consider refactoring:
        // treat all channels = 'record fields' exactly the same in the archiver, just as samples in the sample table
        // and let the archive reading clients handle the relations between the record fields (whether they
        // belong to the 'same' record or influence each other in any way is only of interest for the archive reading
        // tool not for the archive).
        // How about that:
        // Consider making the channel id in the rdb split into two columns, record and field.
        // Hence, asking about a channel's VAL samples, e.g. <record>.<field>=kryoBox.VAL can easily be
        // modified by the client to ask additionally, if channelType of kryoBox.VAL is numeric, get the samples for
        // channel kryoBox.deadband, kryoBox.HIHI and kryoBox.LOLO as well. That can be called meta data or whatever.
        // But the archiver wouldn't notice any difference.
        //
        // DESY has to take care as it is envisioned to have several control systems. Hence record and field might not
        // be appropriate. Generify this idea.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public ITimestamp getLatestTimestampByChannel(@Nonnull final String name) throws ArchiveServiceException {

        IArchiveChannel cfg = null;
        try {
            cfg = ArchiveDaoManager.INSTANCE.getChannelDao().getChannel(name);
            if (cfg != null) {
                return ArchiveEngineAdapter.INSTANCE.adapt(cfg.getLatestTimestamp());
            }
            // Access the sample table
            final DateTime ltstSampleTime =
                ArchiveDaoManager.INSTANCE.getSampleDao().getLatestSampleForChannel(cfg.getId());
            if (ltstSampleTime != null) {
                return ArchiveEngineAdapter.INSTANCE.adapt(ltstSampleTime);
            }
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Channel information could not be retrieved.", e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public SampleEngineConfig findEngine(final int id) throws ArchiveServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public SampleEngineConfig findEngine(@Nonnull final String name) throws ArchiveServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChannelGroupConfig> getGroupsByEngineId(final int engineId) throws ArchiveServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChannelConfig> getChannelsByGroupId(final ChannelGroupConfig group_config) throws ArchiveServiceException {
        // TODO Auto-generated method stub
        return null;
    }

}
