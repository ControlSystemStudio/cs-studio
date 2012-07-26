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
package org.csstudio.scan.ui.scantree;

import java.util.List;

import org.csstudio.scan.client.ScanServerConnector;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.ScanCommandFactory;
import org.csstudio.scan.command.XMLCommandReader;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.ScanState;
import org.csstudio.scan.ui.ScanHandlerUtil;
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

/** Command handler that opens a scan in the tree editor
 *  @author Kay Kasemir
 */
public class OpenScanTreeHandler extends AbstractHandler
{
    /** {@inheritDoc} */
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
		final ScanInfo info = ScanHandlerUtil.getScanInfo(event);
		if (info == null)
			return null;

		final Shell shell = HandlerUtil.getActiveShellChecked(event);
		if (info.getState() == ScanState.Logged)
		{
		    MessageDialog.openInformation(shell, Messages.OpenScanTreeError,
		          NLS.bind(Messages.NoScanCommandsFmt, info.getName()));
		    return null;
		}

		final Display display = shell.getDisplay();

		// Use Job to read commands from server
        final Job job = new Job("Download Scan") //$NON-NLS-1$
        {
            @Override
            protected IStatus run(final IProgressMonitor monitor)
            {
                try
                {
                    // Fetch commands from server
                    final ScanServer server = ScanServerConnector.connect();
                    final String xml_commands = server.getScanCommands(info.getId());
                    final XMLCommandReader reader = new XMLCommandReader(new ScanCommandFactory());
                    final List<ScanCommand> commands = reader.readXMLString(xml_commands);
                    ScanServerConnector.disconnect(server);

                    // Open in editor, which requires UI thread
                    display.asyncExec(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ScanEditor.createInstance(info, commands);
                        }
                    });
                }
                catch (Exception ex)
                {
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                            Messages.OpenScanTreeError, ex);
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();

        return null;
    }
}
