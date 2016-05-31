/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.preferences;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**The preference page for OPIBuilder
 * @author Xihui Chen
 *
 */
public class OPIEditorPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    // private static final String RESTART_MESSAGE = "Changes only takes effect after restart.";


    public OPIEditorPreferencePage() {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(OPIBuilderPlugin.getDefault().getPreferenceStore());

        setMessage("OPI Editor Preferences");
    }

    @Override
    protected void createFieldEditors() {
        final Composite parent = getFieldEditorParent();

        WorkspaceFileFieldEditor schemaOPIEditor =
            new WorkspaceFileFieldEditor(PreferencesHelper.SCHEMA_OPI,
                "Schema OPI: ", new String[]{"opi"}, parent);//$NON-NLS-2$
        schemaOPIEditor.getTextControl(parent).setToolTipText(
                "The opi file that defines the default widget properties value");
        addField(schemaOPIEditor);

        BooleanFieldEditor autoSaveEditor =
            new BooleanFieldEditor(PreferencesHelper.AUTOSAVE,
                    "Automatically save file before running.", parent);
        addField(autoSaveEditor);

        RadioGroupFieldEditor perspectiveEditor = new RadioGroupFieldEditor(
                PreferencesHelper.SWITCH_TO_OPI_EDITOR_PERSPECTIVE,
                "Switch to OPI Editor perspective when opening opi file?", 3,
                new String[][] {{"Always", MessageDialogWithToggle.ALWAYS},
                                {"Never", MessageDialogWithToggle.NEVER},
                                {"Prompt", MessageDialogWithToggle.PROMPT}},
                parent, true);
        addField(perspectiveEditor);

    }

    @Override
    public void init(IWorkbench workbench) {

    }

    @Override
    public boolean performOk() {
        if(!isValid())
            return false;
        return super.performOk();
    }

}
