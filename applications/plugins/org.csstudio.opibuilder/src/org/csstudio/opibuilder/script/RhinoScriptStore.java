package org.csstudio.opibuilder.script;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.UIBundlingThread;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;
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
public class RhinoScriptStore implements IScriptStore{
	
	private Context scriptContext;
	
	private Scriptable scriptScope;
	
	private Object widgetController;	
	
	private Object pvArrayObject;
	
	private Script script;
	
	private IPath scriptPath;
	
	private String errorSource;
	
	private Map<PV, PVListener> pvListenerMap;
	
	private boolean errorInScript;
	
	volatile boolean unRegistered = false;

	
	/**
	 * A map to see if a PV was triggered before, this is used to skip the first trigger.
	 */
	private Map<PV, Boolean> pvTriggeredMap;	
	
	private boolean triggerSuppressed = false; 
	
	public RhinoScriptStore(final ScriptData scriptData, final AbstractBaseEditPart editpart, 
			final PV[] pvArray) throws Exception {
		errorInScript = false;
		scriptContext = ScriptService.getInstance().getScriptContext();		
		scriptScope = new ImporterTopLevel(scriptContext);	
		
		errorSource = scriptData instanceof RuleScriptData ? 
				((RuleScriptData)scriptData).getRuleData().getName() : scriptData.getPath().toString();
		
		if(scriptData instanceof RuleScriptData){
			script = scriptContext.compileString(((RuleScriptData)scriptData).getScriptString(),
					"rule", 1, null);
		}else{
			scriptPath = scriptData.getPath();
			if(!scriptPath.isAbsolute()){
				scriptPath = ResourceUtil.buildAbsolutePath(
						editpart.getWidgetModel(), scriptPath);
			}
			//read file			
			InputStream inputStream = ResourceUtil.pathToInputStream(scriptPath);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(inputStream));	
			
			//compile
			script = scriptContext.compileReader(reader, "script", 1, null); //$NON-NLS-1$
			inputStream.close();
			reader.close();
		}
	
		
		widgetController = Context.javaToJS(editpart, scriptScope);
		pvArrayObject = Context.javaToJS(pvArray, scriptScope);
		ScriptableObject.putProperty(scriptScope, ScriptService.WIDGET_CONTROLLER, widgetController);	
		ScriptableObject.putProperty(scriptScope, ScriptService.PV_ARRAY, pvArrayObject);	
		
		
		pvListenerMap = new HashMap<PV, PVListener>();		
		pvTriggeredMap = new HashMap<PV, Boolean>();
		
		PVListener suppressPVListener = new PVListener() {

			public synchronized void pvValueUpdate(PV pv) {
				if (triggerSuppressed && checkPVsConnected(scriptData, pvArray)) {
					executeScript();
					triggerSuppressed = false;
				}
			}

			public void pvDisconnected(PV pv) {

			}
		};

		PVListener triggerPVListener = new PVListener() {
			public synchronized void pvValueUpdate(PV pv) {

				// skip the first trigger if it is needed.
				if (scriptData.isSkipPVsFirstConnection()
						&& !pvTriggeredMap.get(pv)) {
					pvTriggeredMap.put(pv, true);
					return;
				}

				// execute script only if all input pvs are connected
				if (pvArray.length > 1) {
					if (!checkPVsConnected(scriptData, pvArray)) {
						triggerSuppressed = true;
						return;

					}
				}

				executeScript();
			}

			public void pvDisconnected(PV pv) {
			}
		};
		//register pv listener
		int i=0;
		for(PV pv : pvArray){
			if(pv == null)
				continue;	
			if(!scriptData.getPVList().get(i++).trigger){
				//execute the script if it was suppressed.				
				pv.addListener(suppressPVListener);
				pvListenerMap.put(pv, suppressPVListener);
				continue;
			};
			pvTriggeredMap.put(pv, false);
			pv.addListener(triggerPVListener);
			pvListenerMap.put(pv, triggerPVListener);
			
		}
	}
	
	private void executeScript() {
		UIBundlingThread.getInstance().addRunnable(new Runnable() {
			public void run() {
				if (!errorInScript && !unRegistered) {
					try {
						script.exec(scriptContext, scriptScope);
					} catch (Exception e) {
						errorInScript = true;
						final String message = NLS
								.bind("Error in {0}.\nAs a consequence, the script or rule will not be executed.\n{1}",
										errorSource, e.getMessage());
						ConsoleService.getInstance().writeError(message);
						CentralLogger.getInstance().error(this, e);
					}
				}
			}
		});
	}	
	
	private boolean checkPVsConnected(ScriptData scriptData, PV[] pvArray){
		if(!scriptData.isCheckConnectivity())
			return true;
		for(PV pv : pvArray){
			if(!pv.isConnected())
				return false;
		}
		return true;
		
	}

	public void unRegister() {
		unRegistered = true;
		for(Entry<PV, PVListener> entry :  pvListenerMap.entrySet()){
			entry.getKey().removeListener(entry.getValue());
		}		
	}
	
}
