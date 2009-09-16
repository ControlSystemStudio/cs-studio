package org.csstudio.opibuilder.runmode;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.util.SizeLimitedStack;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**A manager help to manage the display open history and provide go back and forward functions.
 * @author Xihui Chen
 *
 */
public class DisplayOpenManager {
	private SizeLimitedStack<RunnerInput> backStack;
	private SizeLimitedStack<RunnerInput> forwardStack;
	private List<IDisplayOpenManagerListener> listeners;
	private static int STACK_SIZE = 10;
	public DisplayOpenManager() {
		backStack =  new SizeLimitedStack<RunnerInput>(STACK_SIZE);
		forwardStack = new SizeLimitedStack<RunnerInput>(STACK_SIZE);
		listeners = new ArrayList<IDisplayOpenManagerListener>();
	}
	
	public void openNewDisplay(){
		RunnerInput input = getCurrentRunnerInputInEditor();
		if(input !=null)
			backStack.push(input);
		forwardStack.clear();
		fireOperationsHistoryChanged();
	}
	
	public void goBack(){
		if(backStack.size() ==0)
			return;
		
		RunnerInput input = getCurrentRunnerInputInEditor();
		if(input !=null)
			forwardStack.push(input);
		
		openOPI(backStack.pop());
		
	}

	private RunnerInput getCurrentRunnerInputInEditor() {

		IEditorPart activeEditor = PlatformUI.getWorkbench().
			getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IEditorInput input = activeEditor.getEditorInput();
		
		if(input instanceof RunnerInput){
			if(((RunnerInput)input).getDisplayOpenManager() == null)
				((RunnerInput)input).setDisplayOpenManager(
					(DisplayOpenManager)activeEditor.getAdapter(DisplayOpenManager.class));
			return (RunnerInput)input;	
		}
			
		else
			return new RunnerInput(getCurrentFileInEditor(),
					(DisplayOpenManager)activeEditor.getAdapter(DisplayOpenManager.class));

		
	}

	private IFile getCurrentFileInEditor() {
		IFile file = null;
		IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
		getActiveEditor().getEditorInput();
		try {
			file = ResourceUtil.getFileInEditor(input);
		} catch (FileNotFoundException e) {
			CentralLogger.getInstance().error(this, e);
			MessageDialog.openError(Display.getDefault().getActiveShell(), "File Open Error",
					e.getMessage());	
		}
		
		return file;
	}
	
	/**
	 * @param file 
	 * 
	 */
	private void openOPI(RunnerInput input) {
		try {
			RunModeService.getInstance().replaceActiveEditorContent(input);
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
		RunnerInput input = getCurrentRunnerInputInEditor();
		if(input !=null)
			backStack.push(input);
		
		openOPI(forwardStack.pop());
		
	}
	
	public void goBack(int index){
		if(backStack.size() > index){
			
			RunnerInput input = getCurrentRunnerInputInEditor();
			if(input !=null)
				forwardStack.push(input);
			
			for(int i=0; i<index; i++){
				forwardStack.push(backStack.pop());
			}
					
			openOPI(backStack.pop());
		}
	}
	
	public void goForward(int index){
		if(forwardStack.size() > index){
			
			RunnerInput input = getCurrentRunnerInputInEditor();
			if(input !=null)
				backStack.push(input);
			
			for(int i=0; i<index; i++){
				backStack.push(forwardStack.pop());
			}
						
			openOPI(forwardStack.pop());
		}
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
	
	/**Return an array of all elements in the backward stack. 
	 * The oldest element is the first element of the returned array.
	 * @return the array contained all elements in the stack.
	 */
	public Object[] getBackStackEntries(){
		return  backStack.toArray();
	}
	
	/**Return an array of all elements in the forward stack. 
	 * The oldest element is the first element of the returned array.
	 * @return the array contained all elements in the stack.
	 */
	public Object[] getForwardStackEntries(){
		return  forwardStack.toArray();
	}
}
