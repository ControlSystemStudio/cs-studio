package org.csstudio.opibuilder.runmode;

import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.actions.ActionFactory;

public class RunModeService {

	private IWorkbenchWindow runWorkbenchWindow;
	
	
	private static RunModeService instance;
	
	public static RunModeService getInstance(){
		if(instance == null)
			instance = new RunModeService();
		return instance;
	}
	
	
	public IWorkbenchWindow getRunWorkbenchWindow(){
		return runWorkbenchWindow;
	}
	
	/**Run an OPI file.
	 * @param file
	 * @param displayModel
	 */
	public void runOPI(IFile file, DisplayModel displayModel){
		
		if(runWorkbenchWindow == null)
			try {				
				runWorkbenchWindow = 
					PlatformUI.getWorkbench().openWorkbenchWindow("org.csstudio.opibuilder.OPIRunner", null); //$NON-NLS-1$
				ActionFactory.IWorkbenchAction toggleToolbar = ActionFactory.TOGGLE_COOLBAR.create(runWorkbenchWindow); 
				toggleToolbar.run(); 
				runWorkbenchWindow.getShell().setSize(displayModel.getSize().width+36, displayModel.getSize().height + 125);
				runWorkbenchWindow.addPageListener(new IPageListener(){

					public void pageActivated(IWorkbenchPage page) {
						
					}

					public void pageClosed(IWorkbenchPage page) {
						runWorkbenchWindow = null;
					}

					public void pageOpened(IWorkbenchPage page) {
						
					}
					
				});
			
			} catch (WorkbenchException e) {
				CentralLogger.getInstance().error(this, "Failed to open new window", e);
			}
		try {
			runWorkbenchWindow.getActivePage().openEditor(
					new RunnerInput(file, displayModel), "org.csstudio.opibuilder.OPIRunner"); //$NON-NLS-1$
		} catch (PartInitException e) {
			CentralLogger.getInstance().error(this, "Failed to run OPI " + file.getName(), e);
		}
		
		
	}
	
	
	
}
