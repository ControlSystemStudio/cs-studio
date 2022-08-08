package org.csstudio.opibuilder.script;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.script.ScriptService.ScriptType;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.simplepv.IPV;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.swt.widgets.Display;
import org.mozilla.javascript.Context;
import org.osgi.framework.Bundle;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/**The factory to return the corresponding script store according to the script type.
 * @author Xihui Chen
 *
 * TODO Cleanup
 * This has getJavaScriptEngine() and getRhinoContext().
 * Callers invoke one or the other depending on defaultJsEngine = ..RHINO or JDK.
 *
 * A true factory should just have one "getJava..()" call which internally decides
 * to return the Rhino from lib/, or the one bundled in Java 7, or Nashorn from Java 8.
 * After fixing the Java 7 Rhino issues (https://github.com/ControlSystemStudio/cs-studio/issues/723),
 * remove lib/rhino/js.jar
 */
@SuppressWarnings("nls")
public class ScriptStoreFactory {

    public static enum JavaScriptEngine {
        RHINO,
        RHINO_WITH_FAST_PATH,
        JDK;
    }

    final private static JavaScriptEngine defaultJsEngine;

    private static boolean pythonInterpreterInitialized = false;

    private static Map<Display, Context> displayContextMap =
            new HashMap<Display, Context>();
    private static Map<Display, ScriptEngine> displayScriptEngineMap = new HashMap<>();


    static
    {
        String option = JavaScriptEngine.RHINO.name(); // Default
        final IPreferencesService service = Platform.getPreferencesService();
        if (service != null)
            option = service.getString(OPIBuilderPlugin.PLUGIN_ID, "java_script_engine", option, null);
        try
        {
            defaultJsEngine = JavaScriptEngine.valueOf(option);
        }
        catch (Throwable ex)
        {    // Create more obvious exception
            throw new RuntimeException("Invalid preference setting " + OPIBuilderPlugin.PLUGIN_ID + "/java_script_engine=" + option);
        }
    }

    public static void initPythonInterpreter() throws Exception
    {
        if(pythonInterpreterInitialized)
            return;

        // Add Jython's /lib PYTHONPATH
        final Bundle bundle = Platform.getBundle("org.python.jython");
        String pythonPath = null;
        if (bundle == null)
            throw new Exception("Cannot locate jython bundle");
        // Used to be packed as org.python.jython/jython.jar/Lib
        final URL fileURL = FileLocator.find(bundle, new Path("jython.jar"), null);
        if (fileURL != null)
            pythonPath = FileLocator.resolve(fileURL).getPath() + "/Lib";
        else
        {   // Different packaging where jython.jar is expanded, /Lib at plugin root
            pythonPath = FileLocator.resolve(new URL("platform:/plugin/org.python.jython/Lib/")).getPath();
            // Turn politically correct URL path digestible by jython
            if (pythonPath.startsWith("file:/"))
                pythonPath = pythonPath.substring(5);
            pythonPath = pythonPath.replace(".jar!", ".jar");
        }

        final Optional<String> prefPath = PreferencesHelper.getPythonPath();
        if (prefPath.isPresent())
            pythonPath += System.getProperty("path.separator") + prefPath.get();
        final Properties props = new Properties();
        props.setProperty("python.path", pythonPath);
        // Disable cachedir to avoid creation of cachedir folder.
        // See http://www.jython.org/jythonbook/en/1.0/ModulesPackages.html#java-package-scanning
        // and http://wiki.python.org/jython/PackageScanning
        props.setProperty(PySystemState.PYTHON_CACHEDIR_SKIP, "true");

        // Jython 2.7(b2, b3) need these to set sys.prefix and sys.executable.
        // If left undefined, initialization of Lib/site.py fails with
        // posixpath.py", line 394, in normpath AttributeError:
        // 'NoneType' object has no attribute 'startswith'
        props.setProperty("python.home", ".");
        props.setProperty("python.executable", "css");

        PythonInterpreter.initialize(System.getProperties(), props,
                 new String[] {""});
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

            @Override
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

            @Override
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
        if(fileExt.equals(ScriptService.JS)){
            return getJavaScriptStore(scriptData, editpart, pvArray);
        }
        else if (fileExt.equals(ScriptService.PY)){
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
        else if (defaultJsEngine == JavaScriptEngine.RHINO_WITH_FAST_PATH) {
        	boolean rhinoJsEngineInitialized = displayContextMap.containsKey(Display.getCurrent());
            if(!rhinoJsEngineInitialized)
                initRhinoJSEngine();
            return new RhinoWithFastPathScriptStore(scriptData, editpart, pvArray);
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
     * @return the Rhino script context.
     * @throws Exception on error, including invocation when not using <code>JavaScriptEngine.RHINO</code>
     */
    public static Context getRhinoContext() throws Exception {
        if (defaultJsEngine != JavaScriptEngine.RHINO && defaultJsEngine != JavaScriptEngine.RHINO_WITH_FAST_PATH)
            throw new RuntimeException("Fetching Rhino context while not using Rhino?");
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
                @Override
                public void run() {
                    Context.exit();
                }
            });
    }

    /**This method must be executed in UI Thread!
     * @return the JDK's Javascript script engine.
     * @throws Exception on error, including invocation when not using <code>JavaScriptEngine.JDK</code>
     */
    public static ScriptEngine getJavaScriptEngine() throws Exception {
        if (defaultJsEngine != JavaScriptEngine.JDK)
            throw new RuntimeException("Fetching JDK script engine context while not using JDK");
        Display display = Display.getCurrent();
        boolean jsEngineInitialized = displayScriptEngineMap.containsKey(display);
        if(!jsEngineInitialized)
            initJdkJSEngine();
        return displayScriptEngineMap.get(display);
    }

}
