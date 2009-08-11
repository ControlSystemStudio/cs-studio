package org.csstudio.opibuilder.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

/**The center service for script execution. This service is implemented based on Rhino script engine.
 * @author Xihui Chen
 *
 */
public class RhinoScriptService {

	private static RhinoScriptService instance;
	
	private Map<IPath, Script> compiledScriptMap;
	
	private Context scriptContext;
	
	public RhinoScriptService() {
		/*
		UIBundlingThread.getInstance().addRunnable(new Runnable() {
			
			public void run() {
				
			}
		});
			*/	scriptContext = Context.enter();
		compiledScriptMap = new HashMap<IPath, Script>();
	}
	
	public static RhinoScriptService getInstance() {
		if(instance == null)
			instance = new RhinoScriptService();
		return instance;
	}
	
	
	public Context getScriptContext() {
		return scriptContext;
	}
	
	public void executeScript(IPath path){
		if(compiledScriptMap.containsKey(path)){
		//	compiledScriptMap.get(path).exec(scriptContext, );
		}
	}
	
	
	public void exit(){
		Context.exit();
		instance =  null;
	}
	
}
