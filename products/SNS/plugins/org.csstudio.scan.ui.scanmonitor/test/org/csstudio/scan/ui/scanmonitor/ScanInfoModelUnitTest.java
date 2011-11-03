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
package org.csstudio.scan.ui.scanmonitor;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.ui.scanmonitor.ScanInfoModel;
import org.csstudio.scan.ui.scanmonitor.ScanInfoModelListener;
import org.junit.Test;

/** JUnit test of the {@link ScanInfoModel}
 *  @author Kay Kasemir
 */
public class ScanInfoModelUnitTest implements ScanInfoModelListener
{
    final private CountDownLatch updates = new CountDownLatch(1);

    @Test(timeout=15000)
    public void testStart() throws Exception
    {
        final ScanInfoModel model = new ScanInfoModel();
        model.addListener(this);
        model.start();
        updates.await();
        model.stop();
    }

    @Override
    public void scanUpdate(final List<ScanInfo> infos)
    {
        for (ScanInfo info : infos)
            System.out.println(info);
        updates.countDown();
    }

    @Override
    public void connectionError()
    {
        fail("Connection error");
    }

    @Test
    public void keepMonitoring() throws Exception
    {
        final ScanInfoModel model = new ScanInfoModel();
        model.addListener(this);
        model.start();
        // Wait forever
        while (true)
            Thread.sleep(1000);
    }
}
