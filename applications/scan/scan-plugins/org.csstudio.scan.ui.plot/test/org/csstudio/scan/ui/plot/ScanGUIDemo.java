/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFactory;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** [Headless] JUnit Plug-In demo of the Scan GUI
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanGUIDemo
{
    @Test
    public void showScanGUI()
    {
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        shell.setLayout(new GridLayout(1, false));

        final Plot plot = new Plot(shell);

        final Map<String, List<ScanSample>> data = new HashMap<String, List<ScanSample>>();
        data.put("xpos", Arrays.asList(new ScanSample[]
        {
            ScanSampleFactory.createSample(new Date(), 1, 1.0),
            ScanSampleFactory.createSample(new Date(), 2, 2.0),
            ScanSampleFactory.createSample(new Date(), 3, 3.0),
            ScanSampleFactory.createSample(new Date(), 4, 4.0),
        }));
        data.put("readback", Arrays.asList(new ScanSample[]
        {
            ScanSampleFactory.createSample(new Date(), 1, 1.0),
            ScanSampleFactory.createSample(new Date(), 2, 2.0),
            ScanSampleFactory.createSample(new Date(), 3, 4.0),
            ScanSampleFactory.createSample(new Date(), 4, 1.0),
        }));
        data.put("fit", Arrays.asList(new ScanSample[]
        {
            ScanSampleFactory.createSample(new Date(), 1, 1.0),
            ScanSampleFactory.createSample(new Date(), 2, 3.0),
            ScanSampleFactory.createSample(new Date(), 3, 3.0),
            ScanSampleFactory.createSample(new Date(), 4, 1.0),
        }));
        final ScanData scan_data = new ScanData(data);

        final PlotDataProvider readback = new PlotDataProvider(display, "xpos", "readback");
        final PlotDataProvider fit = new PlotDataProvider(display, "xpos", "fit");
        plot.setDataProviders(readback, fit);
        // OK to update the data providers
        plot.setDataProviders(readback, fit);
        plot.setDataProviders(readback, fit);

        // Update data after some delay in thread
        new Thread()
        {
            @Override
            public void run()
            {
                try { sleep(5000); }  catch (InterruptedException e) { }
                readback.update(scan_data);
                try { sleep(5000); }  catch (InterruptedException e) { }
                fit.update(scan_data);
            }
        }.start();

        shell.setSize(800, 600);
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch()) display.sleep();
        }
    }
}
