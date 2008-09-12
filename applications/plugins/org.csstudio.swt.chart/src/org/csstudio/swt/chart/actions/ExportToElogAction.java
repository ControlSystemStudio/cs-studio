package org.csstudio.swt.chart.actions;

import java.io.File;

import org.csstudio.logbook.ILogbook;
import org.csstudio.logbook.ILogbookFactory;
import org.csstudio.logbook.LogbookFactory;
import org.csstudio.swt.chart.Activator;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.Messages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
    private static ExportToElogInfo info;
    private ILogbookFactory logbook_factory;
    private String[] logbooks;
    
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
        // Perform elog entry in background (as far as that's possible)
        final Job job = new Job(Messages.ELog_ActionName)
        {
            @Override
            protected IStatus run(final IProgressMonitor monitor)
            {
                perform_background_task(monitor);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    /** Make elog entry, run in background job */
    private void perform_background_task(final IProgressMonitor monitor)
    {
        monitor.beginTask(Messages.ELog_ActionName, 3);
        
        try
        {
            logbook_factory = LogbookFactory.getInstance();
            logbooks = logbook_factory.getLoogbooks();
        }
        catch (Exception ex)
        {
            MessageDialog.openError(chart.getShell(),
                    "Error", "Cannot obtain logbook support:\n" + ex.getMessage());  //$NON-NLS-1$//$NON-NLS-2$
            monitor.done();
            return;
        }
        
        // Get info
        if (!promptForInfo())
            return;
        monitor.worked(1);
        // Get name for snapshot file
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
        // Create snapshot file in UI thread
        chart.getDisplay().syncExec(new Runnable()
        {
            public void run()
            {
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
            }
        });        
        monitor.worked(1);
        // Write snapshot to elog
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
        monitor.done();
    }

    /** Try to add image file to elog.
     *  On errors, prompt again for user/password so one can try again.
     *  @param image_filename
     */
    private void makeElogEntry(final String image_filename)
    {
        ILogbook logbook;
        while (true)
        {
            try
            {
                logbook = logbook_factory.connect(info.getLogbook(),
                        info.getUser(), info.getPassword());
                // If we get here, connection was successful
                break;
            }
            catch (final Exception ex)
            {   // Display error in UI thread
                chart.getDisplay().syncExec(new Runnable()
                {
                    public void run()
                    {
                        MessageDialog.openError(chart.getShell(),
                                Messages.ELog_ConnectError,
                                NLS.bind(Messages.ELog_ConnectErrorMessage,
                                         ex.getMessage()));
                    }
                });
            }
            if (!promptForInfo())
               return; // canceled
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

    /** Prompt for entry info: user, password, title, ...
     *  Called from non-UI thread, blocks until entry made
     *  @return <code>true</code> if OK, <code>false</code> if canceled
     */
    @SuppressWarnings("nls")
    private boolean promptForInfo()
    {
        // Keep previously entered user/password/... while instance is running
        if (info == null)
            info = new ExportToElogInfo("", "", logbook_factory.getDefaultLogbook(),
                    NLS.bind(Messages.ELog_TitleFormat, application),
                    NLS.bind(Messages.ELog_BodyFormat, application));
        chart.getDisplay().syncExec(new Runnable()
        {
            public void run()
            {
                final Shell shell = chart.getShell();
                final ExportToElogDialog dialog =
                    new ExportToElogDialog(shell, logbooks, info);
                if (dialog.open() == ExportToElogDialog.OK)
                    info = dialog.getInfo();
                else
                    info = null;
            }
        });
        return info != null;
    }
}
