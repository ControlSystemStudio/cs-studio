/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.DisplayEditpart;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.script.ScriptService;
import org.csstudio.opibuilder.script.ScriptStoreFactory;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.gef.GraphicalViewer;
import org.python.core.PyCode;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/**The action executing python script.
 * @author Xihui Chen
 *
 */
public class ExecutePythonScriptAction extends AbstractWidgetAction {

	public static final String PROP_PATH = "path";//$NON-NLS-1$
	private PyCode code;
	private PythonInterpreter interpreter;
	private DisplayEditpart displayEditpart;
	private AbstractBaseEditPart widgetEditPart;

	@Override
	protected void configureProperties() {
		addProperty(new FilePathProperty(
				PROP_PATH, "File Path", WidgetPropertyCategory.Basic, new Path(""),
				new String[]{"py"}));

	}

	@Override
	public ActionType getActionType() {
		return ActionType.EXECUTE_PYTHONSCRIPT;
	}

	@Override
	public void run() {
		if(code == null){
			//read file
			IPath absolutePath = getAbsolutePath();
			//Add the path of script to python module search path
			if(absolutePath != null && !absolutePath.isEmpty()){
				try {
					ScriptStoreFactory.initPythonInterpreter();
				} catch (Exception e) {
					final String message = "Failed to execute Python Script: " + absolutePath;
		            OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
					ConsoleService.getInstance().writeError(message + "\n" + e); //$NON-NLS-1$
				}
				PySystemState state = new PySystemState();
				//If it is a workspace file.
				if(ResourceUtil.isExistingWorkspaceFile(absolutePath)){
					IPath folderPath = absolutePath.removeLastSegments(1);
					String sysLocation = ResourceUtil.workspacePathToSysPath(folderPath).toOSString();
					state.path.append(new PyString(sysLocation));
				}else if(ResourceUtil.isExistingLocalFile(absolutePath)){
					IPath folderPath = absolutePath.removeLastSegments(1);
					state.path.append(new PyString(folderPath.toOSString()));
				}
				
				interpreter = new PythonInterpreter(null,state);
			}else
				interpreter = new PythonInterpreter();	
			GraphicalViewer viewer = getWidgetModel().getRootDisplayModel().getViewer();
			if(viewer != null){
				Object obj = viewer.getEditPartRegistry().get(getWidgetModel());
				if(obj != null && obj instanceof AbstractBaseEditPart){
					displayEditpart = (DisplayEditpart)(viewer.getContents());
					widgetEditPart = (AbstractBaseEditPart)obj;					
				}
			}
		}
		
		Job job = new Job("Load Python Script") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Connecting to " + getAbsolutePath(),
						IProgressMonitor.UNKNOWN);
				runTask();
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();	
	}
	
	public void runTask() {
		try {
			if(code == null){
				//read file
				IPath absolutePath = getAbsolutePath();				
				
				InputStream inputStream = ResourceUtil.pathToInputStream(absolutePath, false);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));				
				
				//compile
				code = interpreter.compile(reader); //$NON-NLS-1$
				inputStream.close();
				reader.close();
			}


			UIBundlingThread.getInstance().addRunnable(new Runnable() {

				public void run() {

						try {
							interpreter.set(ScriptService.WIDGET, widgetEditPart);
							interpreter.set(ScriptService.DISPLAY, displayEditpart);	
							interpreter.exec(code);
						} catch (Exception e) {
							final String message =  "Error exists in script " + getPath();
		                    OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
							ConsoleService.getInstance().writeError(message + "\n" + e); //$NON-NLS-1$
						}
				}
			});
		} catch (Exception e) {
			final String message = "Failed to execute Python Script: " + getPath();
            OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
			ConsoleService.getInstance().writeError(message + "\n" + e); //$NON-NLS-1$
		}
	}

	private IPath getPath(){
		return (IPath)getPropertyValue(PROP_PATH);
	}

	private IPath getAbsolutePath(){
		//read file
		IPath absolutePath = getPath();
		if(!getPath().isAbsolute()){
    		absolutePath =
    			ResourceUtil.buildAbsolutePath(getWidgetModel(), getPath());
    	}
		return absolutePath;
	}

	@Override
	public String getDefaultDescription() {
		return super.getDefaultDescription() + " " + getPath(); //$NON-NLS-1$
	}

}
