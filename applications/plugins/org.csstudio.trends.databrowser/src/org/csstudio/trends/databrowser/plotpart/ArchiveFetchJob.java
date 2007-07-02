package org.csstudio.trends.databrowser.plotpart;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.cache.ArchiveCache;
import org.csstudio.archive.crawl.BatchIterator;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.IPVModelItem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/** Eclipse background job for fetching samples from the data server.
 *  @author Kay Kasemir
 */
class ArchiveFetchJob extends Job
{
    private IPVModelItem item;
    private ITimestamp start, end;
    
    /** Create job that searches given server's keys for pattern,
     *  then notifies view about received names.
     */
    public ArchiveFetchJob(IPVModelItem item, ITimestamp start, ITimestamp end)
    {
        super(Messages.FetchDataForPV
                + "'" + item.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        this.item = item;
        this.start = start;
        this.end = end;
        // Do we need to assert that only one data fetch runs at a time?
        // setRule()...?
    }
    
    @SuppressWarnings("nls")
    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
        final IArchiveDataSource archives[] = item.getArchiveDataSources();
        monitor.beginTask(Messages.FetchingSample, archives.length);
        for (int i=0; i<archives.length; ++i)
        {
            // Display "N/total", using '1' for the first sub-archive.
            monitor.subTask(Messages.Fetch_Archive
                + "'" + archives[i].getName()
                + "' ("
                + (i+1) + "/" + archives.length + ")");
            final ArchiveCache cache = ArchiveCache.getInstance();
            try
            {   // Invoke the possibly lengthy search.
                final ArchiveServer server =
                    cache.getServer(archives[i].getUrl());
                
                // TODO: Get something better than PLOTBINNED
                final String request_type = 
                    item.getRequestType() == IPVModelItem.RequestType.RAW
                    ? ArchiveServer.GET_RAW
                    : ArchiveServer.GET_PLOTBINNED;
                int bins = 800;
                final Object[] request_parms = new Object[] { new Integer(bins) };
                
                BatchIterator batch = new BatchIterator(server,
                                archives[i].getKey(), item.getName(),
                                start, end, request_type, request_parms);
                IValue result[] = batch.getBatch();
                while (result != null)
                {   // Notify model of new samples.
                    // Even when monitor.isCanceled at this point?
                    // Yes, since we have the samples, might as well show them
                    // before bailing out.
                    if (result.length > 0)
                        item.addArchiveSamples(server.getServerName(), result);
                    if (monitor.isCanceled())
                        break;
                    result = batch.next();
                }
            }
            catch (Exception ex)
            {
                Plugin.logException("ArchiveFetchJob", ex);
            }
            // Stop and ignore further results when canceled.
            if (monitor.isCanceled())
                return Status.CANCEL_STATUS;
            // Handled one sub-archive.
            monitor.worked(1);
        }
        monitor.done();
        return Status.OK_STATUS;
    }
}