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

import java.util.concurrent.TimeUnit;

import org.csstudio.simplepv.AbstractPVFactory;
import org.csstudio.simplepv.IPV;
import org.epics.pvmanager.CompositeDataSource;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.loc.LocalDataSource;
import org.epics.pvmanager.sim.SimulationDataSource;
import org.junit.Before;

/** Helper for IPV tests
 *  @author Kay Kasemir
 */
public class TestHelper
{
    final public AbstractPVFactory factory = new PVManagerPVFactory();
    
    final public static int TIMEOUT_SECONDS = 5;
    
    @Before
    public void setup()
    {
        final CompositeDataSource sources = new CompositeDataSource();
        sources.putDataSource("sim", new SimulationDataSource());
        sources.putDataSource("loc", new LocalDataSource());
        sources.setDefaultDataSource("sim");
        PVManager.setDefaultDataSource(sources);
    }
    
    public static void waitForConnection(final IPV pv) throws Exception
    {
        for (int seconds=TIMEOUT_SECONDS;  seconds>=0;  --seconds)
        {
            if (pv.isConnected())
                return;
            if (seconds > 0)
                TimeUnit.SECONDS.sleep(1);
            else
                assertThat("connected", equalTo(pv.getName() + " is disconnected")); // Fail
        }
    }
}
