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
package org.csstudio.archive.service.oracleimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.RDBArchivePreferences;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupHelper;
import org.csstudio.archive.rdb.engineconfig.SampleEngineConfig;
import org.csstudio.archive.rdb.engineconfig.SampleEngineHelper;
import org.csstudio.archive.service.ArchiveConnectionException;
import org.csstudio.archive.service.ArchiveServiceException;
import org.csstudio.archive.service.IArchiveService;
import org.csstudio.archive.service.adapter.IValueWithChannelId;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.utility.rdb.TimeWarp;

/**
 * Example archive service implementation to separate the processing and logic layer from
 * the data access layer.
 *
 * IMPORTANT - PLEASE READ CAREFULLY - this service implementation is a stub to show the decoupling
 * of the engine from archive access!
 * A proper implementation of such a service with dedicated DAO layers can be found in
 * o.c.archive.service.mysqlimpl and later for cassandraimpl.
 *
 * This stub tries <b>always</b> to redirect to o.c.archive.rdb if possible, so that the SNS
 * developers can start to revise/refactor at familiar points.
 * If it is just not possible, as the archive.rdb methods are not accessible (not public and
 * exported), then the rdb access code is duplicated with a comment explaining where to look and why
 * a redirection wasn't possible.
 *
 * @author bknerr
 * @since 01.11.2010
 */
public enum OracleArchiveServiceImpl implements IArchiveService {

    INSTANCE;

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
    public void connect(final Map<String, Object> prefs) throws ArchiveConnectionException {

        final String url = (String) prefs.get(RDBArchivePreferences.URL);
        final String user = (String) prefs.get(RDBArchivePreferences.USER);
        final String password = (String) prefs.get(RDBArchivePreferences.PASSWORD);

        RDBArchive rdbArchive = null;
        try {
            rdbArchive = RDBArchive.connect(url, user, password);
        } catch (final Exception e) {
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
            throw new ArchiveConnectionException("Archive reconnection failed.", e);
        }

    }


    /**
     * {@inheritDoc}
     */
    public void disonnect() {
        _archive.get().close();
    }

    /**
     * {@inheritDoc}
     */
    public void configure(final Map<String, Object> cfgPrefs) {
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
     */
    public boolean writeSamples(final List<IValueWithChannelId> samples) throws ArchiveServiceException { // TODO : Untyped exception? A catch would swallow ALL exceptions!

        try {
            for (final IValueWithChannelId sample : samples) {
                _archive.get().batchSample(sample.getChannelId(), sample.getValue());
                // certainly, batching *could* be done in the processing layer, leaving the commitBatch for here,
                // but that would break encapsulation...
            }
            _archive.get().commitBatch();
        } catch (final Exception e) {
            throw new ArchiveServiceException("Committing of sample batch failed.", e);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public ChannelConfig getChannel(final String name) throws ArchiveServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * FIXME (bknerr, kasemir) : This structure seems severely broken:
     * database accesses scattered all over the place and metadata abstraction not properly designed
     * Question: How are channel, sample, meta data defined?
     *
     * what happens originally:
     *
     * {@link WriteThread#write()} calls
     * {@link ChannelConfig#batchSample(IValue)} calls
     * {@link RDBArchive#batchSample(ChannelConfig, IValue)} in case <code>if (ChannelConfig#getMetaData() == null)</code> calls
     * {@link RDBArchive#writeMetaData(ChannelConfig, IValue)} dispatches over <code>instanceof IValue</code> to
     * {NumericMetaDataHelper#set(RDBArchive, ChannelConfig, IMetaData)} which finally accesses the database with calls to
     * <ol>
     * <li>{NumericMetaDataHelper#get(RDBArchive, ChannelConfig)}</li>
     * <li>{NumericMetaDataHelper#delete(RDBArchive, ChannelConfig)} for deletion in table then return.</li>
     * <li>{NumericMetaDataHelper#delete(RDBArchive, ChannelConfig)} and {NumericMetaDataHelper#insert(RDBArchive, ChannelConfig)} for update</li>
     * </ol>
     * or the same to another table
     * {EnumMetaDataHelper#set(RDBArchive, ChannelConfig, IMetaData)}
     *
     * We have two tables enum_metadata, num_metadata - obviously it is possible that a channel configuration
     * doesn't have the meta data stuff set, so that in that case any single sample (IValue) has to asked about its metadata
     * to set the information initially.
     * IValue itself has to be cast via instanceof cascade to its implementing class to retrieve the 'value'-value.
     * It's not possible to as
     */
    public IMetaData writeMetaData(final ChannelConfig channel, final IValue sample) throws ArchiveServiceException {
        try {
            _archive.get().writeMetaData(channel, sample);
        } catch (final Exception e) {
            // FIXME (kasemir) : untyped exception swallows anything, let getConnection throw typed ones
            throw new ArchiveServiceException("Writing of meta data failed.", e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * REDIRECTION NOT POSSIBLE:
     * Direct rdb access through {@link ChannelConfig#getLastTimestamp()}!
     * Had to duplicate code statement code
     */
    @CheckForNull
    public ITimestamp getLatestTimestampByChannel(@Nonnull final String name) throws ArchiveServiceException {

        final ChannelConfig cfg = getChannel(name);
        if (cfg == null) {
            return null;
        }

        PreparedStatement statement = null;
        try {
            statement =
                _archive.get().getRDB().getConnection().prepareStatement(
                                                                         _archive.get().getSQL().channel_sel_last_time_by_id);
            statement.setQueryTimeout(RDBArchivePreferences.getSQLTimeout());
            statement.setInt(1, cfg.getId());
            final ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                return null;
            }
            final Timestamp end = rs.getTimestamp(1);
            // Channel without any samples?
            if (end == null) {
                return null;
            }
            return TimeWarp.getCSSTimestamp(end);
        } catch (final SQLException e) {
            throw new ArchiveServiceException("Retrieval of latest timestamp failed.", e);
        } catch (final Exception e) {
            // FIXME (kasemir) : untyped exception swallows anything, let getConnection throw typed ones
            throw new ArchiveServiceException("Retrieval of latest timestamp failed.", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (final SQLException e) {
                    LOG.warn("Statement could not be closed properly.", e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @CheckForNull
    public SampleEngineConfig findEngine(final int id) throws ArchiveServiceException {
        // FIXME (kasemir) : data access object is created anew on every invocation?!
        final SampleEngineHelper engines = new SampleEngineHelper(_archive.get());
        try {
            return engines.find(id);
        } catch (final Exception e) {
            // FIXME (kasemir) : untyped exception swallows anything, let getConnection throw typed ones
            throw new ArchiveServiceException("Retrieval of latest timestamp failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @CheckForNull
    public SampleEngineConfig findEngine(@Nonnull final String name) throws ArchiveServiceException {
        // FIXME (kasemir) : data access object is created anew on every invocation?!
        final SampleEngineHelper engines = new SampleEngineHelper(_archive.get());
        try {
            return engines.find(name);
        } catch (final Exception e) {
            // FIXME (kasemir) : untyped exception swallows anything, let getConnection throw typed ones
            throw new ArchiveServiceException("Retrieval of latest timestamp failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<ChannelGroupConfig> getGroups(final int engineId) throws ArchiveServiceException {
        // FIXME (kasemir) : data access object is created anew on every invocation?!
        final ChannelGroupHelper groups = new ChannelGroupHelper(_archive.get());
        try {
            return Arrays.asList(groups.get(engineId));
        } catch (final Exception e) {
            // FIXME (kasemir) : untyped exception swallows anything, let getConnection throw typed ones
            throw new ArchiveServiceException("Retrieval of latest timestamp failed.", e);
        }
    }
}
