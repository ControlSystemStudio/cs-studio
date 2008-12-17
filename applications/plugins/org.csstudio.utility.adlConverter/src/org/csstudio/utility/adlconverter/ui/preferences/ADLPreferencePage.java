package org.csstudio.utility.adlconverter.ui.preferences;

import org.csstudio.utility.adlconverter.Activator;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ADLPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    
    public ADLPreferencePage() {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }
    
    @Override
    protected void createFieldEditors() {
        
        DirectoryFieldEditor source = new DirectoryFieldEditor(ADLConverterPreferenceConstants.P_STRING_Path_Source,"Source Path:",getFieldEditorParent());
        addField(source);
        String defPref = getPreferenceStore().getDefaultString(ADLConverterPreferenceConstants.P_STRING_Path_Target);
        String pref = getPreferenceStore().getString(ADLConverterPreferenceConstants.P_STRING_Path_Target);
        if(defPref.equals(pref)){
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IFolder file = root.getFolder(new Path(pref));
            if(!file.exists()){
                getPreferenceStore().setValue(ADLConverterPreferenceConstants.P_STRING_Path_Target, "/NoPath");
            }
        }
        FieldEditor targetFieldEditor = new ContainerFieldEditor(ADLConverterPreferenceConstants.P_STRING_Path_Target,"Target Path:",getFieldEditorParent());
        addField(targetFieldEditor);
        addField(new DirectoryFieldEditor(ADLConverterPreferenceConstants.P_STRING_Path_Relativ_Target,"Relativ Target Path:",getFieldEditorParent()));
        addField(new StringFieldEditor(ADLConverterPreferenceConstants.P_STRING_Path_Remove_Absolut_Part,"Remove absolute path part:", getFieldEditorParent()));
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common
     * GUI blocks needed to manipulate various types of preferences. Each field
     * editor knows how to save and restore itself.
     */
    public void init(IWorkbench workbench) {

    }
}
