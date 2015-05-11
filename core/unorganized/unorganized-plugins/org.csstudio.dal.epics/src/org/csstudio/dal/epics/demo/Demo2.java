/*
 * Copyright (c) 2004 by Cosylab
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file "LICENSE-CAJ". If the license is not included visit Cosylab web site,
 * <http://www.cosylab.com>.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

package org.csstudio.dal.epics.demo;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Monitor;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

import java.util.concurrent.atomic.AtomicBoolean;

import com.cosylab.epics.caj.CAJChannel;

/**
 * Simple basic usage test.
 * @author <a href="mailto:matej.sekoranjaATcosylab.com">Matej Sekoranja</a>
 * @version $id$
 */
public class Demo2 {

    /**
     * Implementation of monitor listener.
     */
    private static class MonitorListenerImpl implements MonitorListener {

        /**
         * @see gov.aps.jca.event.MonitorListener#monitorChanged(gov.aps.jca.event.MonitorEvent)
         */
        public void monitorChanged(MonitorEvent event)
        {
            // immediately print info
            if (event.getStatus() == CAStatus.NORMAL)
                event.getDBR().printInfo();
            else
                System.err.println("Monitor error: " + event.getStatus());
        }
    }

    private static class ConnectionListenerImpl implements ConnectionListener {
        private AtomicBoolean onlyOnce = new AtomicBoolean(false);
        public void connectionChanged(final ConnectionEvent ev) {
            System.err.println("-----------> connection changed for " + ev.getSource() + " : " + ev.isConnected());
            if (!onlyOnce.getAndSet(true))
            {
                try {
                    ((Channel)ev.getSource()).addMonitor(Monitor.VALUE, new MonitorListenerImpl());
                    ((CAJChannel)ev.getSource()).getContext().flushIO();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * JCA context.
     */
    private Context context = null;

    /**
     * Initialize JCA context.
     * @throws CAException    throws on any failure.
     */
    private void initialize() throws CAException {

        // Get the JCALibrary instance.
        JCALibrary jca = JCALibrary.getInstance();

        // Create a context with default configuration values.
        context = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);

        // Display basic information about the context.
        System.out.println(context.getVersion().getVersionString());
        context.printInfo(); System.out.println();
    }

    /**
     * Destroy JCA context.
     */
    private void destroy() {

        try {

            // Destroy the context, check if never initialized.
            if (context != null)
                context.destroy();

        } catch (Throwable th) {
            th.printStackTrace();
        }
    }


    public void execute() {

        try {

            // initialize context
            initialize();

            // Create the Channel to connect to the PV.
            /*Channel channel1 =*/ context.createChannel("manyChannel_001", new ConnectionListenerImpl());
            /*Channel channel2 =*/ context.createChannel("nonexisting", new ConnectionListenerImpl());

            // ... forever
            synchronized (this) {
                this.wait();
            }

            System.out.println("Done.");

        } catch (Throwable th) {
            th.printStackTrace();
        }
        finally {
            // always finalize
            destroy();
        }

    }


    /**
     * Program entry point.
     * @param args    command-line arguments
     */
    public static void main(String[] args) {

        // execute
        new Demo2().execute();
    }

}
