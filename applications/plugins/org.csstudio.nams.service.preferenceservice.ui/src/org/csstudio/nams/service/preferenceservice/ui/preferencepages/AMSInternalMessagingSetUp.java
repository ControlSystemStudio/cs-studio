package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceJMSKeys;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This preference page is used to set up the configuration database settings.
 */

public class AMSInternalMessagingSetUp
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	private static final int TEXTCOLUMS = 64;

	public static void staticInject(IPreferenceStore preferenceStore) {
		AMSInternalMessagingSetUp.preferenceStore = preferenceStore;
	}

	private static IPreferenceStore preferenceStore;

	public AMSInternalMessagingSetUp() {
		super(GRID);
		
		if( AMSInternalMessagingSetUp.preferenceStore == null ) {
			throw new RuntimeException("class has not been equiped, missing: preference store");
		}
		setPreferenceStore(AMSInternalMessagingSetUp.preferenceStore);
		setDescription("Set up of nams internal messaging");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		// JMS
		Label serversLabel = new Label(getFieldEditorParent(), SWT.NONE);
		GridDataFactory.generate(serversLabel, 2, 1);
		serversLabel.setText("Server");
		addField(
			new StringFieldEditor(
					PreferenceServiceJMSKeys.P_JMS_AMS_CONNECTION_FACTORY_CLASS.getPreferenceStoreId(), 
					PreferenceServiceJMSKeys.P_JMS_AMS_CONNECTION_FACTORY_CLASS.getDescription() +":", 
					TEXTCOLUMS, 
					getFieldEditorParent()
			)
		);
		addField(
				new StringFieldEditor(
						PreferenceServiceJMSKeys.P_JMS_AMS_CONNECTION_FACTORY.getPreferenceStoreId(), 
						PreferenceServiceJMSKeys.P_JMS_AMS_CONNECTION_FACTORY.getDescription() +":", 
						TEXTCOLUMS, 
						getFieldEditorParent()
				)
		);
		addField(
				new StringFieldEditor(
						PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_1.getPreferenceStoreId(), 
						PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_1.getDescription() +":", 
						TEXTCOLUMS, 
						getFieldEditorParent()
				)
		);
		addField(
				new StringFieldEditor(
						PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_2.getPreferenceStoreId(), 
						PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_2.getDescription() +":", 
						TEXTCOLUMS, 
						getFieldEditorParent()
				)
		);
		addField(
				new StringFieldEditor(
						PreferenceServiceJMSKeys.P_JMS_AMS_SENDER_PROVIDER_URL.getPreferenceStoreId(), 
						PreferenceServiceJMSKeys.P_JMS_AMS_SENDER_PROVIDER_URL.getDescription() +":", 
						TEXTCOLUMS, 
						getFieldEditorParent()
				)
		);
		// Topics
		Label topicLabel = new Label(getFieldEditorParent(), SWT.NONE);
		GridDataFactory.generate(topicLabel, 2, 1);
		topicLabel.setText("Topics");
		// - command
		addField(
				new StringFieldEditor(
						PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND.getPreferenceStoreId(), 
						PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND.getDescription() +":", 
						TEXTCOLUMS, 
						getFieldEditorParent()
				)
		);
		addField(
				new StringFieldEditor(
						PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND_DECISSION_DEPARTMENT_SUBSCRIBER_ID.getPreferenceStoreId(), 
						PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND_DECISSION_DEPARTMENT_SUBSCRIBER_ID.getDescription() +":", 
						TEXTCOLUMS, 
						getFieldEditorParent()
				)
		);
		
		// - 
		
		// TODO mz 2008-07-17 More internal fields??...
	}

	public void init(IWorkbench workbench) {
	}
	
}