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
import org.csstudio.simplepv.SimplePVLayer;
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
import org.jdom.Verifier;

/**The preference page for OPIBuilder
 * @author Xihui Chen
 *
 */
public class OPIRuntimePreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    // private static final String RESTART_MESSAGE = "Changes only takes effect after restart.";
    private static final String RESTART_MESSAGE = "Changes only takes effect after restart.";

    private String wrongMacroName = "";

    private StringTableFieldEditor macrosEditor;

    public OPIRuntimePreferencePage() {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(OPIBuilderPlugin.getDefault().getPreferenceStore());
        setMessage("OPI Runtime Preferences");
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

                    @Override
                    public void focusLost(FocusEvent e) {
                        boolean valid = isValid();
                        fireStateChanged(IS_VALID, !valid, valid);                            }

                    @Override
                    public void focusGained(FocusEvent e) {
                        boolean valid = isValid();
                        fireStateChanged(IS_VALID, !valid, valid);                            }
                });
            }

        };
        addField(macrosEditor);

        IntegerFieldEditor guiRefreshCycleEditor =
            new IntegerFieldEditor(PreferencesHelper.OPI_GUI_REFRESH_CYCLE,
                    "OPI GUI Refresh Cycle (ms)", parent);
        guiRefreshCycleEditor.setValidRange(10, 5000);
        guiRefreshCycleEditor.getTextControl(parent).setToolTipText(
                "The fastest refresh cycle for OPI GUI in millisecond");
        addField(guiRefreshCycleEditor);

        IntegerFieldEditor pulsingMinorPeriodFieldEditor =
                new IntegerFieldEditor(PreferencesHelper.PULSING_ALARM_MINOR_PERIOD,
                        "Time period of MINOR alarm if pulsing alarm selected (ms)", parent);
        pulsingMinorPeriodFieldEditor.setValidRange(100, 10000);
        pulsingMinorPeriodFieldEditor.getTextControl(parent).setToolTipText(
                    "If the pulsing alarm box is checked for a widget that monitors a PV, " +
                    "then what is the time period of the pulse with the PV is in MINOR alarm severity");
        addField(pulsingMinorPeriodFieldEditor);

        IntegerFieldEditor pulsingMajorPeriodFieldEditor =
                new IntegerFieldEditor(PreferencesHelper.PULSING_ALARM_MAJOR_PERIOD,
                        "Time period of MAJOR alarm if pulsing alarm selected (ms)", parent);
        pulsingMajorPeriodFieldEditor.setValidRange(100, 10000);
        pulsingMajorPeriodFieldEditor.getTextControl(parent).setToolTipText(
                    "If the pulsing alarm box is checked for a widget that monitors a PV, " +
                    "then what is the time period of the pulse with the PV is in MAJOR alarm severity");
        addField(pulsingMajorPeriodFieldEditor);

        String[] allPVFactories = SimplePVLayer.getAllPVFactoryExtensions();
        String[][] entries = new String[allPVFactories.length][2];
        for(int i=0; i<allPVFactories.length; i++){
            entries[i][0]=allPVFactories[i];
            entries[i][1]=allPVFactories[i];
        }
        ComboFieldEditor pvConnectionLayerEditor =
                new ComboFieldEditor(PreferencesHelper.PV_CONNECTION_LAYER,
                        "PV Connection Layer", entries, parent);
        addField(pvConnectionLayerEditor);

        ComboFieldEditor popupConsoleEditor =
            new ComboFieldEditor(PreferencesHelper.POPUP_CONSOLE,
                    "Console Popup Level", new String[][]{
                    {"Error, Warning and Info", ConsolePopupLevel.ALL.toString()},
                    {"Only Info", ConsolePopupLevel.ONLY_INFO.toString()},
                    {"Don't Popup", ConsolePopupLevel.NO_POP.toString()}}, parent);
        addField(popupConsoleEditor);

        StringFieldEditor pythonPathEditor =
            new StringFieldEditor(PreferencesHelper.PYTHON_PATH, "PYTHONPATH", parent);
        pythonPathEditor.getTextControl(parent).setToolTipText("The path to search python modules");
        addField(pythonPathEditor);

        BooleanFieldEditor showCompactModeDialogEditor =
            new BooleanFieldEditor(PreferencesHelper.SHOW_COMPACT_MODE_DIALOG,
                    "Show tip dialog about how to exit compact mode", parent);
        addField(showCompactModeDialogEditor);

        BooleanFieldEditor showFullScreenDialogEditor =
            new BooleanFieldEditor(PreferencesHelper.SHOW_FULLSCREEN_DIALOG,
                    "Show tip dialog about how to exit fullscreen", parent);
        addField(showFullScreenDialogEditor);

        BooleanFieldEditor startWindowInCompactEditor =
            new BooleanFieldEditor(PreferencesHelper.START_WINDOW_IN_COMPACT_MODE,
                    "Start application window in compact mode.", parent);
        addField(startWindowInCompactEditor);

        BooleanFieldEditor startWindowInFullScreen =
                new BooleanFieldEditor(PreferencesHelper.START_WINDOW_IN_FULL_SCREEN_MODE,
                        "Start application window in full screen mode.", parent);
            addField(startWindowInFullScreen);

    }

    @Override
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
            }else if(prefName.equals(PreferencesHelper.PYTHON_PATH)){
                setMessage(RESTART_MESSAGE, WARNING);
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
