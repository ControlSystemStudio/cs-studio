package org.csstudio.opibuilder.runmode;


import org.csstudio.opibuilder.util.MacrosInput;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.FileEditorInput;

public class RunnerInput extends FileEditorInput {

	private DisplayOpenManager displayOpenManager;
	private MacrosInput macrosInput;
	
	public RunnerInput(IFile file, DisplayOpenManager displayOpenManager, 
			MacrosInput macrosInput){
		super(file);
		this.setDisplayOpenManager(displayOpenManager);
		this.macrosInput = macrosInput;
	}
	
	public RunnerInput(IFile file, DisplayOpenManager displayOpenManager){
		super(file);
		this.setDisplayOpenManager(displayOpenManager);
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
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof RunnerInput)) {
			return false;
		}
		RunnerInput other = (RunnerInput) obj;
		return getFile().equals(other.getFile()) && 
			displayOpenManager == other.getDisplayOpenManager()  && 
			macrosInput == other.getMacrosInput();
	}


	/**
	 * @return the macrosInput
	 */
	public MacrosInput getMacrosInput() {
		return macrosInput;
	}
}
