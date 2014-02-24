package org.csstudio.platform.libs.hibernate.preferences;

import org.csstudio.platform.libs.hibernate.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences prefs = new DefaultScope()
                .getNode(Activator.PLUGIN_ID);

        prefs.put(PreferenceConstants.DDB_USER_NAME, "KRYKMAN");
        prefs.put(PreferenceConstants.DDB_PASSWORD, "KRYKMAN");
        prefs.put(PreferenceConstants.HIBERNATE_CONNECTION_DRIVER_CLASS,
                "oracle.jdbc.driver.OracleDriver");
        prefs.put(PreferenceConstants.HIBERNATE_CONNECTION_URL, "jdbc:oracle:thin:@(DESCRIPTION ="
                + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv01.desy.de)(PORT = 1521))"
                + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv02.desy.de)(PORT = 1521))"
                + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv03.desy.de)(PORT = 1521))"
                + "(LOAD_BALANCE = yes)" + "(CONNECT_DATA = (SERVER = DEDICATED)"
                + "(SERVICE_NAME = desy_db.desy.de)"
                + "(FAILOVER_MODE = (TYPE = NONE)(METHOD = BASIC)(RETRIES = 180)(DELAY = 5))))");
        prefs.put(PreferenceConstants.DIALECT, "org.hibernate.dialect.Oracle10gDialect");
        prefs.put(PreferenceConstants.SHOW_SQL, "false");
        prefs.putInt(PreferenceConstants.DDB_TIMEOUT, 99);
    }
}
