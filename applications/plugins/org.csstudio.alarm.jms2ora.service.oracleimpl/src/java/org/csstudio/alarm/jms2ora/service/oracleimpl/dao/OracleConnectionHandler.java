
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
 *
 */

package org.csstudio.alarm.jms2ora.service.oracleimpl.dao;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.pool.OracleDataSource;
import org.csstudio.alarm.jms2ora.service.ConnectionInfo;
import org.csstudio.alarm.jms2ora.service.MessageArchiveConnectionException;
import org.csstudio.alarm.jms2ora.service.oracleimpl.Activator;
import org.csstudio.alarm.jms2ora.service.oracleimpl.internal.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 19.08.2011
 */
public class OracleConnectionHandler {

    /** The class logger */
    private static Logger LOG = LoggerFactory.getLogger(OracleConnectionHandler.class);

    /**  */
    private final ConnectionInfo conInfo;

    /**  */
    private OracleDataSource dataSource;

    /**  */
    private OracleDriver driver;

    /**  */
    private final ThreadLocal<Connection> connection;

    private final boolean _autoCommit;
    
    /**
     * This flag indicates if a log message have to generated. If the connection causes an exception
     * we will get an cycle of log messages that cause new log messages that causes log messages ...
     * We have to break this chain!!!!!
     */
    private boolean blockLog;

    /**
     * Constructor.
     */
    public OracleConnectionHandler(final boolean autoCommit) {

        _autoCommit = autoCommit;
        connection = new ThreadLocal<Connection>();
        connection.set(null);
        blockLog = false;
        
        boolean driverFound = false;

        final Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            final String clazz = drivers.nextElement().getClass().getName();
            if (clazz.contains("OracleDriver")) {
                driverFound = true;
            }
        }

        if (!driverFound) {
            try {
                driver = new OracleDriver();
                DriverManager.registerDriver(driver);
            } catch (final SQLException sqle) {
                logError("Cannot register OracleDriver.");
            }
        }

        final IPreferencesService prefs = Platform.getPreferencesService();
        final String dbUrl = prefs.getString(Activator.getPluginId(), PreferenceConstants.DATABASE_URL, "", null);
        final String userName = prefs.getString(Activator.getPluginId(), PreferenceConstants.DATABASE_USER, "", null);
        final String password = prefs.getString(Activator.getPluginId(), PreferenceConstants.DATABASE_PASSWORD, "", null);

        conInfo = new ConnectionInfo(userName, password, dbUrl);

        try {
            dataSource = new OracleDataSource();
            dataSource.setUser(userName);
            dataSource.setPassword(password);
            dataSource.setURL(dbUrl);
        } catch (final SQLException sqle) {
            logError("[*** SQLException ***]: " + sqle.getMessage());
        }
    }

    private void logError(String msg) {
        if (!blockLog) {
            LOG.error(msg);
            blockLog = true;
        } else {
            LOG.warn(msg);
        }
    }
    
    private void connect() throws MessageArchiveConnectionException {

        Connection con = connection.get();

        if (con != null) {
            try {
                con.close();
            } catch (final SQLException sqle) {
                LOG.warn("[*** SQLException ***]: {}", sqle.getMessage());
            }

            connection.set(null);
        }

        try {
            con = dataSource.getConnection();
            con.setAutoCommit(_autoCommit);
            connection.set(con);
            blockLog = false;
        } catch (final SQLException sqle) {
            connection.set(null);
            logError("[*** SQLException ***]: " + sqle.getMessage());
            throw new MessageArchiveConnectionException(sqle.getMessage());
        }
    }

    public void disconnect() {

        final Connection con = connection.get();

        if (con != null) {
            try {
                con.close();
            } catch (final SQLException sqle) {
                LOG.warn("[*** SQLException ***]: {}", sqle.getMessage());
            }

            connection.set(null);
        }
    }

    public Connection getConnection() throws MessageArchiveConnectionException {

        Connection con = connection.get();
        if (con == null) {
            connect();
            con = connection.get();
        }

        return con;
    }

    /**
     * Returns the ConnectionInfo object that contains the user name, the password and the database URL
     *
     * @return ConnectionInfo
     */
    public final ConnectionInfo getConnectionInfo() {
        return this.conInfo;
    }
}
