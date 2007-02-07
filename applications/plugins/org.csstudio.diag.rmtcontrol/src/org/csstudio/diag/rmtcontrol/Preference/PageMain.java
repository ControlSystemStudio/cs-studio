package org.csstudio.diag.rmtcontrol.Preference;

import org.csstudio.diag.rmtcontrol.Activator;
import org.csstudio.diag.rmtcontrol.Preference.SampleService;
import org.csstudio.diag.rmtcontrol.Messages;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PageMain extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public PageMain() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
//		setDescription(Messages.getString("PreferencePage.LDAP")); //$NON-NLS-1$

	}

	@Override
	protected void createFieldEditors() {
		FileFieldEditor ffe = new FileFieldEditor(SampleService.RMT_XML_FILE_PATH,Messages.getString("PageMain.File"),getFieldEditorParent()); //$NON-NLS-1$
		ffe.setFileExtensions(new String[]{"*.xml"}); //$NON-NLS-1$
		ffe.setStringValue("rmt.xml"); //$NON-NLS-1$
		addField(ffe);
	}

	public void init(IWorkbench workbench) {	}

}
