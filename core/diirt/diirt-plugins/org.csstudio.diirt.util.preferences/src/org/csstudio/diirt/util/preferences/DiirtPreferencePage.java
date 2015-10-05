package org.csstudio.diirt.util.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class DiirtPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    public DiirtPreferencePage() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void createFieldEditors() {
        addField(new DirectoryFieldEditor("diirt.home",
                "&Diirt configuration directory:", getFieldEditorParent()));
    }

    @Override
    public void init(IWorkbench workbench) {
        final IPreferenceStore store = new ScopedPreferenceStore(
                InstanceScope.INSTANCE, "org.csstudio.diirt.util.preferences");
        store.addPropertyChangeListener((PropertyChangeEvent event) -> {
            if (event.getProperty() == "diirt.home") {
                setMessage("Restart is needed", ERROR);
            }
        });
        setPreferenceStore(store);
        setDescription("Diirt preference page");
    }

}
