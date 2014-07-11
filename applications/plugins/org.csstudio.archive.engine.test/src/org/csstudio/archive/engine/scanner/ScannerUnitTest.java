/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.csstudio.apputil.time.BenchmarkTimer;
import org.junit.Test;

/** Unit test of the Scanner
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScannerUnitTest
{
    final private static double period = 3.0;
    final private static double fast_period = 1.0;

    /** Scannable item that counts invocations */
    static class ScanItem implements Runnable
    {
        final String name;
        int scans = 0;

        ScanItem(final String name)
        {
            this.name = name;
        }

        @Override
        public void run()
        {
            ++scans;
            System.out.format("%s scans: %d\n", name, scans);
        }
    }

    /** Single ScanList, basic due time computation */
    @Test
    public void testScanListScan() throws InterruptedException
    {
        System.out.println("Testing " + period + " second ScanList");

        final ScanList list = new ScanList(period);
        final ScanItem item = new ScanItem("Test");
        list.add(item);

        long delay = list.getNextDueTime() - System.currentTimeMillis();
        System.out.println("Due in " + delay + " ms...");
        assertTrue("About " + period + " second delay",
                        Math.abs(delay - 1000*period) < 100);

        Thread.sleep(delay);
        delay = list.getNextDueTime() - System.currentTimeMillis();
        System.out.println("Due in " + delay + " ms...");
        assertTrue("Should be due", Math.abs(delay) < 100);

        // Scan once
        list.scanItems();
        assertEquals(1, item.scans);

        delay = list.getNextDueTime() - System.currentTimeMillis();
        System.out.println("Due in " + delay + " ms...");
        assertTrue("About " + period + " second delay",
                        Math.abs(delay - 1000*period) < 100);
    }

    /** Scanner, how it schedules ScanLists */
    @Test
    public void testScannerConfig() throws Exception
    {
        final Scanner scanner = new Scanner();
        assertFalse(scanner.isDueAtAll());

        final ScanItem item1 = new ScanItem("Item1");
        scanner.add(item1, period);
        assertTrue(scanner.isDueAtAll());
        assertEquals(1, scanner.size());
        assertEquals(item1, scanner.get(0).get(0));

        // Due at period
        long delay = scanner.getNextDueTime() - System.currentTimeMillis();
        System.out.println("Due in " + delay + " ms...");
        assertTrue("About " + period + " second delay",
                    Math.abs(delay - 1000*period) < 100);

        // Now due at fast period
        scanner.add(item1, fast_period);
        delay = scanner.getNextDueTime() - System.currentTimeMillis();
        System.out.println("Due in " + delay + " ms...");
        assertTrue("About " + fast_period + " second delay",
                        Math.abs(delay - 1000*fast_period) < 100);

        // This should have removed the previous entry, i.e. still only 1
        assertEquals(1, scanner.size());
        assertEquals(item1, scanner.get(0).get(0));

        // Add another entry at the fast period
        final ScanItem item2 = new ScanItem("Item2");
        scanner.add(item2, fast_period);
        assertEquals(1, scanner.size());
        assertEquals(item1, scanner.get(0).get(0));
        assertEquals(item2, scanner.get(0).get(1));

        // Add another entry at the slow period
        final ScanItem item3 = new ScanItem("Item3");
        scanner.add(item3, period);
        assertEquals(2, scanner.size());
        assertEquals(item1, scanner.get(0).get(0));
        assertEquals(item2, scanner.get(0).get(1));
        assertEquals(item3, scanner.get(1).get(0));
    }

    /** Run Scanner, doing the delay for it. */
    @Test
    public void testScannerWithExternalDelay() throws Exception
    {
        System.out.println("Testing " + period + " second Scanner");

        final Scanner scanner = new Scanner();
        final ScanItem item = new ScanItem("Item");
        scanner.add(item, fast_period);

        // Check initial delay
        long delay = scanner.getNextDueTime() - System.currentTimeMillis();
        System.out.println("Due in " + delay + " ms...");
        assertTrue("About " + fast_period + " second delay",
                        Math.abs(delay - 1000*fast_period) < 100);

        // Wait, then scan once
        Thread.sleep(delay);
        scanner.scanDueScanLists();

        // Did item scan?
        assertEquals(1, item.scans);
        delay = scanner.getNextDueTime() - System.currentTimeMillis();
        System.out.println("Due in " + delay + " ms...");
        assertTrue("About " + fast_period + " second delay",
                        Math.abs(delay - 1000*fast_period) < 100);
    }

    /** Ask the scanner to perform the delay */
    @Test
    public void testScannerRun() throws InterruptedException
    {
        System.out.println("Scanning 3 times");

        final Scanner scanner = new Scanner();
        final ScanItem item = new ScanItem("Item");
        scanner.add(item, fast_period);

        while (item.scans < 3)
        {
            System.out.println("scan...");
            scanner.scanOnce();
        }
        assertTrue(item.scans >= 3);

        System.out.format("Idle: %.2f %%\n", scanner.getIdlePercentage());
    }

    /** Run scanner inside a scan thread */
    @Test
    public void testScanThread() throws InterruptedException
    {
        System.out.println("Scanning 3 times");

        final Scanner scanner = new Scanner();
        final ScanItem item = new ScanItem("Item");
        scanner.add(item, fast_period);

        final ScanThread thread = new ScanThread(scanner);
        thread.start();
        BenchmarkTimer timer = new BenchmarkTimer();
        while (item.scans < 3)
        {
            Thread.sleep(100);
        }
        timer.stop();
        thread.stop();
        thread.join();
        System.out.format("Elapsed: %.2f secs\n", timer.getSeconds());
        assertTrue(item.scans >= 3);
        // Should take about 3 seconds...
        assertTrue(timer.getSeconds() > 2.0);
        assertTrue(timer.getSeconds() < 4.0);

        item.scans = 0;
        Thread.sleep(2000);
        assertEquals("Unexpected scans", 0, item.scans);

        System.out.format("Idle: %.2f %%\n", scanner.getIdlePercentage());
    }
}
