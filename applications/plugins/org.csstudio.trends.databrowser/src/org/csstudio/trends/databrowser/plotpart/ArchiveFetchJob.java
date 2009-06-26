package org.csstudio.trends.databrowser.plotpart;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.cache.ArchiveCache;
import org.csstudio.archive.crawl.BatchIterator;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.IPVModelItem;
import org.csstudio.trends.databrowser.preferences.Preferences;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Eclipse background job for fetching samples from the data server.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class ArchiveFetchJob extends Job implements ISchedulingRule
{
    /** Poll period in millisecs */
    private static final int POLL_PERIOD_MS = 1000;
    
    /** Shell for error messages */
    final private Shell shell;

    /** Item for which to fetch samples */
    final private IPVModelItem item;

    /** Start/End time */
    final private ITimestamp start, end;

    /** Listener that's notified when (if) we completed OK */
    final private ArchiveFetchJobListener listener;
    
    /** Thread that performs the actual background work.
     * 
     *  Instead of directly accessing the archive, ArchiveFetchJob launches
     *  a WorkerThread for the actual archive access, so that the Job
     *  can then poll the progress monitor for cancellation and if 
     *  necessary interrupt the WorkerThread which might be 'stuck'
     *  in a long running operation. 
     */
    class WorkerThread extends Thread
    {
        private String message = "idle";
        private volatile boolean cancelled = false;
        private volatile boolean done = false;
        
        /** Server that's currently queried.
         *  Synchronize 'this' on access.
         */
        private ArchiveServer server = null;
        
        /** Construct */
        public WorkerThread()
        {
            super("ArchiveFetchJobWorker");
        }

        /** @return Message that somehow indicates progress */
        public synchronized String getMessage()
        {
            return message;
        }

        /** Request thread to cancel its operation */
        public synchronized void cancel()
        {
            cancelled = true;
            synchronized (this)
            {
                if (server != null)
                    server.cancel();
            }
        }

        /** @return <code>true</code> when done (success, error, cancelled) */
        public synchronized boolean isDone()
        {
            return done;
        }
        
        /** {@inheritDoc} */
        @Override
        public void run()
        {
            final ArchiveCache cache = ArchiveCache.getInstance();
            final IArchiveDataSource archives[] = item.getArchiveDataSources();
            for (int i=0; i<archives.length && !cancelled; ++i)
            {
                // Display "N/total", using '1' for the first sub-archive.
                synchronized  (this)
                {
                    message = Messages.Fetch_Archive
                        + "'" + archives[i].getName()
                        + "' ("
                        + (i+1) + "/" + archives.length + ")";
                }
                try
                {   // Invoke the possibly lengthy search.
                    synchronized (this)
                    {
                        server = cache.getServer(archives[i].getUrl());
                    }
                    String request_type;
                    Object[] request_parms;
                    final int bins = Preferences.getPlotBins();
                    if (item.getRequestType() == IPVModelItem.RequestType.RAW)
                    {
                        request_type = ArchiveServer.GET_RAW;
                        request_parms = new Object[] { new Integer(bins) };
                    }
                    else
                    {
                        request_type = ArchiveServer.GET_AVERAGE;
                        final double interval =
                            (end.toDouble() - start.toDouble()) / bins;
                        request_parms = new Object[] { new Double(interval) };
                    }
                    
                    final BatchIterator batch = new BatchIterator(server,
                                    archives[i].getKey(), item.getName(),
                                    start, end, request_type, request_parms);
                    IValue result[] = batch.getBatch();
                    while (result != null)
                    {   // Notify model of new samples.
                        // Even when 'cancelled' at this point?
                        // Yes, since we have the samples, might as well show them
                        // before bailing out.
                        if (result.length > 0)
                            item.addArchiveSamples(server.getServerName(), result);
                        if (cancelled)
                            break;
                        result = batch.next();
                    }
                }
                catch (Exception ex)
                {
                    final String url = archives[i].getUrl();
                    final String msg = ex.getMessage();
                    Plugin.getLogger().error("ArchiveFetchJob " + url, ex);
                    shell.getDisplay().asyncExec(new Runnable()
                    {
                        public void run()
                        {
                            MessageDialog.openError(shell, Messages.Error,
                                NLS.bind(Messages.ErrorFmt, url, msg));
                        }
                    });
                }
            }
            if (!cancelled)
                listener.fetchCompleted(ArchiveFetchJob.this);
            done = true;
        }

        @Override
        public String toString()
        {
            return "WorkerTread for " + ArchiveFetchJob.this.toString();
        }
    };
    
    /** Construct job that fetches data.
     *  @param shell Shell (for error messages)
     *  @param item Item for which to fetch samples
     *  @param start Start time
     *  @param end End time
     *  @param listener Listener that's notified when (if) we completed OK
     */
    public ArchiveFetchJob(final Shell shell,
            final IPVModelItem item, final ITimestamp start,
            final ITimestamp end, final ArchiveFetchJobListener listener)
    {
        super(Messages.FetchDataForPV
                + "'" + item.getName() + "' "
                + start.toString() + " - " + end.toString());
        this.shell = shell;
        this.item = item;
        this.start = start;
        this.end = end;
        this.listener = listener;
        // Do we need to assert that only one data fetch runs at a time?
        // setRule()...?
        setRule(this);
    }
    
    /** Default implementation
     *  @see ISchedulingRule#contains(ISchedulingRule)
     */
    public boolean contains(ISchedulingRule rule)
    {
        return rule == this;
    }

    /** Force other Job that tries to get data for the same PV to wait.
     *  <p>
     *  Due to imperfections in the rest of the code it could happen
     *  that we request the same data more than once.
     *  <p>
     *  By delaying other requests for the same PV, those subsequent jobs
     *  for the same data will find it in the cache and thus return ASAP,
     *  instead of trying to get the same data in parallel.
     *  @see ISchedulingRule#isConflicting(ISchedulingRule)
     */
    public boolean isConflicting(ISchedulingRule rule)
    {
        if (rule instanceof ArchiveFetchJob)
        {
            final ArchiveFetchJob other = (ArchiveFetchJob) rule;
            return other.item == this.item;
        }
        return rule == this;
    }
    
    /** 'main' routine of the Eclipse Job:
     *  Launches a worker, polls the monitor and worker for completion
     *  or cancellation.
     *  @param monitor Progress Monitor
     */
    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
        // Start worker
        monitor.beginTask(Messages.FetchingSample, IProgressMonitor.UNKNOWN);
        final WorkerThread worker = new WorkerThread();
        worker.start();
        
        // Poll worker and progress monitor
        long seconds = 0;
        while (!worker.isDone())
        {
            monitor.subTask(worker.getMessage() + ", " + seconds + "s");
            try
            {
                Thread.sleep(POLL_PERIOD_MS);
            }
            catch (InterruptedException ex)
            {
                // Ignore
            }
            // Try to cancel the worker in response to user's cancel request.
            // Continues to cancel the worker until isDone()
            if (monitor.isCanceled())
                worker.cancel();
            ++seconds;
        }
        monitor.done();
        return monitor.isCanceled()
            ? Status.CANCEL_STATUS
            : Status.OK_STATUS;
    }
    
    @Override
    public String toString()
    {
        return "ArchiveFetchJob '" + item.getName() + "' "
            + start.toString() + " - " + end.toString();
    }
}
