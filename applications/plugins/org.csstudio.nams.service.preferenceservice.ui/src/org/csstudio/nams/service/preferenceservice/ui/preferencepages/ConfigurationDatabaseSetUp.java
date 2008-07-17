package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This preference page is used to set up the configuration database settings.
 */

public class ConfigurationDatabaseSetUp
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	private static final int TEXTCOLUMS = 64;

	public static void staticInject(IPreferenceStore preferenceStore) {
		ConfigurationDatabaseSetUp.preferenceStore = preferenceStore;
	}

	private static IPreferenceStore preferenceStore;

	public ConfigurationDatabaseSetUp() {
		super(GRID);
		
		if( ConfigurationDatabaseSetUp.preferenceStore == null ) {
			throw new RuntimeException("class has not been equiped, missing: preference store");
		}
		setPreferenceStore(ConfigurationDatabaseSetUp.preferenceStore);
		setDescription("A demonstration of a preference page implementation");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
//		addField(new DirectoryFieldEditor(PreferenceConstants.P_PATH, 
//				"&Directory preference:", getFieldEditorParent()));
//		addField(
//			new BooleanFieldEditor(
//				PreferenceConstants.P_BOOLEAN,
//				"&An example of a boolean preference",
//				getFieldEditorParent()));
//
//		addField(new RadioGroupFieldEditor(
//				PreferenceConstants.P_CHOICE,
//			"An example of a multiple-choice preference",
//			1,
//			new String[][] { { "&Choice 1", "choice1" }, {
//				"C&hoice 2", "choice2" }
//		}, getFieldEditorParent()));
		addField(
			new StringFieldEditor(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION.getPreferenceStoreId(), "Configuration database jdbc-url:", TEXTCOLUMS, getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}
	
}