package org.csstudio.opibuilder.runmode;

import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
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
	
	
	public void replaceActiveEditorContent(RunnerInput input) throws PartInitException{
		IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().
			getActivePage().getActiveEditor();		
		activeEditor.init(activeEditor.getEditorSite(),input);
		
	}
	
	/**Run an OPI file with necessary parameters. This function should be called when open an OPI
	 * from another OPI.
	 * @param file
	 * @param targetWindow
	 * @param displayOpenManager
	 * @param macrosInput
	 */
	public void runOPI(IFile file, TargetWindow targetWindow, DisplayOpenManager displayOpenManager, 
			MacrosInput macrosInput){
		runOPI(file, targetWindow, displayOpenManager, macrosInput, null);
	}
	
	/**Run an OPI file in the target window.
	 * @param file
	 * @param targetWindow
	 */
	public void runOPI(IFile file, TargetWindow targetWindow, Dimension windowSize){
		runOPI(file, targetWindow, null, null, windowSize);
	}
	
	/**Run an OPI file.
	 * @param file the file to be ran. If displayModel is not null, this will be ignored.
	 * @param displayModel the display model to be ran. null for file input only.
	 * @param displayOpenManager the manager help to manage the opened displays. null if the OPI is not 
	 * replacing the current active display. 
	 */
	public void runOPI(final IFile file, final TargetWindow target,
			final DisplayOpenManager displayOpenManager, final MacrosInput macrosInput, final Dimension windowSize){
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			 public void run() {
		
				IWorkbenchWindow targetWindow = null;
				switch (target) {
				case NEW_WINDOW:
					targetWindow = createNewWindow(windowSize);
					break;
				case RUN_WINDOW:
					if(runWorkbenchWindow == null){
						runWorkbenchWindow = createNewWindow(windowSize);
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
								new RunnerInput(file, displayOpenManager, macrosInput), "org.csstudio.opibuilder.OPIRunner"); //$NON-NLS-1$
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
	private IWorkbenchWindow createNewWindow(Dimension size) {
		IWorkbenchWindow newWindow = null;
		try {				
			newWindow = 
				PlatformUI.getWorkbench().openWorkbenchWindow("org.csstudio.opibuilder.OPIRunner", null); //$NON-NLS-1$
			//ActionFactory.IWorkbenchAction toggleToolbar = ActionFactory.TOGGLE_COOLBAR.create(runWorkbenchWindow); 
			//toggleToolbar.run(); 
	
			if(size != null)
				newWindow.getShell().setSize(size.width+40, size.height + 160);			
		
		} catch (WorkbenchException e) {
			CentralLogger.getInstance().error(this, "Failed to open new window", e);
		}
		return newWindow;
	}
	
	
	
}
