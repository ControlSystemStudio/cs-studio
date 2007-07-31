package org.csstudio.alarm.table.dataModel;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.csstudio.alarm.table.JmsLogsPlugin;

public class JMSAlarmMessageList extends JMSMessageList {

	public JMSAlarmMessageList(String[] propNames) {
		super(propNames);
	}

	/**
	 * Add a new JMSMessage to the collection of JMSMessages.
	 * 
	 */
	@Override
	synchronized public void addJMSMessage(MapMessage mm) throws JMSException {
		if (mm == null) {
			return;
		}
		//do not insert messges with type: 'status'
		if (mm.getString("TYPE").equalsIgnoreCase("status")) {
			return;
		} else {
			String severity = null;
			severity = mm.getString("SEVERITY");
			if (severity != null) {
				//is there an old message from same pv (deleteOrGrayOutEqualMessages == true)
				// -> display new message anyway
				//is new message NOT from Type NO_ALARM -> display message
				if ((deleteOrGrayOutEqualMessages(mm))
						|| (severity.equalsIgnoreCase("NO_ALARM")) == false) {
					JMSMessage jmsm = addMessageProperties(mm);
					JMSMessages.add(JMSMessages.size(), jmsm);
					Iterator iterator = changeListeners.iterator();
					while (iterator.hasNext())
						((IJMSMessageViewer) iterator.next())
								.addJMSMessage(jmsm);
				}
			}
		}
	}

	/**
	 * Delete previous messages from the same pv and with the same severity Mark
	 * messages from the same pv and with a different severity that the label
	 * provider can set a brighter color. Test if the EVENTTIME of the new message
	 * is really newer than an existing message. (It is important to use the
	 * <code>removeMessage</code> method from <code>MessageList</code> that
	 * the changeListeners on the model were actualised.)
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
			String newPVName = mm.getString("NAME");
			String newSeverity = mm.getString("SEVERITY");

			if ((newPVName != null) && (newSeverity != null)) {
				while (it.hasNext()) {
					JMSMessage jmsm = it.next();
					String pvNameFromList = jmsm.getProperty("NAME");
					//the 'real' severity in map message we get from the JMSMessage via SEVERITY_KEY
					String severityFromList = jmsm.getProperty("SEVERITY_KEY");
					if ((pvNameFromList != null) && (severityFromList != null)) {
						
						//is there a previous alarm message from same pv?
						if (newPVName.equalsIgnoreCase(pvNameFromList)) {
							equalPreviousMessage = true;
							
							//is old message gray, are both severities equal -> remove
							if ((jmsm.isBackgroundColorGray()) || (newSeverity.equalsIgnoreCase(severityFromList))) {
								jmsMessagesToRemove.add(jmsm);
								
							} else {
								jmsMessagesToRemove.add(jmsm);
								//is old message not acknowledged or is severity from old message not NO_ALARM ->
								//add message to list (not delete message)
                                if(!jmsm.getProperty("ACK_HIDDEN").toUpperCase().equals("TRUE") && 
                                		severityFromList.equalsIgnoreCase("NO_ALARM") == false) {
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

			jmsMessagesToRemove.clear();
			jmsMessagesToRemoveAndAdd.clear();
		} catch (JMSException e) {
			JmsLogsPlugin.logException("No SEVERITY in message", e);
		}
		return equalPreviousMessage;
	}
}