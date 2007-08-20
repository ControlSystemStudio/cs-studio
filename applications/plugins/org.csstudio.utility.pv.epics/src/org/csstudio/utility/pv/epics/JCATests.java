package org.csstudio.utility.pv.epics;

import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

/** JCA Tests
 *  <p>
 *  Must set LD_LIBRARY_PATH to locate JCA lib,
 *  since this is running outside of a plugin.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JCATests
{
    private static JCALibrary jca = null;
    private static Context jca_context = null;
    
    /** Monitor given PV for some time. */
    static private void run(final String pv_name) throws Exception
    {
        // Lib init
        jca = JCALibrary.getInstance();
        jca_context= jca.createContext(JCALibrary.JNI_THREAD_SAFE);
                
        // Connect
        final Channel channel = jca_context.createChannel(pv_name);

        // Subscribe
        MonitorListener monitor = new MonitorListener()
        {
            public void monitorChanged(MonitorEvent event)
            {
                final DBR_TIME_Double value = (DBR_TIME_Double) event.getDBR();
                final Channel source = (Channel)event.getSource();
                System.out.format("%s: %s %f\n",
                        source.getName(),
                        value.getTimeStamp().toMMDDYY(),
                        value.getDoubleValue()[0]);
            }
        };
        channel.addMonitor(DBRType.TIME_DOUBLE, 1, 1, monitor);        
        jca_context.flushIO();

        Thread.sleep(5 * 1000);

        channel.destroy();
        
        jca_context.destroy();
        jca_context = null;
        jca = null;
    }
    
    public static void main(String[] args) throws Exception
    {
        // First channel runs OK: Gets values, completes w/o errors
        JCATests.run("fred");
        // But from then on, I get
        // "pthread_create error Invalid argument
        //  CAC: exception during virtual circuit creation:
        //  epicsThread class was unable to  create a new thread"
        JCATests.run("jane");
    }
}
