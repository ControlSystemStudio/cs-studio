package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 * 
 * TODO mz 2008-07-17 Move to non-ui plugin!
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public static void staticInject(IPreferenceStore preferenceStore) {
		PreferenceInitializer.preferenceStore = preferenceStore;
	}

	private static IPreferenceStore preferenceStore;

	public PreferenceInitializer() {
		if (PreferenceInitializer.preferenceStore == null) {
			throw new RuntimeException(
					"class has not been equiped, missing: preference store");
		}
	}

	public void initializeDefaultPreferences() {
		// configuration db
		preferenceStore.setDefault(
				PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_TYPE
						.getPreferenceStoreId(), DatabaseType.Oracle10g.name());
		preferenceStore
				.setDefault(
						PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION
								.getPreferenceStoreId(),
						"jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST = 134.100.7.235)(PORT = 1521))(LOAD_BALANCE = yes)(CONNECT_DATA =(SERVER = DEDICATED)(FAILOVER_MODE =(TYPE = NONE)(METHOD = BASIC)(RETRIES = 180)(DELAY = 5))))");
		preferenceStore.setDefault(
				PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER
						.getPreferenceStoreId(), "DESY");
		preferenceStore.setDefault(
				PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD
						.getPreferenceStoreId(), "DESY");
		// TODO Initialize other things here...
	}

}
