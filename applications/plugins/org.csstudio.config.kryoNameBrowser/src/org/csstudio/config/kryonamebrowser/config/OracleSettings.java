package org.csstudio.config.kryonamebrowser.config;

import java.sql.Driver;

public class OracleSettings implements Settings {

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

    public String getPassword() {
        return "KRYKLOGT";
    }

    public Driver getDriver() {
        return new oracle.jdbc.driver.OracleDriver();
    }

    public String getUsername() {
        return "KRYKLOGT";
    }

}
