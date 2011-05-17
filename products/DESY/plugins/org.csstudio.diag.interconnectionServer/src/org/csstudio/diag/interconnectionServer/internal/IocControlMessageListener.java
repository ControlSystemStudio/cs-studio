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
import javax.naming.NamingException;

import org.apache.log4j.Logger;
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

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(IocControlMessageListener.class);

	/**
	 * {@inheritDoc}
	 */
    public void onMessage(final Message m) {
		LOG.debug("Received IOC control message: " + m.toString());
		if (m instanceof MapMessage) {
			final MapMessage msg = (MapMessage) m;
			try {
				if (isIcsCommand(msg)) {
					processIcsCommand(msg);
				}
			} catch (final JMSException e) {
				LOG.error("Error processing IOC control message: " + m, e);
			} catch (final NamingException e) {
                LOG.error("LDAP naming error on processing message " + m, e);
            }
		} else {
			LOG.warn("IOC control message is not a MapMessage, ignoring: " + m);
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
	private boolean isIcsCommand(final MapMessage m) throws JMSException {
		final String type = m.getString("TYPE");
		final String destination = m.getString("DESTINATION");
		return "command".equals(type)
				&& "interconnectionServer".equals(destination);
	}

	/**
	 * Process a command sent to the interconnection server.
	 *
	 * @param m
	 *            the command message.
	 * @throws NamingException
	 */
	private void processIcsCommand(final MapMessage m) throws JMSException, NamingException {
		final String command = m.getString("NAME");
		final String args = m.getString("TEXT");
		LOG.info("Received IOC control command (command=" + command +
				", args=" + args);
		if ("disableIoc".equals(command)) {
			setIocEnabled(args, false);
		} else if ("enableIoc".equals(command)) {
			setIocEnabled(args, true);
		} else if ("scheduleDowntime".equals(command)) {
			scheduleDowntime(args);
		} else if ("refreshLogicalIocName".equals(command)) {
			refreshLogicalIocName(args);
		} else {
			LOG.warn("Received unknown IOC control command: " + command);
		}
	}

	/**
	 * Refreshes the logical name of an IOC.
	 *
	 * @param args
	 *            the name of the IOC.
	 * @throws NamingException
	 */
	private void refreshLogicalIocName(final String args) throws NamingException {
		IocConnectionManager.INSTANCE.refreshIocNameDefinition(args);
	}

	/**
	 * Schedules a downtime for an IOC.
	 *
	 * @param args
	 *            the arguments of the command.
	 * @throws NamingException
	 */
	private void scheduleDowntime(final String args) throws NamingException {
		final String[] splittedArgs = args.split(",");
		final int duration = Integer.parseInt(splittedArgs[0]);
		final String hostname = splittedArgs[1];
		final IocConnection ioc = getIocFromHostname(hostname);
		if (ioc != null) {
			ioc.scheduleDowntime(duration, TimeUnit.SECONDS);
		}
	}

	/**
	 * Enables and disables the processing of messages from an IOC.
	 *
	 * @param args
	 *            the name of the IOC.
	 * @param enabled
	 *            enable or disable.
	 * @throws NamingException
	 */
	private void setIocEnabled(final String args, final boolean enabled) throws NamingException {
		final IocConnection ioc = getIocFromHostname(args);
		if (ioc != null) {
			ioc.setDisabled(!enabled);
		}
	}

	/**
	 * Returns the IOC connection object for the IOC with the given hostname.
	 *
	 * @param hostname
	 *            the hostname of the IOC.
	 * @return the IocConnection.
	 * @throws NamingException
	 */
	private IocConnection getIocFromHostname(final String hostname) throws NamingException {
		final int dataPort = Integer.parseInt(
				Platform.getPreferencesService().getString(
						Activator.getDefault().getPluginId(),
						PreferenceConstants.DATA_PORT_NUMBER, "", null));
		final InetAddress iocInetAddress =
			IocConnectionManager.INSTANCE.getIocInetAdressByName(hostname);
		if (iocInetAddress != null) {
			final IocConnection conn =
			    IocConnectionManager.INSTANCE.getIocConnection(iocInetAddress, dataPort);
			return conn;
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return "IocControlMessageListener";
	}

}
