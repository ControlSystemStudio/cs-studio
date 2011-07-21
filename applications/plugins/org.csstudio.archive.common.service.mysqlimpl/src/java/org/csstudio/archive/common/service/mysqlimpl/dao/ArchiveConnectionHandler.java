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

import javax.annotation.CheckForNull;
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

    private static final String ARCHIVE_CONNECTION_EXCEPTION_MSG =
        "Archive connection could not be established";

    static final Logger LOG = LoggerFactory.getLogger(ArchiveConnectionHandler.class);
    static final Logger WORKER_LOG = LoggerFactory.getLogger(PersistDataWorker.class);


    /**
     * The datasource that specifies the connections.
     */
    private final MysqlDataSource _dataSource;

    /**
     * Any thread owns a connection.
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
        ds.setServerName(hosts);
        ds.setPort(prefs.getPort());
        ds.setDatabaseName(prefs.getDatabaseName());
        ds.setUser(prefs.getUser());
        ds.setPassword(prefs.getPassword());
        ds.setFailOverReadOnly(false);
        ds.setMaxAllowedPacket(prefs.getMaxAllowedPacketSizeInKB()*1024);
        ds.setUseTimezone(true);

        ds.setRewriteBatchedStatements(true);

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

        Connection connection = _archiveConnection.get();
        try {
            if (connection != null) { // close existing connection
                _archiveConnection.set(null);
                connection.close();
            }
            // Get class loader to find the driver
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            connection = ds.getConnection();

            if (connection != null) {
                final DatabaseMetaData meta = connection.getMetaData();
                if (meta != null) {
                    // Constructor call -> LOG.debug not possible, not yet initialised
                    LoggerFactory.getLogger(ArchiveConnectionHandler.class).debug("MySQL connection:\n" +
                              meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
                } else {
                    // Constructor call -> LOG.debug not possible, not yet initialised
                    LoggerFactory.getLogger(ArchiveConnectionHandler.class).debug("No meta data for MySQL connection");
                }
                // set to true to enable failover to other host
                connection.setAutoCommit(true);
                _archiveConnection.set(connection);
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
     * Disconnects the connection for the owning thread.
     * @throws ArchiveConnectionException
     */
    public void disconnect() throws ArchiveConnectionException {
        final Connection connection = _archiveConnection.get();
        if (connection != null) {
            try {
                connection.close();
                _archiveConnection.set(null);
            } catch (final SQLException e) {
                throw new ArchiveConnectionException("Archive disconnection failed!", e);
            }
        }
    }


    /**
     * Returns the current connection for the owning thread.
     * This method is invoked by the dedicated daos to retrieve their connection.
     * A connection's datasource is configured via the plugin_customization.ini
     *
     * @return the connection
     * @throws ArchiveConnectionException
     */
    @Nonnull
    public Connection getConnection() throws ArchiveConnectionException {
        final Connection connection = _archiveConnection.get();
        if (connection == null) {
            // the calling thread has not yet a connection registered.
            return connect(_dataSource);
        }
        return connection;
    }

    @CheckForNull
    public String getDatabaseName() {
        return _dataSource.getDatabaseName();
    }

    @Nonnull
    public Integer getMaxAllowedPacketInBytes() {
        return Integer.valueOf(_dataSource.getMaxAllowedPacket());
    }
}

