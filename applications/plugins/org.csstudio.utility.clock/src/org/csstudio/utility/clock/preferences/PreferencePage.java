package org.csstudio.utility.clock.preferences;

import org.csstudio.utility.clock.Messages;
import org.csstudio.utility.clock.Plugin;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/** Clock prefs.
 *  @author Kay Kasemir
 */
public class PreferencePage extends FieldEditorPreferencePage
                implements IWorkbenchPreferencePage
{
    /** Preference ID (also used in preferences.ini) */
    final private static  String P_HOURS = "hours"; //$NON-NLS-1$
    
    /** Minimum value */
    final private static int min = 24;
    
    /** Maximum value */
    final private static int max = 35;

    public PreferencePage()
    {
        setPreferenceStore(Plugin.getDefault().getPreferenceStore());
        setMessage(Messages.PreferencePage_Title);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench)
    { /* NOP */ }

    /** Creates the field editors.
     *  Each one knows how to save and restore itself.
     */
    @Override
    public void createFieldEditors()
    {
        final Composite parent = getFieldEditorParent();
        final IntegerFieldEditor hour_editor =
            new IntegerFieldEditor(P_HOURS, Messages.PreferencePage_Hours, parent);
        hour_editor.setErrorMessage(NLS.bind(Messages.PreferencePage_ErrorMsg,
                                             min, max));
        hour_editor.setValidRange(min, max);
        addField(hour_editor);
    }
    
    /** @return Total hour setting */
    static public int getHours()
    {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        return store.getInt(P_HOURS);
    }
}
