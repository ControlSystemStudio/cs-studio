/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.diag.interconnectionServer.server;

import javax.annotation.CheckForNull;

import org.csstudio.servicelocator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The constructor starts a thread which sends jms messages (formerly also changed ldap state, this is an explanation for the names) and
 * maintains state of the given ioc connection.   
 *
 * @author Matthias Clausen
 *
 */
public class IocChangedState extends Thread{
    private static final Logger LOG = LoggerFactory.getLogger(IocChangedState.class);

	private boolean isRunning = true;
	private final IocConnection iocConnection;

	IocChangedState(final IocConnection connection, final boolean isRunning) {
		this.iocConnection = connection;
		this.isRunning = isRunning;

		this.start();
	}

	@Override
    public void run() {

		IInterconnectionServer interconnectionServer = ServiceLocator.getService(IInterconnectionServer.class);
        final String localHostName = interconnectionServer.getLocalHostName();

		interconnectionServer.getNumberOfIocFailoverCollector().incrementCount();

		LOG.debug("IocChangedState: logical IOC name: " + iocConnection.getNames().getLogicalIocName());

		if ( isRunning()) {
			/*
			 * IOC back online
			 */
			LOG.warn("InterconnectionServer: Host: " + iocConnection.getNames().getLogicalIocName() + " connected again - waiting for alarm updates");
			sendMessageConnected(localHostName);
			/*
			 * do NOT set the connect state for records in LDAP!
			 * This is handled if the select state changes -> get all alarm states from the IOC
			 *
			 * not necessary: setAllRecordsToConnected ( logicalIocName);
			 */
		} else if ( !interconnectionServer.isQuit()){
			// set channels in LDAP to disconnected
			// send messages -> IOC is disconnected!
			//
			// BUT: only if Interconnection Server is still running and was NOT stopped by command, therefore the check for isQuit()

			LOG.warn("InterconnectionServer: All channels set to <disConnected> mode for Host: " + iocConnection.getNames().getLogicalIocName());
			sendMessageDisconnected(localHostName);
			
			// we are not selected any more
			sendMessageNotSelected(localHostName);

			// set changes in LDAP and generate JMS Alarm message
			ServiceLocator.getService(ILdapServiceFacade.class).setAllRecordsToDisconnected (iocConnection.getNames().getLdapIocName());
			
			// remember that we've set this IOC to disconnected!
			// one the IOC is back online - and we are selected - -> get all alarms from the IOC
			iocConnection.setDidWeSetAllChannelToDisconnect(true);
		}
	}

    private void sendMessageConnected(final String localHostName) {
        String partnerState = getPartnerState();
        String text = partnerState != null ? partnerState : "IOC has no partner";

        JmsMessage.INSTANCE.sendMessage(JmsMessage.JMS_MESSAGE_TYPE_ALARM,
        		JmsMessage.MESSAGE_TYPE_IOC_ALARM,                                 // type
        		iocConnection.getNames().getLogicalIocName() + ":connectState",    // name
        		localHostName,                                                     // value
        		JmsMessage.SEVERITY_NO_ALARM,                                      // severity
        		"CONNECTED",                                                       // status
        		iocConnection.getNames().getLogicalIocName(),                      // host
        		null,                                                              // facility
        		text);                                                             // text
    }

    private void sendMessageDisconnected(final String localHostName) {
        String partnerState = getPartnerState();
        String text = partnerState != null ? partnerState : "IOC has no partner";
        
        JmsMessage.INSTANCE.sendMessage(JmsMessage.JMS_MESSAGE_TYPE_ALARM,
        		JmsMessage.MESSAGE_TYPE_IOC_ALARM,                                 // type
        		iocConnection.getNames().getLogicalIocName() + ":connectState",    // name
        		localHostName,                                                     // value
        		JmsMessage.SEVERITY_MAJOR,                                         // severity
        		"DISCONNECTED",                                                    // status
        		iocConnection.getNames().getLogicalIocName(),                      // host
        		null,                                                              // facility
        		text);                                                             // text
    }

    private void sendMessageNotSelected(final String localHostName) {
        String partnerState = getPartnerState();
        String text = partnerState != null ? partnerState : "IOC has no partner";

        JmsMessage.INSTANCE.sendMessage(JmsMessage.JMS_MESSAGE_TYPE_ALARM,
                JmsMessage.MESSAGE_TYPE_IOC_ALARM,                                 // type
                iocConnection.getNames().getLogicalIocName() + ":selectState",     // name
                localHostName,                                                     // value
                JmsMessage.SEVERITY_MINOR,                                         // severity
                "NOT-SELECTED",                                                    // status
                iocConnection.getNames().getLogicalIocName(),                      // host
                null,                                                              // facility
                text);                                                             // text
    }

    /**
     * @return null if ioc has no partner, else readable form of connection state of partner
     */
    @CheckForNull
    private String getPartnerState() {
        String result = null;
        if (iocConnection.getNames().isRedundant()) {
            IIocConnectionManager iocConnectionManager = ServiceLocator.getService(IIocConnectionManager.class);
            IocConnection partnerIocConnection = iocConnectionManager.getIocConnectionFromName(iocConnection.getNames().getPartnerIpAddress());
            if (partnerIocConnection != null) {
                result = "Partner IOC " + partnerIocConnection.getNames().getHostName() + " is "
                        + partnerIocConnection.getCurrentConnectState() + " and "
                        + partnerIocConnection.getCurrentSelectState();
            } else {
                result = "Partner IOC expected at " + iocConnection.getNames().getPartnerIpAddress() + " but not found";
            }
        }
        return result;
    }

    public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(final boolean isRunning) {
		this.isRunning = isRunning;
	}
}

