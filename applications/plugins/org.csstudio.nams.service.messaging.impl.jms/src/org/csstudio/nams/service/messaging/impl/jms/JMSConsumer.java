package org.csstudio.nams.service.messaging.impl.jms;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.SyncronisationsAufforderungsSystemNachchricht;
import org.csstudio.nams.common.material.SyncronisationsBestaetigungSystemNachricht;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.DefaultNAMSMessage;
import org.csstudio.nams.service.messaging.declaration.NAMSMessage;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.declaration.DefaultNAMSMessage.AcknowledgeHandler;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;

class JMSConsumer implements Consumer {

	private static Logger injectedLogger;

	public static void staticInjectLogger(Logger logger) {
		injectedLogger = logger;
	}

	private boolean isClosed = false;
	private WorkThread[] workers;
	private LinkedBlockingQueue<Message> messageQueue;
	private Logger logger;

	public JMSConsumer(String clientId, String messageSourceName,
			PostfachArt art, Session[] sessions) throws JMSException {

		logger = injectedLogger;
		// Schaufeln in BlockingQueue : Maximum size auf 1,
		// damit nicht hunderte Nachrichten während eines updates gepufert
		// werden, das ablegen in der Queue blockiert, wenn diese voll ist.
		// Siehe java.util.concurrent.BlockingQueue.
		messageQueue = new LinkedBlockingQueue<Message>(1);

		workers = new WorkThread[sessions.length];

		try {
			for (int i = 0; i < sessions.length; i++) {
				workers[i] = new WorkThread(messageQueue, sessions[i],
						messageSourceName, clientId, art, logger);
				workers[i].start();
			}
		} catch (JMSException e) {
			close();
			throw e;
		}
	}

	public void close() {
		for (WorkThread worker : workers) {
			if (worker != null) {
				worker.close();
			}
		}
		isClosed = true;
		logger.logDebugMessage(this, "Consumer closed");
	}

	public boolean isClosed() {
		return isClosed;
	}

	public NAMSMessage receiveMessage() throws MessagingException {
		NAMSMessage result = null;
		try {
			Message message = messageQueue.take();

			if (message instanceof MapMessage) {
				final MapMessage mapMessage = (MapMessage) message;
				Map<MessageKeyEnum, String> map = new HashMap<MessageKeyEnum, String>();
				Enumeration<?> mapNames = mapMessage.getMapNames();
				if (!mapNames.hasMoreElements()) {
					logger.logWarningMessage(this,
							"Message does not contain any content: "
									+ mapMessage.toString());
					mapMessage.acknowledge();
				} else {
					while (mapNames.hasMoreElements()) {
						String currentElement = mapNames.nextElement()
								.toString();
						MessageKeyEnum messageKeyEnum = MessageKeyConverter
								.getEnumKeyFor(currentElement);
						if (messageKeyEnum != null) {
							String value = mapMessage.getString(currentElement);
							if (value == null) {
								value = "";
							}
							map.put(messageKeyEnum, value);
						}
					}

					AcknowledgeHandler ackHandler = new AcknowledgeHandler() {
						public void acknowledge() throws Throwable {
							mapMessage.acknowledge();
							logger.logDebugMessage(this,
									"JMSConsumer.ackHandler.acknowledge() called for message "
											+ mapMessage.toString());
						}
					};

					if (MessageKeyConverter.istSynchronisationAuforderung(map)) {
						result = new DefaultNAMSMessage(
								new SyncronisationsAufforderungsSystemNachchricht(),
								ackHandler) {
							@Override
							public String toString() {
								return "SyncronisationsAufforderungsSystemNachchricht: JMS-Message: " + mapMessage.toString();
							}
						};
					} else if (MessageKeyConverter
							.istSynchronisationBestaetigung(map)) {
						result = new DefaultNAMSMessage(
								new SyncronisationsBestaetigungSystemNachricht(),
								ackHandler) {
							@Override
							public String toString() {
								return "SyncronisationsBestaetigungSystemNachricht: JMS-Message: " + mapMessage.toString();
							}
						};
					} else {
						// Alarmnachricht
						result = new DefaultNAMSMessage(
								new AlarmNachricht(map), ackHandler) {
							@Override
							public String toString() {
								return "Alarmnachricht: JMS-Message: " + mapMessage.toString();
							}
						};
					}
				}
			} else {
				logger.logWarningMessage(this,
						"unknown Message type received: " + message.toString());
				message.acknowledge();
			}
		} catch (InterruptedException e) {
			throw new MessagingException("message receiving interrupted", e);
		} catch (JMSException e) {
			throw new MessagingException("message receiving failed", e);
		}
		return result;
	}

	private static class WorkThread extends Thread {
		private final LinkedBlockingQueue<Message> messageQueue;
		private volatile boolean arbeitFortsetzen = true;
		private MessageConsumer consumer;
		private final Logger logger;

		public WorkThread(LinkedBlockingQueue<Message> messageQueue,
				Session session, String source, String clientId,
				PostfachArt art, Logger logger) throws JMSException {
			this.messageQueue = messageQueue;
			this.logger = logger;

			switch (art) {
			case QUEUE:
				Queue queue = session.createQueue(source);
				consumer = session.createConsumer(queue);

				break;
			case TOPIC:
				Topic topic = session.createTopic(source);
				consumer = session.createDurableSubscriber(topic, clientId
						+ "-" + topic.getTopicName());

				break;
			default:
				// TODO exception handling
				break;
			}
		}

		public void close() {
			arbeitFortsetzen = false;
			try {
				consumer.close();
			} catch (JMSException e) {
			}
			this.interrupt(); // Keine Sorge, unbehandelte Nachricht wird
			// nicht acknowledged und kommt daher wieder.
			logger.logDebugMessage(this, "Consumer WorkThread stoped");
		}

		@Override
		public void run() {
			while (arbeitFortsetzen) {
				try {
					Message message = consumer.receive();
					if (message != null) {
						logger.logInfoMessage(this, "Recieved message: "
								+ message.toString());
						messageQueue.put(message);
						logger.logDebugMessage(this,
								"Message put to working queue");
					}
					// Beachten das die connection geschlossen sein könnte
					// und auch auf das failover protokoll achten
				} catch (JMSException e) {
					// TODO exception handling
					// wird von consumer.receive() geworfen
					logger.logInfoMessage(this, "Exception during recieving message from jms", e);
				} catch (InterruptedException e) {
					// TODO exception handling
					// wird von messageQueue.put(message) geworfen
					logger.logInfoMessage(this, "Put of recieved jms-message to local queue has been interrupted", e);
				} catch( Throwable t ) {
					logger.logFatalMessage(this, "Unexpected exception during recieving message from jms", t);
				}
				Thread.yield();
			}
		}
	}
}
