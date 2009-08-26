package org.csstudio.opibuilder.runmode;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.util.SizeLimitedStack;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

/**A manager help to manage the display open history and provide go back and forward functions.
 * @author Xihui Chen
 *
 */
public class DisplayOpenManager {
	private SizeLimitedStack<IFile> backStack;
	private SizeLimitedStack<IFile> forwardStack;
	private List<IDisplayOpenManagerListener> listeners;
	private static int STACK_SIZE = 30;
	public DisplayOpenManager() {
		backStack =  new SizeLimitedStack<IFile>(STACK_SIZE);
		forwardStack = new SizeLimitedStack<IFile>(STACK_SIZE);
		listeners = new ArrayList<IDisplayOpenManagerListener>();
	}
	
	public void openNewDisplay(IFile file){
		backStack.push(file);
		forwardStack.clear();
		fireOperationsHistoryChanged();
	}
	
	public void goBack(){
		if(backStack.size() ==0)
			return;
		IFile file = getCurrentFile();
		
		forwardStack.push(file);
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		.getActivePage().closeEditor(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getActivePage().getActiveEditor(), false);
		
		RunModeService.getInstance().runOPI(backStack.pop(), TargetWindow.SAME_WINDOW);
		
	}
	
	public void goForward(){
		if(forwardStack.size() ==0)
			return;
		IFile file = getCurrentFile();
		
		backStack.push(file);
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		.getActivePage().closeEditor(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getActivePage().getActiveEditor(), false);
		
		RunModeService.getInstance().runOPI(forwardStack.pop(), TargetWindow.SAME_WINDOW);
		
	}

	/**
	 * @return
	 */
	private IFile getCurrentFile() {
		IFile file = null;
		IEditorInput input;
		input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
				getActiveEditor().getEditorInput();
		if(input instanceof FileEditorInput)
			file = ((FileEditorInput)input).getFile();
		else if (input instanceof FileStoreEditorInput) {
			IPath path = URIUtil.toPath(((FileStoreEditorInput) input)
					.getURI());
			//read file
			IFile[] files = 
				ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(
						ResourcesPlugin.getWorkspace().getRoot().getLocation().append(path));
			
			if(files.length < 1)
				try {
					throw new FileNotFoundException("The file " + path.toString() + "does not exist!");
				} catch (FileNotFoundException e) {
					CentralLogger.getInstance().error(this, e);
					MessageDialog.openError(Display.getDefault().getActiveShell(), "File Open Error",
							e.getMessage());					
				}
			
			file = files[0];
		}
		return file;
	}
	
	public void addListener(IDisplayOpenManagerListener listener){
		listeners.add(listener);
	}
	
	public boolean removeListener(IDisplayOpenManagerListener listener){
		return listeners.remove(listener);
	}
	private void fireOperationsHistoryChanged(){
		for(IDisplayOpenManagerListener listener : listeners)
			listener.displayOpenHistoryChanged(this);
	}
	
}
