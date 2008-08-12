package org.csstudio.utility.adlconverter.ui.preferences;

import org.csstudio.utility.adlconverter.Activator;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
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
        
        FieldEditor targetFieldEditor = new ContainerFieldEditor(ADLConverterPreferenceConstants.P_STRING_Path_Target,"Target Path:",getFieldEditorParent());
        addField(targetFieldEditor);
        addField(new DirectoryFieldEditor(ADLConverterPreferenceConstants.P_STRING_Path_Relativ_Target,"Relativ Target Path:",getFieldEditorParent()));
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common
     * GUI blocks needed to manipulate various types of preferences. Each field
     * editor knows how to save and restore itself.
     */
    public void init(IWorkbench workbench) {
        // TODO Auto-generated method stub

    }
}
