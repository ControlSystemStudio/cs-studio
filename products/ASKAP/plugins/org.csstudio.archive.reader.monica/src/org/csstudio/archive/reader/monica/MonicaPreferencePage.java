package org.csstudio.archive.reader.monica;

import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class MonicaPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public MonicaPreferencePage() {
        super(GRID);

        final IScopeContext scope = InstanceScope.INSTANCE;
        // 'main' pref. store for most of the settings
		setPreferenceStore(new ScopedPreferenceStore(scope,
                org.csstudio.archive.reader.monica.Activator.ID));
	}

	@Override
	public void init(IWorkbench workbench) {
		// nothing to do
	}

	@Override
	protected void createFieldEditors() {
		setMessage("Monica Settings");
		final Composite parent = getFieldEditorParent();
		addField(new StringFieldEditor(Preferences.ADAPTORNAME, "Adaptor Name:", parent));
	}

}
