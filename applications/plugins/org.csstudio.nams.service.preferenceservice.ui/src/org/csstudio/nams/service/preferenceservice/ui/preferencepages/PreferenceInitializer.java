
package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceJMSKeys;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 * 
 * TODO mz 2008-07-17 Move to non-ui plugin!
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	private static IPreferenceStore preferenceStore;

	public static void staticInject(final IPreferenceStore preferenceStore) {
		PreferenceInitializer.preferenceStore = preferenceStore;
	}

	public PreferenceInitializer() {
		if (PreferenceInitializer.preferenceStore == null) {
			throw new RuntimeException(
					"class has not been equiped, missing: preference store");
		}
	}

	@Override
	public void initializeDefaultPreferences() {
		// configuration db
		PreferenceInitializer.preferenceStore.setDefault(
				PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_TYPE
						.getPreferenceStoreId(), DatabaseType.Oracle10g.name());
		PreferenceInitializer.preferenceStore
				.setDefault(
						PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION
								.getPreferenceStoreId(),
						"jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST = 134.100.7.235)(PORT = 1521))(LOAD_BALANCE = yes)(CONNECT_DATA =(SERVER = DEDICATED)(FAILOVER_MODE =(TYPE = NONE)(METHOD = BASIC)(RETRIES = 180)(DELAY = 5))))");
		PreferenceInitializer.preferenceStore.setDefault(
				PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER
						.getPreferenceStoreId(), "DESY");
		PreferenceInitializer.preferenceStore.setDefault(
				PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD
						.getPreferenceStoreId(), "DESY");
		
		PreferenceInitializer.preferenceStore.setDefault(
				PreferenceServiceJMSKeys.P_JMS_EXT_SYNCHRONIZE_CONSUMER_ID
						.getPreferenceStoreId(), "syncServiceConsumer");
		PreferenceInitializer.preferenceStore.setDefault(
				PreferenceServiceJMSKeys.P_JMS_EXT_SYNCHRONIZE_PRODUCER_ID
						.getPreferenceStoreId(), "syncServiceProducer");
		// TODO Initialize other things here...
	}

}
