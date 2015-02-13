package org.csstudio.askap.utility;

import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	

	public PreferencePage() {
        super(GRID);

        final IScopeContext scope = InstanceScope.INSTANCE;
        // 'main' pref. store for most of the settings
		setPreferenceStore(new ScopedPreferenceStore(scope, org.csstudio.askap.utility.Activator.PLUGIN_ID));
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		setMessage("ASKAP Settings");
		final Composite parent = getFieldEditorParent();
		FileFieldEditor fileEditor = new FileFieldEditor(
				Preferences.NAVIGATOR_CONFIGURATION_FILE, "ASKAP Navigator File:",
				parent);
		addField(fileEditor);

		DirectoryFieldEditor askapDirectory = new DirectoryFieldEditor(
				Preferences.ASKAP_OPI_RELEASE_LOCATION, "ASKAP opi Directory:",
				parent);		
		addField(askapDirectory);
		
		addField(new ComboFieldEditor(Preferences.DATE_TIME_FORMAT, "Date time format:", 
				new String[][]{ {"LOCAL", "LOCAL"}, {"UTC", "UTC"} },
				parent));

	}


}
