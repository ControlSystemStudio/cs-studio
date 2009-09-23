package org.csstudio.opibuilder.widgetActions;

import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.progress.UIJob;

public class OpenFileAction extends AbstractWidgetAction {

	public static final String PROP_PATH = "path";//$NON-NLS-1$
	
	@Override
	protected void configureProperties() {
		addProperty(new FilePathProperty(
				PROP_PATH, "File Path", WidgetPropertyCategory.Basic, new Path(""), 
				new String[]{"*"}));
	
	}

	@Override
	public ActionType getActionType() {
		return ActionType.OPEN_FILE;
	}

	@Override
	public void run() {
		UIJob job = new UIJob(getDescription()){
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				final IWorkbenchWindow dw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				// Open editor on new file.		       
		        try {
		            if (dw != null) {
		                IWorkbenchPage page = dw.getActivePage();
		                if (page != null) {
		                	IFile file = ResourceUtil.getIFileFromIPath(getPath());
		                	if(file != null)
		                		IDE.openEditor(page, file, true);
		                	else throw new Exception("Cannot find the file at " + getPath());
		                }
		            }
		        } catch (Exception e) {
		        	String message = "Failed to open file " + getPath() + "\n" +  e.getMessage(); //$NON-NLS-2$         		
		        	MessageDialog.openError(dw.getShell(), "Failed to open file", message);
		        	CentralLogger.getInstance().error(this, message);
		        	ConsoleService.getInstance().writeError(message);		        	
		        }
		        return Status.OK_STATUS;
		    }
		};
		job.schedule();		
	}
	
	private IPath getPath(){
		return (IPath)getPropertyValue(PROP_PATH);
	}
	
	
	
	@Override
	public String getDescription() {
		return super.getDescription() + " " + getPath(); //$NON-NLS-1$
	}

}
