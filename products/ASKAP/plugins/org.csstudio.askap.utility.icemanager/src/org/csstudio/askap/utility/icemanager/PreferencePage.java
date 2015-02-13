package org.csstudio.askap.utility.icemanager;

import org.csstudio.opibuilder.preferences.MacroEditDialog;
import org.csstudio.opibuilder.preferences.StringTableFieldEditor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

    private StringTableFieldEditor iceConfigEditor;
 
	public PreferencePage() {
        super(GRID);

        final IScopeContext scope = InstanceScope.INSTANCE;
        // 'main' pref. store for most of the settings
		setPreferenceStore(new ScopedPreferenceStore(scope, Activator.ID));
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		setMessage("ICE Settings");
		final Composite parent = getFieldEditorParent();
		
		addField(new StringFieldEditor(Preferences.ICESTORM_TOPICMANAGER_NAME, "Topic Manager Name:", parent));		
		addField(new StringFieldEditor(Preferences.MONITOR_POINT_POLLING_PERIOD, "Monitor Interface polling period:", parent));		

		iceConfigEditor = new StringTableFieldEditor(
				Preferences.ICE_PROPERTIES, "ICE Properties: " , parent, new String[]{"Name", "Value"}, 
				new boolean[]{true, true}, new MacroEditDialog(parent.getShell()), new int[]{120, 120}){			
		};
		addField(iceConfigEditor);		

	}

}
