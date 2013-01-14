package org.csstudio.archive.common.guard;

import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class ConnectionHandler {


	private Connection _connection;

	public Connection getConnection() throws Exception {
		if (_connection == null || _connection.isClosed()) {
			_connection = null;
            Class.forName("com.mysql.jdbc.Driver").newInstance();
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setUser(Settings.USER);
			dataSource.setPassword(Settings.PASSWORD);
			dataSource.setDatabaseName(Settings.DATABASE);
			dataSource.setServerName(Settings.SERVER);
            _connection = dataSource.getConnection();
		}
		return _connection;
	}

	public void close() {
		if (_connection != null) {
			try {
				_connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			_connection = null;
		}
	}

}
