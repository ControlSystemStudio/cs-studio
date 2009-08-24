package org.csstudio.opibuilder.runmode;


import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.FileEditorInput;

public class RunnerInput extends FileEditorInput {

	private DisplayModel displayModel;
	
	/**
	 * Creates an editor input based of the given file resource.
	 *
	 * @param file the file resource
	 */
	public RunnerInput(IFile file, DisplayModel model) {
		super(file);	
		if(model != null)
			displayModel = model;
		else {
			displayModel = new DisplayModel();
			try {
				XMLUtil.fillDisplayModelFromInputStream(file.getContents(), displayModel);
			} catch (Exception e) {
				CentralLogger.getInstance().error(this, "Failed to run file: " + file, e);
				MessageDialog.openError(Display.getDefault().getActiveShell(), "File Open Error",
						"The file is not a correct OPI file! An empty OPI will be created instead.\n" + e);
			}
		}
	}

	public DisplayModel getDisplayModel(){
		return displayModel;
	}
	
	

}
