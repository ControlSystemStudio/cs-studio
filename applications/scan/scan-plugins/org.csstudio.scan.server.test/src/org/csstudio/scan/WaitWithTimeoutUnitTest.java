/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.csstudio.utility.test.HamcrestMatchers.*;

import java.util.concurrent.TimeoutException;

import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.scan.condition.WaitWithTimeout;
import org.epics.util.time.TimeDuration;
import org.junit.Test;

/** JUnit test of the {@link WaitWithTimeout}
 *  @author Kay Kasemir
 */
public class WaitWithTimeoutUnitTest
{
    @Test(timeout=5000)
    public void testTimeout() throws Exception
    {
        final WaitWithTimeout timeout = new WaitWithTimeout(TimeDuration.ofSeconds(2.0));

        BenchmarkTimer timer = new BenchmarkTimer();
        try
        {
            synchronized (this)
            {
                while (timeout != null) // Example for a condition on which to wait...
                    if (timeout.waitUntilTimeout(this))
                        throw new TimeoutException("Timeout has expired");
            }
            fail("Did not time out");
        }
        catch (TimeoutException ex)
        {
            timer.stop();
            System.out.println("Received expected timeout after " + timer);
            assertThat(Math.abs(2.0 - timer.getSeconds()), lessThan(0.2));
        }

        // Further attempts to wait should fail right away
        synchronized (this)
        {
            timer = new BenchmarkTimer();
            assertThat(timeout.waitUntilTimeout(this), equalTo(true));
            timer.stop();
            assertThat(timer.getSeconds(), lessThan(0.2));
        }
    }
}
