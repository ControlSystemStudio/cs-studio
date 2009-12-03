package org.csstudio.opibuilder.preferences;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**The preference page for OPIBuilder
 * @author Xihui Chen
 *
 */
public class OPIBuilderPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	 private static final String RESTART_MESSAGE = "Changes only takes effect after restart.";
	private static final String PREF_QUALIFIER_ID = OPIBuilderPlugin.PLUGIN_ID;
	
	public OPIBuilderPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(new ScopedPreferenceStore(new InstanceScope(), PREF_QUALIFIER_ID));
		setMessage("OPI Builder Preferences");
		
		
	}

	@Override
	protected void createFieldEditors() {
		final Composite parent = getFieldEditorParent();
		
		StringTableFieldEditor macrosEditor = new StringTableFieldEditor(
				PreferencesHelper.RUN_MACROS, "Macros: " , parent, new String[]{"Name", "Value"}, 
				new boolean[]{true, true}, new MacroEditDialog(parent.getShell()), new int[]{120, 120});
		addField(macrosEditor);
		WorkspaceFileFieldEditor colorEditor = 
			new WorkspaceFileFieldEditor(PreferencesHelper.COLOR_FILE, 
					"color file: ", new String[]{"def"}, parent);//$NON-NLS-2$
		colorEditor.setTooltip(RESTART_MESSAGE);
		addField(colorEditor);  
		
		WorkspaceFileFieldEditor fontEditor =
			new WorkspaceFileFieldEditor(PreferencesHelper.FONT_FILE, 
				"font file: ", new String[]{"def"}, parent);//$NON-NLS-2$
		fontEditor.setTooltip(RESTART_MESSAGE);
		addField(fontEditor);
		
		BooleanFieldEditor autoSaveEditor = 
			new BooleanFieldEditor(PreferencesHelper.AUTOSAVE, 
					"Automatically save file before running.", parent);
		addField(autoSaveEditor);
		
		IntegerFieldEditor guiRefreshCycleEditor = 
			new IntegerFieldEditor(PreferencesHelper.OPI_GUI_REFRESH_CYCLE,
					"OPI GUI Refresh Cycle (ms)", parent);
		guiRefreshCycleEditor.setValidRange(10, 5000);
		addField(guiRefreshCycleEditor);
		
		BooleanFieldEditor noEditModeEditor = 
			new BooleanFieldEditor(PreferencesHelper.NO_EDIT, 
					"No editing mode", parent);		
		addField(noEditModeEditor);
		
	}

	public void init(IWorkbench workbench) {
		
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		Object  src = event.getSource();
		if(src instanceof FieldEditor){
			String prefName = ((FieldEditor)src).getPreferenceName();
			if(prefName.equals(PreferencesHelper.FONT_FILE) || 
					prefName.equals(PreferencesHelper.COLOR_FILE) ||
					prefName.equals(PreferencesHelper.OPI_GUI_REFRESH_CYCLE))
				setMessage(RESTART_MESSAGE, WARNING);
		}
	}
}
