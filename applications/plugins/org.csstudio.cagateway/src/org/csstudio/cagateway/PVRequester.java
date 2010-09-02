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
	
	public static void main(String[] args){
		JCALibrary jca = JCALibrary.getInstance();
		
		try {
			Context ctxt = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
			
			//request for "speed" - Mock has it
			Channel channel = ctxt.createChannel("speed");
			System.out.println("creating channel");
			channel.addConnectionListener(new CListener());
			
			Thread.sleep(1000);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}
		
		while(true);
	}
}

class CListener implements ConnectionListener {

	public void connectionChanged(ConnectionEvent arg0) {
		if (arg0.isConnected()){
			Channel source = (Channel)arg0.getSource();
			System.out.println("Connected!");
			try {
				source.addMonitor(Monitor.VALUE, new MListener());
				source.getContext().flushIO();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
class MListener implements MonitorListener {
	public void monitorChanged(MonitorEvent arg0) {
		arg0.getDBR().printInfo();
	}	
}