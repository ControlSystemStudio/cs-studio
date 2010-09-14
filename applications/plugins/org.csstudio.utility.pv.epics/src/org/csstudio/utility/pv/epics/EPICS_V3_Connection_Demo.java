package org.csstudio.utility.pv.epics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.csstudio.platform.data.IValue;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.junit.Test;

/** JUnit Demo of the PV connect/disconnect.
 *  
 *  Runs forever, meant to be executed in JProfiler or while
 *  watching memory usage of process in OS.
 *  
 *  These tests require the soft-IOC database from lib/test.db,
 *  and soft IOC needs to be manually stopped, restarted.
 *  
 *  When using the JNI CA libs, one also needs ((DY)LD_LIBRARY_)PATH.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EPICS_V3_Connection_Demo implements PVListener
{
	/** Updates received from PV */
    final AtomicInteger updates = new AtomicInteger(0);
    
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
        //                 "gov.aps.jca.event.QueuedEventDispatcher");
        return new EPICS_V3_PV(name);
    }

    // PVListener
    public void pvValueUpdate(final PV pv)
    {
        final IValue v = pv.getValue();
        System.out.println(pv.getName() + ": " + v);
        assertEquals(true, pv.isConnected());
        updates.incrementAndGet();
    }

    // PVListener
    public void pvDisconnected(final PV pv)
    {
    	System.out.println(pv.getName() + " disconnected");
        assertEquals(false, pv.isConnected());
    }

    @Test
    public void testConnections() throws Exception
    {
        Logger.getRootLogger().setLevel(Level.WARN);

        while (true)
        {
	        updates.set(0);
	        final PV pv = getPV("fred");
        	System.out.println("Created PV " + pv.getName());
	        pv.addListener(this);
	        pv.start();
	        // Wait for 2 values to arrive
	        for (int sec=0; sec<10; ++sec)
	        {
	        	if (updates.get() >= 2)
	        		break;
	            Thread.sleep(1000);
	        }
	        assertTrue(updates.get() >= 2);
	        pv.removeListener(this);
	        pv.stop();
        }
    }
}
