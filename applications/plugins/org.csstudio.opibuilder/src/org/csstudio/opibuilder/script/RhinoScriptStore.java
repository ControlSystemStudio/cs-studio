package org.csstudio.opibuilder.script;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * The script store help to store the compiled script for the afterward executions. This is 
 * specified for the rhino script engine. The store must be disposed manually when it is not needed.
 * @author Xihui Chen
 *
 */
public class RhinoScriptStore {
	
	private Context scriptContext;
	
	private Scriptable scriptScope;
	
	private Object widgetController;	
	
	private Object pvArrayObject;
	
	private Script script;
	
	private IPath scriptPath;
	
	private Map<PV, PVListener> pvListenerMap;
	
	private boolean errorInScript;

	public RhinoScriptStore(IPath path, AbstractBaseEditPart editpart, PV[] pvArray) throws Exception {	
		this.scriptPath = path;
		errorInScript = false;
		scriptContext = ScriptService.getInstance().getScriptContext();
	
		scriptScope = new ImporterTopLevel(scriptContext);	
		
		//read file			
		InputStream inputStream = ResourceUtil.pathToInputStream(path);
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(inputStream));	
		
		//compile
		script = scriptContext.compileReader(reader, "script", 1, null); //$NON-NLS-1$
		inputStream.close();
		reader.close();
		
		widgetController = Context.javaToJS(editpart, scriptScope);
		pvArrayObject = Context.javaToJS(pvArray, scriptScope);
		ScriptableObject.putProperty(scriptScope, ScriptService.WIDGET_CONTROLLER, widgetController);	
		ScriptableObject.putProperty(scriptScope, ScriptService.PV_ARRAY, pvArrayObject);	
		
		
		pvListenerMap = new HashMap<PV, PVListener>();
		
		//register pv listener
		for(PV pv : pvArray){
			if(pv == null)
				continue;	
			PVListener pvListener = new PVListener() {			
				public void pvValueUpdate(PV pv) {
					UIBundlingThread.getInstance().addRunnable(new Runnable() {
						
						public void run() {
							if(!errorInScript){
								try {								
									script.exec(scriptContext, scriptScope);
								} catch (Exception e) {
									errorInScript = true;
									MessageDialog.openError(null, "script error", 
											"Error exists in script " +
											scriptPath.toString() + ". The script will not execute in subsequence. \n" + e.getMessage());
								}
							}
						}
					});
				}			
				public void pvDisconnected(PV pv) {			
				}
			};
			pvListenerMap.put(pv, pvListener);
			pv.addListener(pvListener);
		}
	}
	
	public void dispose() {
		for(PV pv : pvListenerMap.keySet()){
			pv.removeListener(pvListenerMap.get(pv));
		}
		pvListenerMap.clear();
	}
	
	
	
	
	
}
