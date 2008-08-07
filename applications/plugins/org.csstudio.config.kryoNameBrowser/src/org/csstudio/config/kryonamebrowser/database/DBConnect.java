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

    public void openConnection() {
        try {
            DriverManager.registerDriver(settings.getDriver());
        } catch (SQLException e) {
            throw new RuntimeException("Missing oracle driver jar - missing dependency. Should not happen");
        }
        try {
            connection = DriverManager.getConnection(settings.getConnection(),
                    settings.getUsername(), settings.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public Settings getSettings() {
        return settings;
    }

    public void closeConnection() {

        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {

            throw new RuntimeException("Failed ", e);
        }
    }


}
