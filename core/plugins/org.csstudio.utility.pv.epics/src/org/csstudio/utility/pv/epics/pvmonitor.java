/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.epics;

import java.util.concurrent.atomic.AtomicInteger;

import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;

/** Very simple command-line pv monitor example.
 *  @author Kay Kasemir
 */
public class pvmonitor
{
    /** Counter for received values. */
    final AtomicInteger values = new AtomicInteger(0);

    /** Monitor given PV.
     *  @param pv_name Name of the PV
     *  @param updates Number of updates to monitor or -1 to continue forever.
     */
    @SuppressWarnings("nls")
    private void run(final String pv_name, final int updates) throws Throwable
    {
        /*
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list",
                        "127.0.0.1 160.91.236.83");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list",
                        "false");
        System.setProperty("com.cosylab.epics.caj.CAJContext.connection_timeout",
                        "30.0");
        System.setProperty("com.cosylab.epics.caj.CAJContext.beacon_period",
                        "15.0");
        System.setProperty("com.cosylab.epics.caj.CAJContext.repeater_port",
                        "5065");
        System.setProperty("com.cosylab.epics.caj.CAJContext.server_port",
                        "5064");
        System.setProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes",
                        "16384");
        */

        final PVListener listener = new PVListener()
        {
            @Override
            public void pvDisconnected(PV pv)
            {
                System.out.println(pv.getName() + " disconnected");
            }

            @Override
            public void pvValueUpdate(PV pv)
            {
                System.out.println(pv.getName() + ": " + pv.getValue().toString());
                values.incrementAndGet();
            }
        };
        final PV pv = new EPICS_V3_PV(pv_name);
        pv.addListener(listener);
        pv.start();
        while (true)
        {
            Thread.sleep(1000);
            if (updates > 0   &&  values.get() > updates)
                break;
        }
        // If you don't stop the PV, the PV threads will run on
        // even after main() quits!
        pv.stop();
    }

    @SuppressWarnings("nls")
    public static void main(String[] args)
    {
        try
        {
            if (args.length == 2)
                new pvmonitor().run(args[0], Integer.parseInt(args[1]));
            else if (args.length == 1)
                new pvmonitor().run(args[0], -1);
            else
                System.err.println("Usage: pvmonitor <PV Name> { count }");
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
    }
}
