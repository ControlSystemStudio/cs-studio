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
package org.csstudio.scan.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanDataIterator;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFactory;
import org.junit.Test;

/** JUnit test of the {@link DataLog}s
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DataLogUnitTest
{
	private void logData(final DataLog logger) throws Exception
    {
		for (int x=0; x<5; ++x)
		{
		    final Date now = new Date();
		    final long serial = logger.getNextScanDataSerial();
			logger.log("x", ScanSampleFactory.createSample(now, serial, Double.valueOf(x)));
			for (int y=0; y<5; ++y)
				logger.log("y", ScanSampleFactory.createSample(now, serial, Double.valueOf(y)));
		}
    }

    @Test
    public void testMemoryDataLogger() throws Exception
    {
        System.out.println("MemoryDataLogger:");
        final DataLog logger = new MemoryDataLog();
        assertTrue(logger.getLastScanDataSerial() < 0);

        logData(logger);

        final ScanData data = logger.getScanData();
        assertEquals(2, data.getDevices().length);
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
	public void testSpreadsheet() throws Exception
	{
        System.out.println("MemoryDataLogger as Spreadsheet:");
		final DataLog logger = new MemoryDataLog();
		logData(logger);
		ScanDataIterator sheet =
	        new ScanDataIterator(logger.getScanData());
        sheet.printTable(System.out);
	}
}
