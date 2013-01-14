package org.csstudio.opibuilder.util;

import java.util.logging.Level;

import org.csstudio.email.EMailSender;
import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.actions.PrintDisplayAction;
import org.csstudio.opibuilder.actions.SendEMailAction;
import org.csstudio.opibuilder.actions.SendToElogAction;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.runmode.OPIView;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.opibuilder.widgetActions.OpenFileAction;
import org.csstudio.ui.util.dialogs.ResourceSelectionDialog;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.UIJob;

public class SingleSourceHelperImpl extends SingleSourceHelper{

	@Override
	protected GC iGetImageGC(Image image) {
		return new GC(image);
	}

	@Override
	protected void iOpenFileActionRun(final OpenFileAction openFileAction) {

		UIJob job = new UIJob(openFileAction.getDescription()){
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				final IWorkbenchWindow dw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				// Open editor on new file.
				IPath absolutePath = openFileAction.getPath();
		        try {
		            if (dw != null) {
		                IWorkbenchPage page = dw.getActivePage();

		                if (page != null) {
		                	if(!openFileAction.getPath().isAbsolute()){
		                		absolutePath =
		                			ResourceUtil.buildAbsolutePath(
		                					openFileAction.getWidgetModel(), openFileAction.getPath());
		                	}
			                	IFile file = ResourceUtilSSHelperImpl.getIFileFromIPath(absolutePath);
			                	if(file != null) // if file exists in workspace
			                		IDE.openEditor(page, file, true);
			                	else if (ResourceUtil.isExistingLocalFile(absolutePath)){ //if it is on local file system.
			                		try {			                			
										IFileStore localFile =
											EFS.getLocalFileSystem().getStore(absolutePath);
										IDE.openEditorOnFileStore(page, localFile);
									} catch (Exception e) {
				                		throw new Exception("Cannot find the file at " +openFileAction.getPath());
									}
			                	}else 
			                		throw new Exception("This action can only open file from workspace or local file system.");

		                }
		            }
		        } catch (Exception e) {
		        	String message = "Failed to open file " + openFileAction.getPath() + "\n" +  e.getMessage(); //$NON-NLS-2$
		        	MessageDialog.openError(dw.getShell(), "Failed to open file", message);
                    OPIBuilderPlugin.getLogger().log(Level.WARNING, "Failed to file " +openFileAction.getPath(), e); //$NON-NLS-1$
		        	ConsoleService.getInstance().writeError(message);
		        }
		        return Status.OK_STATUS;
		    }
		};
		job.schedule();		
	}

	@Override
	protected void iAddPaintListener(Control control,
			PaintListener paintListener) {
		control.addPaintListener(paintListener);	
		
	}

	@Override
	protected void iRemovePaintListener(Control control,
			PaintListener paintListener) {
		control.removePaintListener(paintListener);
		
	}

	@Override
	protected void iRegisterRCPRuntimeActions(ActionRegistry actionRegistry,
			IOPIRuntime opiRuntime) {
		actionRegistry.registerAction(new PrintDisplayAction(opiRuntime));
		if (SendToElogAction.isElogAvailable())
			actionRegistry
					.registerAction(new SendToElogAction(opiRuntime));
		if (EMailSender.isEmailSupported())
			actionRegistry.registerAction(new SendEMailAction(opiRuntime));

	}

	@Override
	protected void iappendRCPRuntimeActionsToMenu(
			ActionRegistry actionRegistry, IMenuManager menu) {
		IAction action = actionRegistry.getAction(SendToElogAction.ID);
		if (action != null)
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		action = actionRegistry.getAction(SendEMailAction.ID);
		if (action != null)
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, 
				actionRegistry.getAction(ActionFactory.PRINT.getId()));

	}

	@Override
	protected IPath iRcpGetPathFromWorkspaceFileDialog(IPath startPath,
			String[] extensions) {
		ResourceSelectionDialog rsDialog = new ResourceSelectionDialog(
				Display.getCurrent().getActiveShell(), "Choose File", extensions);
		if(startPath != null)
			rsDialog.setSelectedResource(startPath);	
		
		if(rsDialog.open() == Window.OK){
			return rsDialog.getSelectedResource();
		}
		return null;
	}
	
	
	//////////////////////////// RAP Related Stuff ///////////////////////////////
	
	
	@Override
	protected void iRapActivatebaseEditPart(AbstractBaseEditPart editPart) {
		
	}

	@Override
	protected void iRapDeactivatebaseEditPart(AbstractBaseEditPart editPart) {
		
	}

	@Override
	protected void iRapOpenOPIInNewWindow(IPath path) {
		
	}

	@Override
	protected void iRapAddDisplayDisposeListener(Display display,
			Runnable runnable) {
		
	}

	@Override
	protected void iRapPlayWavFile(IPath absolutePath) {
		
	}

	@Override
	protected void iRapOPIViewCreatePartControl(OPIView opiView,
			Composite parent) {
		
	}

	@Override
	protected void iRapPluginStartUp() {
		
	}

	

	@Override
	protected void iRapOpenWebPage(String hyperLink) {
		
	}

}
