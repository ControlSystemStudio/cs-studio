/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * A simple implementation of DataSource that wraps around a JDBC url.
 *
 * @author carcassi
 */
public class SimpleDataSource implements DataSource {

    private final String jdbcUrl;
    private PrintWriter logWriter;

    public SimpleDataSource(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.logWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("SimpleDataSource is not a wrapper");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
    
}
