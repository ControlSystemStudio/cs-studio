/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.alarm.table;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.MapMessage;

import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.jms.SendMapMessage;
import org.csstudio.platform.CSSPlatformInfo;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.security.User;
import org.csstudio.utility.ldap.engine.Engine;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * This class gets a list of JMSMessages that should be acknowledged. The
 * acknowledge message is send to the JMS and LDAP server.
 * 
 * @author jhatje
 * 
 */
public class SendAcknowledge extends Job {

	List<JMSMessage> messagesToSend;
	private static String JMS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * @param msg
	 *            JMSMessage to acknowledge
	 */
	private SendAcknowledge(List<JMSMessage> msg) {
		super("Send Ack"); //$NON-NLS-1$
		messagesToSend = msg;
	}

	/**
	 * Creates a new job for sending acknowledgements from a collection of
	 * messages to send. For each message to send, the collection must contain a
	 * map of properties for that message.
	 * 
	 * @param messages
	 *            the collection of messages to send.
	 * @return the <code>SendAcknowledge</code> job.
	 */
	public static SendAcknowledge newFromProperties(
			Collection<Map<String, String>> messages) {
		List<JMSMessage> messagesToSend = new ArrayList<JMSMessage>(messages
				.size());
		for (Map<String, String> map : messages) {
			Set<String> keys = map.keySet();
			String[] keyArray = keys.toArray(new String[0]);
			JMSMessage jmsMsg = new JMSMessage(keyArray);
			for (String key : keys) {
				jmsMsg.setProperty(key, map.get(key));
			}
			messagesToSend.add(jmsMsg);
		}
		return new SendAcknowledge(messagesToSend);
	}

	/**
	 * Creates a new job for sending acknowledgements from a List of
	 * {@link JMSMessage} to send.
	 * 
	 * @param messages
	 *            the List of JMSMessage to send.
	 * @return the <code>SendAcknowledge</code> job.
	 */
	public static SendAcknowledge newFromJMSMessage(List<JMSMessage> messages) {
		return new SendAcknowledge(messages);
	}

	/**
	 * Sends for the list of JMSMessages an acknowledge message to the jms- and
	 * ldap server.
	 * 
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		SendMapMessage sender = SendMapMessage.getInstance();
		try {
			// sender.startSender(true);

			for (JMSMessage message : messagesToSend) {

				SimpleDateFormat sdf = new SimpleDateFormat(JMS_DATE_FORMAT);
				java.util.Date currentDate = new java.util.Date();
				String time = sdf.format(currentDate);

				MapMessage mapMessage = sender.getSessionMessageObject();
				HashMap<String, String> hm = message.getHashMap();
				Iterator<String> it = hm.keySet().iterator();

				while (it.hasNext()) {
					String key = it.next();
					String value = hm.get(key);
					mapMessage.setString(key, value);
				}
				
				//Add username and host to acknowledge message.
				User user = SecurityFacade.getInstance().getCurrentUser();
				if (user != null) {
					mapMessage.setString("USER", user.getUsername());
				} else {
					mapMessage.setString("USER", "NULL");
				}
				String host = CSSPlatformInfo.getInstance()
						.getQualifiedHostname();
				if (host != null) {
					mapMessage.setString("HOST", host);
				} else {
					mapMessage.setString("HOST", "NULL");
				}

				mapMessage.setString("ACK", "TRUE"); //$NON-NLS-1$ //$NON-NLS-2$
				mapMessage.setString("ACK_TIME", time); //$NON-NLS-1$

				// Engine.getInstance().addLdapWriteRequest("epicsAlarmAckn",
				// message.getName(), "ack"); epicsAlarmHighUnAckn
				Engine.getInstance().addLdapWriteRequest(
						"epicsAlarmAcknTimeStamp", message.getName(), time);
				Engine.getInstance().addLdapWriteRequest(
						"epicsAlarmHighUnAckn", message.getName(), "");
				JmsLogsPlugin
						.logInfo(user.getUsername() + " send Ack message, MsgName: "
								+ message.getName()
								+ " MsgTime: " + message.getProperty("EVENTTIME")); //$NON-NLS-2$
				sender.sendMessage();
			}
		} catch (Exception e) {
			JmsLogsPlugin.logException("ACK not set", e);
			return Status.CANCEL_STATUS;
		} finally {
			try {
				// sender.stopSender();
				System.out.println("stop sender!!!"); //$NON-NLS-1$
			} catch (Exception e) {
				JmsLogsPlugin.logException("JMS Error", e);
			}
			// sender = null;

		}

		return Status.OK_STATUS;
	}

}
