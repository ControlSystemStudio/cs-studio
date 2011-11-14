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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.scan.data.NumberScanSample;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.SpreadsheetScanDataIterator;
import org.junit.Test;

/** JUnit test of the {@link SpreadsheetScanDataIterator}
 *  @author Kay Kasemir
 */
public class SpreadsheetScanDataIteratorTest
{
    @Test
    public void testSpreadsheetScanDataIteratorScanData()
    {
        // Create simple ScanData: Devices x, y, values 0...9
        final Map<String, List<ScanSample>> device_data = new HashMap<String, List<ScanSample>>();
        final List<ScanSample> samples = new ArrayList<ScanSample>();
        for (int i=0; i<10; ++i)
            samples.add(new NumberScanSample("x", new Date(), i, i));
        device_data.put("x", samples);
        device_data.put("y", samples);
        final ScanData data = new ScanData(device_data);
        
        // Dump as spreadsheet
        new SpreadsheetScanDataIterator(data).dump(System.out);
        
        // Should have at least one line and 2 columns
        final SpreadsheetScanDataIterator sheet = new SpreadsheetScanDataIterator(data);
        assertTrue(sheet.hasNext());
        assertEquals(2, sheet.getDevices().size());
        // Check remaining rows
        for (int i=1/*!*/; i<samples.size(); ++i)
            assertTrue(sheet.hasNext());
        assertFalse(sheet.hasNext());
    }
}
