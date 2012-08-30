/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.archive;

import static org.junit.Assert.assertTrue;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.model.PlotSamples;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of ArchiveFetchJob
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ArchiveFetchJobTest implements ArchiveFetchJobListener
{
    private PVItem item;
    private volatile boolean got_anything = false;

    /** Start ArchiveFetchJob, wait for its completion */
    @Test(timeout=60000)
    public void testArchiveFetchJob() throws Exception
    {
        final TestProperties settings = new TestProperties();
        String url = settings.getString("archive_rdb_url");
        if (url == null)
        {
            System.out.println("Skipped");
            return;
        }
        item = new PVItem("DTL_LLRF:IOC1:Load", 1.0);
        item.addArchiveDataSource(new ArchiveDataSource(url, 1, "test"));

        runFetchJob();
    }

    /** Start ArchiveFetchJob for multiple data sources, one that fails, wait for its completion */
    @Test(timeout=60000)
    public void testMultipleArchives() throws Exception
    {
        final TestProperties settings = new TestProperties();
        String url = settings.getString("archive_rdb_url");
        if (url == null)
        {
            System.out.println("Skipped");
            return;
        }
        item = new PVItem("DTL_LLRF:IOC1:Load", 1.0);
        // First URL is expected to fail
        item.addArchiveDataSource(new ArchiveDataSource("Broken_URL", 1, "failed_test"));
        // Second URL should return data
        item.addArchiveDataSource(new ArchiveDataSource(url, 1, "test"));
        runFetchJob();
    }

    private void runFetchJob() throws InterruptedException
    {
        final ITimestamp end = TimestampFactory.now();
        final ITimestamp start = TimestampFactory.fromDouble(end.toDouble() - 10.0*60.0);
        final ArchiveFetchJob job = new ArchiveFetchJob(item, start, end, this);
        assertTrue(got_anything == false);
        System.out.println("Starting the job");
        job.schedule();
        job.join();
        System.out.println("Job exited");
        assertTrue(got_anything);
    }

    @Override
    public void fetchCompleted(final ArchiveFetchJob job)
    {
        System.out.println("Completed " + job);
        final PlotSamples samples = item.getSamples();
        System.out.println(samples);
        got_anything = samples.getSize() > 0;
    }

    @Override
    public void archiveFetchFailed(final ArchiveFetchJob job,
            final ArchiveDataSource archive, final Exception error)
    {
        System.out.print("Received error: ");
        error.printStackTrace(System.out);
    }
}
