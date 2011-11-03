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


import org.csstudio.scan.data.ScanSampleFactory;
import org.csstudio.scan.logger.DataLogger;
import org.csstudio.scan.logger.MemoryDataLogger;
import org.csstudio.scan.logger.PrintDataLogger;
import org.csstudio.scan.logger.SpreadsheetDataLoggerFormatter;
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
	public void testPrintLogger()
	{
		final DataLogger logger = new PrintDataLogger();
		logData(logger);
	}

	@Test
	public void testSpreadsheet()
	{
		final MemoryDataLogger logger = new MemoryDataLogger();
		logData(logger);
		new SpreadsheetDataLoggerFormatter().format(System.out, logger);
	}
}
