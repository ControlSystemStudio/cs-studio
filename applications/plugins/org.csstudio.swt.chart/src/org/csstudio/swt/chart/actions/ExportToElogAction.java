package org.csstudio.swt.chart.actions;

import java.io.File;

import org.csstudio.logbook.ILogbook;
import org.csstudio.logbook.LogbookFactory;
import org.csstudio.swt.chart.Activator;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Action to send image of plot to logbook.
 *  @author Kay Kasemir
 */
public class ExportToElogAction extends Action
{
    final private Chart chart;
    final private String application;
    static ExportToElogInfo info;
    
    /** Construct action that exports chart to elog.
     *  @param chart Chart to act on
     *  @param application Application name used to initialize elog text.
     */
    public ExportToElogAction(final Chart chart, final String application)
    {
        super(Messages.ELog_ActionName,
              Activator.getImageDescriptor("icons/snapshot.gif")); //$NON-NLS-1$
        this.chart = chart;
        this.application = application;
        setToolTipText(Messages.ELog_ActionName_TT);
    }

    @Override
    public void run()
    {
        if (!promptForInfo())
            return;
        // Create snapshot file
        final File tmp_file;
        try
        {
            tmp_file = 
                File.createTempFile("ELogImage", ImageFileName.ending); //$NON-NLS-1$
        }
        catch (Exception ex)
        {
            Activator.getLogger().error("Cannot obtain tmp file", ex); //$NON-NLS-1$
            return;
        }
        final String filename = tmp_file.getAbsolutePath();
        try
        {
            chart.createSnapshotFile(filename);
        }
        catch (Exception ex)
        {
            Activator.getLogger().error(
                    "Cannot create snapshot " + filename, ex); //$NON-NLS-1$
            return;
        }
        // Add to elog
        makeElogEntry(filename);
        // Remove snapshot file
        try
        {
            tmp_file.delete();
        }
        catch (Exception ex)
        {
            // NOP
        }
    }

    /** Try to add image file to elog.
     *  On errors, prompt again for user/password so one can try again.
     *  @param image_filename
     */
    private void makeElogEntry(final String image_filename)
    {
        ILogbook logbook = null;
        while (logbook == null)
        {
            try
            {
                logbook =
                    LogbookFactory.connect(info.getUser(), info.getPassword());
                // If we get here, we connected
                break;
            }
            catch (Exception ex)
            {
                MessageDialog.openError(chart.getShell(),
                    Messages.ELog_ConnectError,
                    NLS.bind(Messages.ELog_ConnectErrorMessage, ex.getMessage()));
                logbook = null;
            }
            // Ask for new user/pw info
            if (!promptForInfo())
               return; // cancelled
        }
        
        try
        {
            logbook.createEntry(info.getTitle(), info.getBody(), image_filename);
        }
        catch (Exception ex)
        {
            Activator.getLogger().error(ex);
        }
        finally
        {
            logbook.close();
        }
    }

    @SuppressWarnings("nls")
    private boolean promptForInfo()
    {
        // Keep previously entered user/password/... while instance is running
        if (info == null)
            info = new ExportToElogInfo("", "",
                    NLS.bind(Messages.ELog_TitleFormat, application),
                    NLS.bind(Messages.ELog_BodyFormat, application));
        final Shell shell = chart.getShell();
        final ExportToElogDialog dialog = new ExportToElogDialog(shell, info);
        if (dialog.open() == ExportToElogDialog.OK)
        {
            info = dialog.getInfo();
            return true;
        }
        return false;
    }
}
