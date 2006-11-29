package org.csstudio.platform.libs.epics.preferences;

import org.csstudio.platform.libs.epics.EpicsPlugin;
import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class EpicsPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public EpicsPreferencePage() {
		super(GRID);
		setPreferenceStore(EpicsPlugin.getDefault().getPreferenceStore());
		setDescription("CAJ Preferences");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.constants[0], PreferenceConstants.constants[0] + ":", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.constants[1], PreferenceConstants.constants[1] + ":", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.constants[2], PreferenceConstants.constants[2] + ":", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.constants[3], PreferenceConstants.constants[3] + ":", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.constants[4], PreferenceConstants.constants[4] + ":", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.constants[5], PreferenceConstants.constants[5] + ":", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.constants[6], PreferenceConstants.constants[6] + ":", getFieldEditorParent()));
	}
	
	public boolean performOk(){
		boolean ret = super.performOk();
		EpicsPlugin.getDefault().installPreferences();
		return ret;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}