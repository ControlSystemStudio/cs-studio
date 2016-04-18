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
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.script.ScriptService;
import org.csstudio.opibuilder.script.ScriptStoreFactory;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.widgets.Display;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

/**The action executing javascript with Mozilla Rhino script engine.
 * @author Xihui Chen
 *
 */
public class ExecuteJavaScriptRhinoAction extends AbstractExecuteScriptAction {

    private Script script;
    private ImporterTopLevel scriptScope;
    private Context scriptContext;

    @Override
    public ActionType getActionType() {
        return ActionType.EXECUTE_JAVASCRIPT;
    }

    @Override
    public void run() {
        if(scriptContext == null){
            try {
                scriptContext = ScriptStoreFactory.getRhinoContext();
            } catch (Exception exception) {
                ErrorHandlerUtil.handleError("Failed to get Script Context", exception);
                return;
            }
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
        Job job = new Job("Execute JavaScript") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                String taskName = isEmbedded()?"Execute JavaScript" :
                "Connecting to " + getAbsolutePath();
                monitor.beginTask(taskName,
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
        Display display = getWidgetModel().getRootDisplayModel().getViewer().getControl().getDisplay();

        try {
            if(script == null){
                //read file
                if(!isEmbedded())
                    getReader();

                //compile
                UIBundlingThread.getInstance().addRunnable(display, new Runnable() {

                    @Override
                    public void run() {
                        try {
                            if(isEmbedded())
                                script = scriptContext.compileString(getScriptText(), "script", 1, null);
                            else{
                                script = scriptContext.compileReader(getReader(), "script", 1, null);
                            }
                        } catch (Exception e) {
                            final String message = "Failed to compile JavaScript: " + getAbsolutePath();
                            OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
                            ConsoleService.getInstance().writeError(message + "\n" + e.getMessage()); //$NON-NLS-1$
                        }
                        closeReader();
                    }
                });

            }


            UIBundlingThread.getInstance().addRunnable(display, new Runnable() {

                @Override
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


    @Override
    protected String getFileExtension() {
        return ScriptService.JS;
    }

    @Override
    protected String getScriptHeader() {
        return ScriptService.DEFAULT_JS_HEADER;
    }

}
