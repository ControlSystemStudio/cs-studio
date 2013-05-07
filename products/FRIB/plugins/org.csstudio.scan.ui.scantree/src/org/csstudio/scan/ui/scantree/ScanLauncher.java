/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import java.io.File;
import java.io.FileInputStream;

import org.csstudio.scan.client.ScanInfoModel;
import org.csstudio.scan.server.ScanServer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorLauncher;

/** Launch a saved scan, i.e. submit to scan server
 *
 *  <p>Registered as 'external' editor in plugin.xml
 *  @author Kay Kasemir
 */
public class ScanLauncher implements IEditorLauncher
{
    /** {@inheritDoc} */
	@Override
	public void open(final IPath path)
	{
        final Job job = new Job(Messages.SubmitScan)
        {
            @Override
            protected IStatus run(final IProgressMonitor monitor)
            {
            	monitor.beginTask(Messages.SubmitScan, 6);
                try
                {
                	final File file = path.toFile();
            		final String scan_name = ScanEditor.getScanNameFromFile(file.toString());

            		// Get file content as is
            		final byte[] buf = new byte[(int)file.length()];
            		new FileInputStream(file).read(buf);
            		final String scan = new String(buf);

            		// Parse file, then re-format
					// final XMLCommandReader reader = new XMLCommandReader(new ScanCommandFactory());
					// final List<ScanCommand> commands = reader.readXMLStream(new FileInputStream(file));
					// final String scan = XMLCommandWriter.toXMLString(commands);
                    monitor.worked(1);

                    final ScanInfoModel model = ScanInfoModel.getInstance();
                    // If this is the very first attempt to access the scan info model,
                    // we may not be connected, so try a few times
                    int attempts = 5;
                    while (attempts > 0)
                    {
                    	final ScanServer server;
                    	try
                    	{
                    		server = model.getServer();
                    	}
                		catch (Exception ex)
                		{
                			if (--attempts <= 0)
                				throw ex;
                			// Not connected to server: Try again later
                			Thread.sleep(1000);
                			monitor.subTask(Messages.WaitingForScanServer);
		                    monitor.worked(1);
                			continue;
                		}
						server.submitScan(scan_name, scan);
						break;
                    }
                }
                catch (Exception ex)
                {
                    return new Status(IStatus.ERROR,
                            Activator.PLUGIN_ID,
                            NLS.bind(Messages.ScanSubmitErrorFmt, ex.getMessage()),
                            ex);
                }
                monitor.done();
                return Status.OK_STATUS;
            }
        };
        job.setUser(true);
        job.schedule();
	}
}
