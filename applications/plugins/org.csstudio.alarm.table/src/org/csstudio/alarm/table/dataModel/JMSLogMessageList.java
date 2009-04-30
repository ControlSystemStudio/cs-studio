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

import java.util.Iterator;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.preferences.LogViewPreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;

public class JMSLogMessageList extends JMSMessageList {

	public JMSLogMessageList(String[] propNames) {
		super(propNames);
	}

	/**
	 * Add a new JMSMessage to the collection of JMSMessages
	 * 
	 * @throws JMSException
	 */
	synchronized public void addJMSMessage(MapMessage mm) throws JMSException {
		if (mm != null) {
			Iterator iterator = changeListeners.iterator();
			if (mm.getString("ACK") != null && mm.getString("ACK").toUpperCase().equals("TRUE")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				CentralLogger.getInstance().debug(this,
						"received acknowledge message");
				for (JMSMessage message : JMSMessages) {
					if (message.getName().equals(mm.getString("NAME")) && message.getProperty("EVENTTIME").equals(mm.getString("EVENTTIME"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						message.getHashMap().put("ACK", "TRUE"); //$NON-NLS-1$ //$NON-NLS-2$
						while (iterator.hasNext())
							((IJMSMessageViewer) iterator.next()).updateJMSMessage(message);
						break;
					}
				}
			} else {
				limitMessageListSize();
				JMSMessage jmsm = addMessageProperties(mm);
				JMSMessages.add(JMSMessages.size(), jmsm);
				while (iterator.hasNext())
					((IJMSMessageViewer) iterator.next()).addJMSMessage(jmsm);
			}
		}
	}

	/**
	 * If message list size is bigger than in the preferences defined delete
	 * oldest messages
	 */
	private void limitMessageListSize() {
		String maximumRowNumber = JmsLogsPlugin.getDefault()
				.getPluginPreferences().getString(
						LogViewPreferenceConstants.MAX);
		int maxRowNumber;
		try {
			maxRowNumber = Integer.parseInt(maximumRowNumber);
		} catch (NumberFormatException e) {
			maxRowNumber = 100;
		}
		while (JMSMessages.size() > maxRowNumber) {
			if (JMSMessages.get(0) != null) {
				removeJMSMessage(JMSMessages.get(0));
			}
		}
	}
}
