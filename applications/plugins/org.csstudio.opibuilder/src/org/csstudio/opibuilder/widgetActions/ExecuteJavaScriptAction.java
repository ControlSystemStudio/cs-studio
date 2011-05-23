/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
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
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

/**The action executing javascript.
 * @author Xihui Chen
 *
 */
public class ExecuteJavaScriptAction extends AbstractWidgetAction {

	public static final String PROP_PATH = "path";//$NON-NLS-1$
	private Script script;
	private ImporterTopLevel scriptScope;
	private Context scriptContext;

	@Override
	protected void configureProperties() {
		addProperty(new FilePathProperty(
				PROP_PATH, "File Path", WidgetPropertyCategory.Basic, new Path(""),
				new String[]{"js"}));

	}

	@Override
	public ActionType getActionType() {
		return ActionType.EXECUTE_JAVASCRIPT;
	}

	@Override
	public void run() {
		if(scriptContext == null){
			scriptContext = ScriptStoreFactory.getJavaScriptContext();
			scriptScope = new ImporterTopLevel(scriptContext);
			GraphicalViewer viewer = getWidgetModel().getRootDisplayModel().getViewer();
			if(viewer != null){
				Object obj = viewer.getEditPartRegistry().get(getWidgetModel());
				if(obj != null && obj instanceof AbstractBaseEditPart){
					Object displayObject = Context.javaToJS(viewer.getContents(), scriptScope);
					Object widgetObject = Context.javaToJS(obj, scriptScope);						
					ScriptableObject.putProperty(scriptScope, 
							ScriptService.DISPLAY, displayObject);
					ScriptableObject.putProperty(scriptScope, 
							ScriptService.WIDGET, widgetObject);
				}
			}			
		}
		Job job = new Job("Load JavaScript") {

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
	
	private void runTask() {
		try {
			if(script == null){				
				//read file				
				final InputStream inputStream = ResourceUtil.pathToInputStream(getAbsolutePath(), false);
				final BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));				
				
				//compile
				UIBundlingThread.getInstance().addRunnable(new Runnable() {
					
					public void run() {
						try {
							script = scriptContext.compileReader(reader, "script", 1, null);
						} catch (IOException e) {
							final String message = "Failed to compile JavaScript: " + getAbsolutePath();
				            OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
							ConsoleService.getInstance().writeError(message + "\n" + e.getMessage()); //$NON-NLS-1$
						} 
						try {
							inputStream.close();
							reader.close();
						} catch (IOException e) {							
						}
						
					}
				});
				
			}


			UIBundlingThread.getInstance().addRunnable(new Runnable() {

				public void run() {

						try {
							script.exec(scriptContext, scriptScope);
						} catch (Exception e) {
							final String message =  "Error exists in script " + getAbsolutePath();
		                    OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
							ConsoleService.getInstance().writeError(message + "\n" + e.getMessage()); //$NON-NLS-1$
						}
				}
			});
		} catch (Exception e) {
			final String message = "Failed to execute JavaScript: " + getAbsolutePath();
            OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
			ConsoleService.getInstance().writeError(message + "\n" + e.getMessage()); //$NON-NLS-1$
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
