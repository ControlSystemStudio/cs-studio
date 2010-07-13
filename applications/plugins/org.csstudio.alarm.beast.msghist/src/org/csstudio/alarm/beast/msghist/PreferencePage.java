package org.csstudio.alarm.beast.msghist;

import org.csstudio.platform.ui.security.PasswordFieldEditor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Preference page.
 *  Connected to GUI in plugin.xml
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class PreferencePage extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage
{
    /** Constructor */
	public PreferencePage()
	{
        // This way, preference changes in the GUI end up in a file under
        // {workspace}/.metadata/.plugins/org.eclipse.core.runtime/.settings/,
        // i.e. they are specific to the workspace instance.
        final IPreferenceStore store =
            new ScopedPreferenceStore(new InstanceScope(), Activator.ID);
        setPreferenceStore(store);
        setMessage(Messages.MessageHistory);
	}

    /** {@inheritDoc */
    public void init(IWorkbench workbench)
    { /* NOP */ }
    
    /** {@inheritDoc */
    @Override
    protected void createFieldEditors()
    {
        final Composite parent = getFieldEditorParent();
        addField(new StringFieldEditor(Preferences.RDB_URL, Messages.Pref_URL, parent));
        addField(new PasswordFieldEditor(Preferences.RDB_USER, Messages.Pref_User, parent, Activator.ID, false));
        addField(new PasswordFieldEditor(Preferences.RDB_PASSWORD, Messages.Pref_Password, parent, Activator.ID));
        addField(new StringFieldEditor(Preferences.RDB_SCHEMA, Messages.Pref_Schema, parent));
        addField(new StringFieldEditor(Preferences.START, Messages.Pref_Starttime, parent));
        addField(new TableColumnsFieldEditor(parent));
    }
}
