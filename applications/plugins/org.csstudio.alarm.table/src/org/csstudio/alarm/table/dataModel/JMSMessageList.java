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

import org.exolab.jms.message.MapMessageImpl;



public class JMSMessageList {

	private Vector JMSMessages = new Vector();
	private Set changeListeners = new HashSet();
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
	public void addJMSMessage() {
		JMSMessage jmsm = new JMSMessage(propertyNames);
		JMSMessages.add(JMSMessages.size(), jmsm);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IJMSMessageViewer) iterator.next()).addJMSMessage(jmsm);
	}

	
	
	/**
	 * Add a new JMSMessage to the collection of JMSMessages 
	 */
	public void addJMSMessage(MapMessage mm) {
		if (mm == null) {
			return;
		}
		JMSMessage jmsm = addMessageProperties(mm);
			JMSMessages.add(JMSMessages.size(), jmsm);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IJMSMessageViewer) iterator.next()).addJMSMessage(jmsm);
	}

	/**
	 * Add a new JMSMessageList<HashMap> to the collection of JMSMessages 
	 */
	public void addJMSMessageList(ArrayList<HashMap<String, String>> messageList) {
		HashMap<String, String> message = null;
		MapMessage mm = null;
		JMSMessage jmsm = null;
		
		System.out.println("messageList.size()"+messageList.size());
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
//		Iterator iterator = changeListeners.iterator();
//		while (iterator.hasNext())
//			((IJMSMessageViewer) iterator.next()).addJMSMessage(jmsm);
	}
	

	public MapMessage hashMap2mapMessage(HashMap<String, String> message) {
		MapMessage mm = null;
		try {
			mm = new MapMessageImpl();
		} catch (JMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Set<String> lst = message.keySet();
		Iterator<String> it = lst.iterator();
		while(it.hasNext()) {
			String key = (String)it.next();
			String value = (String) message.get(key);
			try {
				mm.setString(key, value);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mm;
	}

	private JMSMessage addMessageProperties(MapMessage mm) {
		JMSMessage jmsm = new JMSMessage(propertyNames);

		try {
			Enumeration lst = mm.getMapNames();
			while(lst.hasMoreElements()) {
				String key = (String)lst.nextElement();
				jmsm.setProperty(key.toUpperCase(), mm.getString(key));
//				if (key.equalsIgnoreCase("type")) {
//					jmsm.setType(mm.getString(key));
//				}
//				if (key.equalsIgnoreCase("applicationid")) {
//					jmsm.setApplicationId(mm.getString(key));
//				}
//				if (key.equalsIgnoreCase("class")) {
//					jmsm.setClass_(mm.getString(key));
//				}
//				if (key.equalsIgnoreCase("destination")) {
//					jmsm.setDestination(mm.getString(key));
//				}
//				if (key.equalsIgnoreCase("domain")) {
//					jmsm.setDomain(mm.getString(key));
//				}
//				if (key.equalsIgnoreCase("eventtime")) {
//					jmsm.setEventtime(mm.getString(key));
//				}
//				if (key.equalsIgnoreCase("facility")) {
//					jmsm.setFacility(mm.getString(key));
//				}
//				if (key.equalsIgnoreCase("host")) {
//					jmsm.setHost(mm.getString(key));
//				}
//				if (key.equalsIgnoreCase("location")) {
//					jmsm.setLocation(mm.getString(key));
//				}
//				if (key.equalsIgnoreCase("name")) {
//					jmsm.setName(mm.getString(key));
//				}
//				if (key.equalsIgnoreCase("processid")) {
//					jmsm.setProcessId(mm.getString(key));
//				}
//				if (key.equalsIgnoreCase("severity")) {
//					jmsm.setSeverity(mm.getString(key));
//				}
//				if (key.equalsIgnoreCase("status")) {
//					jmsm.setStatus(mm.getString(key));
//				}
//				if (key.equalsIgnoreCase("text")) {
//					jmsm.setText(mm.getString(key));
//				}
//				if (key.equalsIgnoreCase("user")) {
//					jmsm.setUser(mm.getString(key));
//				}
//				if (key.equalsIgnoreCase("value")) {
//					jmsm.setValue(mm.getString(key));
//				}
			}
		} catch (JMSException e) {
			System.out.println("jms ex: " + e);
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
		JMSMessage jmsm = new JMSMessage(propertyNames);
		Iterator iterator = changeListeners.iterator();
		JMSMessages.clear();
	}
	
}