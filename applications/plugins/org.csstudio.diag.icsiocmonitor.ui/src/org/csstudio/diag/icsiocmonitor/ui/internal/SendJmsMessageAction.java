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

package org.csstudio.diag.icsiocmonitor.ui.internal;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.diag.icsiocmonitor.ui.internal.model.MonitorItem;
import org.csstudio.platform.utility.jms.sharedconnection.ISharedConnectionHandle;
import org.csstudio.platform.utility.jms.sharedconnection.SharedJmsConnections;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * Abstract base class for actions that send a JMS message based on the
 * currently selected IOC in the monitor view. Subclasses must override the
 * method {@link #setMessageFields(MapMessage, String)}.
 * 
 * @author Joerg Rathlev
 */
abstract class SendJmsMessageAction extends BaseSelectionListenerAction {

	/**
	 * JMS topic for the messages.
	 */
	private static final String IOC_CONTROL_TOPIC = "IOC_CONTROL";

	/**
	 * Creates a new action with the given text.
	 * 
	 * @param text
	 *            the string used as the text for the action, or
	 *            <code>null</code> if there is no text.
	 */
	protected SendJmsMessageAction(String text) {
		super(text);
		setEnabled(updateSelection(getStructuredSelection()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		if (isIocSelected()) {
			String iocName = selectedIocHostName();
			sendDisableCommandToInterconnectionServers(iocName);
		}
	}

	/**
	 * Sends the Disable command to the interconnection servers via JMS.
	 * 
	 * @param iocName
	 *            the name of the IOC.
	 */
	private void sendDisableCommandToInterconnectionServers(String iocName) {
		try {
			ISharedConnectionHandle jmsConnection = SharedJmsConnections.sharedSenderConnection();
			Session session = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic topic = session.createTopic(IOC_CONTROL_TOPIC);
			MessageProducer producer = session.createProducer(topic);
			MapMessage message = session.createMapMessage();
			message.setString("TYPE", "command");
			message.setString("EVENTTIME", getEventTime());
			message.setString("DESTINATION", "interconnectionServer");
			setMessageFields(message, iocName);
			producer.send(message);
			session.close();
			jmsConnection.release();
		} catch (JMSException e) {
			MessageDialog.openError(null, "Control System Studio",
					"Error sending JMS message: " + e.getMessage());
		}
	}

	/**
	 * Sets the fields of the map message. The fields TYPE, DESTINATION and
	 * EVENTTIME are already set when this method is called.
	 * 
	 * @param message
	 *            the message.
	 * @param iocName
	 *            the name of the IOC.
	 * @throws JMSException
	 *             if an error occurs.
	 */
	protected abstract void setMessageFields(MapMessage message, String iocName)
		throws JMSException;

	/**
	 * Returns the current time in the format required for JMS messages.
	 */
	private String getEventTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(new Date());
	}

	/**
	 * Returns the host name of the selected IOC.
	 * 
	 * @return the host name of the selected IOC.
	 */
	private String selectedIocHostName() {
		IStructuredSelection selection = getStructuredSelection();
		return ((MonitorItem) selection.getFirstElement()).getIocHostname();
	}

	/**
	 * Returns whether a single IOC is selected.
	 * 
	 * @return <code>true</code> if an IOC is selected, <code>false</code>
	 *         otherwise.
	 */
	private boolean isIocSelected() {
		IStructuredSelection selection = getStructuredSelection();
		return (selection.size() == 1)
			&& (selection.getFirstElement() instanceof MonitorItem);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		boolean enabled = selection.size() == 1;
		return enabled;
	}

}
