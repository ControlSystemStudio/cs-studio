package org.csstudio.trends.databrowser.archiveview;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.NameInfo;
import org.csstudio.trends.databrowser.Plugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

/** Eclipse background job for searching names on a data server.
 *  @author Kay Kasemir
 */
class SearchJob extends Job
{
    final private ArchiveView archives_view;
    final private ArchiveServer server;
    final private int keys[];
    final private String pattern;
    
    /** Helper for sending new name infos to the GUI. */
    private class ViewUpdateRunnable implements Runnable
    {
        private NameInfo infos[];
        private int key;
        
        public ViewUpdateRunnable(NameInfo infos[], int key)
        {
            this.infos = infos;
            this.key = key;
        }
        
        public void run()
        {
            archives_view.addNameInfos(infos, key);
        }
    }
    
    /** Create job that searches given server's keys for pattern,
     *  then notifies view about received names.
     */
    public SearchJob(final ArchiveView archives_view,
                    final ArchiveServer server,
                    final int keys[],
                    final String pattern)
    {
        super(Messages.SearchJobName);
        this.archives_view = archives_view;
        this.server = server;
        this.keys = keys;
        this.pattern = pattern.trim();
    }

    /* @see org.eclipse.core.runtime.jobs.Job#run() */
    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
        monitor.beginTask(Messages.SeachJobTaskName, keys.length);
        try
        {
            for (int i=0; i<keys.length; ++i)
            {
                // Display "N/total", using '1' for the first sub-archive.
                final String arch_name = server.getArchiveName(keys[i]);
                monitor.subTask(NLS.bind(Messages.SeachJobSubTask,
                                new Object[] { arch_name, (i+1), keys.length}));
                // Invoke the possibly lengthy search.
                final NameInfo infos[] = server.getNames(keys[i], pattern);
                // Stop and ignore further results when canceled.
                if (monitor.isCanceled())
                    return Status.CANCEL_STATUS;
                // Notify view, but in GUI thread
                Display.getDefault().asyncExec(
                                new ViewUpdateRunnable(infos, keys[i]));
                // Handled search for one key.
                monitor.worked(1);
            }
        }
        catch (final Exception ex)
        {
            Plugin.getLogger().error(Messages.SeachJobTaskName, ex);
            monitor.setCanceled(true);
            
            Display.getDefault().asyncExec(new Runnable()
            {
                public void run()
                {
                    final String error =
                        NLS.bind(Messages.ErrorFmt, ex.getMessage());
                    MessageDialog.openError(archives_view.getSite().getShell(),
                        Messages.SeachJobTaskName, error);
                }
            });
            
            return Status.CANCEL_STATUS;
        }
        monitor.done();
        return Status.OK_STATUS;
    }
}