package org.csstudio.trends.databrowser.archiveview;

import org.csstudio.archive.ArchiveInfo;
import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.cache.ArchiveCache;
import org.csstudio.trends.databrowser.Plugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Eclipse background job for connecting to a data server
 *  and obtaining the list of archives.
 *  @author Kay Kasemir
 */
class ConnectJob extends Job
{
    private ArchiveView archives_view;
    private Shell shell;
    private String url;
    private ArchiveServer server;
    private ArchiveInfo infos[];
    
    /** Create job that connects to given URL, then notifies view when done. */
    public ConnectJob(ArchiveView archives_view, String url)
    {
        super(Messages.ConnectJobName);
        
        this.archives_view = archives_view;
        this.shell = archives_view.getSite().getShell();
        this.url = url;
        this.server = null;
    }

    /* @see org.eclipse.core.runtime.jobs.Job#run() */
    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        monitor.beginTask(Messages.ConnectJobServer + url, IProgressMonitor.UNKNOWN);
        try
        {
            monitor.subTask(Messages.ConnectJobConnecting);
            server = ArchiveCache.getInstance().getServer(url);
            monitor.subTask(Messages.ConnectJobGettingArchives);
            infos = server.getArchiveInfos();
            // Ignore the result when request was cancelled.
            if (monitor.isCanceled())
                return Status.CANCEL_STATUS;
            // Notify view, but in GUI thread
            Display.getDefault().asyncExec(new Runnable()
            {
                public void run()
                {
                    archives_view.setArchiveServer(server, infos);
                }
            });
        }
        catch (final Exception ex)
        {
            // Show this in a message dialog.
            // Usually, this happens because somebody entered
            // a bad server URL, so the user should learn about the error...
            // Also log it.
            Plugin.getLogger().error(url + ": " + ex.getMessage()); //$NON-NLS-1$
            monitor.setCanceled(true);
            
            shell.getDisplay().asyncExec(new Runnable()
            {
                public void run()
                {
                    MessageDialog.openError(shell, Messages.ConnectErrorTitle,
                        NLS.bind(Messages.ConnectErrorMessage, url, ex.getMessage()));
                }
            });
            
            return Status.CANCEL_STATUS;
        }
        monitor.done();
        return Status.OK_STATUS;
    }
}