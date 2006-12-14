package org.csstudio.utility.pv.epics.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.csstudio.utility.pv.epics.EPICS_V3_PV;

/** Test tool for hammering a CA server.
 *  <pre>
 *  USAGE: Invoke from command-line with <seconds> and <file>,
 *         where file contains list of PV names, one per line.
 *  </pre>
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

    public static void main(String argv[]) throws Exception
    {
        int secs = Integer.parseInt(argv[0]);
        BufferedReader r = new BufferedReader(new FileReader(argv[1]));
        ArrayList<String> names = new ArrayList<String>();
        String line = r.readLine();
        while (line != null)
        {
            names.add(line);
            line = r.readLine();
        }
    
        Hammer h = new Hammer(names);
        Thread.sleep(1000 * secs);
        h.dispose();
    }
}
