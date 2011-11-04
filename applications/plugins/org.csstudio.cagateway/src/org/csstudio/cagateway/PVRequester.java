
package org.csstudio.cagateway;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Monitor;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;


/**
 * Simple EPICS client
 */

public class PVRequester {

	public static void main(final String[] args){
		final JCALibrary jca = JCALibrary.getInstance();

		try {
			final Context ctxt = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);

			//request for "speed" - Mock has it
			final Channel channel = ctxt.createChannel("speed");
			System.out.println("creating channel");
			channel.addConnectionListener(new CListener());

			Thread.sleep(1000);

		} catch (final Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}

		while(true) {
            ;
        }
	}
}

class CListener implements ConnectionListener {

	@Override
    public void connectionChanged(final ConnectionEvent arg0) {
		if (arg0.isConnected()){
			final Channel source = (Channel)arg0.getSource();
			System.out.println("Connected!");
			try {
				source.addMonitor(Monitor.VALUE, new MListener());
				source.getContext().flushIO();
			} catch (final IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final CAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
class MListener implements MonitorListener {
	@Override
    public void monitorChanged(final MonitorEvent arg0) {
		arg0.getDBR().printInfo();
	}
}