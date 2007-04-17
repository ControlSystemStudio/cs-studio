package org.csstudio.alarm.table.dataModel;

import java.util.Iterator;

import javax.jms.MapMessage;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.preferences.LogViewerPreferenceConstants;

public class JMSLogMessageList extends JMSMessageList {

	
	public JMSLogMessageList(String[] propNames) {
		super(propNames);
	}

	/**
	 * Add a new JMSMessage to the collection of JMSMessages 
	 */
	synchronized public void addJMSMessage(MapMessage mm) {
		if (mm == null) {
			return;
		} else {
			limitMessageListSize();
			JMSMessage jmsm = addMessageProperties(mm);
			JMSMessages.add(JMSMessages.size(), jmsm);
			Iterator iterator = changeListeners.iterator();
			while (iterator.hasNext())
				((IJMSMessageViewer) iterator.next()).addJMSMessage(jmsm);
		}
	}

	
	/**
	 * If message list size is bigger than in the preferences defined
	 * delete oldest messages
	 */
	private void limitMessageListSize() {
		String maximumRowNumber = JmsLogsPlugin.getDefault().getPluginPreferences().getString(LogViewerPreferenceConstants.MAX);
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