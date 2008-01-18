package org.csstudio.diag.IOCremoteManagement.Preference;

import org.csstudio.diag.IOCremoteManagement.Activator;
import org.csstudio.diag.IOCremoteManagement.Messages;
import org.csstudio.diag.IOCremoteManagement.Preference.SampleService;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/** IOCremoteManagement prefs.
 *  @author Albert Kagarmanov
 */
public class PreferencePage extends FieldEditorPreferencePage
                implements IWorkbenchPreferencePage
{

    public PreferencePage()
    {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription(Messages.PreferencePage_Title);
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
        final StringFieldEditor host_editor =new StringFieldEditor(SampleService.IOC_LIST, 
        		Messages.PreferencePage_Hosts, parent);
        addField(host_editor);
    }
    
    /** @return hostArr */
    static public String getHosts()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        return store.getString(SampleService.IOC_LIST);
    }
}
