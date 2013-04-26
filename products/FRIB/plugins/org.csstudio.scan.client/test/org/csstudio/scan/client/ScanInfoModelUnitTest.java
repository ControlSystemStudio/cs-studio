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
package org.csstudio.scan.client;

import static org.junit.Assert.assertSame;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServerInfo;
import org.junit.Ignore;
import org.junit.Test;

/** JUnit test of the {@link ScanInfoModel}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanInfoModelUnitTest implements ScanInfoModelListener
{
    final private CountDownLatch updates = new CountDownLatch(1);

    @Test(timeout=15000)
    public void testStart() throws Exception
    {
        final ScanInfoModel model = ScanInfoModel.getInstance();
        final ScanInfoModel model2 = ScanInfoModel.getInstance();
        assertSame(model, model2);
        model2.release();

        // Adding the listener will trigger an immediate update
        model.addListener(this);
        updates.await();
        model.removeListener(this);
        model.release();
    }

    @Override
    public void scanServerUpdate(final ScanServerInfo server_info)
    {
        System.out.println("\n-- Scan Info: " + server_info);
    }

	@Override
    public void scanUpdate(final List<ScanInfo> infos)
    {
        System.out.println("\n-- Scan Update --");
        for (ScanInfo info : infos)
            System.out.println(info);
        updates.countDown();
    }

    @Override
    public void connectionError()
    {
        System.out.println("Initial Connection error?");
    }

    @Ignore
    @Test
    public void keepMonitoring() throws Exception
    {
        final ScanInfoModel model = ScanInfoModel.getInstance();
        model.addListener(this);
        // Wait forever
        while (true)
            Thread.sleep(1000);
    }
}
