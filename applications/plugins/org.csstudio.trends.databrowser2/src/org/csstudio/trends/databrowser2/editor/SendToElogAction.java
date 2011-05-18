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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Action to send image of plot to logbook.
 *  @author Kay Kasemir
 */
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
            MessageDialog.openError(shell, Messages.Error, ex.getMessage());
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
                    final ILogbook logbook = getLogbook_factory()
                                        .connect(logbook_name, user, password);
                    try
                    {
                        logbook.createEntry(title, body, images);
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
            MessageDialog.openError(shell, Messages.Error,
                    NLS.bind(Messages.ErrorFmt, ex.getMessage()));
        }
    }
}
