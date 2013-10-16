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
package org.csstudio.scan;

import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.internal.ScanServerImpl;
import org.junit.Ignore;
import org.junit.Test;

/** Start {@link ScanServer} as [Headless] JUnit Plug-in test
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanServerDemo
{
    // Nothing is setting this to false, but it makes FindBugs happy
    private volatile boolean run = true;

    // It is important to keep a reference to the server implementation!!
    //
    // Otherwise it could be garbage collected,
    // and then clients will get java.rmi.NoSuchObjectException
    // when they try to invoke methods in the server.
    private ScanServerImpl server;

    // Run only on demand when testing the server without
    // starting the complete Application or Product
    @Ignore
    @Test
    public void runScanServer() throws Exception
    {
        server = new ScanServerImpl();
        server.start();
        System.out.println("Scan Server running...");
        // Keep running...
        synchronized (this)
        {
            while (run)
                wait();
        }
    }
}
