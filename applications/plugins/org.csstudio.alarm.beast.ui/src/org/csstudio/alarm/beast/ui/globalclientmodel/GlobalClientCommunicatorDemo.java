/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import org.csstudio.alarm.beast.ui.clientmodel.AlarmUpdateInfo;
import org.csstudio.apputil.test.TestProperties;
import org.junit.Test;

/** JUnit demo of the {@link GlobalAlarmCommunicator}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GlobalClientCommunicatorDemo
{
    @Test
    public void testGlobalClientCommunicator() throws Exception
    {
        final TestProperties settings = new TestProperties();
        final String url = settings.getString("alarm_jms_url");
        if (url == null)
        {
            System.out.println("Need test URL, skipping test");
            return;
        }

        final GlobalAlarmCommunicator communicator = new GlobalAlarmCommunicator(url)
        {
            @Override
            void handleAlarmUpdate(final AlarmUpdateInfo info)
            {
                System.out.println("Received global update " + info);
            }
        };
        communicator.start();

        Thread.sleep(20*1000l);

        communicator.stop();
    }
}
