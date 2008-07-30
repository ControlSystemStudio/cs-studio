package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceJMSKeys;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This preference page is used to set up the configuration database settings.
 */

public class AMSInternalMessagingTopicsSetUp extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	private static final int TEXTCOLUMS = 64;

	private static IPreferenceStore preferenceStore;

	public static void staticInject(final IPreferenceStore preferenceStore) {
		AMSInternalMessagingTopicsSetUp.preferenceStore = preferenceStore;
	}

	public AMSInternalMessagingTopicsSetUp() {
		super(FieldEditorPreferencePage.GRID);

		if (AMSInternalMessagingTopicsSetUp.preferenceStore == null) {
			throw new RuntimeException(
					"class has not been equiped, missing: preference store");
		}
		this
				.setPreferenceStore(AMSInternalMessagingTopicsSetUp.preferenceStore);
		this.setDescription("Set up of nams internal messaging topics");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		// JMS
		// - command
		this.addField(new StringFieldEditor(
				PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND
						.getPreferenceStoreId(),
				PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND
						.getDescription()
						+ ":", AMSInternalMessagingTopicsSetUp.TEXTCOLUMS, this
						.getFieldEditorParent()));
		this
				.addField(new StringFieldEditor(
						PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND_DECISSION_DEPARTMENT_SUBSCRIBER_ID
								.getPreferenceStoreId(),
						PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND_DECISSION_DEPARTMENT_SUBSCRIBER_ID
								.getDescription()
								+ ":",
						AMSInternalMessagingTopicsSetUp.TEXTCOLUMS, this
								.getFieldEditorParent()));

		// -

		// TODO mz 2008-07-17 More internal fields??...
	}

	public void init(final IWorkbench workbench) {
	}

}