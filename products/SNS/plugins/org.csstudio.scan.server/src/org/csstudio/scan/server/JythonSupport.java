/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server;

import java.net.URL;

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
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JythonSupport
{
    private static boolean initialized = false;
    private static String std_lib_path;

	final private PythonInterpreter interpreter;

	/** Perform static, one-time initialization
	 *  @throws Exception on error
	 */
	private static synchronized void init() throws Exception
	{
	    if (!initialized)
	    {
	        // Add org.python/jython.jar/Lib to Python path
	        final Bundle bundle = Platform.getBundle("org.python");
	        final URL fileURL = FileLocator.find(bundle, new Path("jython.jar"), null);
	        std_lib_path = FileLocator.resolve(fileURL).getPath() + "/Lib";

	        initialized = true;
	    }
	}

    /** Initialize
     *  @throws Exception on error
     */
	public JythonSupport() throws Exception
	{
	    init();
		// TODO Configure Jython in ScanContext so one is shared for this scan?
		final PySystemState state = new PySystemState();

		// TODO org.python/jython.jar/Lib
		state.path.append(new PyString(std_lib_path));
		// TODO Preferences for Script Path?
		// TODO Use bundle locations?
		// Scan System
		String path = "/Kram/MerurialRepos/cs-studio-3.1/products/SNS/plugins/org.csstudio.scan/examples";
		state.path.append(new PyString(path));
		// NumJy
		path = "/Kram/MerurialRepos/cs-studio-3.1/applications/plugins/org.csstudio.numjy/jython";
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
