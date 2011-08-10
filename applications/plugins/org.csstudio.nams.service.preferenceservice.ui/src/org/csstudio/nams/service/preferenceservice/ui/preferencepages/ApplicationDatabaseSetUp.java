
package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.csstudio.nams.service.preferenceservice.ui.Messages;

/**
 * This preference page is used to set up the configuration database settings.
 */

public class ApplicationDatabaseSetUp extends
		AbstractNewAMSFieldEditorPreferencePage {

	public ApplicationDatabaseSetUp() {
		this.setDescription(Messages.ApplicationDatabaseSetUp_title);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		this
				.addDatabaseTypeField(PreferenceServiceDatabaseKeys.P_APP_DATABASE_TYPE);
		this.addField(PreferenceServiceDatabaseKeys.P_APP_DATABASE_CONNECTION);
		this.addField(PreferenceServiceDatabaseKeys.P_APP_DATABASE_USER);
		this.addField(PreferenceServiceDatabaseKeys.P_APP_DATABASE_PASSWORD);
	}
}