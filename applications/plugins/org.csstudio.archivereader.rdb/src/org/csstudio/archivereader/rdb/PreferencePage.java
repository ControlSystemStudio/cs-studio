package org.csstudio.archivereader.rdb;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Preference Page for the RDB Archive's user and password.
 *  @author Kay Kasemir
 */
public class PreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage
{
    /** Initialize to use 'instance' scope (install location)
     *  and not workspace!
     */
    public PreferencePage()
    {
        super(GRID);
        setPreferenceStore(new ScopedPreferenceStore(new InstanceScope(),
                Activator.ID));
    }
    
    public void init(IWorkbench workbench)
    {
        // NOP
    }

    @Override
    protected void createFieldEditors()
    {
        setMessage("RDB Archive Reader Settings");
        final Composite parent = getFieldEditorParent();
        addField(new StringFieldEditor(Preferences.USER, "User:", parent));
        addField(new StringFieldEditor(Preferences.PASSWORD, "Password:", parent));
        addField(new StringFieldEditor(Preferences.SCHEMA, "Database Schema:", parent));
        addField(new BooleanFieldEditor(Preferences.STORED_PROCEDURE, "Use stored procedure?", parent));
    }
}
