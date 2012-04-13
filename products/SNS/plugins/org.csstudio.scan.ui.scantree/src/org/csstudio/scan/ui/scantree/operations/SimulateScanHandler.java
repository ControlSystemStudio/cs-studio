/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.operations;

import java.util.List;

import org.csstudio.scan.client.ScanInfoModel;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.XMLCommandWriter;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.SimulationResult;
import org.csstudio.scan.ui.SimulationDisplay;
import org.csstudio.scan.ui.scantree.Activator;
import org.csstudio.scan.ui.scantree.Messages;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.csstudio.scan.ui.scantree.gui.ScanEditorContributor;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/** Command handler that simulates scan from the current editor on the server
 *  @author Kay Kasemir
 */
public class SimulateScanHandler extends AbstractHandler
{
	private SimulationResult simulate(final List<ScanCommand> commands) throws Exception
	{
    	final ScanInfoModel scan_info = ScanInfoModel.getInstance();
		try
		{
			final ScanServer server = scan_info.getServer();
			return server.simulateScan(XMLCommandWriter.toXMLString(commands));
		}
		finally
		{
			scan_info.release();
		}
	}

    /** {@inheritDoc} */
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
    	final Shell shell = HandlerUtil.getActiveShell(event);
		final Display display = shell.getDisplay();

    	final ScanEditor editor = ScanEditorContributor.getCurrentScanEditor();
    	final List<ScanCommand> commands = editor.getModel().getCommands();

        // Use background Job to submit scan to server for simulation
        final Job job = new Job(Messages.SimulateScan)
        {

			@Override
            protected IStatus run(IProgressMonitor monitor)
            {
				final SimulationResult simulation;
                try
                {
                	simulation = simulate(commands);
                }
                catch (Exception ex)
                {
                    return new Status(IStatus.ERROR,
                            Activator.PLUGIN_ID,
                            NLS.bind(Messages.ScanSubmitErrorFmt, ex.getMessage()),
                            ex);
                }

                // Open in text editor, on UI thread
                display.asyncExec(new Runnable()
                {
					@Override
                    public void run()
                    {
						try
						{
							SimulationDisplay.show(simulation);
						}
						catch (Exception ex)
						{
							ExceptionDetailsErrorDialog.openError(shell, Messages.SimulateScan, ex);
						}
                    }
                });

                return Status.OK_STATUS;
            }
        };
        job.setUser(true);
        job.schedule();

    	return null;
    }
}
