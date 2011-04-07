/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.preferences;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.preferences.PreferencesHelper.ConsolePopupLevel;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.jdom.Verifier;

/**The preference page for OPIBuilder
 * @author Xihui Chen
 *
 */
public class OPIBuilderPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	// private static final String RESTART_MESSAGE = "Changes only takes effect after restart.";
	private static final String PREF_QUALIFIER_ID = OPIBuilderPlugin.PLUGIN_ID;
	
	private String wrongMacroName = "";

	private StringTableFieldEditor macrosEditor;
	
	public OPIBuilderPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(new ScopedPreferenceStore(new InstanceScope(), PREF_QUALIFIER_ID));
		setMessage("OPI Builder Preferences");
		
		
	}

	@Override
	protected void createFieldEditors() {
		final Composite parent = getFieldEditorParent();
		
		macrosEditor = new StringTableFieldEditor(
				PreferencesHelper.RUN_MACROS, "Macros: " , parent, new String[]{"Name", "Value"}, 
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
		WorkspaceFileFieldEditor colorEditor = 
			new WorkspaceFileFieldEditor(PreferencesHelper.COLOR_FILE, 
					"color file: ", new String[]{"def"}, parent);//$NON-NLS-2$
		addField(colorEditor);  
		
		WorkspaceFileFieldEditor fontEditor =
			new WorkspaceFileFieldEditor(PreferencesHelper.FONT_FILE, 
				"font file: ", new String[]{"def"}, parent);//$NON-NLS-2$
		addField(fontEditor);
		
		IntegerFieldEditor guiRefreshCycleEditor = 
			new IntegerFieldEditor(PreferencesHelper.OPI_GUI_REFRESH_CYCLE,
					"OPI GUI Refresh Cycle (ms)", parent);
		guiRefreshCycleEditor.setValidRange(10, 5000);
		addField(guiRefreshCycleEditor);
		
		BooleanFieldEditor autoSaveEditor = 
			new BooleanFieldEditor(PreferencesHelper.AUTOSAVE, 
					"Automatically save file before running.", parent);
		addField(autoSaveEditor);		
		
		BooleanFieldEditor noEditModeEditor = 
			new BooleanFieldEditor(PreferencesHelper.NO_EDIT, 
					"No-Editing mode", parent);				
		addField(noEditModeEditor);
		
		BooleanFieldEditor advanceGraphicsEditor = 
			new BooleanFieldEditor(PreferencesHelper.DISABLE_ADVANCED_GRAPHICS, 
					"Disable Advanced Graphics", parent);				
		addField(advanceGraphicsEditor);
		
		ComboFieldEditor popupConsoleEditor = 
			new ComboFieldEditor(PreferencesHelper.POPUP_CONSOLE, 
					"Console Popup Level", new String[][]{
					{"Error, Warning and Info", ConsolePopupLevel.ALL.toString()},
					{"Only Info", ConsolePopupLevel.ONLY_INFO.toString()},
					{"Don't Popup", ConsolePopupLevel.NO_POP.toString()}}, parent);				
		addField(popupConsoleEditor);
		
		
		StringFieldEditor topOPIsEditor = 
			new StringFieldEditor(PreferencesHelper.TOP_OPIS, "Top OPIs", parent);
		addField(topOPIsEditor);
		
		WorkspaceFileFieldEditor schemaOPIEditor =
			new WorkspaceFileFieldEditor(PreferencesHelper.SCHEMA_OPI, 
				"Schema OPI: ", new String[]{"opi"}, parent);//$NON-NLS-2$
		addField(schemaOPIEditor);
		
		WorkspaceFileFieldEditor probeOPIEditor =
			new WorkspaceFileFieldEditor(PreferencesHelper.PROBE_OPI, 
				"Probe OPI: ", new String[]{"opi"}, parent);//$NON-NLS-2$
		addField(probeOPIEditor);
	}

	public void init(IWorkbench workbench) {
		
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		Object  src = event.getSource();
		if(src instanceof FieldEditor){
			String prefName = ((FieldEditor)src).getPreferenceName();
			if(prefName.equals(PreferencesHelper.RUN_MACROS)){
				if((Boolean)event.getNewValue())
					setMessage(null);
				else
					setMessage(wrongMacroName + " is not a valid Macro name!", ERROR);
			}
		}
	}
	
	@Override
	public boolean performOk() {
		macrosEditor.tableEditor.getTableViewer().getTable().forceFocus();
		if(!isValid())
			return false;
		return super.performOk();
	}
	
}
