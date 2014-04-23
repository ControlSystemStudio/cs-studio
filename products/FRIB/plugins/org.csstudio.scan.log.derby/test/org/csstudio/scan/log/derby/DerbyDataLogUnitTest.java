/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log.derby;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanDataIterator;
import org.csstudio.scan.data.ScanSampleFactory;
import org.csstudio.scan.server.Scan;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/** JUnit test of the {@link DerbyDataLogger}
 *
 *  <p>Will execute Derby for the database, cannot run if another Derby instance (Scan Server)
 *  already runs the database.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DerbyDataLogUnitTest
{
	private static Scan scan = null;
	private static long device_id = 1;

	// To please FindBugs
	private static void setScan(final Scan s) { scan = s; }
	private static void setDevice(final long id) { device_id = id; }

	@BeforeClass
	public static void startup() throws Exception
	{
		DerbyDataLogger.startup();
	}

	@AfterClass
	public static void shutdown() throws Exception
	{
		DerbyDataLogger.shutdown();
	}

	@Test
	public void testDerbyLog() throws Exception
	{
		final DerbyDataLogger log = new DerbyDataLogger();
		log.close();
	}

    @Test
	public void testCreateScan() throws Exception
	{
		final DerbyDataLogger log = new DerbyDataLogger();
		setScan(log.createScan("Demo"));
		log.close();

		System.out.println("New scan: " + scan);

		assertNotNull(scan);
	}

	@Test
	public void testDeviceLookup() throws Exception
	{
		final DerbyDataLogger log = new DerbyDataLogger();
		setDevice(log.getDevice("setpoint"));
		System.out.println("Device ID: " + device_id);
		assertTrue(device_id > 0);
		final int id2 = log.getDevice("setpoint");
		assertEquals(device_id, id2);
		log.close();
	}

	@Test(timeout=80000)
	public void testSampleLogging() throws Exception
	{
		final DerbyDataLogger log = new DerbyDataLogger();
        // Allows about 1900 samples/second
		// JProfiler shows all time spent in PreparedStatement.executeUpdate()
		// When value was just a DOUBLE number, no SampleValue:
		// Allows about 2500 samples/second
		final long scan_id = scan.getId();
		final long start = System.nanoTime();
		for (long serial = 1; serial < 50000; ++serial)
			log.log(scan_id, "setpoint", ScanSampleFactory.createSample(new Date(), serial, 3.14 + serial * 0.01));
        final long nanos = System.nanoTime() - start;
		log.close();
		final long vals_per_sec = 50000L * 1000000000L / nanos;
		System.out.println("Writing " + vals_per_sec + " vals/sec");
	}

	@Test(timeout=50000)
	public void testSampleRetrieval() throws Exception
	{
		final DerbyDataLogger log = new DerbyDataLogger();
        final long scan_id = scan.getId();

        // Reads about 4400 samples/second
        // JProfiler shows all time spent in PreparedStatement.executeQuery()
        // When value was just a DOUBLE number, no SampleValue: >30000/sec
        final long start = System.nanoTime();
		final ScanData data = log.getScanData(scan_id);
        final long nanos = System.nanoTime() - start;

        // Printout takes 2..3 secs
		new ScanDataIterator(data).printTable(System.out);

        final long vals_per_sec = 50000L * 1000000000L / nanos;
        System.out.println("Reading " + vals_per_sec + " vals/sec");

		log.close();
	}

    @Test(timeout=10000)
    public void testScanList() throws Exception
    {
        final DerbyDataLogger log = new DerbyDataLogger();
        final Scan[] scans = log.getScans();
        for (Scan scan : scans)
            System.out.println(scan);
        log.close();
        assertTrue(scans.length > 0);
        assertEquals(DerbyDataLogUnitTest.scan, scans[scans.length - 1]);
    }
}
