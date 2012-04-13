/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import java.util.List;

import org.csstudio.scan.client.ScanInfoModel;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.XMLCommandWriter;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.SimulationResult;
import org.csstudio.scan.ui.scantree.gui.StringEditorInput;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/** Submit scan for simulation, display result
 *  @author Kay Kasemir
 */
public class SimulationTool
{
	public static SimulationResult simulateScan(final List<ScanCommand> commands) throws Exception
	{
		return simulateScan(XMLCommandWriter.toXMLString(commands));
	}

	public static SimulationResult simulateScan(final String commands_as_xml) throws Exception
	{
    	final ScanInfoModel scan_info = ScanInfoModel.getInstance();
    	try
    	{
	        final ScanServer server = scan_info.getServer();
	        return server.simulateScan(commands_as_xml);
    	}
    	finally
    	{
    		scan_info.release();
    	}
	}

	public static void displaySimulation(final SimulationResult simulation)
	{
		final IEditorInput input =
				new StringEditorInput("Simulation", simulation.getSimulationLog());
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final IWorkbenchPage page = window.getActivePage();
		try
        {
            page.openEditor(input, "org.eclipse.ui.DefaultTextEditor");
        }
        catch (PartInitException ex)
        {
        	ExceptionDetailsErrorDialog.openError(window.getShell(), "Simulation", ex);
        }
	}
}
