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

import java.rmi.RemoteException;

import org.csstudio.scan.client.ScanInfoModel;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.ui.scanmonitor.Activator;
import org.csstudio.scan.ui.scanmonitor.Messages;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/** Action that removes all completed scans
 *  @author Kay Kasemir
 */
public class RemoveCompletedAction extends AbstractGUIAction
{
    /** Initialize
     *  @param shell Parent shell
     *  @param model
     */
    public RemoveCompletedAction(final Shell shell, final ScanInfoModel model)
    {
        super(shell, model, null, Messages.RemoveCompleted, Activator.getImageDescriptior("icons/remove_completed.gif")); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    @Override
    protected void runModelAction() throws Exception
    {
        if (! MessageDialog.openConfirm(shell,  Messages.RemoveScan, Messages.RemoveCompletedScans))
            return;

        // Job because removal of many scans and data in log can be slow
        final ScanServer server = model.getServer();
        final Job job = new Job( Messages.RemoveScan)
        {
            @Override
            protected IStatus run(final IProgressMonitor monitor)
            {
                try
                {
                    server.removeCompletedScans();
                }
                catch (final RemoteException ex)
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
        job.schedule();
    }
}
