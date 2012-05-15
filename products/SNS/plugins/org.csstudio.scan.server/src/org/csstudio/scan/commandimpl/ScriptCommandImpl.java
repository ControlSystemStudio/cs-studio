/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.commandimpl;

import org.csstudio.scan.command.ScanScript;
import org.csstudio.scan.command.ScriptCommand;
import org.csstudio.scan.command.ScriptCommandContext;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanContext;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/** {@link ScanCommandImpl} that executes a script
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScriptCommandImpl extends ScanCommandImpl<ScriptCommand>
{
	/** Initialize
	 *  @param command Command description
	 */
    public ScriptCommandImpl(final ScriptCommand command)
    {
        super(command);
    }

    /** {@inheritDoc} */
	@Override
    public void execute(final ScanContext context) throws Exception
    {
		// TODO Configure Jython
		final PySystemState state = new PySystemState();

		// TODO Preference for Script Path
		final String path = "/Kram/MerurialRepos/cs-studio-3.1/products/SNS/plugins/org.csstudio.scan/examples";
		state.path.append(new PyString(path));

    	final PythonInterpreter interpreter = new PythonInterpreter(null, state);
    	interpreter.exec("from find_peak import FindPeak");

        final PyObject py_class = interpreter.get("FindPeak");
        final PyObject py_object = py_class.__call__();
        final ScanScript java_ref = (ScanScript) py_object.__tojava__(ScanScript.class);

        // TODO Execute script
        try
        {
        	final ScriptCommandContext script_context = new ScriptCommandContextImpl(context);
        	java_ref.run(script_context);
        }
        catch (PyException ex)
        {
        	throw new Exception(getMessage(ex), ex);
        }

		context.workPerformed(1);
    }

	/** We can only report the message back to scan server
	 *  clients, not the whole exception because it doesn't 'serialize'.
	 *  The PyException, however, tends to have no message at all.
	 *  @param ex Python exception
	 *  @return Message with info about python exception
	 */
	private String getMessage(final PyException ex)
    {
    	final StringBuilder buf = new StringBuilder("Script Command:");
    	if (ex.value != null)
    		buf.append(" ").append(ex.value.asString());
    	if (ex.traceback != null)
    	{
    		buf.append(" ");
    		ex.traceback.dumpStack(buf);
    	}
    	return buf.toString();
    }
}
