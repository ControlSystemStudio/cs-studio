package org.csstudio.archivereader.rdb;

import org.csstudio.platform.ui.security.PasswordFieldEditor;
import org.eclipse.core.runtime.preferences.InstanceScope;
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
        setMessage(Messages.PreferenceTitle);
        final Composite parent = getFieldEditorParent();
        addField(new StringFieldEditor(Preferences.USER, Messages.User, parent));
        addField(new PasswordFieldEditor(Preferences.PASSWORD, Messages.Password, parent, Activator.ID));
        addField(new StringFieldEditor(Preferences.SCHEMA, Messages.Schema, parent));
        addField(new StringFieldEditor(Preferences.STORED_PROCEDURE, Messages.StoredProcedure, parent));
    }
}
