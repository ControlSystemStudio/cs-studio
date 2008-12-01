package org.csstudio.debugging.jmsmonitor;

import org.csstudio.apputil.ui.jface.preference.PasswordFieldEditor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Preference GUI for JMS Monitor.
 *  <p>
 *  plugin.xml registers this with the Eclipse preference GUI
 *  @author Kay Kasemir
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
        setMessage(Messages.JMSMonitorPrefs);
    }
        
    /** {@inheritDoc */
    public void init(IWorkbench workbench)
    { /* NOP */ }

    /** {@inheritDoc */
    @Override
    protected void createFieldEditors()
    {
        final Composite parent = getFieldEditorParent();
        addField(new StringFieldEditor(Preferences.JMS_URL, Messages.Preferences_JMS_URL, parent));
        addField(new StringFieldEditor(Preferences.JMS_USER, Messages.Preferences_JMS_USER, parent));
        addField(new PasswordFieldEditor(Preferences.JMS_PASSWORD, Messages.Preferences_JMS_PASSWORD, parent));
    }
}
