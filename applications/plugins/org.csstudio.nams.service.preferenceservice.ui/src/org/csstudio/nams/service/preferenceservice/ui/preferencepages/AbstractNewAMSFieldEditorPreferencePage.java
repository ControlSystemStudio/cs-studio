
package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.preferenceservice.declaration.HoldsAPreferenceId;
import org.csstudio.nams.service.preferenceservice.ui.Messages;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public abstract class AbstractNewAMSFieldEditorPreferencePage extends
		FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final int TEXTCOLUMS = 64;

	private static IPreferenceStore preferenceStore;

	public static void staticInject(final IPreferenceStore preferenceStore) {
		AbstractNewAMSFieldEditorPreferencePage.preferenceStore = preferenceStore;
	}

	public AbstractNewAMSFieldEditorPreferencePage() {
		super(FieldEditorPreferencePage.GRID);

		if (AbstractNewAMSFieldEditorPreferencePage.preferenceStore == null) {
			throw new RuntimeException(
					"class has not been equiped, missing: preference store"); //$NON-NLS-1$
		}
		this
				.setPreferenceStore(AbstractNewAMSFieldEditorPreferencePage.preferenceStore);
	}

	@Override
    public void init(IWorkbench workbench) {
		// By default nothing to do.
	}

	protected void addField(HoldsAPreferenceId idAndName) {
		this.addField(new StringFieldEditor(idAndName.getPreferenceStoreId(),
				idAndName.getDescription() + Messages.AbstractNewAMSFieldEditorPreferencePage_separator_between_label_and_field,
				AbstractNewAMSFieldEditorPreferencePage.TEXTCOLUMS, this
						.getFieldEditorParent()));
	}

	protected void addDatabaseTypeField(HoldsAPreferenceId idAndName) {
		this.addField(new ComboFieldEditor(idAndName.getPreferenceStoreId(),
				idAndName.getDescription() + Messages.AbstractNewAMSFieldEditorPreferencePage_separator_between_label_and_field, this
						.getDatabaseTypesValuesForDropDown(), this
						.getFieldEditorParent()));
	}

	protected void addSeparator() {
		new Label(this.getFieldEditorParent(), SWT.NONE);
		new Label(this.getFieldEditorParent(), SWT.NONE);
	}

	private String[][] getDatabaseTypesValuesForDropDown() {
		final DatabaseType[] databaseTypes = DatabaseType.values();
		final String[][] result = new String[databaseTypes.length][2];

		for (int index = 0; index < databaseTypes.length; index++) {
			result[index][0] = databaseTypes[index].getHumanReadableName();
			result[index][1] = databaseTypes[index].name();
		}

		return result;
	}
}
