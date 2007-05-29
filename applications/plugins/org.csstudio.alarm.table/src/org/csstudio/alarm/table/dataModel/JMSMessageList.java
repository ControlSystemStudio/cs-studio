package org.csstudio.alarm.table.dataModel;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.apache.activemq.command.ActiveMQMapMessage;

public class JMSMessageList {

	protected Vector<JMSMessage> JMSMessages = new Vector<JMSMessage>();
	protected Set<IJMSMessageViewer> changeListeners = new HashSet<IJMSMessageViewer>();
	private String[] propertyNames;
	
	public JMSMessageList(String[] propNames) {
		propertyNames = propNames;
	}
	
	/**
	 * Return the collection of JMSMessages
	 */
	public Vector getMessages() {
		return JMSMessages;
	}
	
	/**
	 * Add a new JMSMessage to the collection of JMSMessages 
	 */
	public void addJMSMessage(JMSMessage jmsm) {
		if (jmsm == null) {
			return;
		} else {
			JMSMessages.add(JMSMessages.size(), jmsm);
			Iterator iterator = changeListeners.iterator();
			while (iterator.hasNext())
				((IJMSMessageViewer) iterator.next()).addJMSMessage(jmsm);
		}
	}
	
	
	/**
	 * Add a new JMSMessage to the collection of JMSMessages 
	 */
	public void addJMSMessage(MapMessage mm) {
		if (mm == null) {
			return;
		} else {
			JMSMessage jmsm = addMessageProperties(mm);
			JMSMessages.add(JMSMessages.size(), jmsm);
			Iterator iterator = changeListeners.iterator();
			while (iterator.hasNext())
				((IJMSMessageViewer) iterator.next()).addJMSMessage(jmsm);
		}
	}

	/**
	 * Add a new JMSMessageList<HashMap> to the collection of JMSMessages 
	 */
	public void addJMSMessageList(ArrayList<HashMap<String, String>> messageList) {
		HashMap<String, String> message = null;
		MapMessage mm = null;
		JMSMessage jmsm = null;
		ListIterator<HashMap<String, String>> it = messageList.listIterator();
		while (it.hasNext()) {
			message = it.next();
			if(message.size()!=0){
				mm = hashMap2mapMessage(message);
				jmsm = addMessageProperties(mm);
				JMSMessages.add(JMSMessages.size(), jmsm);
				Iterator iterator = changeListeners.iterator();
				while (iterator.hasNext())
					((IJMSMessageViewer) iterator.next()).addJMSMessage(jmsm);

			}
		}
	}
	
	public void addJMSMessageList(Vector<JMSMessage> messageList) {
		JMSMessages.addAll(messageList);
	}
	
	public Vector<JMSMessage> getJMSMessageList() {
		return JMSMessages;
	}
	
	public MapMessage hashMap2mapMessage(HashMap<String, String> message) {
		MapMessage mm = null;
		
		mm = new ActiveMQMapMessage();
		Set<String> lst = message.keySet();
		Iterator<String> it = lst.iterator();
		while(it.hasNext()) {
			String key = (String)it.next();
			String value = (String) message.get(key);
			try {
				mm.setString(key, value);
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		return mm;
	}

	protected JMSMessage addMessageProperties(MapMessage mm) {
		JMSMessage jmsm = new JMSMessage(propertyNames);
		try {
			Enumeration lst = mm.getMapNames();
			while(lst.hasMoreElements()) {
				String key = (String)lst.nextElement();
				jmsm.setProperty(key.toUpperCase(), mm.getString(key));
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return jmsm;
	}
	
	/**
	 * @param task
	 */
	public void removeJMSMessage(JMSMessage jmsm) {
		JMSMessages.remove(jmsm);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IJMSMessageViewer) iterator.next()).removeJMSMessage(jmsm);
	}
	
//	public void modelChanged() {
//		Iterator iterator = changeListeners.iterator();
//		while (iterator.hasNext())
//			((IJMSMessageViewer) iterator.next()).removeJMSMessage(jmsm);
//	}
		
	/**
	 * @param viewer
	 */
	public void removeChangeListener(IJMSMessageViewer viewer) {
		changeListeners.remove(viewer);
	}

	/**
	 * @param viewer
	 */
	public void addChangeListener(IJMSMessageViewer viewer) {
		changeListeners.add(viewer);
	}

	public int getSize() {
		return JMSMessages.size();
	}
	
	public void clearList() {
		JMSMessages.clear();
	}
	
}