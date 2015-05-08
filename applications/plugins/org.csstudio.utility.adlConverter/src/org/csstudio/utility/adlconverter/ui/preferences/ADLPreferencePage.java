package org.csstudio.utility.adlconverter.ui.preferences;

import org.csstudio.utility.adlconverter.Activator;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import java.io.File;

public class ADLPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {


    public ADLPreferencePage() {
        super(FieldEditorPreferencePage.GRID);
        ScopedPreferenceStore prefStore = new ScopedPreferenceStore(new InstanceScope(), Activator
                .getDefault().getBundle().getSymbolicName());
        setPreferenceStore(prefStore);
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
//        checkPreference(ADLConverterPreferenceConstants.P_STRING_Path_Target);
//        checkPreference(ADLConverterPreferenceConstants.P_STRING_Path_Target_Strip_Tool);

        DirectoryFieldEditor source = new DirectoryFieldEditor(ADLConverterPreferenceConstants.P_STRING_Path_Source,"Source Path:",getFieldEditorParent());
        addField(source);
        addField(new StringFieldEditor(ADLConverterPreferenceConstants.P_STRING_Display_Paths,"Display paths separated by commas:", getFieldEditorParent()));

        Label label = new Label(getFieldEditorParent(), SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,3,1));

        label = new Label(getFieldEditorParent(), SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false,3,1));
        label.setText("Convert ADL File Settings:");

        label = new Label(getFieldEditorParent(), SWT.SEPARATOR|SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,3,1));

        FieldEditor targetFieldEditor = new ContainerFieldEditor(ADLConverterPreferenceConstants.P_STRING_Path_Target,"Target Path:",getFieldEditorParent());
        addField(targetFieldEditor);

        addField(new StringFieldEditor(ADLConverterPreferenceConstants.P_STRING_Path_Remove_Absolut_Part,"Remove absolute path part for Display:", getFieldEditorParent()));

        label = new Label(getFieldEditorParent(), SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,3,1));

        label = new Label(getFieldEditorParent(), SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false,3,1));
        label.setText("Convert Strip Tool File Settings:");

        label = new Label(getFieldEditorParent(), SWT.SEPARATOR|SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,3,1));

        FieldEditor targetFieldStripToolEditor = new ContainerFieldEditor(ADLConverterPreferenceConstants.P_STRING_Path_Target_Strip_Tool,"Target Path for Strip Tool:",getFieldEditorParent());
        addField(targetFieldStripToolEditor);
        addField(new StringFieldEditor(ADLConverterPreferenceConstants.P_STRING_Path_Remove_Absolut_Part_Strip_Tool,"Remove absolute path part for Strip Tool:", getFieldEditorParent()));
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common
     * GUI blocks needed to manipulate various types of preferences. Each field
     * editor knows how to save and restore itself.
     */
    public void init(IWorkbench workbench) {

    }
}
