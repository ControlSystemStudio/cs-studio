/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.ScanSystemPreferences;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/** Helper for obtaining Jython interpreter
 *
 *  TODO Join with org.csstudio.opibuilder.script.ScriptStoreFactory into a new org.csstudio.jython
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JythonSupport
{
    private static List<String> paths = init();

    final private PythonInterpreter interpreter;

    /** Perform static, one-time initialization */
    private static List<String> init()
    {
        final List<String> paths = new ArrayList<String>();
        try
        {   // Add Jython's /Lib to path
            final Bundle bundle = Platform.getBundle("org.python.jython");
            if (bundle == null)
                throw new Exception("Cannot locate jython bundle");
            // Different packaging where jython.jar is expanded, /Lib at plugin root
            String home = FileLocator.resolve(new URL("platform:/plugin/org.python.jython")).getPath();
            // Turn politically correct URL path digestible by jython
            if (home.startsWith("file:/"))
                home = home.substring(5);
            home = home.replace(".jar!", ".jar");
            paths.add(home + "Lib/");

            // Add scan script paths
            final String[] pref_paths = ScanSystemPreferences.getScriptPaths();
            for (String pref_path : pref_paths)
            {   // Resolve platform:/plugin/...
                if (pref_path.startsWith("platform:/plugin/"))
                {
                    final String plugin_path = pref_path.substring(17);
                    // Locate name of plugin and path within plugin
                    final int sep = plugin_path.indexOf('/');
                    final String plugin, path_within;
                    if (sep < 0)
                    {
                        plugin = plugin_path;
                        path_within = "/";
                    }
                    else
                    {
                        plugin = plugin_path.substring(0, sep);
                        path_within = plugin_path.substring(sep + 1);
                    }
                    final String path = getPluginPath(plugin, path_within);
                    if (path == null)
                        throw new Exception("Error in scan script path " + pref_path);
                    else
                        paths.add(path);
                }
                else // Add as-is
                    paths.add(pref_path);
            }

            // Disable cachedir to avoid creation of cachedir folder.
            // See http://www.jython.org/jythonbook/en/1.0/ModulesPackages.html#java-package-scanning
            // and http://wiki.python.org/jython/PackageScanning
            final Properties props = new Properties();
            props.setProperty(PySystemState.PYTHON_CACHEDIR_SKIP, "true");

            // Jython 2.7(b3) needs these to set sys.prefix and sys.executable.
            // If left undefined, initialization of Lib/site.py fails with
            // posixpath.py", line 394, in normpath AttributeError:
            // 'NoneType' object has no attribute 'startswith'
            props.setProperty("python.home", home);
            props.setProperty("python.executable", "css");

            PythonInterpreter.initialize(System.getProperties(), props, new String[] {""});
        }
        catch (Exception ex)
        {
            Logger.getLogger(JythonSupport.class.getName()).
                log(Level.SEVERE, "Once this worked OK, but now the Jython initialization failed. Don't you hate computers?", ex);
        }
        return paths;
    }

    /** Locate a path inside a bundle.
    *
    *  <p>If the bundle is JAR-ed up, the {@link FileLocator} will
    *  return a location with "file:" and "..jar!/path".
    *  This method patches the location such that it can be used
    *  on the Jython path.
    *
    *  @param bundle_name Name of bundle
    *  @param path_in_bundle Path within bundle
    *  @return Location of that path within bundle, or <code>null</code> if not found or no bundle support
    *  @throws IOException on error
    */
   private static String getPluginPath(final String bundle_name, final String path_in_bundle) throws IOException
   {
       final Bundle bundle = Platform.getBundle(bundle_name);
       if (bundle == null)
           return null;
       final URL url = FileLocator.find(bundle, new Path(path_in_bundle), null);
       if (url == null)
           return null;
       String path = FileLocator.resolve(url).getPath();

       path = path.replace("file:/", "/");
       path = path.replace(".jar!/", ".jar/");

       return path;
   }

    /** Initialize
     *  @throws Exception on error
     */
    public JythonSupport() throws Exception
    {
        final PySystemState state = new PySystemState();

        // Path to Python standard lib, numjy, scan system
        for (String path : paths)
            state.path.append(new PyString(path));

        // Creating a PythonInterpreter is very slow.
        //
        // In addition, concurrent creation is not supported, resulting in
        //     Lib/site.py", line 571, in <module> ..
        //     Lib/sysconfig.py", line 159, in _subst_vars AttributeError: {'userbase'}
        // or  Lib/site.py", line 122, in removeduppaths java.util.ConcurrentModificationException
        //
        // Sync. on JythonSupport to serialize the interpreter creation and avoid above errors.
        // Curiously, this speeds the interpreter creation up,
        // presumably because they're not concurrently trying to access the same resources?
        synchronized (JythonSupport.class)
        {
            interpreter = new PythonInterpreter(null, state);
        }
    }

    /** Load a Jython class
     *
      *  @param type Type of the Java object to return
     *  @param class_name Name of the Jython class,
     *                    must be in package (file) using lower case of class name
     *  @param args Arguments to pass to constructor
     *  @return Java object for instance of Jython class
     *  @throws Exception on error
     */
    @SuppressWarnings("unchecked")
    public <T> T loadClass(final Class<T> type, final String class_name, final String... args) throws Exception
    {
        // Get package name
        final String pack_name = class_name.toLowerCase();
        Logger.getLogger(getClass().getName()).log(Level.FINE,
            "Loading Jython class {0} from {1}",
            new Object[] { class_name, pack_name });

        try
        {
            // Import class into Jython
            interpreter.exec("from " + pack_name +  " import " + class_name);
        }
        catch (PyException ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "Error loading Jython class {0} from {1}",
                new Object[] { class_name, pack_name });
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                    "Search path: {0}",interpreter.getSystemState().path);

            throw new Exception("Error loading Jython class " + class_name + ":" + getExceptionMessage(ex), ex);
        }
        // Create Java reference
        final PyObject py_class = interpreter.get(class_name);
        final PyObject py_object;
        if (args.length <= 0)
            py_object = py_class.__call__();
        else
        {
            final PyObject[] py_args = new PyObject[args.length];
            for (int i=0; i<py_args.length; ++i)
                py_args[i] = new PyString(args[i]);
            py_object = py_class.__call__(py_args);
        }
        final T java_ref = (T) py_object.__tojava__(type);
        return java_ref;
    }

    /** We can only report the message of an exception back to scan server
     *  clients, not the whole exception because it doesn't 'serialize'.
     *  The PyException, however, tends to have no message at all.
     *  This helper tries to generate a somewhat useful message
     *  from the content of the exception.
     *  @param ex Python exception
     *  @return Message with info about python exception
     */
    public static String getExceptionMessage(final PyException ex)
    {
        final StringBuilder buf = new StringBuilder();
        if (ex.value instanceof PyString)
            buf.append(" ").append(ex.value.asString());
        else if (ex.getCause() != null)
            buf.append(" ").append(ex.getCause().getMessage());
        if (ex.traceback != null)
        {
            buf.append(" ");
            ex.traceback.dumpStack(buf);
        }
        return buf.toString();
    }
}
