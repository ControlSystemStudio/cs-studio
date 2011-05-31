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

import org.csstudio.platform.logging.CentralLogger;


/**
 * Whatever needs to be done when an IOC changes state.
 *
 * @author Matthias Clausen
 *
 */
public class IocChangedState extends Thread{
	private String ldapIocName = "unknown Name";
	private String logicalIocName = "unknown logical Name";
	private boolean isRunning = true;
	private final IocConnection iocConnection;

	IocChangedState(final IocConnection connection, final boolean isRunning) {
		this.iocConnection = connection;
		this.logicalIocName = connection.getLogicalIocName();
		this.ldapIocName = connection.getLdapIocName();
		this.isRunning = isRunning;

		this.start();
	}

	@Override
    public void run() {

		final String localHostName = InterconnectionServer.getInstance().getLocalHostName();

		InterconnectionServer.getInstance().getNumberOfIocFailoverCollector().incrementCount();

		CentralLogger.getInstance().debug(this,"IocChangedState: logical IOC name: " + logicalIocName);

		if ( isRunning()) {
			/*
			 * IOC back online
			 */
			CentralLogger.getInstance().warn(this, "InterconnectionServer: Host: " + logicalIocName + " connected again - waiting for alarm updates");
			JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 		// type
					logicalIocName + ":connectState",		// name
					localHostName, 							// value
					JmsMessage.SEVERITY_NO_ALARM, 			// severity
					"CONNECTED", 							// status
					logicalIocName, 						// host
					null, 									// facility
					"virtual channel");
			/*
			 * do NOT set the connect state for records in LDAP!
			 * This is handled if the select state changes -> get all alarm states from the IOC
			 *
			 * not necessary: setAllRecordsToConnected ( logicalIocName);
			 */
		} else if ( !InterconnectionServer.getInstance().isQuit()){
			/*
			 * set channels in LDAP to disconnected
			 * send messages -> IOC is disconnected!
			 *
			 * BUT: only if Interconnection Server is still running and was NOT stopped by command!
			 * -> isQuit()
			 */

			CentralLogger.getInstance().warn(this, "InterconnectionServer: All channels set to <disConnected> mode for Host: " + logicalIocName);
			JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 		// type
					logicalIocName + ":connectState",		// name
					localHostName, 							// value
					JmsMessage.SEVERITY_MAJOR, 				// severity
					"DISCONNECTED", 						// status
					logicalIocName, 						// host
					null, 									// facility
					"virtual channel");
			/*
			 * for sure we are not selected any more
			 *
			 * XXX: Why does this use a different name than the other messages?
			 */
			JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 		// type
					localHostName + ":" + logicalIocName + ":selectState",	// name
					localHostName, 							// value
					JmsMessage.SEVERITY_MINOR, 				// severity
					"NOT-SELECTED", 						// status
					logicalIocName, 						// host
					null, 									// facility
					"virtual channel");
			/*
			 * set changes in LDAP and generate JMS Alarm message
			 */
			LdapServiceFacadeImpl.INSTANCE.setAllRecordsToDisconnected (ldapIocName);
			/*
			 * remember that we've set this IOC to disconnected!
			 * one the IOC is back online - and we are selected - -> get all alarms from the IOC
			 */
			iocConnection.setDidWeSetAllChannelToDisconnect(true);
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(final boolean isRunning) {
		this.isRunning = isRunning;
	}
}

