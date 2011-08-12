
package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceJMSKeys;
import org.csstudio.nams.service.preferenceservice.ui.Messages;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This preference page is used to set up the configuration database settings.
 */

public class AMSInternalMessagingServerSetUp extends
		AbstractNewAMSFieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public AMSInternalMessagingServerSetUp() {
		this.setDescription(Messages.AMSInternalMessagingServerSetUp_title);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		this
				.addField(PreferenceServiceJMSKeys.P_JMS_AMS_CONNECTION_FACTORY_CLASS);
		this.addField(PreferenceServiceJMSKeys.P_JMS_AMS_CONNECTION_FACTORY);
		this.addField(PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_1);
		this.addField(PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_2);
		this.addField(PreferenceServiceJMSKeys.P_JMS_AMS_SENDER_PROVIDER_URL);
	}
}