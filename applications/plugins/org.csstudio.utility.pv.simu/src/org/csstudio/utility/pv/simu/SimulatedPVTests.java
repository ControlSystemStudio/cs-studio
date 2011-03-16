/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.simu;

import static org.junit.Assert.*;

import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.junit.Test;

/** JUnit test of the dynamic values
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SimulatedPVTests implements ValueListener, PVListener
{
    private volatile int updates = 0;

    // ValueListener
    @Override
    public void changed(Value value)
    {
        ++updates;
        System.out.println("Update: " + value.toString());
    }

    // PVListener
    @Override
    public void pvDisconnected(PV pv)
    {
        // NOP
    }

    // PVListener
    @Override
    public void pvValueUpdate(PV pv)
    {
        ++updates;
        System.out.println("Update: " + pv.getName() + " = " + pv.getValue().toString());
    }

    @Test
    public void testLocal() throws Exception
    {
        final PV pv = new LocalPVFactory().createPV("test");
        pv.addListener(this);
        updates = 0;
        pv.start();
        assertEquals(1, updates);
        pv.setValue(3.14);
        assertEquals(2, updates);
        pv.stop();
        pv.removeListener(this);
    }

    @Test
    public void testNoise() throws Exception
    {
        final DynamicValue value = new NoiseValue("noise(0, 10, 1.0)");
        value.addListener(this);
        updates = 0;
        value.start();
        while (updates < 3)
            Thread.sleep(100);
        value.stop();
        value.removeListener(this);
    }

    @Test
    public void testSine() throws Exception
    {
        final DynamicValue value = new SineValue("sine(0, 10, 1.0)");
        value.addListener(this);
        updates = 0;
        value.start();
        while (updates < 20)
            Thread.sleep(100);
        value.stop();
        value.removeListener(this);
    }

}
