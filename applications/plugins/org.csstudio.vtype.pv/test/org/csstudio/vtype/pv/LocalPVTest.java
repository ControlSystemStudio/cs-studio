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
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.vtype.pv.local.LocalPVFactory;
import org.epics.vtype.VString;
import org.epics.vtype.VType;
import org.epics.vtype.ValueUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** JUnit tests
 *  @author Kay Kasemir
 */
public class LocalPVTest implements PVListener
{
    final private CountDownLatch updates = new CountDownLatch(1);
    
    @Before
    public void setup()
    {
        final Logger root = Logger.getLogger("");
        root.setLevel(Level.FINE);
        for (Handler handler : root.getHandlers())
            handler.setLevel(Level.FINE);
        
        PVPool.addPVFactory(new LocalPVFactory());
    }
    
    @After
    public void shutdown()
    {
        assertThat(PVPool.getPVReferences().size(), equalTo(0));
    }

    @Test(timeout=5000)
    public void testReferences() throws Exception
    {
        final PV pv1 = PVPool.getPV("x(0)");
        final PV pv2 = PVPool.getPV("x(0)");
        assertThat(pv1, sameInstance(pv2));
        PVPool.releasePV(pv2);

        // Different initial value means different PV
        final PV pv3 = PVPool.getPV("x(1)");
        assertThat(pv3, not(sameInstance(pv1)));
        
        pv1.write(10);
        pv3.write(30);
        assertThat(ValueUtil.numericValueOf(pv1.read()), equalTo(10.0));
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

    @Test(timeout=5000)
    public void testWrite() throws Exception
    {
        final PV pv = PVPool.getPV("x(3.14)");
        assertThat(ValueUtil.numericValueOf(pv.read()), equalTo(3.14));
        pv.write(47.0);
        assertThat(ValueUtil.numericValueOf(pv.read()), equalTo(47.0));
        PVPool.releasePV(pv);
    }

    @Test(timeout=5000)
    public void testString() throws Exception
    {
        final PV pv = PVPool.getPV("name(\"Fred\")");
        System.out.println(pv.read());
        assertThat(pv.read(), instanceOf(VString.class));
        assertThat(((VString)pv.read()).getValue(), equalTo("Fred"));
        pv.write("was here");
        System.out.println(pv.read());
        assertThat(((VString)pv.read()).getValue(), equalTo("was here"));
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
