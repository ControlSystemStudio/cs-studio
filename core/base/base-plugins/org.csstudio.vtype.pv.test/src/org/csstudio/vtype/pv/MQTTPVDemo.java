/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.vtype.pv.mqtt.MQTT_PVFactory;
import org.diirt.vtype.VType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** JUnit tests
 *  @author Kay Kasemir
 */
public class MQTTPVDemo implements PVListener
{
    final private CountDownLatch updates = new CountDownLatch(1);

    @Before
    public void setup()
    {
        final Logger root = Logger.getLogger("");
        root.setLevel(Level.FINE);
        for (Handler handler : root.getHandlers())
            handler.setLevel(Level.FINE);

        PVPool.addPVFactory(new MQTT_PVFactory());
    }

    @After
    public void shutdown()
    {
        assertThat(PVPool.getPVReferences().size(), equalTo(0));
    }

    @Test
    public void testNameParser() throws Exception
    {
        //String[] ntv;
        //ntv = ValueHelper.parseName("name(3.14)");
        //assertThat(ntv, equalTo(new String[] {"name", null, "3.14"}));
    }

    /*    @Test
    public void testValueParser() throws Exception
    {
        List<String> items = ValueHelper.splitInitialItems("3.14");
        System.out.println(items);

        items = ValueHelper.splitInitialItems(" 1,  2,   3  ");
        System.out.println(items);
        assertThat(items.size(), equalTo(3));
        assertThat(items.get(0), equalTo("1"));

        items = ValueHelper.splitInitialItems("\"A\", \"2 Apples\"");
        System.out.println(items);
        assertThat(items.size(), equalTo(2));
        assertThat(items.get(1), equalTo("\"2 Apples\""));

        items = ValueHelper.splitInitialItems("\"A\", \" Apples, 2\"");
        System.out.println(items);
        assertThat(items.size(), equalTo(2));
        assertThat(items.get(1), equalTo("\" Apples, 2\""));

        items = ValueHelper.splitInitialItems("\"Text with \\\"Quote\\\"\", \"Text\"");
        System.out.println(items);
        assertThat(items.size(), equalTo(2));
        assertThat(items.get(0), equalTo("\"Text with \\\"Quote\\\"\""));
        assertThat(items.get(1), equalTo("\"Text\""));
    }*/

    @Test
    public void testConnect() throws Exception
    {
        final PV pv1 = PVPool.getPV("mqtt://tcp://localhost:1883");
        final PV pv2 = PVPool.getPV("mqtt://tcp://localhost:1883");
        //System.out.println(pv.read());
        try
        {
            pv1.write("1: " + LocalDateTime.now());
            pv2.write("2: " + LocalDateTime.now());
            fail("Allowed text for number");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        PVPool.releasePV(pv1);
        PVPool.releasePV(pv2);
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
