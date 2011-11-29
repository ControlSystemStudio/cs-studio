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

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFactory;
import org.csstudio.scan.data.SpreadsheetScanDataIterator;
import org.csstudio.scan.logger.DataLogger;
import org.csstudio.scan.logger.MemoryDataLogger;
import org.csstudio.scan.logger.PrintDataLogger;
import org.junit.Test;

/** JUnit test of the {@link DataLogger}s
 *  @author Kay Kasemir
 */
public class DataLoggerUnitTest
{
	private void logData(final DataLogger logger)
    {
		for (int x=0; x<5; ++x)
		{
			logger.log(ScanSampleFactory.createSample("x", Double.valueOf(x)));
			for (int y=0; y<5; ++y)
				logger.log(ScanSampleFactory.createSample("y", Double.valueOf(y)));
		}
    }

	@Test
	public void testPrintDataLogger()
	{
	    System.out.println("PrintDataLogger:");
		final DataLogger logger = new PrintDataLogger();
		logData(logger);
	}

    @Test
    public void testMemoryDataLogger()
    {
        System.out.println("MemoryDataLogger:");
        final MemoryDataLogger logger = new MemoryDataLogger();
        assertTrue(logger.getLastScanDataSerial() < 0);
        
        logData(logger);
        
        final ScanData data = logger.getScanData();
        assertEquals(2, data.getDevices().size());
        assertNotNull(data.getSamples("x"));
        assertNotNull(data.getSamples("y"));
        for (ScanSample sample : data.getSamples("x"))
            System.out.println(sample);
        assertEquals(5, data.getSamples("x").size());
        assertEquals(5*5, data.getSamples("y").size());
        
        assertEquals(data.getSamples("y").get(5*5-1).getSerial(),
                     logger.getLastScanDataSerial());
    }

	@Test
	public void testSpreadsheet()
	{
        System.out.println("MemoryDataLogger as Spreadsheet:");
		final MemoryDataLogger logger = new MemoryDataLogger();
		logData(logger);
		SpreadsheetScanDataIterator sheet =
	        new SpreadsheetScanDataIterator(logger.getScanData());
        sheet.dump(System.out);
	}
}
