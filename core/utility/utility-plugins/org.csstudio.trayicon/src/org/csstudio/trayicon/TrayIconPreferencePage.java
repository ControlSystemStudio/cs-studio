package org.csstudio.trayicon;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class TrayIconPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public static final String MINIMIZE_TO_TRAY = "minimize_to_tray";
    public static final String START_MINIMIZED = "start_minimized";
    private ScopedPreferenceStore store;

    @Override
    protected void createFieldEditors() {
        final Composite parent = getFieldEditorParent();

        RadioGroupFieldEditor minimizeOnCloseEditor = new RadioGroupFieldEditor(
                MINIMIZE_TO_TRAY,
                Messages.TrayPreferences_minimize, 3,
                new String[][] {{Messages.TrayPreferences_always, MessageDialogWithToggle.ALWAYS},
                                {Messages.TrayPreferences_never, MessageDialogWithToggle.NEVER},
                                {Messages.TrayPreferences_prompt, MessageDialogWithToggle.PROMPT}},
                parent, true);
        addField(minimizeOnCloseEditor);
        BooleanFieldEditor startMinimizedEditor = new BooleanFieldEditor(START_MINIMIZED,
                Messages.TrayPreferences_startMinimized, BooleanFieldEditor.DEFAULT, parent);
        addField(startMinimizedEditor);
    }

    @Override
    public void init(IWorkbench workbench) {
        store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Plugin.ID);
        setPreferenceStore(store);
    }

}
