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
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.ScanSystemPreferences;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.core.imp;
import org.python.util.PythonInterpreter;

/** Helper for obtaining Jython interpreter
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JythonSupport
{
    private static boolean initialized = false;
    private static List<String> paths = new ArrayList<String>();
    private static ClassLoader plugin_class_loader;

	final private PythonInterpreter interpreter;

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

	/** Perform static, one-time initialization
	 *  @throws Exception on error
	 */
	private static synchronized void init() throws Exception
	{
	    if (!initialized)
	    {
	        // Add org.python/jython.jar/Lib to Python path
	        String path = getPluginPath("org.python", "jython.jar");
	        if (path != null)
	        {
	            paths.add(path + "/Lib");

	            // Have Platform support, so create combined class loader that
	            // first uses this plugin's class loaded and has thus access
	            // to everything that the plugin can reach.
	            // Fall back to the original Jython class loaded.
	            plugin_class_loader = new ClassLoader()
                {
	                @Override
	                public Class<?> loadClass(final String name) throws ClassNotFoundException
	                {
	                    // Temporarily set class loader to null,
	                    // which is the original status of SystemState.
	                    final ClassLoader saveClassLoader = Py.getSystemState().getClassLoader();
	                    Py.getSystemState().setClassLoader(null);
	                    try
	                    {
	                        return JythonSupport.class.getClassLoader().loadClass(name);
	                    }
	                    catch (Exception e)
	                    {
	                        return imp.getSyspathJavaLoader().loadClass(name);
	                    }
	                    finally {
	                        Py.getSystemState().setClassLoader(saveClassLoader);
	                    }
	                }

	                @Override
	                public Enumeration<URL> getResources(final String name) throws IOException
	                {
	                    return JythonSupport.class.getClassLoader().getResources(name);
	                }
                };
	        }

	        // Add numji
	        path = getPluginPath("org.csstudio.numjy", "jython");
	        if (path != null)
                paths.add(path);

	        // Add scan script paths
	        final String[] pref_paths = ScanSystemPreferences.getScriptPaths();
	        for (String pref_path : pref_paths)
	        {   // Resolve platform:/plugin/...
	            if (pref_path.startsWith("platform:/plugin/"))
	            {
	                final String plugin_path = pref_path.substring(17);
	                // Locate name of plugin and path within plugin
	                final int sep = plugin_path.indexOf('/');
	                final String plugin;
	                if (sep < 0)
	                {
	                    plugin = plugin_path;
	                    path = "/";
	                }
	                else
	                {
	                    plugin = plugin_path.substring(0, sep);
	                    path = plugin_path.substring(sep + 1);
	                }
	                path = getPluginPath(plugin, path);
	                if (path == null)
	                    throw new Exception("Error in scan script path " + pref_path);
	                else
	                    paths.add(path);
	            }
	            else // Add as-is
	                paths.add(pref_path);
	        }

	        initialized = true;
	    }
	}

    /** Initialize
     *  @throws Exception on error
     */
	public JythonSupport() throws Exception
	{
	    init();
		final PySystemState state = new PySystemState();
		if (plugin_class_loader != null)
		    state.setClassLoader(plugin_class_loader);

		// Path to Python standard lib, numjy, scan system
        for (String path : paths)
            state.path.append(new PyString(path));

    	interpreter = new PythonInterpreter(null, state);
	}

	/** Load a Jython class
	 *
 	 *  @param type Type of the Java object to return
	 *  @param class_name Name of the Jython class,
	 *                    must be in package (file) using lower case of class name
	 *  @return Java object for instance of Jython class
	 */
    @SuppressWarnings("unchecked")
    public <T> T loadClass(final Class<T> type, final String class_name)
	{
		// Get package name
		final String pack_name = class_name.toLowerCase();
		Logger.getLogger(getClass().getName()).log(Level.FINE,
	        "Loading Jython class {0} from {1}",
	        new Object[] { class_name, pack_name });

		// Display path
		//interpreter.exec("import sys");
        //interpreter.exec("print 'Jython Path: ', sys.path");

    	// Import class into Jython
		interpreter.exec("from " + pack_name +  " import " + class_name);
		// Create Java reference
        final PyObject py_class = interpreter.get(class_name);
        final PyObject py_object = py_class.__call__();
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
