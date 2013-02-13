/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.archive;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveRepository;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.model.RequestType;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.epics.vtype.VType;
import org.epics.util.time.Timestamp;

/** Eclipse Job for fetching archived data.
 *  <p>
 *  Actually spawns another thread so that the 'main' job can
 *  poll the progress monitor for cancellation and ask the secondary
 *  thread to cancel.
 *  @author Kay Kasemir
 */
public class ArchiveFetchJob extends Job
{
    /** Poll period in millisecs */
    private static final int POLL_PERIOD_MS = 1000;

    /** Item for which to fetch samples */
    final private PVItem item;

    /** Start/End time */
    final private Timestamp start, end;

    /** Listener that's notified when (if) we completed OK */
    final private ArchiveFetchJobListener listener;

    private static volatile int worker_instance = 0;

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
        private String message = ""; //$NON-NLS-1$
        private volatile boolean cancelled = false;
        private volatile boolean done = false;

        /** Archive reader that's currently queried.
         *  Synchronize 'this' on access.
         */
        private ArchiveReader reader = null;

        /** Construct */
        public WorkerThread()
        {
            super("ArchiveFetchJobWorker" + (++worker_instance)); //$NON-NLS-1$
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
                if (reader != null)
                    reader.cancel();
            }
        }

        /** @return <code>true</code> when done (success, error, canceled) */
        public synchronized boolean isDone()
        {
            return done;
        }

        /** {@inheritDoc} */
        @Override
        public void run()
        {
            Activator.getLogger().log(Level.FINE, "Starting {0}", ArchiveFetchJob.this); //$NON-NLS-1$
            final int bins = Preferences.getPlotBins();
            final ArchiveDataSource archives[] = item.getArchiveDataSources();
            for (int i=0; i<archives.length && !cancelled; ++i)
            {
                final ArchiveDataSource archive = archives[i];
                final String url = archive.getUrl();
                // Display "N/total", using '1' for the first sub-archive.
                synchronized  (this)
                {
                    message = NLS.bind(Messages.ArchiveFetchDetailFmt,
                            new Object[]
                            {
                                archive.getName(),
                                (i+1),
                                archives.length
                            });
                }
                try
                {
                    final ArchiveReader the_reader;
                    synchronized (this)
                    {
                        the_reader = reader = ArchiveRepository.getInstance().getArchiveReader(url);
                    }
                    final ValueIterator value_iter;
                    if (item.getRequestType() == RequestType.RAW)
                        value_iter = the_reader.getRawValues(archive.getKey(), item.getResolvedName(), start, end);
                    else
                        value_iter = the_reader.getOptimizedValues(archive.getKey(), item.getResolvedName(), start, end, bins);
                    // Get samples into array
                    final List<VType> result = new ArrayList<VType>();
                    while (value_iter.hasNext())
                        result.add(value_iter.next());
                    item.mergeArchivedSamples(the_reader.getServerName(), result);
                    if (cancelled)
                        break;
                    value_iter.close();
                }
                catch (Exception ex)
                {   // Tell listener unless it's the result of a 'cancel'?
                    if (! cancelled)
                        listener.archiveFetchFailed(ArchiveFetchJob.this, archive, ex);
                    // Continue with the next data source
                }
                finally
                {
                    synchronized (this)
                    {
                        if (reader != null)
                            reader.close();
                        reader = null;
                    }
                }
            }
            if (!cancelled)
                listener.fetchCompleted(ArchiveFetchJob.this);
            Activator.getLogger().log(Level.FINE, "Ended {0}", ArchiveFetchJob.this); //$NON-NLS-1$
            done = true;
        }

        @SuppressWarnings("nls")
        @Override
        public String toString()
        {
            return "WorkerTread for " + ArchiveFetchJob.this.toString();
        }
    }

    /** Initialize
     *  @param item
     *  @param start
     *  @param end
     *  @param listener
     */
    public ArchiveFetchJob(PVItem item, final Timestamp start,
            final Timestamp end, final ArchiveFetchJobListener listener)
    {
		super(NLS.bind(Messages.ArchiveFetchJobFmt,
				new Object[] { item.getName(), TimestampHelper.format(start),
						TimestampHelper.format(end) }));
		this.item = item;
		this.start = start;
		this.end = end;
		this.listener = listener;
    }

    /** @return PVItem for which this job was created */
    public PVItem getPVItem()
    {
        return item;
    }

    /** Job's main routine which starts and monitors WorkerThread */
    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
        if (item == null)
            return Status.OK_STATUS;
        // System.out.println("Start: " + this);
        BenchmarkTimer timer = new BenchmarkTimer();

        monitor.beginTask(Messages.ArchiveFetchStart, IProgressMonitor.UNKNOWN);
        final WorkerThread worker = new WorkerThread();
        worker.start();
        // Poll worker and progress monitor
        long seconds = 0;
        while (true)
        {
            try
            {
                Thread.sleep(POLL_PERIOD_MS);
            }
            catch (InterruptedException ex)
            {
                // Ignore
            }
            if (worker.isDone())
                break;
            final String info = NLS.bind(Messages.ArchiveFetchProgressFmt,
                    worker.getMessage(), ++seconds);
            monitor.subTask(info);
            // Try to cancel the worker in response to user's cancel request.
            // Continues to cancel the worker until isDone()
            if (monitor.isCanceled())
                worker.cancel();
        }
        monitor.done();

        timer.stop();
        // System.out.println(this + ": " + timer.toString());

        return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
    }

    /** @return Debug string */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "ArchiveFetchJob " + TimestampHelper.format(start) + " ... " + TimestampHelper.format(end) + " for " + item.getName();
    }
}
