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
package org.csstudio.archive.common.service.mysqlimpl.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.MySQLArchivePreferenceService;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistDataWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * The archive connection handler.
 *
 * Envisioned to handle connection pools and transactions with CRUD command abstraction.
 *
 * @author bknerr
 * @since 11.11.2010
 */
public class ArchiveConnectionHandler {

    static final Logger LOG = LoggerFactory.getLogger(ArchiveConnectionHandler.class);
    static final Logger WORKER_LOG = LoggerFactory.getLogger(PersistDataWorker.class);

    private static final String ARCHIVE_CONNECTION_EXCEPTION_MSG =
        "Archive connection could not be established";


    /**
     * The datasource that specifies the connections.
     */
    private final MysqlDataSource _dataSource;

    /**
     * Each thread owns a connection, some of which may not close it.
     */
    private final ThreadLocal<Connection> _archiveConnection =
        new ThreadLocal<Connection>();

    /**
     * Constructor.
     */
    @Inject
    public ArchiveConnectionHandler(@Nonnull final MySQLArchivePreferenceService prefs) {
        _dataSource = createDataSource(prefs);
    }

    @Nonnull
    private MysqlDataSource createDataSource(@Nonnull final MySQLArchivePreferenceService prefs) {

        final MysqlDataSource ds = new MysqlDataSource();
        String hosts = prefs.getHost();
        final String failoverHost = prefs.getFailOverHost();
        if (!Strings.isNullOrEmpty(failoverHost)) {
            hosts += "," + failoverHost;
        }
        final Integer port = prefs.getPort();
        final String databaseName = prefs.getDatabaseName();
        final String user = prefs.getUser();
        LOG.info("DB preferences - hosts: " + hosts + "; DB Name: " + databaseName + " ; User: " + user + "; port: " + port);
        ds.setServerName(hosts);
        ds.setPort(port);
        ds.setDatabaseName(databaseName);
        ds.setUser(user);
        ds.setPassword(prefs.getPassword());
        ds.setMaxAllowedPacket(prefs.getMaxAllowedPacketSizeInKB()*1024);
        ds.setUseTimezone(true);

        ds.setRewriteBatchedStatements(true);

        ds.setFailOverReadOnly(false);

        ds.setAutoReconnect(true);
        ds.setAutoReconnectForPools(true);
        ds.setAutoReconnectForConnectionPools(true);

        ds.setRoundRobinLoadBalance(true);

        ds.setDefaultFetchSize(10000);


        return ds;
    }

    /**
     * Connects with the RDB instance for the given datasource.
     *
     * An existing connection is closed and an new connection is established.
     *
     * @param ds the mysql data source
     * @return connection the newly established connection
     * @throws ArchiveConnectionException
     */
    @Nonnull
    private Connection connect(@Nonnull final MysqlDataSource ds) throws ArchiveConnectionException {

        Connection connection = null;
        try {
            // Get class loader to find the driver
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            connection = ds.getConnection();

            if (connection != null) {
                final DatabaseMetaData meta = connection.getMetaData();
                if (meta != null) {
                    LOG.debug("MySQL connection:\n{} {}", meta.getDatabaseProductName(), meta.getDatabaseProductVersion());
                } else {
                    LOG.debug("No meta data for MySQL connection");
                }
                // set to true to enable failover to other host
                connection.setAutoCommit(true);
            }
        } catch (final Exception e) {
            throw new ArchiveConnectionException(ARCHIVE_CONNECTION_EXCEPTION_MSG, e);
        }
        if (connection == null || Strings.isNullOrEmpty(_dataSource.getDatabaseName())) {
            throw new ArchiveConnectionException("Connection could not be established or database name is not set.", null);
        }
        return connection;
    }

    /**
     * Closes the connection for the owning thread.
     * @throws SQLException
     */
    public void close() throws SQLException {
        final Connection connection = _archiveConnection.get();
        if (connection != null) {
            connection.close();
            _archiveConnection.set(null);
        }
    }


    /**
     * Creates a new connection.
     * The invoker has to take care that this connection is closed after operation.
     * A connection's datasource is configured via the plugin_customization.ini
     *
     * @return the connection
     * @throws ArchiveConnectionException
     */
    @Nonnull
    public Connection createConnection() throws ArchiveConnectionException {
        return connect(_dataSource);
    }

    /**
     * Returns the current connection for the owning thread (creates a new one if not yet existing).
     * Intended for threads that make high usage of connections.
     * This method is invoked by the dedicated daos to retrieve their 'existing' connection.
     * A connection's datasource is configured via the plugin_customization.ini
     *
     * @return the connection
     * @throws ArchiveConnectionException
     */
    @Nonnull
    public Connection getThreadLocalConnection() throws ArchiveConnectionException {
        final Connection connection = _archiveConnection.get();
        try {
            if (connection == null || connection.isClosed()) {
                // the calling thread has not yet a connection registered.
                _archiveConnection.set(createConnection());
            }
        } catch (final SQLException e) {
            LOG.warn("Thread current 'permanent' connection has been closed. A new one for this {} is created", Thread.currentThread().getName());
        }
        return  _archiveConnection.get();

    }

    @Nonnull
    public String getDatabaseName() {
        return _dataSource.getDatabaseName();
    }

    @Nonnull
    public Integer getMaxAllowedPacketInBytes() {
        return Integer.valueOf(_dataSource.getMaxAllowedPacket());
    }
}

