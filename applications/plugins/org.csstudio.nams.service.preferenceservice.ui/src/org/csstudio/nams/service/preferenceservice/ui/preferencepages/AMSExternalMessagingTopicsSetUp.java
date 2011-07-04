
package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceJMSKeys;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This preference page is used to set up the internal topic settings.
 */
public class AMSExternalMessagingTopicsSetUp extends
		AbstractNewAMSFieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	@Override
	protected void createFieldEditors() {
		// - Alarm
		this.addField(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_ALARM);
		this.addField(PreferenceServiceJMSKeys.P_JMS_EXT_TSUB_ALARM);
		
		// - ext command
		this.addField(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_COMMAND);
		this.addField(PreferenceServiceJMSKeys.P_JMS_EXT_TSUB_COMMAND);
		
		// - ext command
		this.addField(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_STATUSCHANGE);
		
		this.addSeparator();
		
		this.addField(PreferenceServiceJMSKeys.P_JMS_EXT_SYNCHRONIZE_CONSUMER_ID);
		this.addField(PreferenceServiceJMSKeys.P_JMS_EXT_SYNCHRONIZE_PRODUCER_ID);
	}

}
