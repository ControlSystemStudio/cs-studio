package org.csstudio.opibuilder.script;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.script.ScriptService.ScriptType;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.csstudio.utility.pv.PV;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.mozilla.javascript.Context;
import org.osgi.framework.Bundle;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/**The factory to return the corresponding script store according to the script type.
 * @author Xihui Chen
 *
 */
public class ScriptStoreFactory {
	
	private static boolean pythonInterpreterInitialized = false;
	
	
	private static Map<Display, Context> displayContextMap = 
			new HashMap<Display, Context>();
	
	public static void initPythonInterpreter() throws Exception{
		if(pythonInterpreterInitialized)
			return;
		//add org.python/jython.jar/Lib to PYTHONPATH
		Bundle bundle = Platform.getBundle("org.python"); //$NON-NLS-1$
		URL fileURL = FileLocator.find(bundle, new Path("jython.jar"), null);
		String pythonPath = FileLocator.resolve(fileURL).getPath() + "/Lib"; //$NON-NLS-1$
		String prefPath = PreferencesHelper.getPythonPath();
		if( prefPath!=null)
			pythonPath = pythonPath + System.getProperty("path.separator") + //$NON-NLS-1$
				prefPath;
    	Properties props = new Properties();
    	props.setProperty("python.path", pythonPath); //$NON-NLS-1$
    	//Disable cachedir so jython can start fast and no cachedir fold created.
    	//See http://www.jython.org/jythonbook/en/1.0/ModulesPackages.html#java-package-scanning
    	//and http://wiki.python.org/jython/PackageScanning
    	props.setProperty(PySystemState.PYTHON_CACHEDIR_SKIP, "true"); //$NON-NLS-1$
        PythonInterpreter.initialize(System.getProperties(), props,
                 new String[] {""}); //$NON-NLS-1$
		pythonInterpreterInitialized = true;
	}	
	
	/**
	 * Must be called in UI Thread.
	 * @throws Exception 
	 */
	private static void initJSEngine() throws Exception {
		Context scriptContext = Context.enter();
		final Display display = Display.getCurrent();
		displayContextMap.put(display, scriptContext);
		SingleSourceHelper.rapAddDisplayDisposeListener(display, new Runnable() {
			
			public void run() {
				displayContextMap.remove(display);
			}
		});
	}

	/**This method must be called in UI Thread!
	 * @param scriptData
	 * @param editpart
	 * @param pvArray
	 * @return
	 * @throws Exception
	 */
	public static AbstractScriptStore getScriptStore(
			ScriptData scriptData, AbstractBaseEditPart editpart, PV[] pvArray) throws Exception{
		boolean jsEngineInitialized = displayContextMap.containsKey(Display.getCurrent());
		if(!scriptData.isEmbedded() && 
				(scriptData.getPath() == null || scriptData.getPath().getFileExtension() == null)){
			if(scriptData instanceof RuleScriptData){
				if(!jsEngineInitialized)
					initJSEngine();
				return new RhinoScriptStore(scriptData, editpart, pvArray);
			}
			else
				throw new RuntimeException("No Script Engine for this type of script");
		}
		String fileExt = ""; //$NON-NLS-1$
		if(scriptData.isEmbedded()){
			if(scriptData.getScriptType() == ScriptType.JAVASCRIPT)
				fileExt = ScriptService.JS;
			else if (scriptData.getScriptType() == ScriptType.PYTHON)
				fileExt = ScriptService.PY;				
		}else
			fileExt= scriptData.getPath().getFileExtension().trim().toLowerCase();
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
	 * @throws Exception 
	 */
	public static Context getJavaScriptContext() throws Exception {
		Display display = Display.getCurrent();
		boolean jsEngineInitialized = displayContextMap.containsKey(display);
		if(!jsEngineInitialized)
			initJSEngine();		
		return displayContextMap.get(display);
	}
	
	public static void exit(){
		boolean jsEngineInitialized = displayContextMap.containsKey(Display.getCurrent());
		if(jsEngineInitialized)
			UIBundlingThread.getInstance().addRunnable(Display.getCurrent(), new Runnable(){
				public void run() {
					Context.exit();
				}
			});
	}
	
}
