package org.csstudio.askap.utility.icemanager;

import org.csstudio.opibuilder.preferences.MacroEditDialog;
import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.preferences.StringTableFieldEditor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.jdom.Verifier;

public class PreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

    private StringTableFieldEditor macrosEditor;
    private String wrongMacroName = "";

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
		//FileFieldEditor fileEditor = new FileFieldEditor(
		//		Preferences.ICE_PROPERTIES_FILE, "ICE Properties File:",
		//		parent);
		//addField(fileEditor);
		
/// Replaced with StringTableFieldEditor		
		addField(new StringFieldEditor(Preferences.ICESTORM_TOPICMANAGER_NAME, "Topic Manager Name:", parent));		
		macrosEditor = new StringTableFieldEditor(
				Preferences.ICE_PROPERTIES, "ICE Properties: " , parent, new String[]{"Name", "Value"}, 
				new boolean[]{true, true}, new MacroEditDialog(parent.getShell()), new int[]{120, 120}){
			
			@Override
			public boolean isValid() {
				String reason;
				for(String[] row : items){
					reason = Verifier.checkElementName(row[0]);
					if(reason != null){
						wrongMacroName = row[0];
						return false;
					}
				}
				return true;
			}
			
			
			@Override
			protected void doStore() {
				if(!isValid())
					return;
				super.doStore();
			}
			
			@Override
			protected void doFillIntoGrid(Composite parent,
							int numColumns) {
				super.doFillIntoGrid(parent, numColumns);
				tableEditor.getTableViewer().getTable().addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
						boolean valid = isValid();
						fireStateChanged(IS_VALID, !valid, valid);
					}
				});
				tableEditor.getTableViewer().getTable().addFocusListener(new FocusListener() {
							
					public void focusLost(FocusEvent e) {
						boolean valid = isValid();
						fireStateChanged(IS_VALID, !valid, valid);							}
							
					public void focusGained(FocusEvent e) {
						boolean valid = isValid();
						fireStateChanged(IS_VALID, !valid, valid);							}
				});
			}
					
		};
		addField(macrosEditor);		

	}

}
