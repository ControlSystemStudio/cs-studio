/**
 * 
 */
package org.csstudio.sds.ui.internal.preferences;

import org.csstudio.domain.common.ui.WorkspaceDirectoryFieldEditor;
import org.csstudio.domain.common.ui.WorkspaceFileFieldEditor;
import org.csstudio.sds.cosyrules.color.MaintenanceRulePreference;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
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
        setPreferenceStore(new ScopedPreferenceStore(new InstanceScope(),
                                                     MaintenanceRulePreference.MAINTENANCE_DISPLAY_PATH
                                                             .getPluginID()));
        setDescription("Das Display setzt sich aus den Pfad, dem Name und dem RTYP zusammen.\nDer Filename ist dabei so anzugeben das {rtyp} durch den RTYP ersetzt wird.\n(Bsp.: Mein{rtyp}File.css-sds");
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
     */
    @Override
    protected void createFieldEditors() {
        
        WorkspaceDirectoryFieldEditor displayPathFieldEditor = new WorkspaceDirectoryFieldEditor(MaintenanceRulePreference.MAINTENANCE_DISPLAY_PATH
                                                                                 .getKeyAsString(),
                                                                         "Display Pfad",
                                                                         getFieldEditorParent());
        
        addField(displayPathFieldEditor);
        StringFieldEditor preFileNameFielsEditor = new StringFieldEditor(MaintenanceRulePreference.MAINTENANCE_PRE_FILE_NAME
                                                                                 .getKeyAsString(),
                                                                         "Display Name",
                                                                         getFieldEditorParent());
        addField(preFileNameFielsEditor);
        
        WorkspaceFileFieldEditor unknownDisplayPathFieldEditor = new WorkspaceFileFieldEditor(MaintenanceRulePreference.MAINTENANCE_UNKNOWN_DISPLAY_PATH
                                                                                              .getKeyAsString(),
                                                                                              "Unknown Display",
                                                                                              getFieldEditorParent());
        unknownDisplayPathFieldEditor.setFilter(new ViewerFilter() {
            
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof IFile) {
                    IFile file = (IFile) element;
                    if (file != null && file.getFileExtension() != null) {
                        return file.getFileExtension().toLowerCase().equals("css-sds");
                    }
                    return false;
                }
                return true;
            }
        });
        
        addField(unknownDisplayPathFieldEditor);
    }
    
}
