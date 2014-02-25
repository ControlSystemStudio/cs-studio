/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.ui;

import org.csstudio.opibuilder.converter.EDM2OPIConverterPlugin;
import org.csstudio.opibuilder.preferences.WorkspaceFileFieldEditor;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**The preference page of EDM2OPI converter.
 * @author Xihui Chen
 *
 */
public class EDMConverterPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private static final String PREF_QUALIFIER_ID = EDM2OPIConverterPlugin.PLUGIN_ID;
	
	public EDMConverterPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(new ScopedPreferenceStore(new InstanceScope(), PREF_QUALIFIER_ID));
		setMessage("EDM2OPI Converter Preferences");
	}
	
	@Override
	protected void createFieldEditors() {
		final Composite parent = getFieldEditorParent();
		
		WorkspaceFileFieldEditor edmColorListEditor = 
			new WorkspaceFileFieldEditor(PreferencesHelper.EDM_COLORLIST_FILE, 
					"EDM colors.list File", new String[]{"list"}, parent);//$NON-NLS-2$
		addField(edmColorListEditor);  
	
		WorkspaceFileFieldEditor opiColorFileEditor = 
			new WorkspaceFileFieldEditor(PreferencesHelper.OUTPUT_OPICOLOR_FILE, 
					"Output OPI Color File", new String[]{"def"}, parent){//$NON-NLS-2$
			@Override
			protected boolean checkState() {
				String pathString = getTextControl().getText();
				if(pathString==null || pathString.trim().equals(""))
					return true;
				IPath path = Path.fromPortableString(pathString);
				String ext = path.getFileExtension();
				if(ext == null || !ext.equals("def")){ //$NON-NLS-1$
					showErrorMessage("A file with .def extension must be specified for OPI color file.");
					return false;
				}
				IResource r= ResourcesPlugin.getWorkspace().getRoot().findMember(path.removeLastSegments(1));
				if(r != null && (r instanceof IProject || r instanceof IFolder)){
					clearErrorMessage();
					return true;
				}else{
					showErrorMessage("The folder doesn't exist!");
					return false;
				}
			}
		};
		addField(opiColorFileEditor);
		
		
		
		BooleanFieldEditor robustParsingEditor = 
			new BooleanFieldEditor(PreferencesHelper.FAIL_FAST, "Stop parsing at exception", parent);
		addField(robustParsingEditor);
		
		
	}

	public void init(IWorkbench workbench) {
		
	}

	
}
