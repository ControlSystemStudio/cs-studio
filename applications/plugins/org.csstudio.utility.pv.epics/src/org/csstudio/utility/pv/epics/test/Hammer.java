package org.csstudio.utility.pv.epics.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.csstudio.utility.pv.epics.EPICS_V3_PV;

/** Test tool for hammering a CA server.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Hammer implements PVListener
{
    private PV pvs[];
    
    Hammer(ArrayList<String> names) throws Exception
    {
        System.out.println("Connecting...");
        pvs = new PV[names.size()];
        // Create all
        for (int i = 0; i < pvs.length; i++)
            pvs[i] = new EPICS_V3_PV(names.get(i));
        // Start all
        for (int i = 0; i < pvs.length; i++)
        {
            pvs[i].addListener(this);
            pvs[i].start();
        }
    }
    
    void dispose()
    {
        for (int i = 0; i < pvs.length; i++)
        {
            pvs[i].removeListener(this);
            pvs[i].stop();
            pvs[i] = null;
        }
        pvs = null;
        System.out.println("Shut down.");
    }
    
    /* @see PVListener */
    public void pvDisconnected(PV pv)
    {
        System.out.println("Disconnected: " + pv.getName());
    }

    /* @see PVListener */
    public void pvValueUpdate(PV pv)
    {
        System.out.println(pv.getName() + " = " + pv.getValue().toString());
    }
    
    public static void main(final String argv[]) throws Exception
    {
        // Compile-time config
        final int runs = 2;
        final int secs = 4;
        final String filename = "lib/pv_list.txt";

        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "127.0.0.1");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");

        // --------------------
        
        System.out.format("%d runs, %d seconds each, with %s\n",
                          runs, secs, filename);
        final BufferedReader r = new BufferedReader(new FileReader(filename));
        final ArrayList<String> names = new ArrayList<String>();
        String line = r.readLine();
        while (line != null)
        {
            names.add(line);
            line = r.readLine();
        }
    
        for (int run=0; run<runs; ++run)
        {
            final Hammer h = new Hammer(names);
            Thread.sleep(1000 * secs);
            h.dispose();
        }
    }
}
