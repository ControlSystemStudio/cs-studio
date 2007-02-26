/**
 * 
 */
package org.csstudio.trends.databrowser;

import java.util.ArrayList;

import org.csstudio.archive.ArchiveValues;
import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.cache.ArchiveCache;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.util.ITimestamp;
import org.csstudio.swt.chart.ChartListener;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/** Eclipse background job for fetching samples from the data server.
 *  @author Kay Kasemir
 */
class ArchiveFetchJob extends Job
{
    private IModelItem item;
    private ITimestamp start, end;
    
    /** Create job that searches given server's keys for pattern,
     *  then notifies view about received names.
     */
    public ArchiveFetchJob(IModelItem item, ITimestamp start, ITimestamp end)
    {
        super(Messages.FetchDataForPV
                + "'" + item.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        this.item = item;
        this.start = start;
        this.end = end;
        // TODO: Do we need to assert that only one data fetch runs at a time?
        // setRule()...?
    }
    
    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        IArchiveDataSource archives[] = item.getArchiveDataSources();
        monitor.beginTask(Messages.FetchingSample, archives.length);
        for (int i=0; i<archives.length; ++i)
        {
            // Display "N/total", using '1' for the first sub-archive.
            monitor.subTask(Messages.Fetch_Archive
                + "'" + archives[i].getName() //$NON-NLS-1$
                + "' (" //$NON-NLS-1$
                + (i+1) + "/" + archives.length + ")");  //$NON-NLS-1$//$NON-NLS-2$
            ArchiveCache cache = ArchiveCache.getInstance();
            try
            {   // Invoke the possibly lengthy search.
                ArchiveServer server = cache.getServer(archives[i].getUrl()); 
                //int request_type = server.getRequestType(ArchiveServer.GET_PLOTBINNED);
                int bins = 50;
                ArchiveValues result[] = server.getSamples(
                        archives[i].getKey(), new String[] { item.getName() },
                        start, end, item.getDataType(),
                        new Object[] { new Integer(item.getBins()) });
                
                if(server.getLastRequestError() != 0) {
                	fireErrorEvent(server.getLastRequestError());
                }
                if (result.length == 1)
                {   // Notify model of new samples.
                    // Even when monitor.isCanceled at this point?
                    // Yes, since we have the samples, might as well show them
                    // before bailing out.
                    item.addArchiveSamples(result[0]);         
                }
                else
                {
                    throw new Exception("Didn't get expected response"); //$NON-NLS-1$
                }
            }
            catch (Exception e)
            {
                Plugin.logException("ArchiveFetchJob", e); //$NON-NLS-1$
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
    
    // Events
    private ArrayList<ArchiveFetchJobListener> listeners = new ArrayList<ArchiveFetchJobListener>();

    // This methods allows classes to register for MyEvents
    public void addArchiveFetchJobListener(ArchiveFetchJobListener listener) {
    	listeners.add(listener);
    }

    // This methods allows classes to unregister for MyEvents
    public void removeArchiveFetchJobListener(ArchiveFetchJobListener listener) {
    	listeners.remove(listener);
    }

    // This private class is used to fire MyEvents
    void fireErrorEvent(int errorId) {
    	for(ArchiveFetchJobListener listener : listeners) {
    		listener.errorOccured(errorId);
    	}
    }
}