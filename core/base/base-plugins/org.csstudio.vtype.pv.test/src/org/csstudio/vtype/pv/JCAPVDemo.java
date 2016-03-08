/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.csstudio.vtype.pv.jca.JCA_PV;
import org.csstudio.vtype.pv.jca.JCA_PVFactory;
import org.diirt.vtype.VEnumArray;
import org.diirt.vtype.VStringArray;
import org.diirt.vtype.VType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/** JUnit tests
 *
 *  <p>These require a softIoc with
 *  org.csstudio.scan/examples/*.db
 *  and org.csstudio.vtype.pv.test/examples/test.db
 *  @author Kay Kasemir
 */
public class JCAPVDemo implements PVListener
{
    private static final String NETWORK = "127.0.0.1 webopi.sns.gov:5066";
    private static final int MAX_ARRAY = 20000;
    final private CountDownLatch updates = new CountDownLatch(1);

    @Before
    public void setup()
    {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s [%1$tc]%n");
        final Logger root = Logger.getLogger("");
        root.setLevel(Level.FINE);
        for (Handler handler : root.getHandlers())
        {
            handler.setLevel(Level.FINE);
            handler.setFormatter(new SimpleFormatter());
        }

        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", NETWORK);
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");

        System.setProperty("gov.aps.jca.jni.JNIContext.addr_list", NETWORK);
        System.setProperty("gov.aps.jca.jni.JNIContext.auto_addr_list", "false");

        System.setProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes", Integer.toString(MAX_ARRAY));
        System.setProperty("gov.aps.jca.jni.JNIContext.max_array_bytes", Integer.toString(MAX_ARRAY));

        final PVFactory factory = new JCA_PVFactory();
        PVPool.addPVFactory(factory);
    }

    @Test
    public void testPool() throws Exception
    {
        // Create PV
        final PV pv1 = PVPool.getPV("motor_x");
        // Reference the same PV
        final PV pv2 = PVPool.getPV("motor_x");
        final PV pv3 = PVPool.getPV("motor_x");
        assertThat(pv1, sameInstance(pv2));
        assertThat(pv2, sameInstance(pv3));
        // Release all
        PVPool.releasePV(pv3);
        PVPool.releasePV(pv2);
        PVPool.releasePV(pv1);

        // Create _new_ PV
        final PV pv4 = PVPool.getPV("motor_x");
        assertThat(pv1, not(sameInstance(pv4)));
        PVPool.releasePV(pv4);
    }

    @Test
    public void testPrefix() throws Exception
    {
        // Create PV
        final PV pv1 = PVPool.getPV("ca://motor_x");
        assertThat(pv1.getName(), equalTo("ca://motor_x"));

        // Reference the same PV
        final PV pv2 = PVPool.getPV("ca://motor_x");
        assertThat(pv1, sameInstance(pv2));
        PVPool.releasePV(pv2);

        // Different name because of prefix
        final PV pv3 = PVPool.getPV("motor_x");
        assertThat(pv3.getName(), equalTo("motor_x"));
        assertThat(pv1, not(sameInstance(pv3)));
        PVPool.releasePV(pv3);

        PVPool.releasePV(pv1);
    }

    @Test(timeout=5000)
    public void testBasicRead() throws Exception
    {
        final PV pv = PVPool.getPV("motor_x");
        pv.addListener(this);
        updates.await();
        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }

    @Test(timeout=5000)
    public void testPlainRead() throws Exception
    {
        final PV pv = PVPool.getPV("motor_x.PREC");
        pv.addListener(this);
        updates.await();
        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }

    @Test(timeout=5000)
    public void testShortArrayRead() throws Exception
    {
        final PV pv = PVPool.getPV("charge_hist");
        pv.addListener(this);
        updates.await();
        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }

    @Test//(timeout=5000)
    public void testEnumRead() throws Exception
    {
        final PV pv = PVPool.getPV("motor_x.SCAN");
        pv.addListener(this);
        updates.await();
        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }

    @Test(timeout=5000)
    public void testEnumArray() throws Exception
    {
        final PV pv = PVPool.getPV("TestEnumArray");
        pv.addListener(this);
        updates.await();

        // Write known value
        pv.asyncWrite(new short[] { 4, 3, 2, 1 }).get(5, TimeUnit.SECONDS);
        // Check readback
        final VType value = pv.asyncRead().get(5, TimeUnit.SECONDS);
        assertThat(value, instanceOf(VEnumArray.class));
        final VEnumArray enum_val = (VEnumArray) value;
        System.out.println(enum_val);
        assertThat(enum_val.getData().size(), equalTo(10));
        assertThat(enum_val.getIndexes().getInt(0), equalTo(4));
        assertThat(enum_val.getIndexes().getInt(3), equalTo(1));
        // Write other value, so next time the test runs it'll start with this
        pv.asyncWrite(new short[] { 10, 20, 30, 40 }).get(5, TimeUnit.SECONDS);

        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }

    @Test//(timeout=5000)
    public void testStringArray() throws Exception
    {
        final PV pv = PVPool.getPV("TestStringArray");
        pv.addListener(this);
        updates.await();

        // Write known value
        pv.asyncWrite(new String[] { "Hi", "there" }).get(5, TimeUnit.SECONDS);
        // Check readback
        final VType value = pv.asyncRead().get(5, TimeUnit.SECONDS);
        assertThat(value, instanceOf(VStringArray.class));
        final VStringArray str_val = (VStringArray) value;
        System.out.println(str_val);
        assertThat(str_val.getData().size(), equalTo(10));
        assertThat(str_val.getData().get(0), equalTo("Hi"));
        assertThat(str_val.getData().get(1), equalTo("there"));
        // Write other value, so next time the test runs it'll start with this
        pv.asyncWrite(new String[] { "Bye", "for", "now" }).get(5, TimeUnit.SECONDS);

        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }

    @Test(timeout=5000)
    public void testReadWithCallback() throws Exception
    {
        final JCA_PV pv = (JCA_PV) PVPool.getPV("motor_x");
        // A read _right away_ will fail because the channel is not connected
        try
        {
            pv.asyncRead();
        }
        catch (Exception ex)
        {
            System.out.println("Waiting for channel to connect");
        }
        while (pv.read() == null)
            Thread.sleep(10);
        System.out.println("Connected");

        for (int i=0; i<10; ++i)
        {
            final Future<VType> value = pv.asyncRead();
            if (value.isDone())
                System.out.println("Done right away??");
            System.out.println(value.get(1, TimeUnit.SECONDS));
            assertThat(value.isDone(), equalTo(true));
        }

        PVPool.releasePV(pv);
        System.out.println("Done.");
    }

    @Test(timeout=5000)
    public void testRTYP() throws Exception
    {
        final PV pv = PVPool.getPV("motor_x.RTYP");
        pv.addListener(this);
        updates.await();
        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }

    @Test(timeout=5000)
    public void testBasicWrite() throws Exception
    {
        final PV pv = PVPool.getPV("motor_x");
        pv.addListener(this);
        updates.await();

        pv.write(4.0);

        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }

    @Test(timeout=5000)
    public void testAsyncWrite() throws Exception
    {
        final PV pv = PVPool.getPV("callback_test");
        pv.addListener(this);
        updates.await();

        long start = System.currentTimeMillis();
        final Future<?> result = pv.asyncWrite(6.0);
        result.get();
        long end = System.currentTimeMillis();
        double seconds = (end - start) / 1000.0;
        System.out.println("Write-callback took " + seconds + " seconds");
        assertTrue(Math.abs(seconds - 4.0) < 1.0);

        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }

    @Override
    public void permissionsChanged(PV pv, boolean readonly)
    {
        System.out.println("Permissions");
    }

    @Override
    public void valueChanged(PV pv, VType value)
    {
        System.out.println("Update: " + value);
        updates.countDown();
    }

    @Override
    public void disconnected(PV pv)
    {
        System.out.println("Disconnected");
    }
}
