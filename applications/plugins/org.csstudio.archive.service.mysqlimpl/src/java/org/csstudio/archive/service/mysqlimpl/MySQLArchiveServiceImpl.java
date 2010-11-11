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
import org.csstudio.archive.rdb.engineconfig.SampleEngineConfig;
import org.csstudio.archive.service.ArchiveConnectionException;
import org.csstudio.archive.service.ArchiveServiceException;
import org.csstudio.archive.service.IArchiveService;
import org.csstudio.archive.service.adapter.IValueWithChannelId;
import org.csstudio.archive.service.channel.IArchiveChannel;
import org.csstudio.archive.service.mysqlimpl.adapter.ArchiveEngineAdapter;
import org.csstudio.archive.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.service.mysqlimpl.dao.ArchiveDaoManager;
import org.csstudio.archive.service.samplemode.IArchiveSampleMode;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.joda.time.DateTime;



/**
 * Example archive service implementation to separate the processing and logic layer from
 * the data access layer.
 *
 * TODO: Gather here all accesses to the database (best via DAOs).
 *       Should be moved to another plugin that can be loaded/unloaded via
 *       OSGi dynamic services (tracker or declarative)
 *
 * @author bknerr
 * @since 01.11.2010
 */
public enum MySQLArchiveServiceImpl implements IArchiveService {

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
    public void configure(@Nonnull final Map<String, Object> cfgPrefs) {
        // TODO Auto-generated method stub

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
    @Deprecated
    @Override
    public IMetaData writeMetaData(@Nonnull final ChannelConfig channel, final IValue sample) {
        // Don't do anything
        return null;
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





}
