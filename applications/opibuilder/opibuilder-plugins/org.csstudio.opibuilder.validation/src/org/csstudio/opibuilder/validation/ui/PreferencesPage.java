/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.ui;

import org.csstudio.opibuilder.preferences.WorkspaceFileFieldEditor;
import org.csstudio.opibuilder.validation.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 *
 * <code>PreferencesEditor</code> provides the preferences settings page for the OPI validation settings.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class PreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private BooleanFieldEditor makeBackup;

    /**
     * Constructs a new preferences page.
     */
    public PreferencesPage() {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(Activator.getInstance().getPreferenceStore());
        setMessage("OPI Validation Properties");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(IWorkbench workbench) {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
     */
    @Override
    protected void createFieldEditors() {
        final Composite parent = getFieldEditorParent();

        WorkspaceFileFieldEditor rules = new WorkspaceFileFieldEditor(Activator.PREF_RULES_FILE, "Validation Rules: ",
            new String[] { "def" }, parent);
        rules.getTextControl(parent).setToolTipText(
            "The file that defines the rules for validation of properties (read-only, write, read/write)");
        addField(rules);

        BooleanFieldEditor askForBackup = new BooleanFieldEditor(Activator.PREF_SHOW_BACKUP_DIALOG,
            "Ask to do backup before applying quick fix?", parent) {
            private Button button;

            @Override
            protected Button getChangeControl(Composite parent) {
                if (button == null) {
                    button = super.getChangeControl(parent);
                    button.addSelectionListener(new SelectionListener() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            makeBackup.setEnabled(!getBooleanValue(), parent);
                        }

                        @Override
                        public void widgetDefaultSelected(SelectionEvent e) {
                        }
                    });
                }
                return button;
            }
        };
        addField(askForBackup);

        makeBackup = new BooleanFieldEditor(Activator.PREF_DO_BACKUP, "Do backup when applying quick fix?", parent);
        makeBackup.setEnabled(!Activator.getInstance().isShowBackupDialog(), parent);
        addField(makeBackup);

        BooleanFieldEditor showSummary = new BooleanFieldEditor(Activator.PREF_SHOW_SUMMARY,
            "Show summary dialog after validation?", parent);
        addField(showSummary);

        BooleanFieldEditor nestMarkers = new BooleanFieldEditor(Activator.PREF_NEST_MARKERS,
            "Nest markers in the Problems View?", parent);
        nestMarkers.getDescriptionControl(parent).setToolTipText(
            "Display sub validation failures (action, script, rule) as children of their parent properties (actions, scripts, rules).");
        addField(nestMarkers);

        BooleanFieldEditor clearMarkers = new BooleanFieldEditor(Activator.PREF_CLEAR_MARKERS,
            "Clear old markers when validation starts?", parent);
        clearMarkers.getDescriptionControl(parent)
            .setToolTipText("Clear all validation markers on every new validation restart");
        addField(clearMarkers);

        BooleanFieldEditor useDefaultEditor = new BooleanFieldEditor(Activator.PREF_CLEAR_MARKERS,
            "Display markers in default editor?", parent);
        useDefaultEditor.getDescriptionControl(parent).setToolTipText(
            "If checked a validation marker will be displayed in the default editor. If unchecked, the marker will "
                + "always be displayed in the text editor.");
        addField(useDefaultEditor);

        BooleanFieldEditor saveResourcesEditor = new BooleanFieldEditor(Activator.PREF_SAVE_BEFORE_VALIDATION,
            "Save all modified resources automatically prior to validation?", parent);
        addField(saveResourcesEditor);

        BooleanFieldEditor warnAboutJythonScriptsEditor = new BooleanFieldEditor(
            Activator.PREF_WARN_ABOUT_JYTHON_SCRIPTS, "Warn whenever a jython script is attached to a widget?", parent);
        addField(warnAboutJythonScriptsEditor);
    }
}
