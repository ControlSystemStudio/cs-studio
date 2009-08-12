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

import java.io.File;

public class ADLPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    
    public ADLPreferencePage() {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }
    
    /**
     * Checks for the existence of the requested preference both relative
     * to the workspace and to the root system directory.
     *  
     * @param constant: ADLConverterPreferenceConstants
     */
    private void checkPreference(String constant){
    	String defPref = getPreferenceStore().getDefaultString(constant);
        String pref = getPreferenceStore().getString(constant);
        
        if(defPref.equals(pref)){
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            File file1 = new File(pref);
            IFolder file2 = null;
            try{
            	file2 = root.getFolder(new Path(pref));
            }catch (Exception e) {}
            
            if((file1 == null || !file1.exists()) && (file2 == null || !file2.exists()))
                getPreferenceStore().setValue(constant, "/NoPath");
        }        
    }
    
    
    @Override
    protected void createFieldEditors() {
        //checkPref(ADLConverterPreferenceConstants.P_STRING_Path_Source);
    	//checkPref(ADLConverterPreferenceConstants.P_STRING_Path_Relativ_Target);
        checkPreference(ADLConverterPreferenceConstants.P_STRING_Path_Target);
        
        DirectoryFieldEditor source = new DirectoryFieldEditor(ADLConverterPreferenceConstants.P_STRING_Path_Source,"Source Path:",getFieldEditorParent());
    	addField(source);
        
        FieldEditor targetFieldEditor = new ContainerFieldEditor(ADLConverterPreferenceConstants.P_STRING_Path_Target,"Target Path:",getFieldEditorParent());
        addField(targetFieldEditor);
        addField(new DirectoryFieldEditor(ADLConverterPreferenceConstants.P_STRING_Path_Relativ_Target,"Relativ Target Path:",getFieldEditorParent()));
        addField(new StringFieldEditor(ADLConverterPreferenceConstants.P_STRING_Path_Remove_Absolut_Part,"Remove absolute path part:", getFieldEditorParent()));
        addField(new StringFieldEditor(ADLConverterPreferenceConstants.P_STRING_Display_Paths,"Display paths separated by commas:", getFieldEditorParent()));
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common
     * GUI blocks needed to manipulate various types of preferences. Each field
     * editor knows how to save and restore itself.
     */
    public void init(IWorkbench workbench) {

    }
}
