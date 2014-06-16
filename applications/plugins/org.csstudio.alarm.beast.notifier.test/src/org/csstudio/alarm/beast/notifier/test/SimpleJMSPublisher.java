/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.test;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.platform.utility.jms.JMSConnectionFactory;

/**
 * Simple JMS publisher for test purpose. Publish messages on a given URL/Topic.
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public class SimpleJMSPublisher {

	private String url;
	private Session session;
	private Connection connection;
	private MessageProducer producer;

	public int msgCount = 0;

	public SimpleJMSPublisher(String jms_url, String jms_topic)
			throws JMSException {
		this.url = jms_url;
		connection = JMSConnectionFactory.connect(url);
		connection.start();
		session = connection.createSession(/* transacted */false,
				Session.AUTO_ACKNOWLEDGE);
		Topic topic = session.createTopic(jms_topic);
		producer = session.createProducer(topic);
		producer.setTimeToLive(10000);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	}

	public MapMessage createMapMessage() throws JMSException {
		return session.createMapMessage();
	}

	public void sendMessage(MapMessage msg) throws JMSException {
		producer.send(msg);
		msgCount++;
	}

}
