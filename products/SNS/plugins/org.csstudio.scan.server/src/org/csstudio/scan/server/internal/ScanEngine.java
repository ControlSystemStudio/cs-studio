/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.server.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Engine that accepts {@link Scan}s, queuing them and executing
 *  them in order
 *  @author Kay Kasemir
 */
public class ScanEngine
{
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    /** All the scans handled by this engine
     *
     *  <p>New, pending scans, i.e. in Idle state, are added to the end.
     *  The currently executing scan is Running or Paused.
     *  Scans that either Finished, Failed or were Aborted
     *  are kept around for a little while.
     */
    final private List<ScanQueueItem> scan_queue = new LinkedList<ScanQueueItem>();

    /** Start the scan engine, i.e. create thread that will process
     *  scans
     */
    public void start()
    {
        executor = Executors.newSingleThreadExecutor();
    }

    /** Stop the scan engine, aborting scans
     *  that are still running
     */
    public void stop()
    {
        executor.shutdownNow();
        synchronized (scan_queue)
        {
            scan_queue.clear();
        }
    }

    /** @return <code>true</code> if the engine is idle, i.e. all
     *          there is currently no active scan
     */
    public boolean isIdle()
    {
        synchronized (scan_queue)
        {
            for (ScanQueueItem item : scan_queue)
                if (! item.isDone())
                    return false;
        }
        return true;
    }

    /** Submit a scan to the engine for execution
     *  @param scan The {@link Scan}
     */
    public void submit(final Scan scan)
    {
        synchronized (scan_queue)
        {
            scan_queue.add(new ScanQueueItem(executor, scan));
        }
    }

    /** @return List of scans */
    public List<Scan> getScans()
    {
        final List<Scan> scans = new ArrayList<Scan>();
        synchronized (scan_queue)
        {
            for (ScanQueueItem item : scan_queue)
                scans.add(item.getScan());
        }
        return scans;
    }

    /** Locate scan queue item
     *  @param scan Scan
     *  @return {@link ScanQueueItem} or <code>null</code> when scan not found
     */
    private ScanQueueItem getScanItem(final Scan scan)
    {
        synchronized (scan_queue)
        {
            for (ScanQueueItem item : scan_queue)
                if (item.getScan() == scan) // Really exact scan, not equals() !
                    return item;
        }
        return null;
    }

    /** @param scan Scan to abort */
    public void abortScan(final Scan scan)
    {
        final ScanQueueItem item = getScanItem(scan);
        if (item != null)
            item.abort();
    }

    /** @param scan Scan to remove (if it's 'done') */
    public void removeScan(final Scan scan)
    {
        final ScanQueueItem item = getScanItem(scan);
        if (item == null)
            return;
        // Only remove scans that are 'done'
        if (item.isDone())
        {
            synchronized (scan_queue)
            {
                scan_queue.remove(item);
            }
        }
    }

    /** Remove completed scans */
    public void removeCompletedScans()
    {
        synchronized (scan_queue)
        {
            final Iterator<ScanQueueItem> iterator = scan_queue.iterator();
            while (iterator.hasNext())
            {
                if (iterator.next().isDone())
                    iterator.remove();
            }
        }
    }
}
