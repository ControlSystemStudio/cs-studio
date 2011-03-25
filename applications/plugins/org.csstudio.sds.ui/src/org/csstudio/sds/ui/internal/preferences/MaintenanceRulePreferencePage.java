/**
 * 
 */
package org.csstudio.sds.ui.internal.preferences;

import org.csstudio.sds.cosyrules.color.MaintenanceRulePreference;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author hrickens
 *
 */
public class MaintenanceRulePreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {
    
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(new ScopedPreferenceStore(new InstanceScope(), MaintenanceRulePreference.MAINTENANCE_DISPLAY_PATHS.getPluginID()));
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
     */
    @Override
    protected void createFieldEditors() {
        MaintenanceRulePathTableFieldEditor editor = new MaintenanceRulePathTableFieldEditor();
        editor.init(MaintenanceRulePreference.MAINTENANCE_DISPLAY_PATHS.getKeyAsString(), "", getFieldEditorParent());
        addField(editor);
    }
    
}
