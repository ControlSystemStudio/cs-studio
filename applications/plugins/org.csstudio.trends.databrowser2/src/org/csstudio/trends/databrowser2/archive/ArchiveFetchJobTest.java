/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.archive;

import static org.junit.Assert.assertTrue;

import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.model.PlotSamples;
import org.junit.Ignore;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of ArchiveFetchJob
 *
 *  @author Kay Kasemir
 *
 *  FIXME (kasemir) : remove sysos, use assertions, parameterize DB and PV
 */
@SuppressWarnings("nls")
@Ignore("See FIXME")
public class ArchiveFetchJobTest implements ArchiveFetchJobListener
{
    private PVItem item;
    private boolean got_anything = false;

    /** Start ArchiveFetchJob, wait for its completion */
    @Test
    public void testArchiveFetchJob() throws Exception
    {
        item = new PVItem("DTL_LLRF:IOC1:Load", 1.0);
        item.addArchiveDataSource(
                new ArchiveDataSource("jdbc:oracle:thin:sns_reports/sns@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=OFF)(ADDRESS=(PROTOCOL=TCP)(HOST=172.31.75.138)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=172.31.75.141)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=ics_prod_lba)))",
                        1, "rdb"));
        final ITimestamp end = TimestampFactory.now();
        final ITimestamp start = TimestampFactory.fromDouble(end.toDouble() - 10.0*60.0);
        final ArchiveFetchJob job = new ArchiveFetchJob(item, start, end, this);
        assertTrue(got_anything == false);
        //System.out.println("Starting the job");
        job.schedule();
        job.join();
        //System.out.println("Job exited");
        assertTrue(got_anything);
    }

    @Override
    public void fetchCompleted(final ArchiveFetchJob job)
    {
        //System.out.println("Completed " + job);
        final PlotSamples samples = item.getSamples();
        //System.out.println(samples);
        assertTrue(samples.getSize() > 0);
        got_anything = true;
    }

    @Override
    public void archiveFetchFailed(final ArchiveFetchJob job,
            final ArchiveDataSource archive, final Exception error)
    {
        error.printStackTrace();
    }
}
