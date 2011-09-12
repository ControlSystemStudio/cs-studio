
package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceJMSKeys;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class AMSExternalMessagingServerSetUp extends
		AbstractNewAMSFieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	@Override
	protected void createFieldEditors() {
		this
				.addField(PreferenceServiceJMSKeys.P_JMS_EXTERN_CONNECTION_FACTORY_CLASS);
		this.addField(PreferenceServiceJMSKeys.P_JMS_EXTERN_CONNECTION_FACTORY);
		this.addField(PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_1);
		this.addField(PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_2);
		this
				.addField(PreferenceServiceJMSKeys.P_JMS_EXTERN_SENDER_PROVIDER_URL);
	}

}
