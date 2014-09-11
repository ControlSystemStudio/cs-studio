package org.csstudio.opibuilder.script;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.script.ScriptService.ScriptType;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.simplepv.IPV;
import org.csstudio.ui.util.thread.UIBundlingThread;
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
	
	public static enum JavaScriptEngine {
		RHINO,
		JDK;
	}
	
	private static volatile JavaScriptEngine defaultJsEngine = JavaScriptEngine.JDK;
	
	private static boolean pythonInterpreterInitialized = false;
	
	private static Map<Display, Context> displayContextMap = 
			new HashMap<Display, Context>();
	private static Map<Display, ScriptEngine> displayScriptEngineMap = new HashMap<>();
	
	@SuppressWarnings("nls")
    public static void initPythonInterpreter() throws Exception{
		if(pythonInterpreterInitialized)
			return;
		//add org.python.jython/jython.jar/Lib to PYTHONPATH
		final Bundle bundle = Platform.getBundle("org.python.jython");
		String pythonPath = null;
		if (bundle == null)
		    throw new Exception("Cannot locate jython bundle");
		URL fileURL = FileLocator.find(bundle, new Path("jython.jar"), null);
		if (fileURL != null){
			pythonPath = FileLocator.resolve(fileURL).getPath() + "/Lib";
		} else {
			pythonPath = FileLocator.resolve(new URL("platform:/plugin/org.python.jython/Lib/")).getPath();
		}
			
		
		String prefPath = PreferencesHelper.getPythonPath();
		if( prefPath!=null)
			pythonPath = pythonPath + System.getProperty("path.separator") +
				prefPath;
    	Properties props = new Properties();
    	props.setProperty("python.path", pythonPath);
    	//Disable cachedir so jython can start fast and no cachedir fold created.
    	//See http://www.jython.org/jythonbook/en/1.0/ModulesPackages.html#java-package-scanning
    	//and http://wiki.python.org/jython/PackageScanning
    	props.setProperty(PySystemState.PYTHON_CACHEDIR_SKIP, "true");
        PythonInterpreter.initialize(System.getProperties(), props,
                 new String[] {""}); //$NON-NLS-1$
		pythonInterpreterInitialized = true;
	}
	
	public static JavaScriptEngine getDefaultJavaScriptEngine()
	{
		return defaultJsEngine;
	}
	
	/**
	 * Must be called in UI Thread.
	 * @throws Exception 
	 */
	private static void initRhinoJSEngine() throws Exception {
		Context scriptContext = Context.enter();
		final Display display = Display.getCurrent();
		displayContextMap.put(display, scriptContext);
		SingleSourceHelper.rapAddDisplayDisposeListener(display, new Runnable() {
			
			public void run() {
				displayContextMap.remove(display);
			}
		});
	}
	
	/**
	 * Must be called in UI Thread.
	 * @throws Exception 
	 */
	private static void initJdkJSEngine() throws Exception {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
		final Display display = Display.getCurrent();
		displayScriptEngineMap.put(display, engine);
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
			ScriptData scriptData, AbstractBaseEditPart editpart, IPV[] pvArray) throws Exception{
		if(!scriptData.isEmbedded() && 
				(scriptData.getPath() == null || scriptData.getPath().getFileExtension() == null)){
			if(scriptData instanceof RuleScriptData){
				return getJavaScriptStore(scriptData, editpart, pvArray);
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
			return getJavaScriptStore(scriptData, editpart, pvArray);
		}
		else if (fileExt.equals(ScriptService.PY)){ //$NON-NLS-1$
			if(!pythonInterpreterInitialized)
				initPythonInterpreter();
			return new JythonScriptStore(scriptData, editpart, pvArray);
		}
		else
			throw new RuntimeException("No Script Engine for this type of script");
	}
	
	private static AbstractScriptStore getJavaScriptStore(
			ScriptData scriptData, AbstractBaseEditPart editpart, IPV[] pvArray) throws Exception {
		if (defaultJsEngine == JavaScriptEngine.RHINO) {
			boolean rhinoJsEngineInitialized = displayContextMap.containsKey(Display.getCurrent());
			if(!rhinoJsEngineInitialized)
				initRhinoJSEngine();
			return new RhinoScriptStore(scriptData, editpart, pvArray);
		}
		else {
			boolean jdkJsEngineInitialized = displayScriptEngineMap.containsKey(Display.getCurrent());
			if (!jdkJsEngineInitialized) {
				initJdkJSEngine();
			}
			return new JavaScriptStore(scriptData, editpart, pvArray);
		}
	}
	
	/**This method must be executed in UI Thread!
	 * @return the script context.
	 * @throws Exception 
	 */
	public static Context getJavaScriptContext() throws Exception {
		Display display = Display.getCurrent();
		boolean jsEngineInitialized = displayContextMap.containsKey(display);
		if(!jsEngineInitialized)
			initRhinoJSEngine();		
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
	
	/**This method must be executed in UI Thread!
	 * @return the script engine.
	 * @throws Exception 
	 */
	public static ScriptEngine getJavaScriptEngine() throws Exception {
		Display display = Display.getCurrent();
		boolean jsEngineInitialized = displayScriptEngineMap.containsKey(display);
		if(!jsEngineInitialized)
			initJdkJSEngine();		
		return displayScriptEngineMap.get(display);
	}
	
}
