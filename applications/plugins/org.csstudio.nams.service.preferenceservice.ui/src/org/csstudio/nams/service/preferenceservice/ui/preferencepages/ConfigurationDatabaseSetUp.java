package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.eclipse.jface.preference.ComboFieldEditor;
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
		setDescription("Set up of global configuration database");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(
				new ComboFieldEditor(
						PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_TYPE.getPreferenceStoreId(),
						PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_TYPE.getDescription() + ":",
						getDatabaseTypesValuesForDropDown(),
						getFieldEditorParent()
				)
		);
		addField(
			new StringFieldEditor(
					PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION.getPreferenceStoreId(), 
					PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION.getDescription() +":", 
					TEXTCOLUMS, 
					getFieldEditorParent()
			)
		);
		addField(
				new StringFieldEditor(
						PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER.getPreferenceStoreId(), 
						PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER.getDescription() +":", 
						TEXTCOLUMS, 
						getFieldEditorParent()
				)
		);
		addField(
				new StringFieldEditor(
						PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD.getPreferenceStoreId(), 
						PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD.getDescription() +":", 
						TEXTCOLUMS, 
						getFieldEditorParent()
				)
		);
	}

	private String[][] getDatabaseTypesValuesForDropDown() {
		DatabaseType[] databaseTypes = DatabaseType.values();
		String[][] result = new String[databaseTypes.length][2];
		
		for (int index = 0; index < databaseTypes.length; index++) {
			result[index][0] = databaseTypes[index].getHumanReadableName();
			result[index][1] = databaseTypes[index].name();
		}
		
		return result;
	}
	
	public void init(IWorkbench workbench) {
	}
	
}