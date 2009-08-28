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
	private DisplayOpenManager displayOpenManager;
	
	public RunnerInput(IFile file, DisplayModel model, DisplayOpenManager displayOpenManager){
		this(file, model);
		this.setDisplayOpenManager(displayOpenManager);
	}
	
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

	/**
	 * @param displayOpenManager the displayOpenManager to set
	 */
	public void setDisplayOpenManager(DisplayOpenManager displayOpenManager) {
		this.displayOpenManager = displayOpenManager;
	}

	/**
	 * @return the displayOpenManager
	 */
	public DisplayOpenManager getDisplayOpenManager() {
		return displayOpenManager;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof RunnerInput)) {
			return false;
		}
		RunnerInput other = (RunnerInput) obj;
		return getFile().equals(other.getFile()) && 
			displayOpenManager == other.getDisplayOpenManager()  && 
			displayModel == displayModel;
	}
}
