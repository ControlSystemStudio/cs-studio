/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.csstudio.vtype.pv.jca.JCA_PVFactory;
import org.epics.vtype.VType;
import org.junit.Before;

/** JUnit demo
 * 
 *  <p>On IOC, watch "casr 2" output for number of connections
 *  when removing network cable, awaiting disconnect,
 *  re-connecting and awaiting reconnect
 *  
 *  @author Kay Kasemir
 */
public class LongMonitorDemo implements PVListener
{
    @Before
    public void setup()
    {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%n");
        final Logger root = Logger.getLogger("");
        root.setLevel(Level.FINE);
        for (Handler handler : root.getHandlers())
        {
            handler.setLevel(Level.FINE);
            handler.setFormatter(new SimpleFormatter());
        }
        PVPool.addPVFactory(new JCA_PVFactory());
    }
    
    public void run() throws Exception
    {
        final PV pv = PVPool.getPV("ramp");
        pv.addListener(this);

        synchronized (this)
        {
            wait(); // forever...
        }
        
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
    }

    @Override
    public void disconnected(PV pv)
    {
        System.out.println("Disconnected");
    }
    
    public static void main(String[] args) throws Exception
    {
        new LongMonitorDemo().run();
    }
}
