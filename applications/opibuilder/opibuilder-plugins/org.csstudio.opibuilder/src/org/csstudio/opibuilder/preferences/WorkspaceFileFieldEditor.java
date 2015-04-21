/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.preferences;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.widgets.Composite;
/**
 * A field editor for a workspace file path type preference. A workspace file 
 * dialog appears when the user presses the change button.
 * 
 * @author Xihui Chen
 */
public class WorkspaceFileFieldEditor extends StringButtonFieldEditor {

	
	
	
	private String[] extensions = null;

	
	/**
     * Creates a new file field editor 
     */
    protected WorkspaceFileFieldEditor() {
	}

    /**
     * Creates a file field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public WorkspaceFileFieldEditor(String name, String labelText, Composite parent) {
        this(name, labelText, new String[]{"*"}, parent); //$NON-NLS-1$
    }
    
    
    
    /**
     * Creates a file field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param extensions the file extensions
     * @param parent the parent of the field editor's control
     */
    public WorkspaceFileFieldEditor(String name, String labelText, String[] extensions, Composite parent) {
        super(name, labelText, parent);
        setFileExtensions(extensions);
        setChangeButtonText("Browse...");
    }
    
   
    
	@Override
	protected String changePressed() {
		IPath startPath = new Path(getTextControl().getText());
		IPath path = getPath(startPath);
		if(path != null)
			return path.toPortableString();
		else
			return null; 

	}
	
	private IPath getPath(IPath startPath){
		if(!OPIBuilderPlugin.isRAP())
			return SingleSourceHelper.rcpGetPathFromWorkspaceFileDialog(startPath, extensions);
		return null;
	}
	
	@Override
	protected boolean checkState() {
		return true;
			
	}

	/**
     * Sets this file field editor's file extension filter.
     *
     * @param extensions a list of file extension, or <code>null</code> 
     * to set the filter to the system's default value
     */
    public void setFileExtensions(String[] extensions) {
        this.extensions = extensions;
    }
    
    public void setTooltip(String tooltip){
    	getLabelControl().setToolTipText(tooltip);
    	getTextControl().setToolTipText(tooltip);
    	getChangeControl(getTextControl().getParent()).setToolTipText(tooltip);
    }
}
