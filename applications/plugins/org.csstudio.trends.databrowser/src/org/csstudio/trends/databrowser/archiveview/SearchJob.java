/**
 * 
 */
package org.csstudio.trends.databrowser.archiveview;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.NameInfo;
import org.csstudio.trends.databrowser.Plugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

/** Eclipse background job for searching names on a data server.
 *  @author Kay Kasemir
 */
class SearchJob extends Job
{
    private ArchiveView archives_view;
    private ArchiveServer server;
    private int keys[];
    private String pattern;
    
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
    public SearchJob(ArchiveView archives_view,
                    ArchiveServer server, int keys[], String pattern)
    {
        super(Messages.SearchJobName);
        this.archives_view = archives_view;
        this.server = server;
        this.keys = keys;
        this.pattern = pattern;
    }

    /* @see org.eclipse.core.runtime.jobs.Job#run() */
    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        monitor.beginTask(Messages.SeachJobTaskName, keys.length);
        try
        {
            for (int i=0; i<keys.length; ++i)
            {
                // Display "N/total", using '1' for the first sub-archive.
                monitor.subTask(Messages.SeachJobSubTask
                                + (i+1) + "/" + keys.length); //$NON-NLS-1$
                // Invoke the possibly lengthy search.
                NameInfo infos[] = server.getNames(keys[i], pattern);
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
        catch (Exception e)
        {
            Plugin.logException("Archive search error", e); //$NON-NLS-1$
            monitor.setCanceled(true);
            return Status.CANCEL_STATUS;
        }
        monitor.done();
        return Status.OK_STATUS;
    }
}