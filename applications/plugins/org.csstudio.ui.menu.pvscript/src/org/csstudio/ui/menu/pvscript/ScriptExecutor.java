/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.pvscript;

import org.csstudio.csdata.ProcessVariable;

/** Tool for executing a command with PV argument
 *  @author Kay Kasemir
 */
public class ScriptExecutor
{
	/** Execute a script with PV names as arguments
	 *  @param script Name of script
	 *  @param pv One or more PV names
	 *  @throws Exception on error
	 */
	public static void runWithPVs(final String script, final ProcessVariable... pv) throws Exception
	{
		// Build command: Script with PVs as arguments
		final String[] command = new String[pv.length + 1];
		command[0] = script;
		for (int i=0; i<pv.length; ++i)
			command[i+1] = pv[i].getName();
		
		// Simply running the command is easy:
		Runtime.getRuntime().exec(command);
		
		// It would be much more complicated to monitor the
		// error output, check the return code,
		// maybe react if the command does not _stop_
		// after some time.
		// In here, we ignore all that.
	}
}
