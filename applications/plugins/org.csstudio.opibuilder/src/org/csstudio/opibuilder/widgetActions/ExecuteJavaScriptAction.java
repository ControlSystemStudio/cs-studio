package org.csstudio.opibuilder.widgetActions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.script.ScriptService;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
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
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;

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
		try {
			if(script == null){
				scriptContext = ScriptService.getInstance().getScriptContext();
				scriptScope = new ImporterTopLevel(scriptContext);	
				//read file			
				InputStream inputStream = ResourceUtil.pathToInputStream(getPath());
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));	
				
				//compile
				script = scriptContext.compileReader(reader, "script", 1, null); //$NON-NLS-1$
				inputStream.close();
				reader.close();
			}
			
			
			UIBundlingThread.getInstance().addRunnable(new Runnable() {
				
				public void run() {

						try {								
							script.exec(scriptContext, scriptScope);
						} catch (Exception e) {
							String message =  "Error exists in script " + 
								getPath() + "\n" + e; //$NON-NLS-1$
							MessageDialog.openError(null, "script error", message);
							CentralLogger.getInstance().error(this, message, e);
							ConsoleService.getInstance().writeError(message);
							
						}
					
				}
			});
		} catch (Exception e) {
			String s = "Failed to execute JavaScript: " + getPath() + "\n" + e;
			MessageDialog.openError(null, "script error",s);
			CentralLogger.getInstance().error(this, s, e);
			ConsoleService.getInstance().writeError(s);
		
		} 
	}
	
	private IPath getPath(){
		return (IPath)getPropertyValue(PROP_PATH);
	}
	
	
	
	@Override
	public String getDescription() {
		return super.getDescription() + " " + getPath(); //$NON-NLS-1$
	}

}
