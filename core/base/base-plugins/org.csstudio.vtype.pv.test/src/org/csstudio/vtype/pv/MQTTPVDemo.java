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
 *  @author Kay Kasemir
 */
public class MQTTPVDemo implements PVListener
{
    final private CountDownLatch updates = new CountDownLatch(1);

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
    public void testDouble() throws Exception
    {
        final PV pv = PVPool.getPV("mqtt://testDouble(3.14)");

        try
        {
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VDouble.class));
            // Accepts string that contains a number
            pv.write("6.28");
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

        PVPool.releasePV(pv);
    }

    @Test
    public void testNumberArray() throws Exception
    {
        final PV pv = PVPool.getPV("mqtt://testNumArray(1, 2, 3)");

        try
        {
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VNumberArray.class));

            pv.write(new double[] { 10.5, 20.5, 30.5 });
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VNumberArray.class));
            collector.checkThat(((VNumberArray)pv.read()).getData().getDouble(1), equalTo(20.5));

            pv.write(Arrays.asList(1.5, 2.5, 3.5));
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VNumberArray.class));
            collector.checkThat(((VNumberArray)pv.read()).getData().getDouble(2), equalTo(3.5));

            pv.write("100, 200, 300");
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VNumberArray.class));
            collector.checkThat(((VNumberArray)pv.read()).getData().getDouble(1), equalTo(200.0));
        }
        catch (Exception e)
        {
            collector.addError(new Throwable("testNumberArray pv read/write failure"));
            e.printStackTrace();
        }

        PVPool.releasePV(pv);
    }

    @Test
    public void testLong() throws Exception
    {
        final PV pv = PVPool.getPV("mqtt://testLong<VLong>(3e2)");

        try
        {
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VLong.class));
            collector.checkThat(((VLong) pv.read()).getValue(), equalTo(300L));

            // Accepts string that contains a number
            pv.write("6.28");
            // Number stays number
            collector.checkThat(pv.read(), instanceOf(VLong.class));
            collector.checkThat(((VLong) pv.read()).getValue(), equalTo(6L));
        }
        catch (Exception e)
        {
            collector.addError(new Throwable("testLong pv read/write failure"));
            e.printStackTrace();
        }

        PVPool.releasePV(pv);
    }

    @Test
    public void testString() throws Exception
    {
        final PV pv = PVPool.getPV("mqtt://testString(\"Fred\")");
        try
        {
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VString.class));
            collector.checkThat(((VString)pv.read()).getValue(), equalTo("Fred"));

            pv.write("was here");
            System.out.println(pv.read());
            collector.checkThat(((VString)pv.read()).getValue(), equalTo("was here"));

            pv.write("\"Quoted text\"");
            System.out.println(pv.read());
            collector.checkThat(((VString)pv.read()).getValue(), equalTo("\"Quoted text\""));

            // String stays a string even when value is "3.14"
            pv.write("3.14");
            System.out.println(pv.read());
            collector.checkThat(pv.read(), instanceOf(VString.class));
        }
        catch (Exception e)
        {
            collector.addError(new Throwable("testString pv read/write failure"));
            e.printStackTrace();
        }

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
    public void testConnect() throws Exception
    {
        //Double Value
        final PV pv1 = PVPool.getPV("mqtt://test");
        //String Value
        //final PV pv2 = PVPool.getPV("mqtt://MQTTPVDemo/pv2<VString>");
        //System.out.println(pv.read());

        try
        {
            pv1.write("73.14");
            //pv2.write("2: " + LocalDateTime.now());
        }
        catch (Exception ex)
        {
            collector.addError(new Throwable("testConnect pv read/write failure"));
            ex.printStackTrace();
        }

        PVPool.releasePV(pv1);
        //PVPool.releasePV(pv2);
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
