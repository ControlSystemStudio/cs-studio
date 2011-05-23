package org.csstudio.opibuilder.script;

import java.util.Properties;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.platform.ui.util.UIBundlingThread;
import org.csstudio.utility.pv.PV;
import org.mozilla.javascript.Context;
import org.python.util.PythonInterpreter;

/**The factory to return the corresponding script store according to the script type.
 * @author Xihui Chen
 *
 */
public class ScriptStoreFactory {
	
	private static boolean pythonInterpreterInitialized = false;
	
	private static boolean jsEngineInitialized = false;
	
	private static Context scriptContext;
	
	public static void initPythonInterpreter() throws Exception{
		if(pythonInterpreterInitialized)
			return;
		String pythonPath = PreferencesHelper.getPythonPath();
		if(pythonPath != null){
    		Properties props = new Properties();
    		props.setProperty("python.path", pythonPath); //$NON-NLS-1$
        	PythonInterpreter.initialize(System.getProperties(), props,
                    new String[] {""}); //$NON-NLS-1$
    	}
		pythonInterpreterInitialized = true;
	}	
	
	public static void initJSEngine() {
		scriptContext = Context.enter();
		jsEngineInitialized = true;

	}

	public static AbstractScriptStore getScriptStore(
			ScriptData scriptData, AbstractBaseEditPart editpart, PV[] pvArray) throws Exception{
		
		if(scriptData.getPath() == null || scriptData.getPath().getFileExtension() == null){
			if(scriptData instanceof RuleScriptData){
				if(!jsEngineInitialized)
					initJSEngine();
				return new RhinoScriptStore(scriptData, editpart, pvArray);
			}
			else
				throw new RuntimeException("No Script Engine for this type of script");
		}
		String fileExt = scriptData.getPath().getFileExtension().trim().toLowerCase();
		if(fileExt.equals(ScriptService.JS)){ //$NON-NLS-1$
			if(!jsEngineInitialized)
				initJSEngine();
			return new RhinoScriptStore(scriptData, editpart, pvArray);
		}
		else if (fileExt.equals(ScriptService.PY)){ //$NON-NLS-1$
			if(!pythonInterpreterInitialized)
				initPythonInterpreter();
			return new JythonScriptStore(scriptData, editpart, pvArray);
		}
		else
			throw new RuntimeException("No Script Engine for this type of script");
	}
	
	/**This method must be executed in UI Thread!
	 * @return the script context.
	 */
	public static Context getJavaScriptContext() {
		if(!jsEngineInitialized)
			initJSEngine();		
		return scriptContext;
	}
	
	public static void exit(){
		if(jsEngineInitialized)
			UIBundlingThread.getInstance().addRunnable(new Runnable(){
				public void run() {
					Context.exit();
				}
			});
	}
	
}
