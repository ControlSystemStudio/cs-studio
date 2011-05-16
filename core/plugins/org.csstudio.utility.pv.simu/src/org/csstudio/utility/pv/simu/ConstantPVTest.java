/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.simu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.csstudio.data.values.ValueUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.junit.Test;

/** JUnit test of ConstantPV
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ConstantPVTest implements PVListener
{
    private int updates = 0;

    @Override
    public void pvValueUpdate(PV pv)
    {
        ++updates ;
        System.out.println(pv.getName() + " = " + pv.getValue().toString());
    }

    @Override
    public void pvDisconnected(PV pv)
    {
        System.out.println("Disconnected??");
    }

    @Test
    public void testConstantPV() throws Exception
    {
        PV pv = new ConstantPVFactory().createPV("pi(3.14)");
        assertEquals(ConstantPVFactory.PREFIX + PVFactory.SEPARATOR + "pi(3.14)",
                     pv.getName());

        // Not running, no updates
        assertFalse(pv.isRunning());
        pv.addListener(this);
        assertEquals(0, updates);

        // Should be running, and send one update
        pv.start();
        assertTrue(pv.isRunning());
        assertEquals(1, updates);

        assertEquals(3.14, ValueUtil.getDouble(pv.getValue()), 0.001);

        // When adding a listener while running, that one should get another update
        pv.addListener(this);
        assertEquals(2, updates);

        pv.stop();
        assertFalse(pv.isRunning());
    }
}
