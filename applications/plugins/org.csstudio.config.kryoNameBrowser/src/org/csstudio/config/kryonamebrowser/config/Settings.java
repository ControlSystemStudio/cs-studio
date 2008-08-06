package org.csstudio.config.kryonamebrowser.config;

import java.sql.Driver;

public interface Settings {
	String getConnection();

	String getUsername();

	String getPassword();

    Driver getDriver();
}
