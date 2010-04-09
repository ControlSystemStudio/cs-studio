package org.csstudio.opibuilder.runmode;


import java.io.InputStream;

import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**The editor input for OPI Runner. 
 * @author Xihui Chen
 *
 */
public class RunnerInput implements IRunnerInput{

	private DisplayOpenManager displayOpenManager;
	private MacrosInput macrosInput;
	private IPath path;
	
	public RunnerInput(IPath path, DisplayOpenManager displayOpenManager, 
			MacrosInput macrosInput){
		this.path = path;
		this.setDisplayOpenManager(displayOpenManager);
		this.macrosInput = macrosInput;
	}
	
	public RunnerInput(IPath path, DisplayOpenManager displayOpenManager){
		this(path, displayOpenManager, null);
	}
	

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.runmode.IRunnerInput#setDisplayOpenManager(org.csstudio.opibuilder.runmode.DisplayOpenManager)
	 */
	public void setDisplayOpenManager(DisplayOpenManager displayOpenManager) {
		this.displayOpenManager = displayOpenManager;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.runmode.IRunnerInput#getDisplayOpenManager()
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
		return getPath().equals(other.getPath()) && macroSame;
	//		displayOpenManager == other.getDisplayOpenManager()  && 
			
	}


	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.runmode.IRunnerInput#getMacrosInput()
	 */
	public MacrosInput getMacrosInput() {
		return macrosInput;
	}
	
	public void saveState(IMemento memento) {
		RunnerInputFactory.saveState(memento, this);
	}
	
	public String getFactoryId() {
		return RunnerInputFactory.getFactoryId();
	}

	public IPath getPath() {
		return path;
	}

	public boolean exists() {
		InputStream in = null;
		try {
			 in = getInputStream();
		} catch (Exception e) {
			return false;
		}
		return in != null;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return getPath().lastSegment();
	}

	public IPersistableElement getPersistable() {
		return this;
	}

	public String getToolTipText() {
		return path.toString();
	}

	

	public InputStream getInputStream() throws Exception {
		return ResourceUtil.pathToInputStream(getPath());
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	@Override
	public String toString() {
		return getPath().toString();
	}
	
}
