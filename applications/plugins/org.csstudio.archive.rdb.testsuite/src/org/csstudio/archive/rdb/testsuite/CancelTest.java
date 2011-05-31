/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.testsuite;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.SampleIterator;
import org.csstudio.data.values.TimestampFactory;
import org.junit.Test;

/** JUnit Plug-in test (headless)
 *
 *  Must run as plug-in because is uses RDB SCHEMA from preferences.
 *
 *  Try to 'cancel' a long running query.
 *  @author kasemirk@ornl.gov
 */
@SuppressWarnings("nls")
public class CancelTest
{
    @Test
    public void testCancelLongRunningRequest() throws Exception
    {
        final RDBArchive archive =
            RDBArchive.connect(TestSetup.URL, TestSetup.USER, TestSetup.PASSWORD);

        // Thread to simulate user who requests "cancel" after 4 seconds
        final Thread user = new Thread("ImpatientUser")
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(4000);
                    System.out.println("Cancelling...");
                    archive.cancel();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };

        try
        {
            final ChannelConfig[] channels = archive.findChannels("DTL_LLRF:IOC1:Load");
            System.out.println("Starting query...");
            user.start();
            final SampleIterator samples =
                channels[0].getSamples(TimestampFactory.createTimestamp(10, 0),
                                       TimestampFactory.now());
            while (samples.hasNext())
                System.out.println(samples.next());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            archive.close();
        }
    }
}
