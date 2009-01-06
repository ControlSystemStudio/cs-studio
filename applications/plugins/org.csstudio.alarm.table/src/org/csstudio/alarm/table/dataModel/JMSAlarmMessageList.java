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
package org.csstudio.alarm.table.dataModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import javazoom.jl.player.Player;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.RemoveAcknowledgedMessagesTask;
import org.csstudio.alarm.table.preferences.AlarmViewerPreferenceConstants;
import org.csstudio.alarm.table.utility.Functions;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.jobs.Job;

/**
 * List of alarm messages for alarm table. The class includes also the logic in
 * which case a message should be deleted, highlighted etc.
 * 
 * @author jhatje
 * 
 */
public class JMSAlarmMessageList extends JMSMessageList {

	/** number of alarm status changes in the message with the same pv name. */
	private int alarmStatusChanges = 0;
	private static Player _mp3Player;

	private RemoveAcknowledgedMessagesTask _removeMessageTask;
	private Vector<JMSMessage> _messagesToRemove = new Vector<JMSMessage>();

	public JMSAlarmMessageList(String[] propNames) {
		super(propNames);
	}

	/**
	 * Add a new JMSMessage to the collection of JMSMessages.
	 * 
	 */
	@Override
	synchronized public void addJMSMessage(MapMessage newMessage)
			throws JMSException {
		if (checkValidity(newMessage)) {
			Iterator iterator = changeListeners.iterator();
			if (alarmSound) {
				playAlarmSound(newMessage);
			}
			// An acknowledge message was received.
			if (newMessage.getString("ACK") != null && newMessage.getString("ACK").toUpperCase().equals("TRUE")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				JMSMessage jmsm = setAcknowledge(newMessage);
				if (jmsm != null) {
					while (iterator.hasNext())
						((IJMSMessageViewer) iterator.next())
								.updateJMSMessage(jmsm);
				}
				return;
			}
			boolean equalMessageInTable = equalMessageNameInTable(newMessage);
			// do not insert messages with type: 'status', unless there is a
			// previous message with the same NAME in the table.
			if ((newMessage.getString("TYPE").equalsIgnoreCase("status") && (equalMessageInTable == false))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				return;
			}
			// is there an old message from same pv
			// (deleteOrGrayOutEqualMessages == true) -> display new message
			// anyway is new message NOT from Type NO_ALARM -> display message
			if ((deleteOrGrayOutEqualMessages(newMessage))
					|| (newMessage.getString("SEVERITY").equalsIgnoreCase("NO_ALARM")) == false) { //$NON-NLS-1$
				JMSMessage jmsm = addMessageProperties(newMessage);
				if (equalMessageInTable == false) {
					jmsm.setProperty("COUNT", "0");
				} else {
					jmsm.setProperty("COUNT", String
							.valueOf(alarmStatusChanges + 1));
				}
				jmsm.set_alarmChangeCount(alarmStatusChanges + 1);
				JMSMessages.add(JMSMessages.size(), jmsm);
				while (iterator.hasNext())
					((IJMSMessageViewer) iterator.next()).addJMSMessage(jmsm);
			}
		}
	}

	/**
	 * Plays a mp3 file set in the preferences if a alarm message with severity
	 * MAJOR is received.
	 * 
	 * @param newMessage
	 */
	private void playAlarmSound(MapMessage newMessage) throws JMSException {
		if (newMessage.getString("SEVERITY").equalsIgnoreCase("MAJOR")) {
			String mp3Path = JmsLogsPlugin
					.getDefault()
					.getPluginPreferences()
					.getString(
							AlarmViewerPreferenceConstants.LOG_ALARM_SOUND_FILE);
			if ((mp3Path != null) && (!mp3Path.equals(""))) {
				Functions.playMp3(mp3Path);
			}
		}
	}

	/**
	 * Check if the new message is valid alarm message
	 * 
	 * @param newMessage
	 */
	private boolean checkValidity(MapMessage newMessage) throws JMSException {
		if (newMessage == null) {
			return false;
		}
		if (newMessage.getString("TYPE") == null) {
			return false;
		}
		if (newMessage.getString("SEVERITY") == null) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param newMessage
	 * @throws JMSException
	 * @throws JMSException
	 */
	protected JMSMessage setAcknowledge(final MapMessage newMessage)
			throws JMSException {

		JMSMessage newJMSMessage = null;
		CentralLogger.getInstance().info(this, "ViewAlarm Ack message received, MsgName: "
				+ newMessage.getString("NAME") + " MsgTime: "
				+ newMessage.getString("EVENTTIME"));
		System.out.println("ViewAlarm Ack message received, MsgName: "
		+ newMessage.getString("NAME") + " MsgTime: "
		+ newMessage.getString("EVENTTIME"));
		for (JMSMessage message : JMSMessages) {
			if (message.getName().equals(newMessage.getString("NAME"))
					&& message.getProperty("EVENTTIME").equals(
							newMessage.getString("EVENTTIME"))) {
				if ((message.isBackgroundColorGray() == true)
						|| (message.getProperty("SEVERITY_KEY")
								.equalsIgnoreCase("NO_ALARM"))
						|| (message.getProperty("SEVERITY_KEY")
								.equalsIgnoreCase("INVALID"))) {
					_messagesToRemove.add(message);
					System.out.println("ViewAlarm add message MsgName: "
							+ message.getProperty("NAME") + " MsgTime: "
							+ message.getProperty("EVENTTIME"));
					CentralLogger.getInstance().debug(
							this,
							"add message, removelist size: "
									+ _messagesToRemove.size());
					if ((_removeMessageTask == null)
							|| (_removeMessageTask.getState() == Job.NONE)) {
						CentralLogger.getInstance().debug(this,
								"Create new 'RemoveAckMessage Task'");
						_removeMessageTask = null;
						_removeMessageTask = new RemoveAcknowledgedMessagesTask(
								this, _messagesToRemove);
						_removeMessageTask.schedule();
					}
				} else {
					System.out.println("ViewAlarm set ack MsgName: "
							+ message.getProperty("NAME") + " MsgTime: "
							+ message.getProperty("EVENTTIME"));
					message.getHashMap().put("ACK_HIDDEN", "TRUE");
					message.setProperty("ACK", "TRUE");
					message.setAcknowledged(true);
					return message;
				}
				break;
			}
		}
		return null;
	}

	/**
	 * Searching for a previous message in alarm table with the same NAME.
	 * 
	 * @param mm
	 * @return boolean Is there a previous message
	 */
	private boolean equalMessageNameInTable(MapMessage mm) throws JMSException {
		boolean messageInTable = false;
		for (JMSMessage message : JMSMessages) {
			String currentInList = message.getProperty("NAME");
			String currentMessage = mm.getString("NAME");
			if (currentInList.equalsIgnoreCase(currentMessage) == true) {
				String alarmChangeCount = message.getProperty("COUNT");
				try {
					alarmStatusChanges = Integer.parseInt(alarmChangeCount);
				} catch (NumberFormatException e) {
					alarmStatusChanges = 0;
				}
				messageInTable = true;
				break;
			}
		}
		return messageInTable;
	}

	/**
	 * Delete previous messages from the same pv and with the same severity Mark
	 * messages from the same pv and with a different severity that the label
	 * provider can set a brighter color. Test if the EVENTTIME of the new
	 * message is really newer than an existing message. (It is important to use
	 * the <code>removeMessage</code> method from <code>MessageList</code> that
	 * the changeListeners on the model were updated.)
	 * 
	 * @param mm
	 *            The new MapMessage
	 * @return Is there a previous message in the list with the same pv name
	 */
	private boolean deleteOrGrayOutEqualMessages(MapMessage mm) {
		if (mm == null) {
			return false;
		}
		boolean equalPreviousMessage = false;
		Iterator<JMSMessage> it = JMSMessages.listIterator();
		List<JMSMessage> jmsMessagesToRemove = new ArrayList<JMSMessage>();
		List<JMSMessage> jmsMessagesToRemoveAndAdd = new ArrayList<JMSMessage>();
		try {
			String newPVName = mm.getString("NAME"); //$NON-NLS-1$
			String newSeverity = mm.getString("SEVERITY"); //$NON-NLS-1$

			if ((newPVName != null) && (newSeverity != null)) {
				while (it.hasNext()) {
					JMSMessage jmsm = it.next();
					String pvNameFromList = jmsm.getProperty("NAME"); //$NON-NLS-1$
					// the 'real' severity in map message we get from the
					// JMSMessage via SEVERITY_KEY
					String severityFromList = jmsm.getProperty("SEVERITY_KEY"); //$NON-NLS-1$
					if ((pvNameFromList != null) && (severityFromList != null)) {

						// is there a previous alarm message from same pv?
						if (newPVName.equalsIgnoreCase(pvNameFromList)) {
							equalPreviousMessage = true;

							// is old message gray, are both severities equal ->
							// remove
							if ((jmsm.isBackgroundColorGray())
									|| (newSeverity
											.equalsIgnoreCase(severityFromList))) {
								jmsMessagesToRemove.add(jmsm);

							} else {
								jmsMessagesToRemove.add(jmsm);
								// is old message not acknowledged or is
								// severity from old message not NO_ALARM ->
								// add message to list (not delete message)
								if (!jmsm
										.getProperty("ACK_HIDDEN").toUpperCase().equals("TRUE") && //$NON-NLS-1$ //$NON-NLS-2$
										severityFromList
												.equalsIgnoreCase("NO_ALARM") == false) { //$NON-NLS-1$
									jmsm.setBackgroundColorGray(true);
									jmsMessagesToRemoveAndAdd.add(jmsm);
								}
							}
						}
					}
				}
			}
			it = jmsMessagesToRemove.listIterator();
			while (it.hasNext()) {
				removeJMSMessage(it.next());
			}

			it = jmsMessagesToRemoveAndAdd.listIterator();
			while (it.hasNext()) {
				addJMSMessage(it.next());
			}
System.out.println("toRemove size: " + jmsMessagesToRemove.size());
			jmsMessagesToRemove.clear();
			System.out.println("toRemoveAndAdd size: " + jmsMessagesToRemoveAndAdd.size());
			jmsMessagesToRemoveAndAdd.clear();
		} catch (JMSException e) {
			JmsLogsPlugin.logException("No SEVERITY in message", e);
		}
		return equalPreviousMessage;
	}
}