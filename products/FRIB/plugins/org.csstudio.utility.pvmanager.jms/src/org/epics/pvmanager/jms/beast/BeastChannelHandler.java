/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
package org.epics.pvmanager.jms.beast;

import org.csstudio.alarm.beast.Messages;
import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.client.AlarmConfiguration;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelRoot;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.epics.pvmanager.jms.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.transport.TransportListener;
import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.MultiplexedChannelHandler;
import org.epics.pvmanager.ValueCache;
import org.epics.util.time.Timestamp;

/**
 * 
 * @author msekoranja
 */
public class BeastChannelHandler extends
		MultiplexedChannelHandler<BeastChannelHandler, Message> implements
		MessageListener {

	private final String topicName;
	private final String pvName;
	private MessageConsumer consumer;
	private MessageProducer producer;
	private MessageConsumer consumer_client;
	private MessageProducer producer_client;
	private volatile Session session;
	private BeastDataSource beastDataSource;

	private final static Pattern hasOptions = Pattern
			.compile("(.*) (\\{.*\\})");
	private final static Pattern options = Pattern
			.compile("\\{\"(\\w+)\":\"(.*)\"\\}");

	/**
	 * 
	 * @param chanelName
	 */
	public BeastChannelHandler(String topicName, String pvName,
			BeastDataSource beastDataSource) {
		super(pvName);
		Matcher matcher = hasOptions.matcher(getChannelName());
		if (matcher.matches()) {
			this.pvName = matcher.group(1);
			String clientOptions = matcher.group(2);
			Matcher optionsMatcher = options.matcher(clientOptions);
			if (optionsMatcher.matches()) {
				String option = optionsMatcher.group(1);
				String arg = optionsMatcher.group(2);
				// TODO: Hack, this should have a real JSON parser
				switch (option) {
				case "topic":
					this.beastDataSource = beastDataSource;
					this.topicName = arg;
					break;
				default:
					throw new IllegalArgumentException(
							"Option not recognized for " + getChannelName());
				}
			} else {
				throw new IllegalArgumentException("Option not recognized for "
						+ getChannelName());
			}

		} else {
			this.beastDataSource = beastDataSource;
			this.topicName = topicName;
			this.pvName = pvName;
		}

	}

	@Override
	public void connect() {
		try {
			final ActiveMQConnection amq_connection = (ActiveMQConnection) beastDataSource
					.getConnection();
			amq_connection.addTransportListener(transportListener);
			amq_connection.setExceptionListener(exceptionListener);
			session = beastDataSource.getConnection().createSession(
			/* transacted */false, Session.AUTO_ACKNOWLEDGE);
			this.producer = createProducer(topicName + "_SERVER");
			this.consumer = createConsumer(topicName + "_SERVER");
			this.consumer.setMessageListener(this);

			this.producer_client = createProducer(topicName + "_CLIENT");
			this.consumer_client = createConsumer(topicName + "_CLIENT");
			this.consumer_client.setMessageListener(this);
			createInitBeastMapMessage();
			processConnection(this);

		} catch (Exception ex) {
			Logger.getLogger(BeastChannelHandler.class.getName()).log(
					Level.SEVERE, null, ex);
		}

	}

	@Override
	public boolean isConnected(BeastChannelHandler beastChannelHandler) {
		final ActiveMQConnection amq_connection = ((ActiveMQSession) beastChannelHandler.session)
				.getConnection();
		return amq_connection.getTransport().isConnected();

	}

	@Override
	public boolean isWriteConnected(BeastChannelHandler beastChannelHandler) {
		final ActiveMQConnection amq_connection = ((ActiveMQSession) beastChannelHandler.session)
				.getConnection();
		return amq_connection.getTransport().isConnected();

	}

	@Override
	public void disconnect() {
		try {
			session.close();
		} catch (JMSException ex) {
			Logger.getLogger(BeastChannelHandler.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	@Override
	protected BeastTypeAdapter findTypeAdapter(ValueCache<?> cache,
			BeastChannelHandler beastChannelHandler) {
		return beastDataSource.getTypeSupport()
				.find(cache, beastChannelHandler);
	}

	@Override
	public void write(Object newValue, ChannelWriteCallback callback) {
		try {

			if (newValue instanceof String) {
				MapMessage message = createBeastMapMessage((String) newValue);
				producer_client.send(message);
			} else {
				throw new RuntimeException("Unsupported type for JMS: "
						+ newValue.getClass());
			}
		} catch (JMSException ex) {
			Logger.getLogger(BeastChannelHandler.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	@Override
	public void onMessage(Message msg) {
		if (msg instanceof MapMessage) {
			MapMessage mapMessage = (MapMessage) msg;
			try {
				if (mapMessage.getString(JMSLogMessage.NAME) != null) {
					if (mapMessage.getString(JMSLogMessage.NAME).equals(pvName)
							|| pvName.isEmpty()) {
						processMessage(mapMessage);
					}
					if (mapMessage.getString(JMSLogMessage.NAME).equals(
							"CONFIG")) {
						createInitBeastMapMessage();
					}
				}
			} catch (Exception ex) {
				Logger.getLogger(BeastChannelHandler.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		} else {
			throw new RuntimeException("Unsupported type for Beast: "
					+ msg.getClass());
		}
	}

	private final TransportListener transportListener = new TransportListener() {
		@Override
		public void onCommand(Object o) {
		}

		@Override
		public void onException(IOException ioe) {
			Logger.getLogger(BeastChannelHandler.class.getName()).log(
					Level.SEVERE, null, ioe);
		}

		@Override
		public void transportInterupted() {
			processConnection(BeastChannelHandler.this);
		}

		@Override
		public void transportResumed() {
			processConnection(BeastChannelHandler.this);
			try {
				createInitBeastMapMessage();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};;

	private final ExceptionListener exceptionListener = new ExceptionListener() {
		@Override
		public void onException(JMSException jmse) {
			Logger.getLogger(BeastChannelHandler.class.getName()).log(
					Level.SEVERE, null, jmse);
		}
	};

	;

	/**
	 * Create a producer. Derived class can use this to create one or more
	 * producers, sending MapMessages to them in the communicator thread.
	 * 
	 * @param topic_name
	 *            Name of topic for the new producer
	 * @return MessageProducer
	 * @throws JMSException
	 *             on error
	 */
	protected MessageProducer createProducer(final String topic_name)
			throws JMSException {
		final Topic topic = session.createTopic(topic_name);
		final MessageProducer producer = session.createProducer(topic);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		return producer;
	}

	/**
	 * Create a consumer.
	 * 
	 * @param topic_name
	 *            Name of topic for the new consumer
	 * @return MessageProducer
	 * @throws JMSException
	 *             on error
	 */
	protected MessageConsumer createConsumer(final String topic_name)
			throws JMSException {
		final Topic topic = session.createTopic(topic_name);
		final MessageConsumer consumer = session.createConsumer(topic);
		return consumer;
	}

	public String channelName() {
		return pvName;
	}

	private MapMessage createBeastMapMessage(final String text)
			throws JMSException {
		MapMessage map = session.createMapMessage();
		map.setString(JMSLogMessage.TYPE, JMSAlarmMessage.TYPE_ALARM);
		map.setString(JMSAlarmMessage.CONFIG, this.topicName);
		map.setString(JMSLogMessage.TEXT, text);
		map.setString(JMSLogMessage.APPLICATION_ID, "CSS");
		map.setString(JMSLogMessage.HOST, "host");
		map.setString(JMSLogMessage.USER, "user");
		map.setString(JMSLogMessage.NAME,
				text.equals("CONFIG") ? this.topicName : this.pvName);
		return map;
	}

	private void createInitBeastMapMessage() throws Exception {

		final AlarmConfiguration config = new AlarmConfiguration(
				Preferences.getRDB_Url(), Preferences.getRDB_User(),
				Preferences.getRDB_Password(), Preferences.getRDB_Schema());
		final String topicName = this.topicName;
		final String pvName = this.pvName;
		// todo: check for topicName in database, with error
		String[] test = config.listConfigurations();

		Job job = new Job("Loading init beast pvmanager from DB") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(
						Messages.AlarmClientModel_ReadingConfiguration,
						IProgressMonitor.UNKNOWN);
				try {
					config.readConfiguration(topicName, false, monitor);
					AlarmTreePV pv = config.findPV(pvName);
					MapMessage map = session.createMapMessage();
					map.setString(JMSLogMessage.TYPE,
							JMSAlarmMessage.TYPE_ALARM);
					map.setString(JMSAlarmMessage.CONFIG, topicName);
					map.setString(JMSLogMessage.APPLICATION_ID, "CSS");
					map.setString(JMSLogMessage.HOST, "host");
					map.setString(JMSLogMessage.USER, "user");
					map.setString(JMSLogMessage.NAME, pvName);
					map.setString(JMSAlarmMessage.VALUE,
							pv.getValue() == null ? "" : pv.getValue());
					map.setString(JMSLogMessage.SEVERITY, pv.getSeverity()
							.name());
					map.setString(JMSAlarmMessage.STATUS,
							pv.getCurrentMessage());
					map.setString(
							JMSAlarmMessage.EVENTTIME,
							new SimpleDateFormat(JMSLogMessage.DATE_FORMAT,
									Locale.ENGLISH)
									.format(pv.getTimestamp() == null ? new Date()
											: pv.getTimestamp().toDate()));
					processMessage(map);
				} catch (Exception e) {
					e.printStackTrace();
					return Status.CANCEL_STATUS;
				}
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
}
