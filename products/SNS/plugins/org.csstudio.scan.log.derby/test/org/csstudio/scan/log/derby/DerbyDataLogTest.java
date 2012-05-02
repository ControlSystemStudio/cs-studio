/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log.derby;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.csstudio.scan.data.NumberScanSample;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.SpreadsheetScanDataIterator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("nls")
public class DerbyDataLogTest
{
	private static int scan_id = 1;
	private static int device_id = 1;

	// To please FindBugs
	private static void setScan(int id) { scan_id = id; }
	private static void setDevice(int id) { device_id = id; }

	@BeforeClass
	public static void startup() throws Exception
	{
		DerbyDataLog.startup();
	}

	@AfterClass
	public static void shutdown() throws Exception
	{
		DerbyDataLog.shutdown();
	}

	@Test
	public void testDerbyLog() throws Exception
	{
		final DerbyDataLog log = new DerbyDataLog();
		log.close();
	}

    @Test
	public void testCreateScan() throws Exception
	{
		final DerbyDataLog log = new DerbyDataLog();
		setScan(log.createScan("Demo"));
		log.close();

		System.out.println("New scan ID: " + scan_id);

		assertTrue(scan_id > 0);
	}

	@Test
	public void testDeviceLookup() throws Exception
	{
		final DerbyDataLog log = new DerbyDataLog();
		setDevice(log.getDevice("setpoint"));
		System.out.println("Device ID: " + device_id);
		assertTrue(device_id > 0);
		final int id2 = log.getDevice("setpoint");
		assertEquals(device_id, id2);
		log.close();
	}

	@Test(timeout=50000)
	public void testSampleLogging() throws Exception
	{
		final DerbyDataLog log = new DerbyDataLog();
    	// Allows about 2500 samples/second (50000 in 20 seconds)
		for (long serial = 1; serial < 50000; ++serial)
			log.log(scan_id, new NumberScanSample("setpoint", new Date(), serial, 3.14 + serial * 0.01));

		log.close();
	}

	@Test(timeout=10000)
	public void testSampleRetrieval() throws Exception
	{
		final DerbyDataLog log = new DerbyDataLog();

		// Fetches >30000/sec (50000 in 1.6)
		final ScanData data = log.getScanData(scan_id);
		// Printout takes ~2.5 secs
		new SpreadsheetScanDataIterator(data).dump(System.out);

		log.close();
	}
}
