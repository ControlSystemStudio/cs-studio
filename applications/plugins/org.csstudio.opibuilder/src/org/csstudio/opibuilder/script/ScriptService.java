package org.csstudio.opibuilder.script;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.pv.PV;
import org.eclipse.jface.dialogs.MessageDialog;
import org.mozilla.javascript.Context;

/**The center service for script execution.
 * @author Xihui Chen
 *
 */
public class ScriptService {
	
	public static final String PV_ARRAY = "pvArray";

	public static final String WIDGET_CONTROLLER = "widgetController";

	private static ScriptService instance;
	
	private Map<ScriptData, RhinoScriptStore> scriptMap = new HashMap<ScriptData, RhinoScriptStore>();
	
	private Context scriptContext;
	
	/** Private constructor to prevent instantiation
	 *  @see #getInstance()
	 */
	private ScriptService() {
		
		UIBundlingThread.getInstance().addRunnable(new Runnable() {
			
			public void run() {
				scriptContext = Context.enter();
			}
		});
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
	public void registerScript(final ScriptData scriptData, final AbstractBaseEditPart editpart, final PV[] pvArray){		
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				RhinoScriptStore scriptStore = null;
				try {
					scriptStore = new RhinoScriptStore(scriptData.getPath(), editpart, pvArray);
					scriptMap.put(scriptData, scriptStore);
				}catch (Exception e) {
					String errorInfo = "Failed to register script: " +
					scriptData.getPath().toString() + ". ";
					MessageDialog.openError(null, "script error", errorInfo + e.getMessage());
					CentralLogger.getInstance().error(this, errorInfo, e);
				} 				
			}
		});
		
	}
	
	public void unregisterScript(final ScriptData scriptData){
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				scriptMap.get(scriptData).dispose();
				scriptMap.remove(scriptData);						
			}
		});
		
	}
	
	
	public void exit(){
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				Context.exit();
			}			
		});
		
		instance =  null;
	}
	
}
