package org.csstudio.opibuilder.widgetActions;

import java.util.logging.Level;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;

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

/**
 * The action executing javascript with the default javascript engine embedded in the JDK.
 * The default JDK javascript engine is Rhino for Java 7, Nashorn for Java 8.
 */
class ExecuteJavaScriptJdkAction extends AbstractExecuteScriptAction {

    private ScriptEngine scriptEngine;
    private Bindings scriptScope;
    private CompiledScript script;

    @Override
    protected String getFileExtension() {
        return ScriptService.JS;
    }

    @Override
    protected String getScriptHeader() {
        return ScriptService.DEFAULT_JS_HEADER;
    }

    @Override
    public void run() {
        if(scriptEngine == null){
            try {
                scriptEngine = ScriptStoreFactory.getJavaScriptEngine();
            } catch (Exception exception) {
                ErrorHandlerUtil.handleError("Failed to get Script Context", exception);
                return;
            }
            scriptScope = scriptEngine.createBindings();
            GraphicalViewer viewer = getWidgetModel().getRootDisplayModel().getViewer();
            if(viewer != null){
                Object obj = viewer.getEditPartRegistry().get(getWidgetModel());
                if(obj != null && obj instanceof AbstractBaseEditPart){
                    scriptScope.put(ScriptService.DISPLAY, viewer.getContents());
                    scriptScope.put(ScriptService.WIDGET, obj);
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
                                script = ((Compilable) scriptEngine).compile(getScriptText());
                            else{
                                script = ((Compilable) scriptEngine).compile(getReader());
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
                        script.eval(scriptScope);
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
        public ActionType getActionType() {
            return ActionType.EXECUTE_JAVASCRIPT;
        }
    }
