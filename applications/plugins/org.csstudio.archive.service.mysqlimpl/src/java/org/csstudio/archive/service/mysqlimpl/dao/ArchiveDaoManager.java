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
package org.csstudio.archive.service.mysqlimpl.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.rdb.RDBArchivePreferences;
import org.csstudio.archive.service.ArchiveConnectionException;
import org.csstudio.archive.service.mysqlimpl.MySQLArchiveServiceImpl;
import org.csstudio.archive.service.mysqlimpl.MySQLArchiveServicePreference;
import org.csstudio.archive.service.mysqlimpl.channel.ArchiveChannelDaoImpl;
import org.csstudio.archive.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.service.mysqlimpl.sample.ArchiveSampleDaoImpl;
import org.csstudio.archive.service.mysqlimpl.sample.IArchiveSampleDao;
import org.csstudio.archive.service.mysqlimpl.samplemode.ArchiveSampleModeDaoImpl;
import org.csstudio.archive.service.mysqlimpl.samplemode.IArchiveSampleModeDao;
import org.csstudio.platform.logging.CentralLogger;

import com.google.common.collect.Maps;

/**
 * The archive dao manager.
 *
 * @author bknerr
 * @since 11.11.2010
 */
public enum ArchiveDaoManager {

    INSTANCE;

    private static final Logger LOG = CentralLogger.getInstance().getLogger(ArchiveDaoManager.class);

    private static final String ARCHIVE_CONNECTION_EXCEPTION_MSG = "Archive connection could not be established";

    private String _url;
    private String _user;
    private String _password;

    private final Boolean _autoConnect = MySQLArchiveServicePreference.AUTO_CONNECT.getValue();

    /**
     * In case there'll be several WriteThreads later on.
     */
    private final ThreadLocal<Connection> _archiveConnection = new ThreadLocal<Connection>();

    /**
     * DAO.
     * Don't forget to propagate the connection to the DAOs
     * in {@link MySQLArchiveServiceImpl#propagateConnectionToDaos(Connection)}
     */
    private IArchiveChannelDao _archiveChannelDao;
    /**
     * DAO.
     * Don't forget to propagate the connection to the DAOs
     * in {@link MySQLArchiveServiceImpl#propagateConnectionToDaos(Connection)}
     */
    private IArchiveSampleModeDao _archiveSampleModeDao;
    /**
     * DAO.
     * Don't forget to propagate the connection to the DAOs
     * in {@link MySQLArchiveServiceImpl#propagateConnectionToDaos(Connection)}
     */
    private IArchiveSampleDao _archiveSampleDao;

    /**
     * Constructor.
     */
    private ArchiveDaoManager() {

        if (_autoConnect) {
            final Map<String, Object> prefs = Maps.newHashMap();
            prefs.put(RDBArchivePreferences.URL, RDBArchivePreferences.getURL());
            prefs.put(RDBArchivePreferences.USER, RDBArchivePreferences.getUser());
            prefs.put(RDBArchivePreferences.PASSWORD, RDBArchivePreferences.getPassword());

            try {
                connect(prefs);
            } catch (final ArchiveConnectionException e) {
                // FIXME (bknerr) : Cannot be propagated by enum constructor!
                _archiveConnection.set(null);
            }
        }
    }

    public void connect(@Nonnull final Map<String, Object> prefs) throws ArchiveConnectionException {

        try {
            Connection connection = _archiveConnection.get();
            if (connection != null) {
                _archiveConnection.set(null);
                connection.close();
            }
            _url = (String) prefs.get(RDBArchivePreferences.URL);
            _user = (String) prefs.get(RDBArchivePreferences.USER);
            _password = (String) prefs.get(RDBArchivePreferences.PASSWORD);

            // Get class loader to find the driver
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            connection = DriverManager.getConnection(_url, _user, _password);

            // TODO (bknerr) : refactor logging guard to slf4j fast API
            // Basic database info
            if (LOG.isDebugEnabled()) {
                final DatabaseMetaData meta = connection.getMetaData();
                LOG.debug("MySQL connection: " +
                          meta.getDatabaseProductName() +
                          " " +
                          meta.getDatabaseProductVersion());
            }
            connection.setAutoCommit(false);

            // Propagate the connection
            _archiveConnection.set(connection);

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

    public void reconnect() throws ArchiveConnectionException {
        final Map<String, Object> prefs = Maps.newHashMap();
        prefs.put(MySQLArchiveServicePreference.URL.getKeyAsString(), MySQLArchiveServicePreference.URL.getValue());
        prefs.put(MySQLArchiveServicePreference.USER.getKeyAsString(), MySQLArchiveServicePreference.USER.getValue());
        prefs.put(MySQLArchiveServicePreference.PASSWORD.getKeyAsString(), MySQLArchiveServicePreference.PASSWORD.getValue());

        connect(prefs);
    }

    /**
     * @throws ArchiveConnectionException
     */
    public void disconnect() throws ArchiveConnectionException {
        try {
            _archiveConnection.get().close();
        } catch (final SQLException e) {
            throw new ArchiveConnectionException("Archive disconnection failed.", e);
        }
    }



    /**
     * Returns the current connection for the owning thread.
     * This method is invoked by the dedicated daos to retrieve their connection.
     *
     * @return the connection
     */
    @CheckForNull
    Connection getConnection() {
        return _archiveConnection.get();
    }

    /**
     * @return the archive channel dao
     */
    @Nonnull
    public IArchiveChannelDao getChannelDao() {
        if (_archiveChannelDao == null) {
            _archiveChannelDao = new ArchiveChannelDaoImpl();
        }
        return _archiveChannelDao;
    }

    /**
     * @return the archive sample mode dao
     */
    @Nonnull
    public IArchiveSampleModeDao getSampleModeDao() {
        if (_archiveSampleModeDao == null) {
            _archiveSampleModeDao = new ArchiveSampleModeDaoImpl();
        }
        return _archiveSampleModeDao;
    }

    /**
     * @return the archive sample dao
     */
    @Nonnull
    public IArchiveSampleDao getSampleDao() {
        if (_archiveSampleDao == null) {
            _archiveSampleDao = new ArchiveSampleDaoImpl();
        }
        return _archiveSampleDao;
    }
}
