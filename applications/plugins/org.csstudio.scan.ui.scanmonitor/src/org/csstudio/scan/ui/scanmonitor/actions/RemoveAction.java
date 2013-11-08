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
package org.csstudio.scan.ui.scanmonitor.actions;

import org.csstudio.scan.client.ScanClient;
import org.csstudio.scan.client.ScanInfoModel;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.ui.scanmonitor.Activator;
import org.csstudio.scan.ui.scanmonitor.Messages;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/** Action that removes a scan
 *  @author Kay Kasemir
 */
public class RemoveAction extends AbstractGUIAction
{
    /** Initialize
     *  @param shell Parent shell
     *  @param model
     *  @param infos
     */
    public RemoveAction(final Shell shell, final ScanInfoModel model, final ScanInfo[] infos)
    {
        super(shell, model, infos, Messages.Remove, Activator.getImageDescriptior("icons/remove.gif")); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    @Override
    protected void runModelAction() throws Exception
    {
        if (! MessageDialog.openConfirm(shell, Messages.RemoveScan, Messages.RemoveSelectedScan))
            return;

        // Job because removal of many scans and data in log can be slow
        final ScanClient client = model.getScanClient();
        final Job job = new Job( Messages.RemoveScan)
        {
            @Override
            protected IStatus run(final IProgressMonitor monitor)
            {
                try
                {
                    for (ScanInfo info : infos)
                        client.removeScan(info.getId());
                }
                catch (final Exception ex)
                {
                    shell.getDisplay().asyncExec(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ExceptionDetailsErrorDialog.openError(shell, Messages.Error, ex);
                        }
                    });
                }
                return Status.OK_STATUS;
            }
        };
        job.setUser(true);
        job.schedule();
    }
}
