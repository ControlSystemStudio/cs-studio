package org.csstudio.alarm.dbaccess.preferences;

import org.csstudio.alarm.dbaccess.archivedb.Activator;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class AlarmArchiveDBPreferences extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public AlarmArchiveDBPreferences() {
		super();
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		Composite fieldEditorParent = getFieldEditorParent();
		StringFieldEditor dbString = new StringFieldEditor(AlarmArchiveDBPreferenceConstants.DB_CONNECTION_STRING,"Database Connection String", 120, fieldEditorParent);
		addField(dbString);
		StringFieldEditor dbUser = new StringFieldEditor(AlarmArchiveDBPreferenceConstants.DB_USER, "Username", fieldEditorParent);
		addField(dbUser);
		StringFieldEditor dbPassword = new StringFieldEditor(AlarmArchiveDBPreferenceConstants.DB_PASSWORD, "Password", fieldEditorParent);
        dbPassword.getTextControl(fieldEditorParent).setEchoChar('*');
		addField(dbPassword);
		
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}

}
