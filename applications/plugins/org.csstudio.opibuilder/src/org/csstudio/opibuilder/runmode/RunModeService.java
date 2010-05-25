package org.csstudio.opibuilder.runmode;

import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**The service for running of OPI.
 * @author Xihui Chen
 *
 */
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
	
	
	public void replaceActiveEditorContent(IRunnerInput input) throws PartInitException{
		IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().
			getActivePage().getActiveEditor();		
		activeEditor.init(activeEditor.getEditorSite(),input);
		
	}
	
	/**Run an OPI file with necessary parameters. This function should be called when open an OPI
	 * from another OPI.
	 * @param path
	 * @param targetWindow
	 * @param displayOpenManager
	 * @param macrosInput
	 */
	public void runOPI(IPath path, TargetWindow targetWindow, DisplayOpenManager displayOpenManager, 
			MacrosInput macrosInput){
		runOPI(path, targetWindow, displayOpenManager, macrosInput, null);
	}
	
	/**Run an OPI file in the target window.
	 * @param path
	 * @param targetWindow
	 */
	public void runOPI(IPath path, TargetWindow targetWindow, Rectangle windowSize){
		runOPI(path, targetWindow, null, null, windowSize);
	}
	
	/**Run an OPI file.
	 * @param path the file to be ran. If displayModel is not null, this will be ignored.
	 * @param displayModel the display model to be ran. null for file input only.
	 * @param displayOpenManager the manager help to manage the opened displays. null if the OPI is not 
	 * replacing the current active display. 
	 */
	public void runOPI(final IPath path, final TargetWindow target,
			final DisplayOpenManager displayOpenManager, final MacrosInput macrosInput, final Rectangle windowBounds){
		final RunnerInput runnerInput = new RunnerInput(path, displayOpenManager, macrosInput);
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			 public void run() {
		
				IWorkbenchWindow targetWindow = null;
				switch (target) {
				case NEW_WINDOW:
					targetWindow = createNewWindow(windowBounds);
					break;
				case RUN_WINDOW:
					if(runWorkbenchWindow == null){
						runWorkbenchWindow = createNewWindow(windowBounds);
						runWorkbenchWindow.addPageListener(new IPageListener(){
							public void pageClosed(IWorkbenchPage page) {
								runWorkbenchWindow = null;
							}
		
							public void pageActivated(IWorkbenchPage page) {
								
							}
		
							public void pageOpened(IWorkbenchPage page) {
								
							}
						});
					}else{
						for(IEditorReference editor : 
							runWorkbenchWindow.getActivePage().getEditorReferences()){
							try {
								if(editor.getEditorInput().equals(runnerInput))
									editor.getPage().closeEditor(editor.getEditor(false), false);
							} catch (PartInitException e) {
								CentralLogger.getInstance().error(this,e);
							}
						}
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
						targetWindow.getShell().forceActive();
						targetWindow.getShell().forceFocus();
						targetWindow.getActivePage().openEditor(
								runnerInput, "org.csstudio.opibuilder.OPIRunner"); //$NON-NLS-1$
						targetWindow.getShell().moveAbove(null);
					} catch (PartInitException e) {
						CentralLogger.getInstance().error(this, "Failed to run OPI " + path.lastSegment(), e);
					}
				}
				
				}
			});
	}


	/**
	 * @param displayModel
	 */
	private IWorkbenchWindow createNewWindow(Rectangle windowBounds) {
		IWorkbenchWindow newWindow = null;
		try {				
			newWindow = 
				PlatformUI.getWorkbench().openWorkbenchWindow("org.csstudio.opibuilder.OPIRunner", null); //$NON-NLS-1$
			if(windowBounds != null){
				if(windowBounds.x >=0 && windowBounds.y > 1)
					newWindow.getShell().setLocation(windowBounds.x, windowBounds.y);
				newWindow.getShell().setSize(windowBounds.width+45, windowBounds.height + 165);
			}
		
		} catch (WorkbenchException e) {
			CentralLogger.getInstance().error(this, "Failed to open new window", e);
		}
		return newWindow;
	}
	
	
	
}
