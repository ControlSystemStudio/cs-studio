/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.scan.client.ScanServerConnector;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.junit.Test;

/** JUnit test of the {@link PlotDataModel}
 *  @author Kay Kasemir
 */
public class PlotDataModelUnitTest
{
    /** Execute a scan that logs something */
    private void runDemoScan() throws Exception
    {
        final ScanServer server = ScanServerConnector.connect();

        final List<ScanCommand> commands = new ArrayList<ScanCommand>();
        commands.add(new LoopCommand("xpos", 1.0, 2.0, 1.0,
                                     new LogCommand("xpos", "readback")));
        final long id = server.submitScan("PlotDemo", commands);
        
        while (! server.getScanInfo(id).isDone())
            Thread.sleep(100);
        
        ScanServerConnector.disconnect(server);
    }

    @Test(timeout=5000)
    public void testPlotDataModel() throws Exception
    {
        runDemoScan();
        
        // Wait for model to obtain scans
        final PlotDataModel model = new PlotDataModel(null);
        model.start();
        List<ScanInfo> infos = model.getScanInfos();
        while (infos.size() <= 0)
        {
            Thread.sleep(100);
            infos = model.getScanInfos();
        }
        for (ScanInfo info : infos)
            System.out.println(info);
        model.selectScan(infos.get(0).getId());
        
        model.selectXDevice("xpos");
        model.selectYDevice("readback");
        final PlotDataProvider data = model.getPlotData();
        while (data.getSize() <= 0)
        {
            Thread.sleep(100);
        }
        synchronized (data)
        {
            for (int i=0; i<data.getSize(); ++i)
                System.out.println(data.getSample(i));
        }        
        model.stop();
    }
}
