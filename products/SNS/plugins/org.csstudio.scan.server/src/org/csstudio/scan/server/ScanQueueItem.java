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
package org.csstudio.scan.server;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.csstudio.scan.device.DeviceContext;

/** Scan engine queue info for a scan
 *  @author Kay Kasemir
 */
class ScanQueueItem implements Callable<Object>
{
    final private DeviceContext devices;
    final private Scan scan;
    final private Future<Object> future;

    /** Initialize
     *  @param executor Executor that will call this queue item
     *  @param devices {@link DeviceContext} to use for scan
     *  @param scan The {@link Scan}
     */
    public ScanQueueItem(final ExecutorService executor, final DeviceContext devices, final Scan scan)
    {
        this.devices = devices;
        this.scan = scan;
        this.future = executor.submit(this);
    }

    /** Callable for executing the scan within device context */
    @Override
    public Object call() throws Exception
    {
        System.out.println("== " + scan.getName());
        final ScanContext context = new ScanContext(devices);
        scan.execute(context);
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
        // TODO Wait for isDone() to allow graceful end?

        // .. and force it to do so if wait()ing
        future.cancel(true);
    }
};
