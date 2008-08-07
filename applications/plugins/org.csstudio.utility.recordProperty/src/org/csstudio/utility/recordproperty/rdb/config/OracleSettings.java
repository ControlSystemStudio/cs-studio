package org.csstudio.utility.recordproperty.rdb.config;

import java.sql.Driver;

/**
 * Basic settings to connect a database.
 * 
 * @author Alen Vrecko
 */
public class OracleSettings implements IOracleSettings {

	public String getConnection() {
		return "jdbc:oracle:thin:@(DESCRIPTION = "
        + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv01.desy.de)(PORT = 1521)) "
        + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv02.desy.de)(PORT = 1521)) "
        + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv03.desy.de)(PORT = 1521)) "
        + "(LOAD_BALANCE = yes) " + "(CONNECT_DATA = "
        + "(SERVER = DEDICATED) " + "(SERVICE_NAME = desy_db.desy.de) "
        + "(FAILOVER_MODE = " + "(TYPE = NONE) " + "(METHOD = BASIC) "
        + "(RETRIES = 180) " + "(DELAY = 5) " + ")" + ")" + ")";
	}
	
	public String getUsername() {
		return "KRYKMAN";
	}
	
	public String getPassword() {
		return "KRYKMAN";
	}
	
	public Driver getDriver() {
		return new oracle.jdbc.driver.OracleDriver();
	}
}
