package org.csstudio.logbook.midas;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    public PreferencePage() {
        super(GRID);
        final IPreferenceStore store =
                new ScopedPreferenceStore(new InstanceScope(),
                                          org.csstudio.logbook.midas.Activator.ID);
            setPreferenceStore(store);
            setMessage(Messages.PreferenceTitle);

    }


    @Override
    public void init(IWorkbench workbench) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void createFieldEditors()
    {
        setMessage(Messages.PreferenceTitle);
        final Composite parent = getFieldEditorParent();

        addField(new StringFieldEditor(LogbookMidasPreferences.HOST, Messages.Host, parent));
        addField(new StringFieldEditor(LogbookMidasPreferences.PORT, Messages.Port, parent));
    }

}
