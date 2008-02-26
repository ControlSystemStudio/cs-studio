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
		if (mm.getString("TYPE") == null) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return;
		}
		//do not insert messges with type: 'status', unless there is a previous message
		//with the same NAME in the table.
		if ((mm.getString("TYPE").equalsIgnoreCase("status") && 
				(equalMessageNameInTable(mm) == false))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return;
		} else {
			String severity = null;
			severity = mm.getString("SEVERITY"); //$NON-NLS-1$
			if (severity != null) {
				//is there an old message from same pv (deleteOrGrayOutEqualMessages == true)
				// -> display new message anyway
				//is new message NOT from Type NO_ALARM -> display message
				if ((deleteOrGrayOutEqualMessages(mm))
						|| (severity.equalsIgnoreCase("NO_ALARM")) == false) { //$NON-NLS-1$
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
	 * Searching for a previous message in alarm table with the sam NAME.
	 * 
	 * @param mm
	 * @return boolean Is there a previous message
	 */
	private boolean equalMessageNameInTable(MapMessage mm) throws JMSException {
		boolean messageInTable = false;
		for (JMSMessage message : JMSMessages) {
			String currentInList = message.getProperty("NAME");
			String currentMessage = mm.getString("NAME");
			if(currentInList.equalsIgnoreCase(currentMessage) == true) {
				messageInTable = true;
				break;
			}
		}
		return messageInTable;
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
			String newPVName = mm.getString("NAME"); //$NON-NLS-1$
			String newSeverity = mm.getString("SEVERITY"); //$NON-NLS-1$

			if ((newPVName != null) && (newSeverity != null)) {
				while (it.hasNext()) {
					JMSMessage jmsm = it.next();
					String pvNameFromList = jmsm.getProperty("NAME"); //$NON-NLS-1$
					//the 'real' severity in map message we get from the JMSMessage via SEVERITY_KEY
					String severityFromList = jmsm.getProperty("SEVERITY_KEY"); //$NON-NLS-1$
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
                                if(!jmsm.getProperty("ACK_HIDDEN").toUpperCase().equals("TRUE") &&  //$NON-NLS-1$ //$NON-NLS-2$
                                		severityFromList.equalsIgnoreCase("NO_ALARM") == false) { //$NON-NLS-1$
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