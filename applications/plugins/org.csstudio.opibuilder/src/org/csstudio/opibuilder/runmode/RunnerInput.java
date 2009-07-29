package org.csstudio.opibuilder.runmode;


import org.csstudio.opibuilder.model.DisplayModel;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.part.FileEditorInput;

public class RunnerInput extends FileEditorInput {

	private DisplayModel displayModel;
	
	/**
	 * Creates an editor input based of the given file resource.
	 *
	 * @param file the file resource
	 */
	public RunnerInput(IFile file, DisplayModel displayModel) {
		super(file);	
		this.displayModel = displayModel;
	}

	public DisplayModel getDisplayModel(){
		return displayModel;
	}
	
	

}
