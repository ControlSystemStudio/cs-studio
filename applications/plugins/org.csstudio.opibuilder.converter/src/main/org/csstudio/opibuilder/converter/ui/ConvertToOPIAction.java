package org.csstudio.opibuilder.converter.ui;

import java.util.List;

import org.csstudio.opibuilder.converter.writer.OpiWriter;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**Convert an EDM file to an OPI file.
 * @author Xihui Chen
 *
 */
public class ConvertToOPIAction implements IObjectActionDelegate {

	private List<IResource> selectedFiles;
	
	
	
	public ConvertToOPIAction() {
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
	}

	public void run(IAction action) {
		Job job = new Job("Converting EDM files to OPI files") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Converting", selectedFiles.size());
				for(IResource selectedFile : selectedFiles){
					monitor.subTask("Converting " + selectedFile);
					convertFile(selectedFile); 
					monitor.worked(1);
					if(monitor.isCanceled())
						return Status.CANCEL_STATUS;
				}
				
				
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		
	}

	private void convertFile(IResource selectedFile) {
		IPath convertedFile = null;
		try {
			OpiWriter writer = OpiWriter.getInstance();	
			IPath outputOPIsFolder = PreferencesHelper.getOutputOPIsFolderPath();
			if(outputOPIsFolder != null && !outputOPIsFolder.isEmpty()){
				IResource r= ResourcesPlugin.getWorkspace().getRoot().findMember(outputOPIsFolder);
				if(r != null)					
					convertedFile = r.getLocation().append(
										selectedFile.getFullPath().removeFileExtension().lastSegment() + ".opi");
			}
				
			else
				convertedFile = selectedFile.getLocation().removeFileExtension().addFileExtension(".opi");
			writer.writeDisplayFile(
					selectedFile.getLocation().toOSString(), 
					convertedFile.toOSString());
			
			IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(
					PreferencesHelper.getOutputOPIsFolderPath());
			r.refreshLocal(IResource.DEPTH_INFINITE, null);	
//			IResource convertedOPI = 
//				ResourcesPlugin.getWorkspace().getRoot().findMember(convertedFile);
//			if(PreferencesHelper.isOpenOPIsAfterConverted() && convertedOPI != null &&
//					convertedOPI instanceof IFile)
//				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
//						new FileEditorInput((IFile) convertedOPI), "org.csstudio.opibuilder.OPIEditor"); //$NON-NLS-1$, editorId)
		} catch (Exception e) {
			String message = "Exception during converting " + selectedFile + "\n" + e;
			CentralLogger.getInstance().error(this, message, e);
			ConsoleService.getInstance().writeError(message);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection && !((IStructuredSelection)selection).isEmpty())
			selectedFiles = ((List<IResource>)((IStructuredSelection)selection).toList());
	}

	
	
}
