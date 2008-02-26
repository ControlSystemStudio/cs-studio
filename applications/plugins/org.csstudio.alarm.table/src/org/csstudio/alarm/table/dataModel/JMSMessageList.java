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
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.csstudio.alarm.table.JmsLogsPlugin;

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
	 * @throws JMSException 
	 */
	public void addJMSMessage(MapMessage mm) throws JMSException {
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
		JMSMessage[] jmsmArray = new JMSMessage[messageList.size()];
		int i = 0;
		ArrayList<JMSMessage> newMessages = new ArrayList<JMSMessage>();
		ListIterator<HashMap<String, String>> it = messageList.listIterator();
		while (it.hasNext()) {
			message = it.next();
			if(message.size()!=0){
				mm = hashMap2mapMessage(message);
				jmsm = addMessageProperties(mm);
				JMSMessages.add(JMSMessages.size(), jmsm);
				newMessages.add(jmsm);
				jmsmArray[i] = jmsm;
				i++;
			}
		}
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			((IJMSMessageViewer) iterator.next()).addJMSMessages(jmsmArray);
		}
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
				JmsLogsPlugin.logException("can not create jms property", e);
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
			JmsLogsPlugin.logException("can not set jms property", e);
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