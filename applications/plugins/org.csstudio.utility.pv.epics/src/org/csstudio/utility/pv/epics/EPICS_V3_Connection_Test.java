package org.csstudio.utility.pv.epics;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.csstudio.platform.data.IValue;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.junit.Test;

/** JUnit Test of the PV connect/disconnect.
 *  <p>
 *  These tests require the soft-IOC database from lib/test.db,
 *  and soft IOC needs to be manually stopped, restarted.
 *  <p>
 *  When using the JNI CA libs, one also needs ((DY)LD_LIBRARY_)PATH.
 *  <p>
 *  </pre>
 *  @author Kay Kasemir
 *
 *  FIXME (bknerr) : commented sysos (showstopper for org.csstudio.testsuite) - use assertions anyway
 */
@SuppressWarnings("nls")
public class EPICS_V3_Connection_Test implements PVListener
{

    private volatile int _timeout = 1000;
    private static int _interval = 100;

    /** Get a PV.
     *
     *  <b>This is where the implementation is hard-coded!</b>
     *
     *  @return PV
     */
    static private PV getPV(final String name)
    {
        PVContext.use_pure_java = true;
        System.setProperty("gov.aps.jca.jni.ThreadSafeContext.event_dispatcher",
                           "gov.aps.jca.event.DirectEventDispatcher");
        //                   "gov.aps.jca.event.QueuedEventDispatcher");
        return new EPICS_V3_PV(name);
    }

    public void pvValueUpdate(final PV pv)
    {
        final IValue v = pv.getValue();
        //System.out.println(pv.getName() + ": " + v);
        assertEquals(true, pv.isConnected());
    }

    public void pvDisconnected(final PV pv)
    {
        //System.out.println(pv.getName() + " disconnected");
        assertEquals(false, pv.isConnected());
    }

    @Test
    public void testConnections() throws Exception
    {
        Logger.getRootLogger().setLevel(Level.WARN);
        boolean connected = false;

        final PV pv = getPV("fred");
        assertEquals(connected, pv.isConnected());
        pv.addListener(this);
        pv.start();

        //System.out.println("Monitoring " + pv.getName() + ", polling isConnected()");
        //System.out.println("Stop & restart the IOC and see what happens!");
        int duration = 0;
        while (true)
        {
            assertEquals(true, pv.isRunning());
            final boolean state = pv.isConnected();
            if (state != connected)
            {
                connected = state;
                //System.out.println("PV isConnected() changed to " + connected);
            }
            duration += _interval;
            if (duration >= _timeout) {
                break;
            }
            Thread.sleep(_interval);
        }
    }
}
