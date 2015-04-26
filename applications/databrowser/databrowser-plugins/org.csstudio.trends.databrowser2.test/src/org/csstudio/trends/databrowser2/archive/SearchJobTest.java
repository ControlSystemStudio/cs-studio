/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.archive;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveRepository;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.ChannelInfo;
import org.eclipse.osgi.util.NLS;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the SearchJob
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SearchJobTest
{
    private volatile String info;
    final private CountDownLatch done = new CountDownLatch(1);

    @Test(timeout=20000)
    public void testSearchJob() throws Exception
    {
        final TestProperties settings = new TestProperties();
        String url = settings.getString("archive_rdb_url");
        if (url == null)
        {
            System.out.println("Skipped");
            return;
        }
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
                for (final ChannelInfo channel : channels)
                    System.out.println(channel.getArchiveDataSource().getName() + " - " + channel.getProcessVariable().getName());
                info = "Found " + channels.length + " channels";
                done.countDown();
            }

            @Override
            protected void archiveServerError(final String url, final Exception ex)
            {
                info = NLS.bind(Messages.ArchiveServerErrorFmt, url, ex.getMessage());
                done.countDown();
            }
        }.schedule();

        // Wait for success or error
        done.await();
        System.out.println(info);
        assertThat(info, notNullValue());
        assertTrue(info.startsWith("Found"));
    }
}
