/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server;

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
	final private PythonInterpreter interpreter;

	public JythonSupport()
	{
		// TODO Configure Jython in ScanContext so one is shared for this scan?
		final PySystemState state = new PySystemState();

		// TODO Preference for Script Path
		final String path = "/Kram/MerurialRepos/cs-studio-3.1/products/SNS/plugins/org.csstudio.scan/examples";
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
    public String getExceptionMessage(final PyException ex)
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
