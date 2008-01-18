package org.csstudio.diag.interconnectionServer.server;

import java.text.SimpleDateFormat;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;



public class IocChangedState extends Thread{
	private String iocName = "unknown Name";
	private String logicalIocName = "unknown logical Name";
	private String iocIpAddress = "unknown IP Address";
	private boolean isRunning = true;
	
	IocChangedState ( String iocName, String iocIpAddress, String logicalIocName, boolean isRunning) {
		this.isRunning = isRunning;
		this.iocName = iocName;
		this.iocIpAddress = iocIpAddress;
		this.logicalIocName = logicalIocName;
		
		this.start();
	}
	
	public void run() {
		String localHostName = null;
		
		/*
		 * get host name of interconnection server
		 */
		try {
			java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
			localHostName = localMachine.getHostName();
		}
		catch (java.net.UnknownHostException uhe) { 
		}
		
		CentralLogger.getInstance().debug(this,"IocChangedState: logical IOC name: " + logicalIocName); 
		/*
		 * depending on the running state ...
		 */
		
		if ( isRunning()) {
			/*
			 * IOC back online
			 */
			CentralLogger.getInstance().warn(this, "InterconnectionServer: Host: " + logicalIocName + " connected again - waiting for alarm updates");
			/*
			 * generate JMS alarm message NAME: "Localhost:logicalIocName:connectState" VALUE: "CONNECTED" SEVERITY: "NO_ALARM"
			 */
			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 						// type
					localHostName + ":" + logicalIocName + ":connectState",	// name
					localHostName, 											// value
					JmsMessage.SEVERITY_NO_ALARM, 							// severity
					"CONNECTED", 											// status
					iocName, 												// host
					null, 													// facility
					"virtual channel", 								// text
					null);													// howTo
			/*
			 * second message without localHostName
			 */
			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 						// type
					logicalIocName + ":connectState",	// name
					localHostName, 											// value
					JmsMessage.SEVERITY_NO_ALARM, 							// severity
					"CONNECTED", 											// status
					iocName, 												// host
					null, 													// facility
					"virtual channel", 								// text
					null);	
			/*
			 * do NOT set the connect state for records in LDAP!
			 * This is handled if the select state changes -> get all alarm states from the IOC
			 * 
			 * not necessary: setAllRecordsToConnected ( logicalIocName);
			 */
			
		} else {
			/*
			 * set channels in LDAP to disconnected
			 */
			CentralLogger.getInstance().warn(this, "InterconnectionServer: All channels set to <disConnected> mode for Host: " + logicalIocName);
			/*
			 * generate JMS alarm message NAME: "Localhost:logicalIocName:connectState" VALUE: "NOT_CONNECTED" SEVERITY: "MAJOR"
			 */
			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 						// type
					localHostName + ":" + logicalIocName + ":connectState",	// name
					localHostName, 											// value
					JmsMessage.SEVERITY_MAJOR, 								// severity
					"DISCONNECTED", 										// status
					iocName, 												// host
					null, 													// facility
					"virtual channel", 								// text
					null);		
			/*
			 * second message without localHostName
			 */
			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 						// type
					logicalIocName + ":connectState",						// name
					localHostName, 											// value
					JmsMessage.SEVERITY_MAJOR, 								// severity
					"DISCONNECTED", 										// status
					iocName, 												// host
					null, 													// facility
					"virtual channel", 								// text
					null);		
			/*
			 * for sure we are not selected any more 
			 */
			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
					localHostName + ":" + logicalIocName + ":selectState",				// name
					localHostName, 														// value
					JmsMessage.SEVERITY_MINOR, 											// severity
					"NOT-SELECTED", 													// status
					iocName, 															// host
					null, 																// facility
					"virtual channel", 													// text
					null);	
			
			LdapSupport.getInstance().setAllRecordsToDisconnected ( logicalIocName);
			
		}
		
	}
	
	
	

	
	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}

