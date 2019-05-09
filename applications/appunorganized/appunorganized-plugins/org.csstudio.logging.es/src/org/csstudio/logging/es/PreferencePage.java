package org.csstudio.logging.es;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage
{
    /** Constructor */
    public PreferencePage()
    {
        // This way, preference changes in the GUI end up in a file under
        // {workspace}/.metadata/.plugins/org.eclipse.core.runtime/.settings/,
        // i.e. they are specific to the workspace instance.
        final IPreferenceStore store = new ScopedPreferenceStore(
                InstanceScope.INSTANCE, Activator.ID);
        setPreferenceStore(store);
        setMessage(Messages.MessageHistory);
    }

    /** {@inheritDoc} */
    @Override
    protected void createFieldEditors()
    {
        final Composite parent = getFieldEditorParent();
        addField(new StringFieldEditor(Preferences.ES_URL,
                Messages.PreferencePage_ES_URL, parent));
        addField(new StringFieldEditor(Preferences.ES_INDEX,
                Messages.PreferencePage_ES_Index, parent));
        addField(new StringFieldEditor(Preferences.ES_MAPPING,
                Messages.PreferencePage_ES_Mapping, parent));
        addField(new StringFieldEditor(Preferences.JMS_URL,
                Messages.PreferencePage_JMS_URL, parent));
        addField(new StringFieldEditor(Preferences.JMS_USER,
                Messages.PreferencePage_JMS_User, parent));
        addField(new StringFieldEditor(Preferences.JMS_PASSWORD,
                Messages.PreferencePage_JMS_Pass, parent));
        addField(new StringFieldEditor(Preferences.JMS_TOPIC,
                Messages.PreferencePage_JMS_Topic, parent));
        addField(new StringFieldEditor(Preferences.START,
                Messages.PreferencePage_Starttime, parent));
        addField(new TableColumnsFieldEditor(parent));
    }

    /** {@inheritDoc} */
    @Override
    public void init(IWorkbench workbench)
    {
        /* NOP */
    }
}
