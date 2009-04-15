package org.csstudio.trends.databrowser.plotpart;

import java.io.File;

import org.csstudio.apputil.ui.elog.ElogDialog;
import org.csstudio.apputil.ui.elog.SendToElogActionHelper;
import org.csstudio.logbook.ILogbook;
import org.csstudio.swt.chart.Activator;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.actions.ImageFileName;
import org.csstudio.trends.databrowser.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Action to send image of plot to logbook.
 *  @author Kay Kasemir
 */
public class SendToElogAction extends SendToElogActionHelper
{
    final private Chart chart;

    /** Constructor */
	public SendToElogAction(final Chart chart)
	{
        this.chart = chart;
	}

    @Override
    public void run()
    {
        final Shell shell = chart.getShell();

        // Get name for snapshot file
        final File tmp_file;
        try
        {
            tmp_file = 
                File.createTempFile(org.csstudio.trends.databrowser.Messages.DataBrowser,
                        ImageFileName.ending);
            tmp_file.deleteOnExit();
        }
        catch (Exception ex)
        {
            final String error = "Cannot create tmp. file:\n" + ex.getMessage(); //$NON-NLS-1$
            Activator.getLogger().error(error);
            MessageDialog.openError(shell, Messages.ErrorMessageTitle, error);
            return;
        }
        final String filename = tmp_file.getAbsolutePath();
        
        // Create snapshot file
        try
        {
            chart.createSnapshotFile(filename);
        }
        catch (Exception ex)
        {
            Activator.getLogger().error(
                    "Cannot create snapshot " + filename, ex); //$NON-NLS-1$
            MessageDialog.openError(shell, Messages.ErrorMessageTitle,
                    NLS.bind("Cannot create snapshot in {0}:\n{1}", //$NON-NLS-1$
                            filename, ex.getMessage()));
            return;
        }
        
        // Display dialog, create entry
        try
        {
            final ElogDialog dialog =
                new ElogDialog(shell, Messages.SendToElogAction_message,
                        Messages.SendToElogAction_InitialTitle,
                        Messages.SendToElogAction_InitialBody,
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
            MessageDialog.openError(shell, Messages.ErrorMessageTitle,
                    NLS.bind("Cannot create snapshot in {0}:\n{1}", //$NON-NLS-1$
                            filename, ex.getMessage()));
        }
    }
}
