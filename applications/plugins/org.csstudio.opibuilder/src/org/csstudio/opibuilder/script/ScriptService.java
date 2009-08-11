package org.csstudio.opibuilder.script;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.utility.pv.PV;
import org.mozilla.javascript.Context;

/**The center service for script execution.
 * @author Xihui Chen
 *
 */
public class ScriptService {
	
	public static final String PV_ARRAY = "pvArray";

	public static final String WIDGET_CONTROLLER = "widgetController";

	private static ScriptService instance;
	
	private Map<ScriptData, RhinoScriptStore> scriptMap;
	
	private Context scriptContext;
	
	public ScriptService() {
		
		UIBundlingThread.getInstance().addRunnable(new Runnable() {
			
			public void run() {
				scriptContext = Context.enter();
			}
		});
				
		scriptMap = new HashMap<ScriptData, RhinoScriptStore>();
	}
	
	public synchronized static ScriptService getInstance() {
		if(instance == null)
			instance = new ScriptService();
		return instance;
	}
	
	
	public Context getScriptContext() {
		return scriptContext;
	}
	
	/**Register the script in the script service, so that it could be executed afterwards.
	 * @param scriptData
	 * @param editpart
	 * @param pvArray
	 * @throws Exception
	 */
	public void registerScript(ScriptData scriptData, AbstractBaseEditPart editpart, PV[] pvArray) throws Exception{
		//if(scriptMap.containsKey(scriptData))
		//	throw new Exception("The script has been registered before.");
		RhinoScriptStore scriptStore = new RhinoScriptStore(scriptData.getPath(), editpart, pvArray);
		scriptMap.put(scriptData, scriptStore);
	}
	
	public void unregisterScript(ScriptData scriptData){
		scriptMap.remove(scriptData);		
	}
	
	
	public void exit(){
		Context.exit();
		instance =  null;
	}
	
}
