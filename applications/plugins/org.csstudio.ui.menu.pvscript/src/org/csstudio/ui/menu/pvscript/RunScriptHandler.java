/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.pvscript;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.handlers.HandlerUtil;

/** Handler for the "runscript" command.
 * 
 *  Runs a script with received PVs.
 *  
 *  <p>The script name is a command parameter.
 *
 *  <p>Receives the PVs with the {@link ExecutionEvent}
 *  from the context menu invocation.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RunScriptHandler extends AbstractHandler
{
	/** ID of the command for which this is the default handler */
	final public static String COMMAND_ID = "org.csstudio.ui.menu.pvscript.runscript";

	/** ID of the command parameter for the PV name */
	final public static String PARAM_SCRIPT = "script";

	/** {@inheritDoc} */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		// Get script name from command
        final String script = event.getParameter(PARAM_SCRIPT);
        if (script == null)
        	throw new ExecutionException("Missing " + PARAM_SCRIPT + " parameter");

		// Get PVs from event (context menu invocation)
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        final ProcessVariable[] pvs = AdapterUtil.convert(selection, ProcessVariable.class);
        
        if (Preferences.getRunIndividualScripts())
        {   // Execute script for each received PV
	        for (ProcessVariable pv : pvs)
	        {
	        	try
	        	{
	        		ScriptExecutor.runWithPVs(script, pv);
	        	}
	        	catch (Throwable ex)
	        	{
	        		MessageDialog.openError(null,
	        				Messages.Error,
	        				NLS.bind(Messages.ScriptExecutionErrorFmt, ex.getMessage()));
	        		break;
	        	}
	        }
        }
        else
        {	// Execute script once for all PVs
        	try
        	{
        		ScriptExecutor.runWithPVs(script, pvs);
        	}
        	catch (Throwable ex)
        	{
        		MessageDialog.openError(null,
        				Messages.Error,
        				NLS.bind(Messages.ScriptExecutionErrorFmt, ex.getMessage()));
        	}
        }
        return null;
	}
}
