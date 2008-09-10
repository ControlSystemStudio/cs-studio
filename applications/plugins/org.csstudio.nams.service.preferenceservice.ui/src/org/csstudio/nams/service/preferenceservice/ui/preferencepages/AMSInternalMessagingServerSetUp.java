package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceJMSKeys;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This preference page is used to set up the configuration database settings.
 */

public class AMSInternalMessagingServerSetUp extends AbstractNewAMSFieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	private static final int TEXTCOLUMS = 64;

	public AMSInternalMessagingServerSetUp() {
		this.setDescription("Set up of nams internal messaging server");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		// JMS
		// Label serversLabel = new Label(getFieldEditorParent(), SWT.NONE);
		// GridDataFactory.generate(serversLabel, 2, 1);
		// serversLabel.setText("Server");
		this.addField(new StringFieldEditor(
				PreferenceServiceJMSKeys.P_JMS_AMS_CONNECTION_FACTORY_CLASS
						.getPreferenceStoreId(),
				PreferenceServiceJMSKeys.P_JMS_AMS_CONNECTION_FACTORY_CLASS
						.getDescription()
						+ ":", AMSInternalMessagingServerSetUp.TEXTCOLUMS, this
						.getFieldEditorParent()));
		this.addField(new StringFieldEditor(
				PreferenceServiceJMSKeys.P_JMS_AMS_CONNECTION_FACTORY
						.getPreferenceStoreId(),
				PreferenceServiceJMSKeys.P_JMS_AMS_CONNECTION_FACTORY
						.getDescription()
						+ ":", AMSInternalMessagingServerSetUp.TEXTCOLUMS, this
						.getFieldEditorParent()));
		this.addField(new StringFieldEditor(
				PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_1
						.getPreferenceStoreId(),
				PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_1
						.getDescription()
						+ ":", AMSInternalMessagingServerSetUp.TEXTCOLUMS, this
						.getFieldEditorParent()));
		this.addField(new StringFieldEditor(
				PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_2
						.getPreferenceStoreId(),
				PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_2
						.getDescription()
						+ ":", AMSInternalMessagingServerSetUp.TEXTCOLUMS, this
						.getFieldEditorParent()));
		this.addField(new StringFieldEditor(
				PreferenceServiceJMSKeys.P_JMS_AMS_SENDER_PROVIDER_URL
						.getPreferenceStoreId(),
				PreferenceServiceJMSKeys.P_JMS_AMS_SENDER_PROVIDER_URL
						.getDescription()
						+ ":", AMSInternalMessagingServerSetUp.TEXTCOLUMS, this
						.getFieldEditorParent()));
		// Topics
		// Label topicLabel = new Label(getFieldEditorParent(), SWT.NONE);
		// GridDataFactory.generate(topicLabel, 2, 1);
		// topicLabel.setText("Topics");
		// // - command
		// addField(
		// new StringFieldEditor(
		// PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND.getPreferenceStoreId(),
		// PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND.getDescription()
		// +":",
		// TEXTCOLUMS,
		// getFieldEditorParent()
		// )
		// );
		// addField(
		// new StringFieldEditor(
		// PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND_DECISSION_DEPARTMENT_SUBSCRIBER_ID.getPreferenceStoreId(),
		// PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND_DECISSION_DEPARTMENT_SUBSCRIBER_ID.getDescription()
		// +":",
		// TEXTCOLUMS,
		// getFieldEditorParent()
		// )
		// );

		// -

		// TODO mz 2008-07-17 More internal fields??...
	}

}