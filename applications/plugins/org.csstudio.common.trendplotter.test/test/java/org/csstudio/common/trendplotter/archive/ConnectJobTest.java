/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.archive;

import static org.junit.Assert.fail;

import java.util.concurrent.Semaphore;

import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.common.trendplotter.Messages;
import org.eclipse.osgi.util.NLS;
import org.junit.Ignore;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the ConnectJob
 *  @author Kay Kasemir
 *  FIXME (kasemir) : remove sysos, use assertions, parameterize DB and PV
 */
@SuppressWarnings("nls")
@Ignore("See FIXME")
public class ConnectJobTest
{
    /** Archive data server URL
     *
     *  MUST BE ADAPTED TO YOUR SITE FOR TEST TO SUCCEED!
     */
    final private String url =
        "jdbc:oracle:thin:sns_reports/sns@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=OFF)(ADDRESS=(PROTOCOL=TCP)(HOST=172.31.75.138)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=172.31.75.141)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=ics_prod_lba)))";
//        "xnds://ics-srv-web2.sns.ornl.gov/archive/cgi/ArchiveDataServer.cgi";


    final private Semaphore done = new Semaphore(0);

    @Test
    public void testConnectJob() throws Exception
    {
        new ConnectJob(url)
        {
            @Override
            protected void archiveServerConnected(final ArchiveReader reader,
                    final ArchiveInfo infos[])
            {
                //System.out.println("Connected to " + reader.getServerName());
                //System.out.println(infos.length + " archives:");
                for (final ArchiveInfo info : infos)
                {
                    System.out.println(info.getKey() + ": " + info.getName() + " (" + info.getDescription() + ")");
                }
                done.release();
            }

            @Override
            protected void archiveServerError(final String url, final Exception ex)
            {
                fail(NLS.bind(Messages.ArchiveServerErrorFmt, url, ex.getMessage()));
                done.release();
            }
        }.schedule();

        // Wait for success or error
        done.acquire();
    }

    @Test
    public void testError() throws Exception
    {
        new ConnectJob("bad_url")
        {
            @Override
            protected void archiveServerConnected(final ArchiveReader reader,
                    final ArchiveInfo infos[])
            {
                fail("It connected??");
                done.release();
            }

            @Override
            protected void archiveServerError(final String url, final Exception ex)
            {
                //System.out.println(NLS.bind(Messages.ArchiveServerErrorFmt, url, ex.getMessage()));
                done.release();
            }
        }.schedule();

        // Wait for success or error
        done.acquire();
    }
}
