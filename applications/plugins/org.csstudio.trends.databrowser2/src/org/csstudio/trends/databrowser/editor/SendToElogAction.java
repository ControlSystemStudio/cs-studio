package org.csstudio.trends.databrowser.editor;

import org.csstudio.apputil.ui.elog.ElogDialog;
import org.csstudio.apputil.ui.elog.SendToElogActionHelper;
import org.csstudio.logbook.ILogbook;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.trends.databrowser.Messages;
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
                        final String password, final String title, final String body)
                        throws Exception
                {
                    final ILogbook logbook = getLogbook_factory()
                                        .connect(logbook_name, user, password);
                    try
                    {
                        logbook.createEntry(title, body, filename);
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
