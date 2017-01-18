/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.csstudio.vtype.pv.RefCountMap.ReferencedEntry;
import org.csstudio.vtype.pv.local.LocalPV;
import org.csstudio.vtype.pv.local.LocalPVFactory;
import org.csstudio.vtype.pv.local.ValueHelper;
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
import org.junit.Test;

/** JUnit tests
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LocalPVTest implements PVListener
{
    final private CountDownLatch updates = new CountDownLatch(1);

    private String last_message;

    @Before
    public void setup()
    {
        final Logger root = Logger.getLogger("");
        root.setLevel(Level.FINE);
        for (Handler handler : root.getHandlers())
            handler.setLevel(Level.FINE);
        root.addHandler(new Handler()
        {
            @Override
            public void publish(LogRecord record)
            {
                last_message = record.getMessage();
            }

            @Override
            public void flush()
            {
                // NOP
            }

            @Override
            public void close() throws SecurityException
            {
                // NOP
            }
        });

        PVPool.addPVFactory(new LocalPVFactory());
    }

    @After
    public void shutdown()
    {
        final Collection<LocalPV> locs = LocalPVFactory.getLocalPVs();
        if (! locs.isEmpty())
            System.out.println("Local PVs at shutdown: " + locs);

        final Collection<ReferencedEntry<PV>> pvs = PVPool.getPVReferences();
        if (! pvs.isEmpty())
            System.out.println("PVs at shutdown: " + pvs);

        assertThat(locs.size(), equalTo(0));
        assertThat(pvs.size(), equalTo(0));
    }

    @Test
    public void testNameParser() throws Exception
    {
        String[] ntv;
        ntv = ValueHelper.parseName("name(3.14)");
        assertThat(ntv, equalTo(new String[] {"name", null, "3.14"}));

        ntv = ValueHelper.parseName("name");
        assertThat(ntv, equalTo(new String[] {"name", null, null}));

        ntv = ValueHelper.parseName("name<VDouble>(3.14)");
        assertThat(ntv, equalTo(new String[] {"name", "VDouble", "3.14"}));

        ntv = ValueHelper.parseName("name<VStringArray>(\"a\", \"b\", \"c\")");
        assertThat(ntv, equalTo(new String[] {"name", "VStringArray", "\"a\", \"b\", \"c\""}));
    }

    @Test
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
    }

    @Test
    public void testDouble() throws Exception
    {
        final PV pv = PVPool.getPV("name(3.14)");
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VDouble.class));
        // Accepts string that contains a number
        pv.write("6.28");
        // Number stays number
        assertThat(pv.read(), instanceOf(VDouble.class));
        assertThat(ValueUtil.numericValueOf(pv.read()), equalTo(6.28));
        try
        {
            pv.write("ten");
            fail("Allowed text for number");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        PVPool.releasePV(pv);
    }

    @Test
    public void testLong() throws Exception
    {
        final PV pv = PVPool.getPV("name<VLong>(3e2)");
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VLong.class));
        assertThat(((VLong) pv.read()).getValue(), equalTo(300L));

        // Accepts string that contains a number
        pv.write("6.28");
        // Number stays number
        assertThat(pv.read(), instanceOf(VLong.class));
        assertThat(((VLong) pv.read()).getValue(), equalTo(6L));

        PVPool.releasePV(pv);
    }

    @Test
    public void testString() throws Exception
    {
        final PV pv = PVPool.getPV("name(\"Fred\")");
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VString.class));
        assertThat(((VString)pv.read()).getValue(), equalTo("Fred"));

        pv.write("was here");
        System.out.println(pv.read());
        assertThat(((VString)pv.read()).getValue(), equalTo("was here"));

        pv.write("\"Quoted text\"");
        System.out.println(pv.read());
        assertThat(((VString)pv.read()).getValue(), equalTo("\"Quoted text\""));

        // String stays a string even when value is "3.14"
        pv.write("3.14");
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VString.class));

        PVPool.releasePV(pv);
    }

    @Test
    public void testNumberArray() throws Exception
    {
        final PV pv = PVPool.getPV("name(1, 2, 3)");
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VNumberArray.class));

        pv.write(new double[] { 10.5, 20.5, 30.5 });
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VNumberArray.class));
        assertThat(((VNumberArray)pv.read()).getData().getDouble(1), equalTo(20.5));

        pv.write(Arrays.asList(1.5, 2.5, 3.5));
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VNumberArray.class));
        assertThat(((VNumberArray)pv.read()).getData().getDouble(2), equalTo(3.5));

        pv.write("100, 200, 300");
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VNumberArray.class));
        assertThat(((VNumberArray)pv.read()).getData().getDouble(1), equalTo(200.0));

        PVPool.releasePV(pv);
    }

    @Test
    public void testStringArray() throws Exception
    {
        final PV pv = PVPool.getPV("name(\"Nothing\", \"2 Apples\")");
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VStringArray.class));

        pv.write("One, two");
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VStringArray.class));
        assertThat(((VStringArray)pv.read()).getData().size(), equalTo(1));

        pv.write(new String[] { "One", "two" });
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VStringArray.class));
        assertThat(((VStringArray)pv.read()).getData().size(), equalTo(2));

        pv.write(Arrays.asList("One", "two"));
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VStringArray.class));
        assertThat(((VStringArray)pv.read()).getData().size(), equalTo(2));


        PVPool.releasePV(pv);
    }

    @Test
    public void testEnum() throws Exception
    {
        PV pv = PVPool.getPV("name<VEnum>(1, \"Nothing\", \"2 Apples\")");
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VEnum.class));
        assertThat(((VEnum)pv.read()).getValue(), equalTo("2 Apples"));

        pv.write(0);
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VEnum.class));
        assertThat(((VEnum)pv.read()).getValue(), equalTo("Nothing"));

        pv.write("2 Apples");
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VEnum.class));
        assertThat(((VEnum)pv.read()).getIndex(), equalTo(1));

        PVPool.releasePV(pv);


        // Request PV several times with different initializers
        pv = PVPool.getPV("name<VEnum>(1, One, Two)");
        System.out.println(pv.read());

        // Creates a warning "was initialized as .. and is now requested as .."
        PV pv2 = PVPool.getPV("name<VEnum>(0, Uno, Due)");
        System.out.println(pv2.read());
        // The value stays with the original enum index and labels
        assertThat(pv2.read(), instanceOf(VEnum.class));
        final VEnum value = (VEnum) pv2.read();
        assertThat(value.getIndex(), equalTo(1));
        assertThat(value.getLabels(), equalTo(Arrays.asList("One", "Two")));
        PVPool.releasePV(pv2);

        PVPool.releasePV(pv);
    }

    @Test(timeout=5000)
    public void testReferences() throws Exception
    {
        // No initialization is same as <VDouble>(0)
        final PV pv1 = PVPool.getPV("x(0)");
        final PV pv2 = PVPool.getPV("x<VDouble>(0)");
        assertThat(pv1, sameInstance(pv2));
        PVPool.releasePV(pv2);

        // Different initial value still results in same PV
        last_message = null;
        final PV pv3 = PVPool.getPV("x(1)");
        assertThat(pv3, sameInstance(pv1));
        // Check that warning was issued
        assertThat(last_message, containsString("was initialized as"));

        pv1.write(10);
        assertThat(ValueUtil.numericValueOf(pv1.read()), equalTo(10.0));
        assertThat(ValueUtil.numericValueOf(pv3.read()), equalTo(10.0));

        pv3.write(30);
        assertThat(ValueUtil.numericValueOf(pv1.read()), equalTo(30.0));
        assertThat(ValueUtil.numericValueOf(pv3.read()), equalTo(30.0));

        PVPool.releasePV(pv3);
        PVPool.releasePV(pv1);
    }

    @Test(timeout=5000)
    public void testBasicRead() throws Exception
    {
        final PV pv = PVPool.getPV("x(3.14)");
        pv.addListener(this);
        updates.await();
        pv.removeListener(this);
        assertThat(ValueUtil.numericValueOf(pv.read()), equalTo(3.14));
        PVPool.releasePV(pv);
    }

    @Test // (timeout=5000)
    public void testCallbacks() throws Exception
    {
        final PV pv = PVPool.getPV("x(3.14)");

        // Async write should be 'immediate'
        long start = System.currentTimeMillis();
        Future<?> result = pv.asyncWrite(47.11);
        result.get();
        long end = System.currentTimeMillis();
        double seconds = (end - start) / 1000.0;
        System.out.println("Write-callback took " + seconds + " seconds");
        assertTrue(seconds < 0.5);

        // Async read should be 'immediate'
        start = System.currentTimeMillis();
        final VType value = pv.asyncRead().get();
        end = System.currentTimeMillis();
        seconds = (end - start) / 1000.0;
        System.out.println("Read-callback took " + seconds + " seconds");
        assertTrue(seconds < 0.5);

        assertThat(ValueUtil.numericValueOf(value), equalTo(47.11));
        PVPool.releasePV(pv);
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
