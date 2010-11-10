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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.service.ArchiveConnectionException;
import org.csstudio.archive.service.ArchiveServiceException;
import org.csstudio.archive.service.IArchiveService;
import org.csstudio.archive.service.channel.IArchiveChannel;
import org.csstudio.archive.service.mysqlimpl.adapter.ArchiveEngineAdapter;
import org.csstudio.archive.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.service.mysqlimpl.samplemode.IArchiveSampleModeDao;
import org.csstudio.archive.service.samplemode.IArchiveSampleMode;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;

import com.google.common.collect.Maps;

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

    private static final String ARCHIVE_CONNECTION_EXCEPTION_MSG = "Archive connection could not be established";

    private static Logger LOG =
        CentralLogger.getInstance().getLogger(MySQLArchiveServiceImpl.class);


    private String _url = MySQLArchiveServicePreference.URL.getValue();
    private String _user = MySQLArchiveServicePreference.USER.getValue();
    private String _password = MySQLArchiveServicePreference.PASSWORD.getValue();
    private final Boolean _autoConnect = MySQLArchiveServicePreference.AUTO_CONNECT.getValue();

    /**
     * In case there'll be several WriteThreads later on.
     */
    private final ThreadLocal<Connection> _archiveConnection = new ThreadLocal<Connection>();

    /**
     * DAO.
     * Don't forget to propagate the connection to the DAOs in {@link MySQLArchiveServiceImpl#propagateConnectionToDaos(Connection)}
     */
    private IArchiveChannelDao _archiveChannelDao;
    /**
     * DAO.
     * Don't forget to propagate the connection to the DAOs in {@link MySQLArchiveServiceImpl#propagateConnectionToDaos(Connection)}
     */
    private IArchiveSampleModeDao _archiveSampleModeDao;

    /**
     * Constructor.
     *
     * Establishes connection to archive.
     */
    private MySQLArchiveServiceImpl() {

        if (_autoConnect) {
            final Map<String, Object> prefs = Maps.newHashMap();
            prefs.put(MySQLArchiveServicePreference.URL.getKeyAsString(), MySQLArchiveServicePreference.URL.getValue());
            prefs.put(MySQLArchiveServicePreference.USER.getKeyAsString(), MySQLArchiveServicePreference.USER.getValue());
            prefs.put(MySQLArchiveServicePreference.PASSWORD.getKeyAsString(), MySQLArchiveServicePreference.PASSWORD.getValue());

            try {
                connect(prefs);
            } catch (final ArchiveConnectionException e) {
                // FIXME (bknerr) : Cannot be propagated by enum constructor!
                _archiveConnection.set(null);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reconnect() throws ArchiveConnectionException {
        final Map<String, Object> prefs = Maps.newHashMap();
        prefs.put(MySQLArchiveServicePreference.URL.getKeyAsString(), MySQLArchiveServicePreference.URL.getValue());
        prefs.put(MySQLArchiveServicePreference.USER.getKeyAsString(), MySQLArchiveServicePreference.USER.getValue());
        prefs.put(MySQLArchiveServicePreference.PASSWORD.getKeyAsString(), MySQLArchiveServicePreference.PASSWORD.getValue());

        connect(prefs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public void connect(@Nonnull final Map<String, Object> prefs) throws ArchiveConnectionException {

        try {
            Connection connection = _archiveConnection.get();
            if (connection != null) {
                _archiveConnection.set(null);
                propagateConnectionToDaos(null);
                connection.close();
            }
            _url = (String) prefs.get(MySQLArchiveServicePreference.URL.getKeyAsString());
            _user = (String) prefs.get(MySQLArchiveServicePreference.USER.getKeyAsString());
            _password = (String) prefs.get(MySQLArchiveServicePreference.PASSWORD.getKeyAsString());

            // Get class loader to find the driver
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            connection = DriverManager.getConnection(_url, _user, _password);

            // Basic database info
            if (LOG.isDebugEnabled()) { // TODO (bknerr) : refactor logging guard to slf4j fast API
                final DatabaseMetaData meta = connection.getMetaData();
                LOG.debug("MySQL connection: " +
                          meta.getDatabaseProductName() +
                          " " +
                          meta.getDatabaseProductVersion());
            }
            connection.setAutoCommit(false);

            // Propagate the connection
            _archiveConnection.set(connection);
            propagateConnectionToDaos(null);

        } catch (final InstantiationException e) {
            throw new ArchiveConnectionException(ARCHIVE_CONNECTION_EXCEPTION_MSG, e);
        } catch (final IllegalAccessException e) {
            throw new ArchiveConnectionException(ARCHIVE_CONNECTION_EXCEPTION_MSG, e);
        } catch (final ClassNotFoundException e) {
            throw new ArchiveConnectionException(ARCHIVE_CONNECTION_EXCEPTION_MSG, e);
        } catch (final SQLException e) {
            throw new ArchiveConnectionException(ARCHIVE_CONNECTION_EXCEPTION_MSG, e);
        }
    }

    private void propagateConnectionToDaos(@Nullable final Connection connection) {
        _archiveChannelDao.setConnection(connection);
        _archiveSampleModeDao.setConnection(connection);

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
    public boolean writeSamples(final int channelId, @Nonnull final List<IValue> samples) throws ArchiveServiceException { // TODO : Untyped exception? A catch would swallow ALL exceptions!

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
            channel = _archiveChannelDao.getChannel(name);
            sampleMode = _archiveSampleModeDao.getSampleModeById(channel.getSampleModeId());
        } catch (final AbstractArchiveDaoException e) {
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

}
