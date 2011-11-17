/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import java.util.List;

import org.csstudio.scan.data.SpreadsheetScanDataIterator;
import org.csstudio.scan.server.ScanInfo;
import org.junit.Test;

/** JUnit test of the {@link PlotDataModel}
 *  @author Kay Kasemir
 */
public class PlotDataModelTest
{
    @Test
    public void testPlotDataModel() throws Exception
    {
        final PlotDataModel model = new PlotDataModel();

        // TODO better test
        Thread.sleep(2000);
        final List<ScanInfo> infos = model.getScanInfos();
        for (ScanInfo info : infos)
            System.out.println(info);
        
        model.selectScan(infos.get(infos.size()-1).getId());
        
        new SpreadsheetScanDataIterator(model.getScanData()).dump(System.out);
        
        model.dispose();
    }
}
