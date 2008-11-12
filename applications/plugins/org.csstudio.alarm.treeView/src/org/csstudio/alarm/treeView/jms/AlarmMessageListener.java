/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.alarm.treeView.jms;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.csstudio.alarm.treeView.model.Severity;
import org.csstudio.platform.logging.CentralLogger;

/**
 * Listens for alarm messages and triggers the necessary updates to an alarm
 * tree.
 * 
 * @author Joerg Rathlev
 */
public final class AlarmMessageListener implements MessageListener {
	
	/**
	 * The alarm tree updater which applies updates to the tree.
	 */
	private AlarmTreeUpdater _updater;
	
	/**
	 * The logger used by this listener.
	 */
	private final CentralLogger _log = CentralLogger.getInstance();

	/**
	 * Creates a new alarm message listener.
	 * 
	 * @param updater
	 *            the updater which this listener will use.
	 */
	AlarmMessageListener(final AlarmTreeUpdater updater) {
		if (updater == null) {
			throw new NullPointerException("updater must not be null");
		}
		
		_updater = updater;
	}

	/**
	 * Sets the updater which this listener will use.
	 * 
	 * @param updater
	 *            the updater which this listener will use.
	 */
	void setUpdater(final AlarmTreeUpdater updater) {
		if (updater == null) {
			throw new NullPointerException("updater must not be null");
		}
		
		_updater = updater;
	}

	/**
	 * Called when a JMS message is received. The message is interpreted as an
	 * alarm message. If the message contains valid information, the respective
	 * updates of the alarm tree are triggered.
	 * 
	 * @param message
	 *            the JMS message.
	 */
	public void onMessage(final Message message) {
		_log.debug(this, "received: " + message);
		if (message instanceof MapMessage) {
			try {
				processAlarmMessage((MapMessage) message);
			} catch (JMSException e) {
				_log.error(this, "error processing JMS message", e);
			}
		} else {
			_log.warn(this,
					"received message which is not a MapMessage: " + message);
		}
	}

	/**
	 * Processes an alarm message.
	 * 
	 * @param message
	 *            the alarm message.
	 * @throws JMSException
	 *             if a JMS error occurs.
	 */
	private void processAlarmMessage(final MapMessage message)
			throws JMSException {
		String name = message.getString("NAME");
		if (isAcknowledgement(message)) {
			_log.info(this, "received ack: name=" + name);
			_updater.applyAcknowledgement(name);
		} else {
			Severity severity = Severity.parseSeverity(message.getString("SEVERITY"));
			_log.info(this, "received alarm: name=" + name + ", severity=" + severity);
			_updater.applyAlarm(name, severity);
		}
	}

	/**
	 * Returns whether the given message is an alarm acknowledgement.
	 * 
	 * @param message
	 *            the message.
	 * @return <code>true</code> if the message is an alarm acknowledgement,
	 *         <code>false</code> otherwise.
	 */
	private boolean isAcknowledgement(final MapMessage message) {
		try {
			String ack = message.getString("ACK");
			return ack != null && ack.equals("TRUE");
		} catch (JMSException e) {
			return false;
		}
	}

}
