/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.editor;

import org.csstudio.apputil.ui.elog.ElogDialog;
import org.csstudio.apputil.ui.elog.SendToElogActionHelper;
import org.csstudio.logbook.ILogbook;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

/** Action to send image of plot to logbook.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SendToElogAction extends SendToElogActionHelper
{
    final private Shell shell;
    final private XYGraph graph;

    /** Initialize
     *  @param shell Parent shell
     *  @param graph Graph to print
     */
    public SendToElogAction(final Shell shell, final XYGraph graph)
    {
        this.shell = shell;
        this.graph = graph;
    }

    @Override
    public void run()
    {
        // Get name for snapshot file
        final String filename;
        try
        {
            filename = new Screenshot(graph).getFilename();
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(shell, Messages.Error, ex);
            return;
        }

        // Display dialog, create entry
        try
        {
            final ElogDialog dialog =
                new ElogDialog(shell, Messages.SendToElog,
                        Messages.LogentryDefaultTitle,
                        Messages.LogentryDefaultBody,
                        filename)
            {
                @Override
                public void makeElogEntry(final String logbook_name, final String user,
                        final String password, final String title, final String body,
                        final String images[])
                        throws Exception
                {
                    final Job create = new Job("Create log entry")
                    {
						@Override
						protected IStatus run(final IProgressMonitor monitor)
						{
							try
							{
							    final ILogbook logbook = getLogbook_factory()
							            .connect(logbook_name, user, password);
								logbook.createEntry(title, body, images);
								logbook.close();
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
					create.setUser(true);
					create.schedule();
                }
            };
            dialog.open();
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(shell, Messages.Error, ex);
        }
    }
}
