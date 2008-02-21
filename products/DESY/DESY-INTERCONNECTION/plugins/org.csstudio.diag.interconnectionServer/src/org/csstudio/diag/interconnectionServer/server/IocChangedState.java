package org.csstudio.diag.interconnectionServer.server;
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

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


/**
 * Whatever needs to be done when an IOC changes state.
 * 
 * @author Matthias Clausen
 *
 */
public class IocChangedState extends Thread{
	private String iocName = "unknown Name";
	private String ldapIocName = "unknown Name";
	private String logicalIocName = "unknown logical Name";
	private String iocIpAddress = "unknown IP Address";
	private boolean isRunning = true;
	
	IocChangedState ( String iocName, String iocIpAddress, String logicalIocName, String ldapIocName, boolean isRunning) {
		this.isRunning = isRunning;
		this.iocName = iocName;
		this.ldapIocName = ldapIocName;
		this.iocIpAddress = iocIpAddress;
		this.logicalIocName = logicalIocName;
		
		this.start();
	}
	
	public void run() {
		String localHostName = null;
		
		// increment statistic counter
		
		InterconnectionServer.getInstance().getNumberOfIocFailoverCollector().incrementCount();
		
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
//			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
//					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 						// type
//					localHostName + ":" + logicalIocName + ":connectState",	// name
//					localHostName, 											// value
//					JmsMessage.SEVERITY_NO_ALARM, 							// severity
//					"CONNECTED", 											// status
//					logicalIocName, 										// host
//					null, 													// facility
//					"virtual channel", 								// text
//					null);													// howTo
			/*
			 * second message without localHostName
			 */
			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 						// type
					logicalIocName + ":connectState",	// name
					localHostName, 											// value
					JmsMessage.SEVERITY_NO_ALARM, 							// severity
					"CONNECTED", 											// status
					logicalIocName, 										// host
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
//			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
//					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 						// type
//					localHostName + ":" + logicalIocName + ":connectState",	// name
//					localHostName, 											// value
//					JmsMessage.SEVERITY_MAJOR, 								// severity
//					"DISCONNECTED", 										// status
//					logicalIocName, 										// host
//					null, 													// facility
//					"virtual channel", 								// text
//					null);		
			/*
			 * second message without localHostName
			 */
			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 						// type
					logicalIocName + ":connectState",						// name
					localHostName, 											// value
					JmsMessage.SEVERITY_MAJOR, 								// severity
					"DISCONNECTED", 										// status
					logicalIocName, 										// host
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
					logicalIocName, 													// host
					null, 																// facility
					"virtual channel", 													// text
					null);
			/*
			 * set changes in LDAP and generate JMS Alarm message
			 */
			LdapSupport.getInstance().setAllRecordsToDisconnected ( ldapIocName);
			
		}
		
	}
	
	
	

	
	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}

