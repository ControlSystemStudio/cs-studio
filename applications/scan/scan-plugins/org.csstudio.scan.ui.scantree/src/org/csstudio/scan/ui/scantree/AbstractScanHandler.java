/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import org.csstudio.scan.client.ScanClient;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanState;
import org.csstudio.scan.ui.ScanHandlerUtil;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/** Base for Command handler that fetches a scan from server and then does something with it
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class AbstractScanHandler extends AbstractHandler
{
    /** Shell which the command was invoked */
    protected volatile Shell shell;

    /** To be implemented: Perform something on a downloaded scan.
     *
     *  <p>Will be executed off the UI thread.
     *
     *  @param client {@link ScanClient}
     *  @param info {@link ScanInfo}
     *  @param xml_commands XML of scan
     *  @throws Exception on error
     */
    abstract protected void handleScan(final ScanClient client, final ScanInfo info, final String xml_commands) throws Exception;

    /** {@inheritDoc} */
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        final ScanInfo info = ScanHandlerUtil.getScanInfo(event);
        if (info == null)
            return null;

        shell = HandlerUtil.getActiveShellChecked(event);
        if (info.getState() == ScanState.Logged)
        {
            MessageDialog.openInformation(shell, Messages.OpenScanTreeError,
                  NLS.bind(Messages.NoScanCommandsFmt, info.getName()));
            return null;
        }

        // Use Job to read commands from server
        final Job job = new Job("Download Scan")
        {
            @Override
            protected IStatus run(final IProgressMonitor monitor)
            {
                try
                {
                    // Fetch commands from server
                    final ScanClient client = new ScanClient();
                    final String xml_commands = client.getScanCommands(info.getId());
                    // Do something
                    handleScan(client, info, xml_commands);
                }
                catch (Exception ex)
                {
                    final Display display = shell.getDisplay();
                    display.asyncExec(() -> ExceptionDetailsErrorDialog.openError(shell, Messages.Error, ex));
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();

        return null;
    }
}
