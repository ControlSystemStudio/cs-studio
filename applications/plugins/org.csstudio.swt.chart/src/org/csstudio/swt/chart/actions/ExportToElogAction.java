package org.csstudio.swt.chart.actions;

import java.io.File;

import org.csstudio.logbook.ILogbook;
import org.csstudio.logbook.ILogbookFactory;
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
    private ILogbookFactory logbook_factory;
    
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
        final Shell shell = chart.getShell();

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
            MessageDialog.openError(shell, "Error", //$NON-NLS-1$
                    NLS.bind("Cannot create tmp. file:\n{0}", ex.getMessage())); //$NON-NLS-1$
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
            MessageDialog.openError(shell, "Error", //$NON-NLS-1$
                    NLS.bind("Cannot create snapshot in {0}:\n{1}", //$NON-NLS-1$
                            filename, ex.getMessage()));
            return;
        }
        
        String[] logbooks;
        try
        {
            logbook_factory = LogbookFactory.getInstance();
            logbooks = logbook_factory.getLoogbooks();
        }
        catch (Exception ex)
        {
            MessageDialog.openError(chart.getShell(),
                    "Error", "Cannot obtain logbook support:\n" + ex.getMessage());  //$NON-NLS-1$//$NON-NLS-2$
            return;
        }
        
        // Display dialog
        final ExportToElogDialog dialog =
            new ExportToElogDialog(shell, application, logbooks,
                    logbook_factory.getDefaultLogbook())
        {
            @Override
            void makeElogEntry(final String logbook_name, final String user,
                    final String password, final String title, final String body)
                    throws Exception
            {
                final ILogbook logbook = logbook_factory.connect(logbook_name,
                                user, password);
                        logbook.createEntry(title, body, filename);
                        logbook.close();
            }
            
        };
        dialog.open();
        
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

}
