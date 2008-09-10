package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceJMSKeys;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This preference page is used to set up the configuration database settings.
 */

public class AMSInternalMessagingTopicsSetUp extends AbstractNewAMSFieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public AMSInternalMessagingTopicsSetUp() {
		this.setDescription("Set up of nams internal messaging topics");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	protected void createFieldEditors() {
		// JMS
		// - command
		this.addField(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND);

		// - command dd
		this
				.addField(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND_DECISSION_DEPARTMENT_SUBSCRIBER_ID);

		// - dd outbox
		this.addField(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_DD_OUTBOX);

		// - dd outbox sub
		this.addField(PreferenceServiceJMSKeys.P_JMS_AMS_TSUB_DD_OUTBOX);

		addSeparator();
		
		// - distributors inbox
		this.addField(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_DISTRIBUTOR);

		// - distributors inbox sub
		this.addField(PreferenceServiceJMSKeys.P_JMS_AMS_TSUB_DISTRIBUTOR);

		// - reply topic
		this.addField(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_REPLY);

		// - reply topic sub
		this.addField(PreferenceServiceJMSKeys.P_JMS_AMS_TSUB_REPLY);

		addSeparator();
		
		// - SMS
		this.addField(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_SMS_CONNECTOR);

		// - SMS sub
		this.addField(PreferenceServiceJMSKeys.P_JMS_AMS_TSUB_SMS_CONNECTOR);
		
		// EMAIL
		
		// VOICE MAIL
		
		// JMS TOPIC
		
		
		// TODO mz 2008-07-17 More internal fields??...
	}
}