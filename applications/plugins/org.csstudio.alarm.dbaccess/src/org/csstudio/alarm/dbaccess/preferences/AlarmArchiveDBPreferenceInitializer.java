package org.csstudio.alarm.dbaccess.preferences;

import org.csstudio.alarm.dbaccess.archivedb.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class AlarmArchiveDBPreferenceInitializer extends
		AbstractPreferenceInitializer {


	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		store.setDefault(AlarmArchiveDBPreferenceConstants.DB_CONNECTION_STRING, "jdbc:oracle:thin:@(DESCRIPTION = "
		        + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv01.desy.de)(PORT = 1521)) "
		        + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv02.desy.de)(PORT = 1521)) "
		        + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv03.desy.de)(PORT = 1521)) "
		        + "(LOAD_BALANCE = yes) " + "(CONNECT_DATA = " + "(SERVER = DEDICATED) "
		        + "(SERVICE_NAME = desy_db.desy.de) " + "(FAILOVER_MODE = " + "(TYPE = NONE) "
		        + "(METHOD = BASIC) " + "(RETRIES = 180) " + "(DELAY = 5) " + ")" + ")" + ")");
		store.setDefault(AlarmArchiveDBPreferenceConstants.DB_USER, "krykams");
		store.setDefault(AlarmArchiveDBPreferenceConstants.DB_PASSWORD, "krykams");

	}

}
