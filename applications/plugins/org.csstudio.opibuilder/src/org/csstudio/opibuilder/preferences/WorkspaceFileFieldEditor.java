package org.csstudio.opibuilder.preferences;

import org.csstudio.platform.ui.dialogs.ResourceSelectionDialog;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.window.Window;
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
		IPath path = new Path(getTextControl().getText());

		return getPath(path).toPortableString();

	}
	
	private IPath getPath(IPath startPath){
		ResourceSelectionDialog rsDialog = new ResourceSelectionDialog(getShell(), "Choose File", extensions);
		if(startPath != null)
			rsDialog.setSelectedResource(startPath);	
		
		if(rsDialog.open() == Window.OK){
			return rsDialog.getSelectedResource();
		}
		return null;
	}
	
	@Override
	protected boolean checkState() {
		String pathString = getTextControl().getText();
		if(pathString==null || pathString.trim().equals(""))
			return true;
		IPath path = Path.fromPortableString(pathString);
		IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if(r != null && r instanceof IFile){
			clearErrorMessage();
			return true;
		}else{
			showErrorMessage("The file doesn't exist!");
			return false;
		}
			
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
