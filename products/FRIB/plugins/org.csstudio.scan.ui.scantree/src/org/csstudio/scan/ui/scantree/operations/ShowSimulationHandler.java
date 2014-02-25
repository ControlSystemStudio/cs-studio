/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.operations;

import org.csstudio.scan.ui.scantree.Messages;
import org.csstudio.scan.ui.scantree.gui.StringEditorInput;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/** Handler for parameterized command that shows simulation result
 *
 *  <p>Refer to plugin.xml for command definition
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ShowSimulationHandler extends AbstractHandler
{
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
    	// We should probably use HandlerUtil.getActiveWorkbenchWindowChecked(event)
    	// to move away from PlatformUI, HandlerUtil will fail if a dialog or other
    	// window was in focus, while PlatformUI always returns the window in which
    	// we're running
    	final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    	final String log = event.getParameter("org.csstudio.scan.ui.scantree.show_simulation.log");
		final IEditorInput input = new StringEditorInput(Messages.ScanSimulation, log);
		try
        {
			final IWorkbenchPage page = window.getActivePage();
            page.openEditor(input, "org.eclipse.ui.DefaultTextEditor");
        }
        catch (PartInitException ex)
        {
        	ExceptionDetailsErrorDialog.openError(window.getShell(), Messages.ScanSimulation, ex);
        }
	    return null;
    }
}
