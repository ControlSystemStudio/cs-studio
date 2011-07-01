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

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveRepository;
import org.csstudio.common.trendplotter.Messages;
import org.csstudio.common.trendplotter.model.ArchiveDataSource;
import org.csstudio.common.trendplotter.model.ChannelInfo;
import org.eclipse.osgi.util.NLS;
import org.junit.Ignore;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the SearchJob
 *  @author Kay Kasemir
 *  FIXME (kasemir) : remove sysos, use assertions, parameterize DB and PV
 */
@SuppressWarnings("nls")
@Ignore("See FIXME")
public class SearchJobTest
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
    public void testSearchJob() throws Exception
    {
        final ArchiveReader reader =
            ArchiveRepository.getInstance().getArchiveReader(url);
        final ArchiveDataSource archives[] = new ArchiveDataSource[]
        {
            new ArchiveDataSource(url, 1, ""),
        };
        new SearchJob(reader, archives, "DTL_LLRF:FCM1:*", true)
        {
            @Override
            protected void receivedChannelInfos(final ChannelInfo[] channels)
            {
                System.out.println("Found these channels:");
                for (final ChannelInfo channel : channels) {
                    System.out.println(channel.getArchiveDataSource().getName() + " - " + channel.getProcessVariable().getName());
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
}
