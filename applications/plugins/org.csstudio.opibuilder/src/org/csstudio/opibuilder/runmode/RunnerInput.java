package org.csstudio.opibuilder.runmode;


import org.csstudio.opibuilder.util.MacrosInput;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.part.FileEditorInput;

/**The editor input for OPI Runner. 
 * @author Xihui Chen
 *
 */
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
		boolean macroSame = false;
		if(macrosInput != null && other.getMacrosInput() !=null){
			macroSame = macrosInput.equals(other.getMacrosInput());
		}else if(macrosInput == null && other.getMacrosInput() == null)
			macroSame = true;
		return getFile().equals(other.getFile()) && macroSame;
	//		displayOpenManager == other.getDisplayOpenManager()  && 
			
	}


	/**
	 * @return the macrosInput
	 */
	public MacrosInput getMacrosInput() {
		return macrosInput;
	}
	
	@Override
	public void saveState(IMemento memento) {
		RunnerInputFactory.saveState(memento, this);
	}
	
	@Override
	public String getFactoryId() {
		return RunnerInputFactory.getFactoryId();
	}
	
	
}
