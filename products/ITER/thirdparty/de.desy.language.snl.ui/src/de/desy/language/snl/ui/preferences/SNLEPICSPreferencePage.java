package de.desy.language.snl.ui.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.desy.language.snl.configurationservice.PreferenceConstants;
import de.desy.language.snl.ui.SNLUiActivator;

/**
 * A preference page to set the colors and the font-styles for highlighting.
 */
public class SNLEPICSPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public SNLEPICSPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		this.setMessage("Specifies compiler location and options.");
		this.setPreferenceStore(SNLUiActivator.getDefault()
				.getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		Group groupOfLocationElement = new Group(getFieldEditorParent(),
				SWT.NONE);
		groupOfLocationElement
				.setText("Location of the EPICS environment (BASE-path)");
		groupOfLocationElement.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		this.addField(new DirectoryFieldEditor(SNLUiActivator.PLUGIN_ID
				+ PreferenceConstants.EPICS_BASE_LOCATION_POST_FIX, "",
				groupOfLocationElement));
		
		Group seqGroup = new Group(getFieldEditorParent(),
				SWT.NONE);
		seqGroup
				.setText("Location of \"seq\" folder in the EPICS environment");
		seqGroup.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		this.addField(new DirectoryFieldEditor(SNLUiActivator.PLUGIN_ID
				+ PreferenceConstants.EPICS_SEQ_LOCATION_POST_FIX, "",
				seqGroup));
	}

	@Override
	protected void adjustGridLayout() {
		final int numColumns = 1;
		((GridLayout) this.getFieldEditorParent().getLayout()).numColumns = numColumns;
	}

	public void init(final IWorkbench workbench) {
		// do nothing
	}

}
