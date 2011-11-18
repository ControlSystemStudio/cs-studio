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

import org.csstudio.scan.data.NumberScanSample;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** [Headless] JUnit Plug-In demo of the Scan GUI
 *  @author Kay Kasemir
 */
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
                new NumberScanSample("xpos", new Date(), 1, 1),
                new NumberScanSample("xpos", new Date(), 2, 2),
        }));
        data.put("readback", Arrays.asList(new ScanSample[]
        {
                new NumberScanSample("readback", new Date(), 1, 3),
                new NumberScanSample("readback", new Date(), 2, 4),
        }));

        final PlotDataProvider trace = new PlotDataProvider(display);
        trace.update(new ScanData(data), "xpos", "readback");
        plot.addTrace(trace);
        
        shell.setSize(800, 600);
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch()) display.sleep();
        }
    }
}
