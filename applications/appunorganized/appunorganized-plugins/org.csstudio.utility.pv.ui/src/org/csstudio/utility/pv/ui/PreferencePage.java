package org.csstudio.utility.pv.ui;

import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.Preferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Preference GUI for Utility.PV
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
            new ScopedPreferenceStore(InstanceScope.INSTANCE,
                                      org.csstudio.utility.pv.Activator.ID);
        setPreferenceStore(store);
        setMessage(Messages.PreferencePage_Message);
    }

    /** {@inheritDoc */
    @Override
    public void init(IWorkbench workbench)
    { /* NOP */ }

    /** Creates the field editors.
     *  Each one knows how to save and restore itself.
     */
    @Override
    protected void createFieldEditors()
    {
        final Composite parent = getFieldEditorParent();

        try
        {
            final String prefixes[] = PVFactory.getSupportedPrefixes();
            final String values[][] = new String[prefixes.length][2];
            for (int i = 0; i < prefixes.length; i++)
            {
                values[i][0] = prefixes[i] + PVFactory.SEPARATOR;
                values[i][1] = prefixes[i];
            }
            addField(new ComboFieldEditor(Preferences.DEFAULT_TYPE,
                    Messages.PreferencePage_DefaultPV, values, parent));
        }
        catch (Exception ex)
        {
            setMessage("Error: " + ex.getMessage(), ERROR); //$NON-NLS-1$
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event)
    {
        setMessage(Messages.PreferencePage_RestartInfo, INFORMATION);
        super.propertyChange(event);
    }
}
