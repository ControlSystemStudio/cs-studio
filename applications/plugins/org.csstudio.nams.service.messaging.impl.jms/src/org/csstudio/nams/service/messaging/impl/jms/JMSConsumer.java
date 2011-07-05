
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

	private static class WorkThread extends Thread {
		private static long instanceCount = 0;
		private final LinkedBlockingQueue<Message> messageQueue;
		private volatile boolean arbeitFortsetzen = true;
		private MessageConsumer consumer;

		private final Logger logger;

		public WorkThread(final LinkedBlockingQueue<Message> messageQueue,
				final Session session, final String source,
				final String clientId, final PostfachArt art,
				final Logger logger) throws JMSException {
			super("JMSConsumer#WorkThread-" + (++WorkThread.instanceCount));
			this.messageQueue = messageQueue;
			this.logger = logger;

			switch (art) {
			case QUEUE:
				final Queue queue = session.createQueue(source);
				this.consumer = session.createConsumer(queue);

				break;
			case TOPIC:
				final Topic topic = session.createTopic(source);
				this.consumer = session.createConsumer(topic);
				break;
			case TOPIC_DURABLE:
				final Topic topic2 = session.createTopic(source);
				consumer = session.createDurableSubscriber(topic2, clientId
						+ "-" + topic2.getTopicName());
				break;
			default:
				// TODO exception handling
				break;
			}
		}

		public void close() {
			this.arbeitFortsetzen = false;
			this.interrupt(); // Keine Sorge, unbehandelte Nachricht wird
			// nicht acknowledged und kommt daher wieder.
			Thread.yield();
			try {
				this.consumer.close();
			} catch (final JMSException e) {
			    // Can be ignored
			}
			this.logger.logDebugMessage(this, "Consumer WorkThread stoped");
		}

		@Override
		public void run() {
			this.logger.logDebugMessage(this,
					"start to receive jms Messages - " + this.consumer);
			while (this.arbeitFortsetzen) {
				try {
					final Message message = this.consumer.receive();
					if (message != null) {
						this.logger.logInfoMessage(this, "Recieved message: "
								+ message.toString());
						this.messageQueue.put(message);
						this.logger.logDebugMessage(this,
								"Message put to working queue");
					}
					// Beachten das die connection geschlossen sein könnte
					// und auch auf das failover protokoll achten
				} catch (final JMSException e) {
					// TODO exception handling
					// wird von consumer.receive() geworfen
					this.logger.logInfoMessage(this,
							"Exception during recieving message from jms", e);
				} catch (final InterruptedException e) {
					// TODO exception handling
					// wird von messageQueue.put(message) geworfen
					this.logger
							.logInfoMessage(
									this,
									"Put of recieved jms-message to local queue has been interrupted",
									e);
				} catch (final Throwable t) {
					this.logger
							.logFatalMessage(
									this,
									"Unexpected exception during recieving message from jms",
									t);
				}
				Thread.yield();
			}
		}
	}

	private static Logger injectedLogger;

	public static void staticInjectLogger(final Logger logger) {
		JMSConsumer.injectedLogger = logger;
	}

	private boolean isClosed = false;
	private final WorkThread[] workers;
	private final LinkedBlockingQueue<Message> messageQueue;

	private final Logger logger;

	public JMSConsumer(final String clientId, final String messageSourceName,
			final PostfachArt art, final Session[] sessions)
			throws JMSException {

		this.logger = JMSConsumer.injectedLogger;
		// Schaufeln in BlockingQueue : Maximum size auf 1,
		// damit nicht hunderte Nachrichten während eines updates gepufert
		// werden, das ablegen in der Queue blockiert, wenn diese voll ist.
		// Siehe java.util.concurrent.BlockingQueue.
		this.messageQueue = new LinkedBlockingQueue<Message>(1);

		this.workers = new WorkThread[sessions.length];

		try {
			for (int i = 0; i < sessions.length; i++) {
				this.workers[i] = new WorkThread(this.messageQueue,
						sessions[i], messageSourceName, clientId, art,
						this.logger);
				this.workers[i].start();
			}
		} catch (final JMSException e) {
			this.close();
			throw e;
		}
	}

	@Override
    public void close() {
		for (final WorkThread worker : this.workers) {
			if (worker != null) {
				worker.close();
			}
		}
		this.isClosed = true;
		this.logger.logDebugMessage(this, "Consumer closed");
	}

	@Override
    public boolean isClosed() {
		return this.isClosed;
	}

	@Override
    public NAMSMessage receiveMessage() throws MessagingException {
		NAMSMessage result = null;
		try {
			final Message message = this.messageQueue.take();

			if (message instanceof MapMessage) {
				final MapMessage mapMessage = (MapMessage) message;
				final Map<MessageKeyEnum, String> map = new HashMap<MessageKeyEnum, String>();
				final Map<String, String> unknownMap = new HashMap<String, String>();
				final Enumeration<?> mapNames = mapMessage.getMapNames();
				if (!mapNames.hasMoreElements()) {
					this.logger.logWarningMessage(this,
							"Message does not contain any content: "
									+ mapMessage.toString());
					mapMessage.acknowledge();
				} else {
					while (mapNames.hasMoreElements()) {
						final String currentElement = mapNames.nextElement()
								.toString();
						MessageKeyEnum messageKeyEnum = null;
						try {
							messageKeyEnum = MessageKeyEnum
									.getEnumFor(currentElement);
						} catch (RuntimeException e) {
							logger.logWarningMessage(this, "unknown field", e);
						}
						if (messageKeyEnum != null) {
							String value = mapMessage.getString(currentElement);
							if (value == null) {
								value = "";
							}
							map.put(messageKeyEnum, value);
						} else {
							unknownMap.put(currentElement, mapMessage.getString(currentElement));
						}
					}

					final AcknowledgeHandler ackHandler = new AcknowledgeHandler() {
						@Override
                        public void acknowledge() throws Throwable {
							mapMessage.acknowledge();
							JMSConsumer.this.logger.logDebugMessage(this,
									"JMSConsumer.ackHandler.acknowledge() called for message "
											+ mapMessage.toString());
						}
					};

					if (MessageKeyUtil.istSynchronisationAuforderung(map)) {
						result = new DefaultNAMSMessage(
								new SyncronisationsAufforderungsSystemNachchricht(),
								ackHandler) {
							@Override
							public String toString() {
								return "SyncronisationsAufforderungsSystemNachchricht: JMS-Message: "
										+ mapMessage.toString();
							}
						};
					} else if (MessageKeyUtil
							.istSynchronisationBestaetigung(map)) {
						result = new DefaultNAMSMessage(
								new SyncronisationsBestaetigungSystemNachricht(),
								ackHandler) {
							@Override
							public String toString() {
								return "SyncronisationsBestaetigungSystemNachricht: JMS-Message: "
										+ mapMessage.toString();
							}
						};
					} else {
						// Alarmnachricht
						result = new DefaultNAMSMessage(
								new AlarmNachricht(map, unknownMap), ackHandler) {
							@Override
							public String toString() {
								return "Alarmnachricht: JMS-Message: "
										+ mapMessage.toString();
							}
						};
					}
				}
			} else {
				final Message unknownMessage = message;
				this.logger.logWarningMessage(this,
						"unknown Message type received: "
								+ unknownMessage.toString());
				result = new DefaultNAMSMessage(new AcknowledgeHandler() {
					@Override
                    public void acknowledge() throws Throwable {
						unknownMessage.acknowledge();
						JMSConsumer.this.logger.logDebugMessage(this,
								"JMSConsumer.ackHandler.acknowledge() called for message "
										+ unknownMessage.toString());
					}
				}) {
					@Override
					public String toString() {
						return "Unknown-message-type of message: JMS-Message: "
								+ unknownMessage.toString();
					}
				};
			}
		} catch (final InterruptedException e) {
			throw new MessagingException("message receiving interrupted", e);
		} catch (final JMSException e) {
			throw new MessagingException("message receiving failed", e);
		}
		return result;
	}
}
