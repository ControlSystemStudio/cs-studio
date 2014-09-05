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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanDataIterator;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFactory;
import org.junit.Test;

/** JUnit test of the {@link ScanDataIterator}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanDataIteratorUnitTest
{
    @Test
    public void testScanDataIteratorUnitTest()
    {
        // Create simple ScanData: Devices x, y, values 0...9
    	final Date now = new Date();
        final List<ScanSample> xsamples = new ArrayList<ScanSample>();
        final List<ScanSample> ysamples = new ArrayList<ScanSample>();
        for (int i=0; i<20; ++i)
        {
        	if (i % 2 == 0)
	            xsamples.add(ScanSampleFactory.createSample(new Date(now.getTime() + i*1000), i, i/2));
        	else
        		ysamples.add(ScanSampleFactory.createSample(new Date(now.getTime() + i*1000), i, i/2));
        }
        final Map<String, List<ScanSample>> device_data = new HashMap<String, List<ScanSample>>();
        device_data.put("x", xsamples);
        device_data.put("y", ysamples);
        final ScanData data = new ScanData(device_data);

        // Dump as spreadsheet table
        new ScanDataIterator(data).printTable(System.out);

        // Should have at least one line and 2 columns
        final ScanDataIterator sheet = new ScanDataIterator(data);
        assertEquals(2, sheet.getDevices().length);
        // Check rows
        Date last_time = null;
        for (int i=0; i<20; ++i)
        {
            assertTrue(sheet.hasNext());
            last_time = sheet.getTimestamp();
        }
        assertFalse(sheet.hasNext());

        assertEquals( new Date(now.getTime() + 19*1000), last_time);

        // Dump as CVS
        new ScanDataIterator(data).printCSV(System.out);
    }
}
