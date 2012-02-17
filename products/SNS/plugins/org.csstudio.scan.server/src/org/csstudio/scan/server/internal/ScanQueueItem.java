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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Scan engine queue info for a scan
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class ScanQueueItem implements Callable<Object>
{
    final private Scan scan;
    final private Future<Object> future;

    /** Initialize
     *  @param executor Executor that will call this queue item
     *  @param scan The {@link Scan}
     */
    public ScanQueueItem(final ExecutorService executor, final Scan scan)
    {
        this.scan = scan;
        this.future = executor.submit(this);
    }

    /** Callable for executing the scan within device context */
    @Override
    public Object call() throws Exception
    {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Executing Scan: {0}", scan.getName());
        scan.execute();
        return null;
    }

    /** @return Scan of this queue item */
    public Scan getScan()
    {
        return scan;
    }

    /** return <code>true</code> if the scan has been executed */
    public boolean isDone()
    {
        return future.isDone();
    }

    /** Abort the scan, nicely and forcefully */
    public void abort()
    {
        // Ask scan to stop
        scan.abort();

        // TODO Preference setting for the abort wait time
        // Wait for isDone() to allow graceful end?
        for (int i=0; i<10; ++i)
            if (isDone())
            {   // Scan is done
                return;
            }
            else
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException ex)
                {
                    break;
                }
            }
        // Forced stop
        future.cancel(true);
    }
};
