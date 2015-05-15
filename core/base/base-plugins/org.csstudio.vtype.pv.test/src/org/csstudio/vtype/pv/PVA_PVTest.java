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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.vtype.pv.pva.PVA_Context;
import org.csstudio.vtype.pv.pva.PVA_PVFactory;
import org.epics.vtype.VEnum;
import org.epics.vtype.VString;
import org.epics.vtype.VType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/** JUnit demo of the PVA_PV
 *
 *  <p>Requires PVs from V4 pvDatabaseCPP/exampleServer/iocBoot/exampleServer
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVA_PVTest
{
    private static PVFactory factory;

    @BeforeClass
    public static void setup()
    {
        // Set log level
        final Level level = Level.CONFIG;
        final Logger logger = Logger.getLogger("");
        logger.setLevel(level);
        for (Handler handler : logger.getHandlers())
            handler.setLevel(level);

        // Not using PVPool, but directly creating factory
        factory = new PVA_PVFactory();
    }

    @AfterClass
    public static void shutdown() throws Exception
    {
        // In real application, the singleton context would remain open
        PVA_Context.getInstance().close();
    }

    @Test(timeout=10000)
    public void testValueUpdates() throws Exception
    {
        for (String type_name[] : new String[][]
            {
                { "Double", "pvdouble" },
                { "Long",   "pvdouble.RVAL" },
                { "Enum",   "pvdouble.SCAN" },
                { "String", "pvdouble.DESC" },
                { "Byte",   "pvdouble.UDF" },
                { "String", "pvdouble.RTYP" },
                { "Enum",   "pvdouble.DTYP" },
                { "Double Array",  "pvdoubleArray" },
                { "String Array",  "pvstringArray" },

                // Fails in C++ 'pvget -m' as well with
                // "can not monitor a link field".
                // TODO Issue initial get?
                // { "String", "pvcounter.INPA" },
            })
        {
            final String type = type_name[0];
            final String name = type_name[1];
            System.out.println("Testing updates for type " + type + " PV " + name + ":");
            final CountDownLatch updates = new CountDownLatch(1);
            final PV pv = factory.createPV(name, name);
            final PVListener listener = new PVListenerAdapter()
            {
                @Override
                public void valueChanged(final PV pv, final VType value)
                {
                    System.out.println(pv.getName() + " value changed to " + value);
                    updates.countDown();
                }

                @Override
                public void disconnected(final PV pv)
                {
                    System.out.println(pv.getName() + " closed");
                }
            };
            pv.addListener(listener);
            updates.await();
            pv.close();
        }
    }

    final private static String name = "pvdouble";

    @Test(timeout=10000)
    public void testWriteString() throws Exception
    {
        final PV pv = factory.createPV("pva://" + name + ".DESC", name + ".DESC");

        Future<?> written = pv.asyncWrite("Hi");
        written.get(2000000, TimeUnit.SECONDS);

        Future<VType> get_current = pv.asyncRead();
        VType current = get_current.get();
        System.out.println(current);
        assertThat(current, instanceOf(VString.class));
        assertThat("Hi", equalTo( ((VString)current).getValue() ));

        written = pv.asyncWrite("There");
        written.get(200000, TimeUnit.SECONDS);

        get_current = pv.asyncRead();
        current = get_current.get();
        System.out.println(current);
        assertThat(current, instanceOf(VString.class));
        assertThat("There", equalTo( ((VString)current).getValue() ));

        pv.close();
    }

    @Test(timeout=10000)
    public void testWriteEnum() throws Exception
    {
        final PV pv = factory.createPV("pva://" + name + ".SCAN", name + ".SCAN");

        // .. as number
        Future<?> written = pv.asyncWrite(5);
        written.get(2000000, TimeUnit.SECONDS);

        Future<VType> get_current = pv.asyncRead();
        VType current = get_current.get();
        System.out.println(current);
        assertThat(current, instanceOf(VEnum.class));
        assertThat(5, equalTo( ((VEnum)current).getIndex() ));

        written = pv.asyncWrite(6);
        written.get(200000, TimeUnit.SECONDS);

        get_current = pv.asyncRead();
        current = get_current.get();
        System.out.println(current);
        assertThat(current, instanceOf(VEnum.class));
        assertThat(6, equalTo( ((VEnum)current).getIndex() ));

        // as label
        written = pv.asyncWrite("2 second");
        written.get(2000000, TimeUnit.SECONDS);

        get_current = pv.asyncRead();
        current = get_current.get();
        System.out.println(current);
        assertThat(current, instanceOf(VEnum.class));
        assertThat("2 second", equalTo( ((VEnum)current).getValue() ));

        written = pv.asyncWrite("1 second");
        written.get(200000, TimeUnit.SECONDS);

        get_current = pv.asyncRead();
        current = get_current.get();
        System.out.println(current);
        assertThat(current, instanceOf(VEnum.class));
        assertThat("1 second", equalTo( ((VEnum)current).getValue() ));

        pv.close();
    }

    @Test(timeout=10000)
    public void testAsynRead() throws Exception
    {
        final PV pv = factory.createPV(name, name);

        for (int i=0; i<10; ++i)
        {
            final Future<VType> result = pv.asyncRead();
            final VType value = result.get();
            System.out.println(pv + "  " + pv.read());
            assertThat(value, not(nullValue()));
        }

        pv.close();
    }
}
