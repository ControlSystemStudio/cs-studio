/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import javax.print.attribute.standard.Severity;

import org.csstudio.apputil.ui.elog.ElogDialog;
import org.csstudio.apputil.ui.elog.SendToElogActionHelper;
import org.csstudio.logbook.ILogbook;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/** Action to send image of plot to logbook.
 *  @author Kay Kasemir, Xihui Chen
 */
public class SendToElogAction extends SendToElogActionHelper
{
    final private IOPIRuntime opiRuntime;
    public static final String ID = "org.csstudio.opibuilder.actions.sendToElog";


    /** Initialize
     *  @param part Parent shell
     *  @param graph Graph to print
     */
    public SendToElogAction(final IOPIRuntime part)
    {
        this.opiRuntime = part;
        setId(ID);
    }

    @Override
    public void run()
    {
        // Get name for snapshot file
        final String filename;
        try
        {
            filename = ResourceUtil.getScreenshotFile(
            		(GraphicalViewer) opiRuntime.getAdapter(GraphicalViewer.class));
        }
        catch (Exception ex)
        {
            MessageDialog.openError(opiRuntime.getSite().getShell(), "error", ex.getMessage());
            return;
        }

        // Display dialog, create entry
        try
        {
            final ElogDialog dialog =
                new ElogDialog(opiRuntime.getSite().getShell(), "Send To Logbook",
                        opiRuntime.getDisplayModel().getName(),
                        "See attached opi screenshot",
                        filename)
            {
                @Override
                public void makeElogEntry(final String logbook_name, final String user,
                        final String password, final String title, final String body,
                        final String images[])
                        throws Exception
                {
                    final ILogbook logbook = getLogbook_factory()
                                        .connect(logbook_name, user, password);
                    try {
						Job create = new Job("Creating log entry.") {

							@Override
							protected IStatus run(IProgressMonitor monitor) {
								try {
									logbook.createEntry(title, body, images);
								} catch (final Exception e) {
									return new Status(
											Severity.ERROR.getValue(),
											ID,
											"Failed to create log entry", e);
								}
								return Status.OK_STATUS;
							}
						};
						create.addJobChangeListener(new JobChangeAdapter() {
							public void done(final IJobChangeEvent event) {
								if (!event.getResult().isOK()) {
									Display.getDefault().asyncExec(
											new Runnable() {
												public void run() {
													MessageDialog.openError(
															opiRuntime.getSite().getShell(),
															"Error",
															event.getResult().getException().getMessage());
									

												}
											});
								}
							}
						});
						create.setUser(true);
						create.schedule();
					} 
                    finally
                    {
                        logbook.close();
                    }
                }

            };
            dialog.open();
        }
        catch (Exception ex)
        {
            MessageDialog.openError(opiRuntime.getSite().getShell(), "Error", ex.getMessage());
        }
    }
}
