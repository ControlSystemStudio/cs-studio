package org.csstudio.utility.recordproperty.rdb.config;

import java.sql.Driver;

public interface IOracleSettings {

	String getConnection();
	
	String getUsername();
	
	String getPassword();
	
	Driver getDriver();
}
