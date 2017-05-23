/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.vtype.pv.mqtt.MQTT_PVFactory;
import org.diirt.vtype.VDouble;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VLong;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VString;
import org.diirt.vtype.VStringArray;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

/** JUnit tests
 *  @author Megan Grodowitz
 */
@SuppressWarnings("nls")
public class MQTTPVDemo implements PVListener
{
    public class StatusListener implements PVListener
    {
        private volatile boolean is_online;

        StatusListener() { is_online = false; }

        @Override
        public void permissionsChanged(PV pv, boolean readonly)
        {
            System.out.println("Permissions");
        }

        @Override
        public void valueChanged(PV pv, VType value)
        {
            if (!(value instanceof VString))
            {
                System.out.println("Got value with Vtype not VString: " + value.getClass().getName());
                return;
            }

            final String msg = ((VString)value).getValue();

            synchronized(this) {
                if (msg.equals("online"))
                {
                    is_online = true;
                    notify();
                }
                else if (msg.equals("offline"))
                {
                    is_online = false;
                }
            }
        }

        public void wait_for_online()
        {
            synchronized(this)
            {
                while (!is_online)
                {
                    try
                    {
                        wait();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        public boolean is_online() { return is_online; }

        @Override
        public void disconnected(PV pv)
        {
            synchronized(this)
            {
                is_online = false;
            }
        }

    };


    private CountDownLatch updates = new CountDownLatch(1);

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

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Before
    public void setup()
    {
        final Logger root = Logger.getLogger("");
        root.setLevel(Level.INFO);
        for (Handler handler : root.getHandlers())
            handler.setLevel(Level.INFO);

        PVPool.addPVFactory(new MQTT_PVFactory());
    }

    @After
    public void shutdown()
    {
        collector.checkThat(PVPool.getPVReferences().size(), equalTo(0));
    }

    @Test
    public void testMulti() throws Exception
    {
        updates = new CountDownLatch(4);

        final PV pv0 = PVPool.getPV("mqtt://testDoubleA(3.14)");
        final PV pv1 = PVPool.getPV("mqtt://testDoubleB(42)");
        final PV pv2 = PVPool.getPV("mqtt://testDoubleC(1.0101)");
        final PV pv3 = PVPool.getPV("mqtt://testDoubleD(2.0202)");

        pv0.addListener(this);
        pv1.addListener(this);
        pv2.addListener(this);
        pv3.addListener(this);

        updates.await();

        try
        {
            System.out.println(pv0.read());
            System.out.println(pv1.read());
            System.out.println(pv2.read());
            System.out.println(pv3.read());

            collector.checkThat(ValueUtil.numericValueOf(pv0.read()), equalTo(3.14));
            collector.checkThat(ValueUtil.numericValueOf(pv1.read()), equalTo(42));
            collector.checkThat(ValueUtil.numericValueOf(pv2.read()), equalTo(1.0101));
            collector.checkThat(ValueUtil.numericValueOf(pv3.read()), equalTo(2.0202));
        }
        catch (Exception e)
        {
            collector.addError(new Throwable("testDouble pv read/write failure"));
            e.printStackTrace();
        }

        pv0.removeListener(this);
        pv1.removeListener(this);
        pv2.removeListener(this);
        pv3.removeListener(this);

        PVPool.releasePV(pv0);
        PVPool.releasePV(pv1);
        PVPool.releasePV(pv2);
        PVPool.releasePV(pv3);
    }

    @Test
    public void testDouble() throws Exception
    {
        final PV pv = PVPool.getPV("mqtt://testDouble(3.14)");
        pv.addListener(this);
        updates.await();

        updates = new CountDownLatch(1);

        try
        {
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VDouble.class));
            // Accepts string that contains a number
            pv.write("6.28");
            updates.await();
            // Number stays number
            collector.checkThat(pv.read(), instanceOf(VDouble.class));
            collector.checkThat(ValueUtil.numericValueOf(pv.read()), equalTo(6.28));
            try
            {
                pv.write("ten");
                collector.addError(new Throwable("Allowed text for number"));
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        catch (Exception e)
        {
            collector.addError(new Throwable("testDouble pv read/write failure"));
            e.printStackTrace();
        }

        pv.removeListener(this);
        PVPool.releasePV(pv);
    }

    @Test
    public void testNumberArray() throws Exception
    {
        final PV pv = PVPool.getPV("mqtt://testNumArray(1, 2, 3)");
        pv.addListener(this);
        updates.await();
        updates = new CountDownLatch(1);

        try
        {
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VNumberArray.class));

            pv.write(new double[] { 10.5, 20.5, 30.5 });
            updates.await();
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VNumberArray.class));
            collector.checkThat(((VNumberArray)pv.read()).getData().getDouble(1), equalTo(20.5));

            updates = new CountDownLatch(1);
            pv.write(Arrays.asList(1.5, 2.5, 3.5));
            updates.await();
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VNumberArray.class));
            collector.checkThat(((VNumberArray)pv.read()).getData().getDouble(2), equalTo(3.5));

            updates = new CountDownLatch(1);
            pv.write("100, 200, 300");
            updates.await();
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VNumberArray.class));
            collector.checkThat(((VNumberArray)pv.read()).getData().getDouble(1), equalTo(200.0));
        }
        catch (Exception e)
        {
            collector.addError(new Throwable("testNumberArray pv read/write failure"));
            e.printStackTrace();
        }

        pv.removeListener(this);
        PVPool.releasePV(pv);
    }

    @Test
    public void testLong() throws Exception
    {
        final PV pv = PVPool.getPV("mqtt://testLong<VLong>(3e2)");
        pv.addListener(this);
        updates.await();

        try
        {
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VLong.class));
            collector.checkThat(((VLong) pv.read()).getValue(), equalTo(300L));

            // Accepts string that contains a number
            updates = new CountDownLatch(1);
            pv.write("6.28");
            updates.await();
            // Number stays number
            collector.checkThat(pv.read(), instanceOf(VLong.class));
            collector.checkThat(((VLong) pv.read()).getValue(), equalTo(6L));
        }
        catch (Exception e)
        {
            collector.addError(new Throwable("testLong pv read/write failure"));
            e.printStackTrace();
        }

        pv.removeListener(this);
        PVPool.releasePV(pv);
    }

    @Test
    public void testString() throws Exception
    {
        final PV pv = PVPool.getPV("mqtt://testString(\"Fred\")");
        pv.addListener(this);
        updates.await();

        try
        {
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VString.class));
            collector.checkThat(((VString)pv.read()).getValue(), equalTo("Fred"));

            updates = new CountDownLatch(1);
            pv.write("was, here");
            updates.await();
            System.out.println(pv.read());
            collector.checkThat(((VString)pv.read()).getValue(), equalTo("was, here"));

            updates = new CountDownLatch(1);
            pv.write("\"was\", \"here\"");
            updates.await();
            System.out.println(pv.read());
            collector.checkThat(((VString)pv.read()).getValue(), equalTo("was\", \"here"));

            updates = new CountDownLatch(1);
            pv.write("\"Quoted text\"");
            updates.await();
            System.out.println(pv.read());
            collector.checkThat(((VString)pv.read()).getValue(), equalTo("\"Quoted text\""));

            // String stays a string even when value is "3.14"
            updates = new CountDownLatch(1);
            pv.write("3.14");
            updates.await();
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VString.class));
        }
        catch (Exception e)
        {
            collector.addError(new Throwable("testString pv read/write failure"));
            e.printStackTrace();
        }

        pv.removeListener(this);
        PVPool.releasePV(pv);
    }

    @Test
    public void testStringArray() throws Exception
    {
        final PV pv = PVPool.getPV("mqtt://testStringArray(\"Nothing\", \"2 Apples\")");
        try
        {
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VStringArray.class));

            pv.write("\"One, two\"");
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VStringArray.class));
            collector.checkThat(((VStringArray)pv.read()).getData().size(), equalTo(1));

            pv.write(new String[] { "One", "two" });
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VStringArray.class));
            collector.checkThat(((VStringArray)pv.read()).getData().size(), equalTo(2));

            pv.write(Arrays.asList("One", "two"));
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VStringArray.class));
            collector.checkThat(((VStringArray)pv.read()).getData().size(), equalTo(2));
        }
        catch (Exception e)
        {
            collector.addError(new Throwable("testStringArray pv read/write failure"));
            e.printStackTrace();
        }

        PVPool.releasePV(pv);
    }

    @Test
    public void testEnum() throws Exception
    {
        final PV pv = PVPool.getPV("mqtt://testEnum<VEnum>(1, \"Nothing\", \"2 Apples\")");
        try
        {
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VEnum.class));
            collector.checkThat(((VEnum)pv.read()).getValue(), equalTo("2 Apples"));

            pv.write(0);
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VEnum.class));
            collector.checkThat(((VEnum)pv.read()).getValue(), equalTo("Nothing"));

            pv.write("2 Apples");
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VEnum.class));
            collector.checkThat(((VEnum)pv.read()).getIndex(), equalTo(1));
        }
        catch (Exception e)
        {
            collector.addError(new Throwable("testEnum pv read/write failure"));
            e.printStackTrace();
        }

        PVPool.releasePV(pv);
    }

    @Test
    public void testDevice() throws Exception
    {
        final PV pv1 = PVPool.getPV("mqtt://dev/servo/rotation");
        final PV pv2 = PVPool.getPV("mqtt://dev/servo/status<VString>");
        //System.out.println(pv.read());
        final StatusListener stat = new StatusListener();
        pv2.addListener(stat);


        for (int deg = 0; deg <= 180; deg += 20)
        {
            stat.wait_for_online();
            try
            {
                pv1.write(deg);
                //pv2.write("2: " + LocalDateTime.now());
            }
            catch (Exception ex)
            {
                collector.addError(new Throwable("testConnect pv read/write failure"));
                ex.printStackTrace();
            }
            Thread.sleep(5000);
        }

        PVPool.releasePV(pv1);
        PVPool.releasePV(pv2);
    }
}
