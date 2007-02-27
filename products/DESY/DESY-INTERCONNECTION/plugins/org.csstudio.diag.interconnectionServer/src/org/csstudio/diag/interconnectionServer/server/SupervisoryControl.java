package org.csstudio.diag.interconnectionServer.server;

import java.util.Enumeration;

import javax.jms.JMSException;

import org.csstudio.diag.interconnectionServer.server.Statistic.StatisticContent;


public class SupervisoryControl {
	
	private static SupervisoryControl supervisoryControlInstance = null;
	
	public SupervisoryControl() {
		//
		// initialize
		//
		
	}
	
	
	public static SupervisoryControl getInstance() {
		//
		// get an instance of our sigleton
		//
		if ( supervisoryControlInstance == null) {
			synchronized (SupervisoryControl.class) {
				if (supervisoryControlInstance == null) {
					supervisoryControlInstance = new SupervisoryControl();
				}
			}
		}
		return supervisoryControlInstance;
	}
	
	public void scanManager ( int scanTime) {
		
		switch (scanTime) {
		case Timer.scan1Sec:
			
			checkBeaconTimeout();
			break;
			
			default:
				break;
		
		}
		
	}
	
	private void checkBeaconTimeout () {
		
		Enumeration connections = Statistic.getInstance().connectionList.elements();
		 while (connections.hasMoreElements()) {
			 StatisticContent thisContent = (StatisticContent)connections.nextElement();
			 System.out.println("... supervisor: checking : " + thisContent.getStatisticId());
			 
			 if ( thisContent.gregorianTimeDifferenceFromNow( thisContent.getTimeLastBeaconReceived()) > PreferenceProperties.BEACON_TIMEOUT) {
				 thisContent.setConnectState( false);
				 System.out.println("---------- Client disconnecte ---------------");
				 System.out.println("Host:" +  thisContent.host + "  Port: " + thisContent.port +"\n");
				 //
				 // cannot send message from here ..
				 // TODO:
				 /*
				 InterconnectionServer.getInstance().sendLogMessage ( ClientRequest jmsLogMessageLostClientConnection( thisContent.getStatisticId()));
				 try{
					 MessageProducer sender = logSession.createProducer(logDestination);
                     message = logSession.createMapMessage();
                     message = jmsLogMessageNewClientConnected( statisticId);
             		sender.send(message);
         		}
         		catch(JMSException jmse)
                 {
         			status = false;
                     System.out.println("ClientRequest : send NewClientConnected-LOG message : *** EXCEPTION *** : " + jmse.getMessage());
                 }
                 */
			 }
			 
			 
			 
		 }
		
	}

}
