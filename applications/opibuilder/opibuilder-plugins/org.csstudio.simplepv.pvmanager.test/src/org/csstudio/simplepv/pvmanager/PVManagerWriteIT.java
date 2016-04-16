/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.simplepv.pvmanager;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.simplepv.SimplePVLayer;
import org.csstudio.simplepv.VTypeHelper;
import org.diirt.vtype.VType;
import org.junit.Test;

/** JUnit test for writing with PVManagerPVFactory
 *
 *  <p>Directly accesses PVManagerPVFactory to run as plain JUnit test.
 *  CSS code should use {@link SimplePVLayer}
 *  @author Kay Kasemir
 */
public class PVManagerWriteIT extends TestHelper
{
    /** Read from PV, allow waiting for desired value update */
    class TestReader extends IPVListener.Stub
    {
        final private IPV pv;
        private VType value = null;

        public TestReader(final String name) throws Exception
        {
            pv = factory.createPV(name);
            pv.addListener(this);
            pv.start();
        }

        @Override
        public synchronized void valueChanged(IPV pv)
        {
            value = pv.getValue();
            System.out.println(pv.getName() + " = " + value);
            notifyAll();
        }

        public synchronized void waitFor(final double desired_value) throws Exception
        {
            for (int seconds=5;  seconds>=0;  --seconds)
            {
                final double num = VTypeHelper.getDouble(value);
                if (num == desired_value)
                    return;
                if (seconds > 0)
                    wait(TimeUnit.SECONDS.toMillis(1));
                else
                    assertThat(num, equalTo(desired_value)); // Fail
            }
        }

        public void stop()
        {
            pv.stop();
        }
    }

    /** Write, check separate readback PV */
    @Test
    public void testBasicWriting() throws Exception
    {
        final TestReader readback = new TestReader("loc://pv(3)");
        readback.waitFor(3.0);

        final IPV pv = factory.createPV("loc://pv(3)");

        pv.start();
        TestHelper.waitForConnection(pv);

        pv.setValue(4.0);
        readback.waitFor(4.0);

        pv.stop();
        readback.stop();
    }

    /** Check read-only state */
    @Test
    public void testReadonly() throws Exception
    {
        IPV pv = factory.createPV("sim://sine");
        pv.start();
        TestHelper.waitForConnection(pv);
        assertThat(pv.isWriteAllowed(), equalTo(false));
        pv.stop();

        pv = factory.createPV("loc://pv(0)");
        pv.start();
        TestHelper.waitForConnection(pv);
        assertThat(pv.isWriteAllowed(), equalTo(true));
        pv.stop();
    }

    /** Write, check 'done' event as well as value update */
    @Test
    public void testWriteListener() throws Exception
    {
        final IPV pv = factory.createPV("loc://pv(3)");

        // Expect one 'write' confirmation
        final CountDownLatch written = new CountDownLatch(1);
        // Expect initial value and the written update
        final CountDownLatch updates = new CountDownLatch(2);

        pv.addListener(new IPVListener()
        {
            @Override
            public void connectionChanged(final IPV pv)
            {
            }

            @Override
            public void exceptionOccurred(final IPV pv, final Exception exception)
            {
                exception.printStackTrace();
                fail("Received exception");
            }

            @Override
            public void valueChanged(final IPV pv)
            {
                final VType value = pv.getValue();
                System.out.println(pv.getName() + " = " + value);
                if (value != null)
                    updates.countDown();
            }

            @Override
            public void writeFinished(final IPV pv, final boolean isWriteSucceeded)
            {
                if (! isWriteSucceeded)
                    fail("Write failed");
                written.countDown();
            }

            @Override
            public void writePermissionChanged(final IPV pv)
            {
            }
        });
        pv.start();
        TestHelper.waitForConnection(pv);

        // Await initial value
        for (int seconds=TIMEOUT_SECONDS;  seconds>=0;  --seconds)
        {
            if (updates.getCount() == 1)
                break;
            TimeUnit.SECONDS.sleep(1);
        }
        assertThat(updates.getCount(), equalTo(1L));

        pv.setValue(4.0);
        assertThat(written.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), equalTo(true));
        assertThat(updates.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), equalTo(true));

        pv.stop();
    }
}
