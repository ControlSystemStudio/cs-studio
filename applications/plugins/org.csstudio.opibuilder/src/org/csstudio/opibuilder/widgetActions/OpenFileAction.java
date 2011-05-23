/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.UIJob;

/**The action opening a file using its default editor.
 * @author Xihui Chen
 *
 */
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
				IPath absolutePath = getPath();
		        try {
		            if (dw != null) {
		                IWorkbenchPage page = dw.getActivePage();

		                if (page != null) {
		                	if(!getPath().isAbsolute()){
		                		absolutePath =
		                			ResourceUtil.buildAbsolutePath(getWidgetModel(), getPath());
		                	}
			                	IFile file = ResourceUtil.getIFileFromIPath(absolutePath);
			                	if(file != null) // if file exists in workspace
			                		IDE.openEditor(page, file, true);
			                	else if (ResourceUtil.isExistingLocalFile(absolutePath)){ //if it is on local file system.
			                		try {			                			
										IFileStore localFile =
											EFS.getLocalFileSystem().getStore(absolutePath);
										IDE.openEditorOnFileStore(page, localFile);
									} catch (Exception e) {
				                		throw new Exception("Cannot find the file at " + getPath());
									}
			                	}else 
			                		throw new Exception("This action can only open file from workspace or local file system.");

		                }
		            }
		        } catch (Exception e) {
		        	String message = "Failed to open file " + getPath() + "\n" +  e.getMessage(); //$NON-NLS-2$
		        	MessageDialog.openError(dw.getShell(), "Failed to open file", message);
                    OPIBuilderPlugin.getLogger().log(Level.WARNING, "Failed to file " + getPath(), e); //$NON-NLS-1$
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
	public String getDefaultDescription() {
		return super.getDefaultDescription() + " " + getPath(); //$NON-NLS-1$
	}

}
