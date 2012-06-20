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

/** Engine that accepts {@link ExecutableScan}s, queuing them and executing
 *  them in order
 *  @author Kay Kasemir
 */
public class ScanEngine
{
    final private ExecutorService executor = Executors.newSingleThreadExecutor();

    /** All the scans handled by this engine
     *
     *  <p>New, pending scans, i.e. in Idle state, are added to the end.
     *  The currently executing scan is Running or Paused.
     *  Scans that either Finished, Failed or were Aborted
     *  are kept around for a little while.
     */
    final private List<ExecutableScan> scan_queue = new LinkedList<ExecutableScan>();

    /** Start the scan engine, i.e. create thread that will process
     *  scans
     */
    public void start()
    {
        // TODO Read old scans

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

    /** Submit a scan to the engine for execution
     *  @param scan The {@link ExecutableScan}
     *  @throws IllegalStateException if scan had been submitted before
     */
    public void submit(final ExecutableScan scan)
    {
        scan.submit(executor);
        synchronized (scan_queue)
        {
            scan_queue.add(scan);
        }
    }

    /** @return List of scans */
    public List<ExecutableScan> getScans()
    {
        final List<ExecutableScan> scans = new ArrayList<ExecutableScan>();
        synchronized (scan_queue)
        {
            scans.addAll(scan_queue);
        }
        return scans;
    }

    /** @param scan Scan to remove (if it's 'done') */
    public void removeScan(final ExecutableScan scan)
    {
        // Only remove scans that are 'done'
        if (scan.getScanState().isDone())
        {
            synchronized (scan_queue)
            {
                scan_queue.remove(scan);
            }
        }
    }

    /** Remove completed scans */
    public void removeCompletedScans()
    {
        synchronized (scan_queue)
        {
            final Iterator<ExecutableScan> iterator = scan_queue.iterator();
            while (iterator.hasNext())
            {
                if (iterator.next().getScanState().isDone())
                    iterator.remove();
            }
        }
    }

    /** Remove the oldest completed scan
     *  @return <code>true</code> if a scan could be removed
     */
    public boolean removeOldestCompletedScan()
    {
        synchronized (scan_queue)
        {
        	for (ExecutableScan scan : scan_queue)
        		if (scan.getScanState().isDone())
        		{
        			scan_queue.remove(scan);
        			return true;
        		}
        }
        return false;
    }
}
