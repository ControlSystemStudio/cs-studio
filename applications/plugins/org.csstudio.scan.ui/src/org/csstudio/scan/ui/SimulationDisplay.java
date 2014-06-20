/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui;

import org.csstudio.scan.server.SimulationResult;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

/** Submit scan for simulation, display result
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SimulationDisplay
{
	/** Display result of a scan simulation
	 *  @param simulation {@link SimulationResult}
	 *  @throws Exception on error
	 */
    public static void show(final SimulationResult simulation) throws Exception
	{
    	// Could open a text editor with the simulation result right in here,
    	// but the necessary StringEditorInput and dependencies on IStorageEditorInput (org.eclipse.ide)
    	// made this very difficult from a BOY script.
    	//
    	// By using a command with parameters this code is
    	// decoupled from the actual implementation
    	// in org.csstudio.scan.ui.scantree.
    	//
    	// Better in the long run, fewer classpath issues from scripts
        final ICommandService cmd_info =
        		(ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
        final IHandlerService handler =
        		(IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);

    	final Command command = cmd_info.getCommand("org.csstudio.scan.ui.scantree.show_simulation");

    	final ParameterizedCommand parm_command = new ParameterizedCommand(command,
			new Parameterization[]
			{
    			new Parameterization(command.getParameter("org.csstudio.scan.ui.scantree.show_simulation.log"),
    					simulation.getSimulationLog())
			});

        handler.executeCommand(parm_command, null);
	}
}
