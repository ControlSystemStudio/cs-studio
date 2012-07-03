/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.csstudio.scan.client.ScanServerConnector;
import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.XMLCommandWriter;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.dataprovider.IDataProviderListener;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the {@link PlotDataModel}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PlotDataModelHeadlessTest
{
    /** Execute a scan that logs something
     *  @return ID of submitted scan
     */
    private long startDemoScan() throws Exception
    {
        final ScanServer server = ScanServerConnector.connect();

        final List<ScanCommand> commands = new ArrayList<ScanCommand>();
        commands.add(new LoopCommand("xpos", 1.0, 3.0, 1.0,
                                     new LogCommand("xpos", "readback"),
                                     new DelayCommand(2.0)));
        final long id = server.submitScan("PlotDemo", XMLCommandWriter.toXMLString(commands));
        ScanServerConnector.disconnect(server);
        // Scan continues...
        return id;
    }

    @Test(timeout=12000)
    public void testPlotData() throws Exception
    {
        final long id = startDemoScan();

        // Wait for model to obtain scans
        final PlotDataModel model = new PlotDataModel(null);
        model.start();

        ScanInfo scan = model.getScan(id);
        while (scan == null)
        {
            Thread.sleep(100);
            scan = model.getScan(id);
        }

        final List<ScanInfo> infos = model.getScanInfos();
        for (ScanInfo info : infos)
            System.out.println(info);
        assertTrue(infos.contains(scan));

        model.selectScan(scan.getId());
        model.selectXDevice("xpos");
        model.addYDevice("readback");

        PlotDataProvider[] data_providers = null;
        boolean poll = true;
        while (poll)
        {
            scan = model.getScan(id);
            data_providers = model.getPlotDataProviders();
            for (PlotDataProvider data : data_providers)
            {
                synchronized (data)
                {
                    System.out.println("\n" + scan);
                    for (int i=0; i<data.getSize(); ++i)
                        System.out.println(data.getSample(i));
                }
            }
            if (scan.getState().isDone())
                poll = false;
            else
                Thread.sleep(500);
        }
        assertEquals(1, data_providers.length);
        assertTrue(data_providers[0].getSize() >= 3);
        model.stop();
    }

    @Test(timeout=12000)
    public void testListener() throws Exception
    {
        final long id = startDemoScan();

        // Wait for model to obtain scans
        final PlotDataModel model = new PlotDataModel(null);
        model.start();
        ScanInfo scan = model.getScan(id);
        while (scan == null)
        {
            Thread.sleep(100);
            scan = model.getScan(id);
        }

        model.selectScan(scan.getId());
        model.selectXDevice("xpos");
        model.addYDevice("readback");
        final PlotDataProvider[] data_providers = model.getPlotDataProviders();
        assertNotNull(data_providers);
        assertEquals(1, data_providers.length);

        final PlotDataProvider data = data_providers[0];

        final AtomicInteger updates = new AtomicInteger();

        data.addDataProviderListener(new IDataProviderListener()
        {
            @Override
            public void dataChanged(final IDataProvider changed)
            {
                assertSame(data, changed);
                synchronized (changed)
                {
                    if (changed.getSize() <= 0)
                    {
                        System.out.println("Reveived update with no data");
                        return;
                    }
                    final int update = updates.incrementAndGet();
                    System.out.println("\nUpdate " + update);
                    for (int i=0; i<changed.getSize(); ++i)
                        System.out.println(changed.getSample(i));
                }
            }
        });

        scan = model.getScan(id);
        while (! scan.getState().isDone())
        {
            Thread.sleep(500);
            scan = model.getScan(id);
        }

        model.stop();

        assertEquals(3, updates.get());
    }
}
