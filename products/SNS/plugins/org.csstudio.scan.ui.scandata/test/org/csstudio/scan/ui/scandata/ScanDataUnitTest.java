/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scandata;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.csstudio.scan.client.ScanInfoModel;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanDataIterator;
import org.csstudio.scan.server.ScanInfo;
import org.junit.Test;

/** JUnit test of the {@link ScanDataModel}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanDataUnitTest implements ScanDataModelListener
{
	final private CountDownLatch received_data = new CountDownLatch(1);

	/** @return The first scan we can find on the server */
    private ScanInfo findMeAScan() throws Exception
	{
		System.out.println("Looking for a scan...");
		// Get some scan info
		final ScanInfoModel model = ScanInfoModel.getInstance();
		try
		{	// Poll...
			List<ScanInfo> scans = model.getInfos();
			while (scans.size() < 1)
			{
				Thread.sleep(500);
				scans = model.getInfos();
			}

			final ScanInfo scan = scans.get(0);
			System.out.println("Found " + scan);
			return scan;
		}
		finally
		{
			model.release();
		}
	}

    /** Should receive a scan data update and NOT time out */
	@Test(timeout=10000)
	public void testScanData() throws Exception
	{
		final ScanInfo scan = findMeAScan();

		final ScanDataModel model = new ScanDataModel(scan.getId(), this);
		try
		{
			received_data.await();
		}
		finally
		{
			model.release();
		}
	}

	/** {@inheritDoc} */
	@Override
    public void updateScanData(final ScanData data)
    {
		new ScanDataIterator(data).printTable(System.out);
		received_data.countDown();
    }
}
