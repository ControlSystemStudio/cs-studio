package org.csstudio.config.kryonamebrowser.database;

import org.csstudio.config.kryonamebrowser.config.Settings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Enables opening and closing of the connection for the given {@link Settings}.
 * 
 * @author Alen Vrecko
 */
public class DBConnect {
	private Settings settings;
	private Connection connection;

	public DBConnect(Settings settings) {
		this.settings = settings;
	}

	public void openConnection() throws SQLException {

		DriverManager.registerDriver(settings.getDriver());

		connection = DriverManager.getConnection(settings.getConnection(),
				settings.getUsername(), settings.getPassword());

	}

	public Connection getConnection() {
		return connection;
	}

	public Settings getSettings() {
		return settings;
	}

	public void closeConnection() throws SQLException {

		if (connection != null && !connection.isClosed()) {
			connection.close();
		}

	}

}
