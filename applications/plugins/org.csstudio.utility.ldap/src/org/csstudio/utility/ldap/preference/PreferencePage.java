package org.csstudio.utility.ldap.preference;

import org.csstudio.utility.ldap.Activator;
import org.csstudio.utility.ldap.Messages;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Messages.getString("PreferencePage.LDAP")); //$NON-NLS-1$
	}

	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {

		addField(
			new StringFieldEditor(PreferenceConstants.P_STRING_URL, Messages.getString("PreferencePage.URL"), getFieldEditorParent())); //$NON-NLS-1$
		addField(
				new StringFieldEditor(PreferenceConstants.P_STRING_USER_DN, Messages.getString("PreferencePage.DN"), getFieldEditorParent())); //$NON-NLS-1$
		StringFieldEditor field = new StringFieldEditor(PreferenceConstants.P_STRING_USER_PASSWORD, Messages.getString("PreferencePage.PASS"), getFieldEditorParent()); //$NON-NLS-1$
		field.getTextControl(getFieldEditorParent()).setEchoChar('*');
		addField(field);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}


}
