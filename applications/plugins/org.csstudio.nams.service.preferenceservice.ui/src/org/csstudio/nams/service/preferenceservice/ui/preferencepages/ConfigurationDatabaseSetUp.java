package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This preference page is used to set up the configuration database settings.
 */

public class ConfigurationDatabaseSetUp extends
		AbstractNewAMSFieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public ConfigurationDatabaseSetUp() {
		this.setDescription("Set up of global configuration database");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		this
				.addDatabaseTypeField(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_TYPE);
		this
				.addField(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION);
		this.addField(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER);
		this.addField(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD);
	}
}