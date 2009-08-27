package org.csstudio.opibuilder.runmode;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.util.SizeLimitedStack;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
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
	private static int STACK_SIZE = 10;
	public DisplayOpenManager() {
		backStack =  new SizeLimitedStack<IFile>(STACK_SIZE);
		forwardStack = new SizeLimitedStack<IFile>(STACK_SIZE);
		listeners = new ArrayList<IDisplayOpenManagerListener>();
	}
	
	public void openNewDisplay(){
		backStack.push(getCurrentFileInEditor());
		forwardStack.clear();
		fireOperationsHistoryChanged();
	}
	
	public void goBack(){
		if(backStack.size() ==0)
			return;
		IFile file = getCurrentFileInEditor();
		
		forwardStack.push(file);
		
		openOPI(backStack.pop());
		
	}

	/**
	 * @param file 
	 * 
	 */
	private void openOPI(IFile file) {
		try {
			RunModeService.getInstance().replaceActiveEditorContent(file);
		} catch (PartInitException e) {
			CentralLogger.getInstance().error(this, "Failed to go back", e);
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Open file error", 
					"Failed to go back");
		}
		
		fireOperationsHistoryChanged();
	}
	
	public void goForward(){
		if(forwardStack.size() ==0)
			return;
		IFile file = getCurrentFileInEditor();
		
		backStack.push(file);
		
		openOPI(forwardStack.pop());
		
	}
	
	public void goBack(IFile file){
		if(backStack.contains(file)){
			
			IFile currentFile = getCurrentFileInEditor();
			forwardStack.push(currentFile);
			
			while(!backStack.peek().equals(file) && backStack.size() >0){
				forwardStack.push(backStack.pop());
			}
					
			openOPI(backStack.pop());
		}
	}
	
	public void goForward(IFile file){
		if(forwardStack.contains(file)){
			
			IFile currentFile = getCurrentFileInEditor();
			backStack.push(currentFile);
			
			while(!forwardStack.peek().equals(file) && forwardStack.size() >0){
				backStack.push(forwardStack.pop());
			}
						
			openOPI(forwardStack.pop());
		}
	}

	/**
	 * @return
	 */
	private IFile getCurrentFileInEditor() {
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
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public boolean removeListener(IDisplayOpenManagerListener listener){
		return listeners.remove(listener);
	}
	private void fireOperationsHistoryChanged(){
		for(IDisplayOpenManagerListener listener : listeners)
			listener.displayOpenHistoryChanged(this);
	}
	
	
	public boolean canBackward(){
		return backStack.size() > 0;
	}
	
	public boolean canForward(){
		return forwardStack.size() > 0;
	}
}
