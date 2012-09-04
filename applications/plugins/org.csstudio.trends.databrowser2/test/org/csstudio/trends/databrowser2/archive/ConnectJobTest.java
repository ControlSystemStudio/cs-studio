/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.archive;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.trends.databrowser2.Messages;
import org.eclipse.osgi.util.NLS;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the {@link ConnectJob}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ConnectJobTest
{
    private volatile String info = null;
    final private CountDownLatch done = new CountDownLatch(1);

    @Test(timeout=20000)
    public void testConnectJob() throws Exception
    {
        final TestProperties settings = new TestProperties();
        String url = settings.getString("archive_rdb_url");
        if (url == null)
        {
            System.out.println("Skipped");
            return;
        }
        System.out.println("Connecting to " + url);
        new ConnectJob(url)
        {
            @Override
            protected void archiveServerConnected(final ArchiveReader reader,
                    final ArchiveInfo infos[])
            {
                System.out.println("Connected to " + reader.getServerName());
                System.out.println(infos.length + " archives:");
                for (final ArchiveInfo info : infos)
                {
                    System.out.println(info.getKey() + ": " + info.getName() + " (" + info.getDescription() + ")");
                }
                info = "Connected";
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
        assertThat(info, equalTo("Connected"));
    }

    @Test(timeout=10000)
    public void testError() throws Exception
    {
        new ConnectJob("bad_url")
        {
            @Override
            protected void archiveServerConnected(final ArchiveReader reader,
                    final ArchiveInfo infos[])
            {
                info = "Connected to bad URL?";
                done.countDown();
            }

            @Override
            protected void archiveServerError(final String url, final Exception ex)
            {
                System.out.println(NLS.bind(Messages.ArchiveServerErrorFmt, url, ex.getMessage()));
                info = "Failed as expected";
                done.countDown();
            }
        }.schedule();

        // Wait for success or error
        done.await();
        assertThat(info, equalTo("Failed as expected"));
    }
}
