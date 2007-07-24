package org.csstudio.platform.internal.ldapauthorization.ui;

import org.csstudio.platform.internal.ldapauthorization.PreferenceConstants;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.csstudio.platform.internal.ldapauthorization.Activator;

/**
 * Preference page for the LDAP authorization.
 * 
 * @author Joerg Rathlev
 */
public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(new ScopedPreferenceStore(new InstanceScope(),
				Activator.getDefault().getBundle().getSymbolicName()));
	}
	
	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.LDAP_URL,
				"URL:",
				getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.LDAP_USER,
				"User:",
				getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.LDAP_PASSWORD,
				"Password:",
				getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
		// nothing to do
	}

}
