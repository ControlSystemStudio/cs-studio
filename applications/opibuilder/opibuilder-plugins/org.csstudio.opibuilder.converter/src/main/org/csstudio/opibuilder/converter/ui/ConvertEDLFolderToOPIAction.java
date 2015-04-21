/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.ui;

import java.util.List;
import java.util.logging.Level;

import org.csstudio.opibuilder.converter.EDM2OPIConverterPlugin;
import org.csstudio.opibuilder.util.ConsoleService;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.progress.UIJob;

/**
 * Convert edm files in the folder to opi files recursively
 * 
 * @author Xihui Chen
 * 
 */
public class ConvertEDLFolderToOPIAction implements IObjectActionDelegate {

	private static final String OUTPUT_FOLDER_TAIL = "_opi_output"; //$NON-NLS-1$
	private List<IFolder> selectedFolders;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// NOP
	}

	public void run(IAction action) {
		UIJob job = new UIJob(Display.getCurrent(), "Creating target output folders") {

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				monitor.beginTask("Creating", selectedFolders.size());
				for (final IFolder selectedFolder : selectedFolders) {
					monitor.subTask("Creating output target for " + selectedFolder);
					try {
						final IFolder targetFolder = createTargetFolder(selectedFolder, null);
						if(targetFolder != null){
							Job backgroundJob = new Job("Converting .edl files to opi...") {
								
								@Override
								protected IStatus run(IProgressMonitor monitor) {
									try {
										return convertFolder(selectedFolder, targetFolder, monitor);
									} catch (CoreException e) {
										final String message = "Failed to convert folder " + selectedFolder;
										EDM2OPIConverterPlugin.getLogger().log(Level.SEVERE, message, e);
										ConsoleService.getInstance().writeError(message + "\n" + e.getMessage()); //$NON-NLS-1$
									}
									return Status.OK_STATUS;
								}
							};
							backgroundJob.schedule();
						}
							
					} catch (CoreException e) {
						String message = "Failed to create target folder: " + e.getMessage();
						MessageDialog.openError(null, "Failed", message);
						EDM2OPIConverterPlugin.getLogger().log(Level.SEVERE, message, e);
						return Status.CANCEL_STATUS;
					}					
					monitor.worked(1);
					if (monitor.isCanceled())
						return Status.CANCEL_STATUS;
				}
				
				

				return Status.OK_STATUS;
			}
			
		};
		job.schedule();
		
	}
	
	/**Convert all edm files in source folder
	 * @param src the source folder
	 * @param target an exist target folder
	 * @param monitor 
	 * @throws CoreException 
	 */
	private IStatus convertFolder(IFolder src, IFolder target, IProgressMonitor monitor) throws CoreException{
		for(IResource resource : src.members()){			
			if(monitor.isCanceled())
				return Status.CANCEL_STATUS;
			if(resource instanceof IFile){
				String extension = ((IFile)resource).getFileExtension();
				if(extension!=null && extension.equals("edl")){ //$NON-NLS-1$					
					String message = "Converting " + resource;
					System.out.println(message);
					EDM2OPIConverterPlugin.getLogger().log(Level.INFO, message);
					ConsoleService.getInstance().writeInfo(message);
					if(!ConvertToOPIAction.convertFile(resource, target.getLocation().append(resource.getName().substring(0, resource.getName().length()-4)+".opi"))){ //$NON-NLS-1$
						if(!PreferencesHelper.isRobustParsing())
							monitor.setCanceled(true);
					}
				}else{
					try {
						resource.copy(target.getFullPath().append(resource.getName()), false, monitor);
					} catch (Exception e) {
						final String message = "Failed to copy " + resource;
						EDM2OPIConverterPlugin.getLogger().log(Level.WARNING, message, e);
						ConsoleService.getInstance().writeWarning(message + "\n" + e.getMessage()); //$NON-NLS-1$
					
					}
				}
			}else if(resource instanceof IFolder){
				IFolder copyFolder = target.getFolder(resource.getName());
				copyFolder.create(false, true, null);
				convertFolder((IFolder) resource, copyFolder, monitor);
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * Create the output target folder which is under the same parent but end
	 * with "_opi_output"
	 * 
	 * @param src the source folder
	 * @param targetFolderName the target folder name. null if appended with "_opi_output".
	 * @return the target folder
	 * @throws CoreException if failed
	 */
	private IFolder createTargetFolder(IFolder src, String targetFolderName) throws CoreException {
		IContainer parent = src.getParent();
		if (targetFolderName == null)
			targetFolderName = src.getName() + OUTPUT_FOLDER_TAIL;
		IFolder target = parent.getFolder(new Path(targetFolderName));
		if (target.exists()) {
			if (MessageDialog.openQuestion(null, "Target Folder Exists",
					NLS.bind("There is already a folder named \"{0}\"."
							+ "Do you want to replace it?", targetFolderName))) {
				target.delete(true, null);
			} else {
				InputDialog inputDialog = new InputDialog(null, "Target folder name",
						"Please provides the target folder name:", null, new IInputValidator() {

							public String isValid(String newText) {
								if (newText.trim().isEmpty())
									return "Folder name cannot be empty";
								return null;
							}
						});
				if (inputDialog.open() == Window.OK) {
					targetFolderName = inputDialog.getValue().trim();
					return createTargetFolder(src, targetFolderName);
				}else
					return null;
			}
		}
		target.create(false, true, null);
		return target;
	}
	

	@SuppressWarnings("unchecked")
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection
				&& !((IStructuredSelection) selection).isEmpty())
			selectedFolders = ((List<IFolder>) ((IStructuredSelection) selection).toList());
	}
}
