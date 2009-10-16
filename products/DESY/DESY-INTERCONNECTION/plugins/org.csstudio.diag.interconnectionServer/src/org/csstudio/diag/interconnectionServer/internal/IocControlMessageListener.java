/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.diag.interconnectionServer.internal;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.diag.interconnectionServer.preferences.PreferenceConstants;
import org.csstudio.diag.interconnectionServer.server.IocConnection;
import org.csstudio.diag.interconnectionServer.server.IocConnectionManager;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;

/**
 * Listens to IOC control messages and performs the required state changes in
 * reaction to receiving the messages.
 * 
 * @author Joerg Rathlev
 */
public class IocControlMessageListener implements MessageListener {
	
	private static CentralLogger _log = CentralLogger.getInstance();

	/**
	 * {@inheritDoc}
	 */
	public void onMessage(Message m) {
		_log.debug(this, "Received IOC control message: " + m.toString());
		if (m instanceof MapMessage) {
			MapMessage msg = (MapMessage) m;
			try {
				if (isIcsCommand(msg)) {
					processIcsCommand(msg);
				}
			} catch (JMSException e) {
				_log.error(this, "Error processing IOC control message: " + m, e);
			}
		} else {
			_log.warn(this, "IOC control message is not a MapMessage, ignoring: " + m);
		}
	}

	/**
	 * Returns whether the message is a command for an interconnection server.
	 * 
	 * @param m
	 *            the message.
	 * @return <code>true</code> if the message is an interconnection server
	 *         command, <code>false</code> otherwise.
	 */
	private boolean isIcsCommand(MapMessage m) throws JMSException {
		String type = m.getString("TYPE");
		String destination = m.getString("DESTINATION");
		return "command".equals(type)
				&& "interconnectionServer".equals(destination);
	}

	/**
	 * Process a command sent to the interconnection server.
	 * 
	 * @param m
	 *            the command message.
	 */
	private void processIcsCommand(MapMessage m) throws JMSException {
		String command = m.getString("NAME");
		String args = m.getString("TEXT");
		_log.info(this, "Received IOC control command (command=" + command +
				", args=" + args);
		if ("disableIoc".equals(command)) {
			setIocEnabled(args, false);
		} else if ("enableIoc".equals(command)) {
			setIocEnabled(args, true);
		} else if ("scheduleDowntime".equals(command)) {
			scheduleDowntime(args);
		} else {
			_log.warn(this, "Received unknown IOC control command: " + command);
		}
	}

	/**
	 * Schedules a downtime for an IOC.
	 * 
	 * @param args
	 *            the arguments of the command.
	 */
	private void scheduleDowntime(String args) {
		String[] splittedArgs = args.split(",");
		int duration = Integer.parseInt(splittedArgs[0]);
		String hostname = splittedArgs[1];
		IocConnection ioc = getIocFromHostname(hostname);
		ioc.scheduleDowntime(duration, TimeUnit.SECONDS);
	}

	/**
	 * Enables and disables the processing of messages from an IOC.
	 * 
	 * @param args
	 *            the name of the IOC.
	 * @param enabled
	 *            enable or disable.
	 */
	private void setIocEnabled(String args, boolean enabled) {
		IocConnection ioc = getIocFromHostname(args);
		ioc.setDisabled(!enabled);
	}

	/**
	 * Returns the IOC connection object for the IOC with the given hostname.
	 * 
	 * @param hostname
	 *            the hostname of the IOC.
	 * @return the IocConnection.
	 */
	private IocConnection getIocFromHostname(String hostname) {
		int dataPort = Integer.parseInt(
				Platform.getPreferencesService().getString(
						Activator.getDefault().getPluginId(),
						PreferenceConstants.DATA_PORT_NUMBER, "", null));
		InetAddress iocInetAddress =
			IocConnectionManager.getInstance().getIocInetAdressByName(hostname);
		IocConnection ioc = IocConnectionManager.getInstance().getIocConnection(
				iocInetAddress, dataPort);
		return ioc;
	}

	@Override
	public String toString() {
		return "IocControlMessageListener";
	}

}
