package org.csstudio.opibuilder.runmode;

import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.actions.ActionFactory;

public class RunModeService {
	
	public enum TargetWindow{
		NEW_WINDOW,
		SAME_WINDOW,
		RUN_WINDOW;
	}

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
	
	
	public void runOPI(IFile file, TargetWindow targetWindow ){
		runOPI(file, null, targetWindow);
	}
	
	/**Run an OPI file.
	 * @param file the file to be ran. If displayModel is not null, this will be ignored.
	 * @param displayModel the display model to be ran. null for file input only.
	 */
	public void runOPI(final IFile file, final DisplayModel displayModel, final TargetWindow target){
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			 public void run() {
		
				IWorkbenchWindow targetWindow = null;
				switch (target) {
				case NEW_WINDOW:
					targetWindow = createNewWindow(displayModel);
					break;
				case RUN_WINDOW:
					if(runWorkbenchWindow == null){
						runWorkbenchWindow = createNewWindow(displayModel);
						runWorkbenchWindow.addPageListener(new IPageListener(){
							public void pageClosed(IWorkbenchPage page) {
								runWorkbenchWindow = null;
							}
		
							public void pageActivated(IWorkbenchPage page) {
								
							}
		
							public void pageOpened(IWorkbenchPage page) {
								
							}
						});
					}
					targetWindow = runWorkbenchWindow;
					break;
				case SAME_WINDOW:
				default:
					targetWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					break;
				}
				
				
				
				if(targetWindow != null){
					try {
						targetWindow.getActivePage().openEditor(
								new RunnerInput(file, displayModel), "org.csstudio.opibuilder.OPIRunner"); //$NON-NLS-1$
						targetWindow.getShell().moveAbove(null);
					} catch (PartInitException e) {
						CentralLogger.getInstance().error(this, "Failed to run OPI " + file.getName(), e);
					}
				}
				
				}
			});
	}


	/**
	 * @param displayModel
	 */
	private IWorkbenchWindow createNewWindow(DisplayModel displayModel) {
		IWorkbenchWindow newWindow = null;
		try {				
			newWindow = 
				PlatformUI.getWorkbench().openWorkbenchWindow("org.csstudio.opibuilder.OPIRunner", null); //$NON-NLS-1$
			//ActionFactory.IWorkbenchAction toggleToolbar = ActionFactory.TOGGLE_COOLBAR.create(runWorkbenchWindow); 
			//toggleToolbar.run(); 
			if(displayModel != null)
				newWindow.getShell().setSize(
						displayModel.getSize().width+36, displayModel.getSize().height + 125);			
		
		} catch (WorkbenchException e) {
			CentralLogger.getInstance().error(this, "Failed to open new window", e);
		}
		return newWindow;
	}
	
	
	
}
